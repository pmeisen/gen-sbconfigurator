package net.meisen.general.sbconfigurator.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.meisen.general.genmisc.resources.Resource;
import net.meisen.general.genmisc.resources.ResourceInfo;
import net.meisen.general.genmisc.resources.Xml;
import net.meisen.general.genmisc.types.Objects;
import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.api.placeholder.IPropertyReplacer;
import net.meisen.general.sbconfigurator.api.placeholder.IXmlPropertyReplacer;
import net.meisen.general.sbconfigurator.api.transformer.ILoaderDefinition;
import net.meisen.general.sbconfigurator.api.transformer.IXsdValidator;
import net.meisen.general.sbconfigurator.api.transformer.IXsltTransformer;
import net.meisen.general.sbconfigurator.config.exception.InvalidConfigurationException;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;
import net.meisen.general.sbconfigurator.config.exception.ValidationFailedException;
import net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.MethodInvoker;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * The default implementation of the <code>IConfiguration</code> interface. This
 * is needed to load the configuration.
 * 
 * @author pmeisen
 */
public class DefaultConfiguration implements IConfiguration {
	private final static Logger LOG = LoggerFactory
			.getLogger(DefaultConfiguration.class);

	/**
	 * The <code>coreSettings</code> are defined in the
	 * <code>sbconfigurator-core.xml</code> context. The id must be
	 * <code>coreSettings</code> to be wired correctly.
	 * 
	 * @see #coreSettingsId
	 */
	@Autowired
	@Qualifier(coreSettingsId)
	private ConfigurationCoreSettings coreSettings;

	@Autowired
	@Qualifier(corePropertyHolderId)
	private SpringPropertyHolder corePropertyHolder;

	/**
	 * This <code>Collection</code> is auto-wired with all the
	 * <code>LoaderDefintions</code>, which are configured via the
	 * <code>sbconfigurator-core.xml</code> context.
	 */
	@Autowired(required = false)
	private Map<String, ILoaderDefinition> loaderDefinitions;

	/**
	 * It is possible to wire the <code>DefaultConfiguration</code> with a
	 * <code>XsdValidator</code>, which is used to validate XML content. The
	 * <code>XsdValidator</code> must be defined in the
	 * <code>sbconfigurator-core.xml</code> context and must use the id
	 * <code>xsdValidator</code>.
	 */
	@Autowired(required = false)
	@Qualifier("xsdValidator")
	private IXsdValidator xsdValidator;

	/**
	 * The <code>XsltTransformer</code> is used to transform XML definitions
	 * into bean XML definitions. The validator must be defined in the
	 * <code>sbconfigurator-core.xml</code> context and must use the id
	 * <code>xsltTransformer</code>.
	 */
	@Autowired
	@Qualifier("xsltTransformer")
	private IXsltTransformer xsltTransformer;

	@Autowired(required = false)
	@Qualifier(IXmlPropertyReplacer.xmlReplacerId)
	private IXmlPropertyReplacer xmlReplacer;

	@Autowired
	@Qualifier(IPropertyReplacer.replacerId)
	private IPropertyReplacer replacer;

	/**
	 * The <code>Collection</code> of all the loaded modules. A module can be
	 * anything which is defined to be loaded via a
	 * <code>LoaderDefinition</code>. Each module is represented by its bean-id.
	 */
	private final Map<String, Object> modules = new HashMap<String, Object>();

	/**
	 * The <code>Collection</code> of all the <code>BeanDefinition</code> found
	 * (so far)
	 */
	private final Map<String, BeanDefinition> moduleDefinitions = new HashMap<String, BeanDefinition>();

	/**
	 * The <code>DefaultListableBeanFactory</code> which is used to load all the
	 * modules. This factory has to be an attribute, because of pre-loading
	 * purposes, i.e. if a bean retrieves a module from the configuration prior
	 * to the loading of the module (i.e. within an init-method).
	 */
	private DefaultListableBeanFactory moduleFactory = null;

	@Override
	public void loadConfiguration(final Map<String, Object> injections)
			throws InvalidConfigurationException {
		final Map<String, ILoaderDefinition> userLoaderDefinitions = new HashMap<String, ILoaderDefinition>();

		if (LOG.isTraceEnabled()) {
			LOG.trace("Starting to load the Configuration...");
		}

		// check if something was added via auto-wiring, if not there is nothing
		// more to do
		if (loaderDefinitions == null) {

			// make sure we have a Collection from now on
			loaderDefinitions = new HashMap<String, ILoaderDefinition>();
		} else {

			// load the default loader definitions
			for (final Entry<String, ILoaderDefinition> entry : loaderDefinitions
					.entrySet()) {
				final ILoaderDefinition loaderDefinition = entry.getValue();

				// do some logging
				if (LOG.isDebugEnabled()) {
					LOG.debug("Loading configuration from loader '"
							+ entry.getKey() + "'; " + loaderDefinition);
				}

				// now load the definition
				final DefaultListableBeanFactory beanFactory = loadBeanFactory(loaderDefinition);
				final Map<String, ILoaderDefinition> loaderBeans = beanFactory
						.getBeansOfType(ILoaderDefinition.class);

				// load the core LoaderDefinitions
				for (final Entry<String, ILoaderDefinition> coreEntry : loaderBeans
						.entrySet()) {
					final ILoaderDefinition loader = coreEntry.getValue();
					final String id = coreEntry.getKey();

					// ILoaderDefinitions have to be loaded
					if (loader instanceof ILoaderDefinition) {
						if (userLoaderDefinitions.containsKey(id)) {
							if (!isUserLoaderOverridingAllowed()) {
								throw new InvalidConfigurationException(
										"The id of the loader '"
												+ id
												+ "' is used multiple times, which is not allowed via the coreSettings.");
							}

							// override the current one
							final ILoaderDefinition newDef = (ILoaderDefinition) loader;
							final ILoaderDefinition oldDef = userLoaderDefinitions
									.put(id, newDef);

							// log the overriding
							if (LOG.isDebugEnabled()) {
								LOG.debug("The loader '" + id + "' ('"
										+ oldDef.getClass().getName()
										+ "' was overridden by '"
										+ newDef.getClass().getName());
							}
						} else if (loaderDefinitions.containsKey(id)) {
							throw new InvalidConfigurationException(
									"The id of the loader '"
											+ id
											+ "' is already used by a core LoaderDefinition.");
						} else {
							userLoaderDefinitions.put(id,
									(ILoaderDefinition) loader);
						}
					}
				}

				// add all the other definitions to be loaded later
				final Map<String, BeanDefinition> defs = SpringHelper
						.getBeanDefinitions(beanFactory,
								ILoaderDefinition.class);
	
				// everything else is registered as module
				registerModuleBeanDefinitions(defs, entry.getKey());
			}
		}

		// the core engine is up and running now
		if (LOG.isTraceEnabled()) {
			LOG.trace("Core implementation of Configuration is up and running, found "
					+ userLoaderDefinitions.size()
					+ " more LoaderDefinition(s) to be loaded.");
		}

		// load the additional definitions which are defined by users
		registerUserLoaderDefinitions(userLoaderDefinitions);

		// we collected everything
		if (LOG.isTraceEnabled()) {
			LOG.trace("Loaded '"
					+ moduleDefinitions.size()
					+ "' moduleDefinitions. The modules will be instantiated now.");

			LOG.trace("The following properties have been loaded:");

			final List<Entry<Object, Object>> list = new ArrayList<Entry<Object, Object>>();
			list.addAll(getProperties().entrySet());

			Collections.sort(list, new Comparator<Entry<Object, Object>>() {

				@Override
				public int compare(final Entry<Object, Object> e1,
						final Entry<Object, Object> e2) {

					// determine if there are nulls
					final boolean e1Null = e1 == null;
					final boolean e2Null = e2 == null;

					// check and return the result
					if (e1Null && e2Null) {
						return 0;
					} else if (e1Null || e2Null) {
						return e1Null ? -1 : 1;
					} else {
						return e1.getKey().toString()
								.compareTo(e2.getKey().toString());
					}
				}
			});

			for (final Entry<Object, Object> e : list) {
				LOG.trace(" - " + e.getKey() + " = " + e.getValue());
			}
		}

		// create the factory
		moduleFactory = SpringHelper.createBeanFactory(true, false);
		moduleFactory.registerSingleton(coreSettingsId, coreSettings);
		moduleFactory.registerSingleton(coreConfigurationId, this);
		moduleFactory.registerSingleton(corePropertyHolderId,
				corePropertyHolder);
		for (final Entry<String, BeanDefinition> entry : moduleDefinitions
				.entrySet()) {
			moduleFactory.registerBeanDefinition(entry.getKey(),
					entry.getValue());
		}
		for (final Entry<String, Object> entry : injections.entrySet()) {
			moduleFactory.registerSingleton(entry.getKey(), entry.getValue());
		}

		// now let's add all the modules
		final Map<String, Object> modules = moduleFactory
				.getBeansOfType(Object.class);
		for (final Entry<String, Object> entry : modules.entrySet()) {
			registerModule(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Registers all the user <code>LoaderDefintions</code> defined via the
	 * <code>coreLoaderDefinition</code>.
	 * 
	 * @param userLoaderDefinitions
	 *            the <code>LoaderDefinition</code> instances which are provided
	 *            via the <code>coreLoaderDefinition</code>
	 */
	protected void registerUserLoaderDefinitions(
			final Map<String, ILoaderDefinition> userLoaderDefinitions) {

		// lets load the userDefinitions now
		for (final Entry<String, ILoaderDefinition> entry : userLoaderDefinitions
				.entrySet()) {
			final ILoaderDefinition loaderDefinition = entry.getValue();

			// load the definitions
			final DefaultListableBeanFactory beanFactory = loadBeanFactory(loaderDefinition);

			// add it to the loaded ones
			loaderDefinitions.put(entry.getKey(), loaderDefinition);

			// add all the definitions to be loaded later
			final Map<String, BeanDefinition> defs = SpringHelper
					.getBeanDefinitions(beanFactory, ILoaderDefinition.class);

			// everything else is registered as module
			registerModuleBeanDefinitions(defs, entry.getKey());
		}
	}

	/**
	 * Checks if the specified <code>module</code> is really a valid module,
	 * i.e. if it can be added to the loaded modules or not.
	 * 
	 * @param id
	 *            the id of the module to be checked
	 * @param module
	 *            the module to be checked
	 * 
	 * @return <code>true</code> if the specified <code>module</code> should be
	 *         added, otherwise <code>false</code>
	 */
	protected boolean isModule(final String id, final Object module) {
		if (id == null || module == null) {
			return false;
		} else if (coreSettingsId.equals(id)) {
			return false;
		} else if (coreConfigurationId.equals(id)) {
			return false;
		} else if (corePropertyHolderId.equals(id)) {
			return false;
		} else if (module instanceof ConfigurationCoreSettings) {
			return false;
		}
		// also don't add any Factories of Spring those are helper beans
		else if (module instanceof MethodInvoker) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Registers the specified <code>beanDefinitions</code> to the one that will
	 * be loaded for the <code>Configuration</code>.
	 * 
	 * @param beanDefinitions
	 *            the collection of <code>BeanDefinition</code> instances to be
	 *            registered
	 * @param loaderId
	 *            the loader's identifier for logging purposes
	 */
	protected void registerModuleBeanDefinitions(
			final Map<String, BeanDefinition> beanDefinitions,
			final String loaderId) {

		// add each beanDefinition
		for (final Entry<String, BeanDefinition> entry : beanDefinitions
				.entrySet()) {
			registerModuleBeanDefinition(entry.getKey(), entry.getValue(),
					loaderId);
		}
	}

	/**
	 * Registers a single <code>beanDefinition</code> to be loaded for the
	 * <code>Configuration</code>.
	 * 
	 * @param id
	 *            the <code>beanDefinition</code>'s identifier
	 * @param beanDefinition
	 *            the <code>BeanDefinition</code> instance to be registered
	 * @param loaderId
	 *            the loader's identifier for logging purposes
	 */
	protected void registerModuleBeanDefinition(final String id,
			final BeanDefinition beanDefinition, final String loaderId) {

		// register the module
		if (Objects.empty(beanDefinition) || Objects.empty(id)) {
			throw new IllegalArgumentException("The id ('" + id
					+ "') or the beanDefinition cannot be null.");
		} else if (moduleDefinitions.put(id, beanDefinition) != null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Overloading the moduleDefinition '" + id
						+ "' with the one from the loaderDefinition '"
						+ loaderId + "'");
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Added the moduleDefinition '" + id
						+ "' from loaderDefinition '" + loaderId + "'");
			}
		}
	}

	/**
	 * Registers the specified <code>module</code> to all the loaded modules.
	 * 
	 * @param id
	 *            the id of the module to be registered
	 * @param module
	 *            the <code>module</code> to be registered
	 * 
	 * @return <code>true</code> if the <code>module</code> was added, otherwise
	 *         <code>false</code>
	 */
	protected boolean registerModule(final String id, final Object module) {
		final Object current;

		// register the module
		if (!isModule(id, module)) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Skipping the bean '" + id + "' as module");
			}

			return false;
		} else if ((current = modules.put(id, module)) != null) {

			if (LOG.isWarnEnabled() && !Objects.equals(current, module)) {
				LOG.warn("Overloading the module '" + id + "'");
			}

			return true;
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loaded the module '" + id + "'");
			}

			return true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModule(final String name) {
		Object module = modules.get(name);

		// it might be that the module is not instantiated yet
		if (module == null && moduleFactory != null
				&& moduleDefinitions.containsKey(name)) {
			module = moduleFactory.getBean(name);

			// register the module
			if (registerModule(name, module)) {

				// do some logging
				if (LOG.isDebugEnabled()) {
					LOG.debug("Pre-Loaded the module '"
							+ name
							+ "', no need to be worried this might happen if init-methods are used.");
				}
			} else {
				module = null;
			}
		}

		// return the module
		return (T) module;
	}

	@Override
	public Map<String, Object> getAllModules() {
		return Collections.unmodifiableMap(modules);
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>LoaderDefinition</code>.
	 * 
	 * @param loaderDefinition
	 *            the <code>LoaderDefinition</code> which specifies which data
	 *            to be loaded
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         <code>LoaderDefinition</code>
	 */
	public DefaultListableBeanFactory loadBeanFactory(
			final ILoaderDefinition loaderDefinition) {
		return loadBeanFactory(loaderDefinition.getSelector(),
				loaderDefinition.getXsltTransformerInputStream(),
				loaderDefinition.getContext(),
				loaderDefinition.isValidationEnabled(),
				loaderDefinition.isBeanOverridingAllowed(),
				loaderDefinition.isLoadFromClassPath(),
				loaderDefinition.isLoadFromWorkingDir());
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>xmlFileName</code>, using the passed <code>xsltTransformer</code>
	 * to transform the data into a bean XML definition. The XML might be
	 * validated if <code>validate</code> is set to <code>true</code>.
	 * Furthermore the <code>beanOverriding</code> specifies if beans can be
	 * overwritten within the context, i.e. all the XML files found with the
	 * specified <code>xmlFileName</code>.
	 * 
	 * @param xmlFileName
	 *            the XML files to be loaded
	 * @param xsltTransformer
	 *            the XSLT transformer to be used to transform the XML files
	 *            into bean definitions
	 * @param validate
	 *            <code>true</code> if the XML of the <code>xmlFileName</code>
	 *            should be validated, otherwise <code>false</code>
	 * @param beanOverriding
	 *            <code>true</code> if beans of the context can be overwritten,
	 *            otherwise <code>false</code>
	 * @param loadFromClasspath
	 *            <code>true</code> if the <code>xmlFileName</code> should be
	 *            searched on the classpath, otherwise <code>false</code>
	 * @param loadFromWorkingDir
	 *            <code>true</code> if the <code>xmlFileName</code> should be
	 *            searched in the current working-directory (and all
	 *            sub-directories), otherwise <code>false</code>
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         parameters
	 */
	public DefaultListableBeanFactory loadBeanFactory(final String xmlFileName,
			final InputStream xsltTransformer, final boolean validate,
			final boolean beanOverriding, final boolean loadFromClasspath,
			final boolean loadFromWorkingDir) {
		return loadBeanFactory(xmlFileName, xsltTransformer, null, validate,
				beanOverriding, loadFromClasspath, loadFromWorkingDir);
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>xmlFileName</code>, using the passed <code>xsltTransformer</code>
	 * to transform the data into a bean XML definition. The XML might be
	 * validated if <code>validate</code> is set to <code>true</code>.
	 * Furthermore the <code>beanOverriding</code> specifies if beans can be
	 * overwritten within the context, i.e. all the XML files found with the
	 * specified <code>xmlFileName</code>.
	 * 
	 * @param xmlSelector
	 *            the XML files to be loaded
	 * @param xsltStream
	 *            the stream of the XSLT used for transformation
	 * @param context
	 *            the context to look for the specified <code>xmlFileName</code>
	 *            , might be <code>null</code> if all files on the class-path
	 *            with the specified <code>xmlFileName</code> should be loaded
	 * @param validate
	 *            <code>true</code> if the XML of the <code>xmlFileName</code>
	 *            should be validated, otherwise <code>false</code>
	 * @param beanOverriding
	 *            <code>true</code> if beans of the context can be overwritten,
	 *            otherwise <code>false</code>
	 * @param loadFromClasspath
	 *            <code>true</code> if the <code>xmlFileName</code> should be
	 *            searched on the classpath, otherwise <code>false</code>
	 * @param loadFromWorkingDir
	 *            <code>true</code> if the <code>xmlFileName</code> should be
	 *            searched in the current working-directory (and all
	 *            sub-directories), otherwise <code>false</code>
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         parameters
	 */
	public DefaultListableBeanFactory loadBeanFactory(final String xmlSelector,
			final InputStream xsltStream, final Class<?> context,
			final boolean validate, final boolean beanOverriding,
			final boolean loadFromClasspath, final boolean loadFromWorkingDir) {

		// get the selector with replaced properties
		final String replaceXmlSelector = replacer.replacePlaceholders(
				xmlSelector, getProperties());

		// create the factory and enable auto-wiring to setup the core system
		final DefaultListableBeanFactory factory = SpringHelper
				.createBeanFactory(beanOverriding, false);

		// create the reader
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
				factory);

		// create the transformer
		if (xsltTransformer != null) {

			// replace the properties within the xslt
			org.springframework.core.io.Resource res = replacePlaceholders(xsltStream);

			try {
				final InputStream xsltReplacedStream = res == null ? null : res
						.getInputStream();
				xsltTransformer.setXsltTransformer(xsltReplacedStream);
			} catch (final InvalidXsltException e) {
				throw new InvalidConfigurationException(
						"The specified XSLT is invalid and therefore cannot be used.",
						e);
			} catch (final IOException e) {
				throw new InvalidConfigurationException(
						"The specified XSLT stream cannot be accessed.", e);
			}
		}

		// check if we have a context
		if (context == null) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Creating factory for files '" + replaceXmlSelector
						+ "' without context");
			}

			// read all the bean.xmls on the classpath
			final Collection<ResourceInfo> resInfos = Resource.getResources(
					replaceXmlSelector, loadFromClasspath, loadFromWorkingDir);

			if (LOG.isTraceEnabled()) {
				LOG.trace("Found '" + resInfos.size()
						+ "' to be loaded for selector '" + replaceXmlSelector
						+ "'");
			}

			// read all the loaded resources
			for (final ResourceInfo resInfo : resInfos) {
				final InputStream resIo = Resource.getResourceAsStream(resInfo);

				// log the current resource
				if (LOG.isTraceEnabled()) {
					LOG.trace("Loading '" + replaceXmlSelector
							+ "' at location '" + resInfo.getFullPath() + "'");
				}

				// add the resource
				addResourceToReader(reader, xsltTransformer, resIo, validate);
			}
		} else {
			final String contextPath = context.getPackage().getName()
					.replace(".", "/");
			final String fileClassPath = contextPath + "/" + replaceXmlSelector;

			if (LOG.isTraceEnabled()) {
				LOG.trace("Creating factory for file '" + replaceXmlSelector
						+ "' using context '" + fileClassPath + "'");
			}

			// get the resource
			final InputStream resIo = context.getClassLoader()
					.getResourceAsStream(fileClassPath);
			addResourceToReader(reader, xsltTransformer, resIo, validate);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Loaded factory for files '" + replaceXmlSelector
					+ "' (size: " + factory.getBeanDefinitionCount() + ")");
		}

		/*
		 * use the postProcessing to replace properties (i.e. for imports, those
		 * are loaded directly via Spring and therefore not replaced within the
		 * normal replacement)
		 */
		if (corePropertyHolder != null) {
			corePropertyHolder.postProcessBeanFactory(factory);
		}

		return factory;
	}

	/**
	 * Adds a specific resource to the <code>reader</code>.
	 * 
	 * @param reader
	 *            the <code>XmlBeanDefinitionReader</code> to which the resource
	 *            should be added
	 * @param xsltTransformer
	 *            the XSLT transformer used to transform the XML stream into a
	 *            XML bean definition
	 * @param resStream
	 *            the resource to be added to the <code>reader</code>
	 * @param validate
	 *            <code>true</code> if the streamed XML should be validated,
	 *            otherwise <code>false</code>
	 */
	protected void addResourceToReader(final XmlBeanDefinitionReader reader,
			final IXsltTransformer xsltTransformer,
			final InputStream resStream, final boolean validate) {

		// get the content of the stream
		org.springframework.core.io.Resource res = replacePlaceholders(resStream);

		// validate the resource if needed
		if (validate && xsdValidator != null
				&& coreSettings.isConfigurationValidationEnabled()) {

			if (LOG.isTraceEnabled()) {
				LOG.trace("Start to validate the current resource.");
			}

			try {
				xsdValidator.validate(SpringHelper.getInputStream(res));
			} catch (final ValidationFailedException e) {
				throw new BeanDefinitionStoreException(
						"The resource could not be validated", e);
			}
		}

		// now we have to transform the resource
		if (xsltTransformer != null) {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			if (LOG.isTraceEnabled()) {
				LOG.trace("Start to transform the current resource.");
			}

			try {
				xsltTransformer.transform(SpringHelper.getInputStream(res),
						outputStream);
			} catch (final TransformationFailedException e) {
				throw new BeanDefinitionStoreException(
						"The resource could not be transformed", e);
			}

			if (LOG.isTraceEnabled()) {
				LOG.trace("Finished transformation, result: "
						+ System.getProperty("line.separator")
						+ outputStream.toString());
			}

			// get the new content
			res = new ByteArrayResource(outputStream.toByteArray());
		}

		// finally add the transformed resource
		reader.setValidating(isConfigurationValidationEnabled());
		reader.loadBeanDefinitions(res);
	}

	/**
	 * Defines if any loaded configuration should be validated against it's
	 * defined or specified XSD schema. If no
	 * <code>ConfigurationCoreSettings</code> are defined, the default return
	 * value is <code>false</code>, otherwise the value defined by the
	 * <code>ConfigurationCoreSettings</code> is used.
	 * 
	 * @return <code>true</code> if any loaded configuration should be validated
	 *         against it's defined or specified XSD schema, otherwise
	 *         <code>false</code>
	 */
	public boolean isConfigurationValidationEnabled() {
		return coreSettings != null
				&& coreSettings.isConfigurationValidationEnabled();
	}

	/**
	 * Defines if an instance of a <code>ILoaderDefinition</code> defined by a
	 * user can be overridden by another user's <code>ILoaderDefinition</code>.
	 * If no <code>ConfigurationCoreSettings</code> are defined, the default
	 * return value is <code>false</code>, otherwise the value defined by the
	 * <code>ConfigurationCoreSettings</code> is used.
	 * 
	 * @return <code>true</code> if a user's <code>ILoaderDefinition</code> can
	 *         be overridden, otherwise <code>false</code>
	 */
	public boolean isUserLoaderOverridingAllowed() {
		return coreSettings != null
				&& coreSettings.isUserLoaderOverridingAllowed();
	}

	/**
	 * Loads the <code>Document</code> from the specified
	 * {@link org.springframework.core.io.Resource Resource}.
	 * 
	 * @param res
	 *            the resource to load the {@link Document} from
	 * 
	 * @return the <code>Document</code> specified by the passed
	 *         <code>Resource</code>
	 */
	protected Document loadDocument(
			final org.springframework.core.io.Resource res) {

		// get the resource as encoded one
		final EncodedResource encRes = new EncodedResource(res);

		// read the XML document and replace the placeholders
		InputStream inputStream = null;
		InputSource inputSource = null;
		Document doc = null;
		try {
			inputStream = encRes.getResource().getInputStream();
			inputSource = new InputSource(inputStream);
			if (encRes.getEncoding() != null) {
				inputSource.setEncoding(encRes.getEncoding());
			}

			// get the Document
			final DefaultDocumentLoader loader = new DefaultDocumentLoader();
			doc = loader.loadDocument(inputSource, null, null,
					XmlValidationModeDetector.VALIDATION_NONE, false);
		} catch (final Exception e) {

			// log it
			if (LOG.isWarnEnabled()) {
				LOG.warn(
						"Unable to parse the passed ByteArrayResource '"
								+ res.getDescription() + "'.", e);
			}

			throw new IllegalArgumentException(
					"The passed resource contains an invalid document.", e);
		} finally {

			// close the streams
			Streams.closeIO(inputSource);
			Streams.closeIO(inputStream);
		}

		return doc;
	}

	/**
	 * Replaces the properties within the passed <code>InputStream</code>.
	 * 
	 * @param resStream
	 *            the <code>InputStream</code> which provides the source to
	 *            replace the properties in
	 * 
	 * @return the <code>Resource</code> with the replaced properties
	 */
	protected org.springframework.core.io.Resource replacePlaceholders(
			final InputStream resStream) {

		// if we don't have anything we cannot create anything
		if (resStream == null) {
			return null;
		} else {

			final byte[] content;
			try {
				content = Streams.copyStreamToByteArray(resStream);
			} catch (final IOException e) {
				throw new BeanDefinitionStoreException(
						"The resource could not be read", e);
			} finally {

				// close the stream
				Streams.closeIO(resStream);
			}

			// now let's get the resource
			org.springframework.core.io.Resource res = new ByteArrayResource(
					content);
			return replacePlaceholders(res);
		}
	}

	/**
	 * Replaces all the placeholders (i.e. properties) within the passed
	 * {@link org.springframework.core.io.Resource Resource}.
	 * 
	 * @param res
	 *            the <code>Resource</code> to replace the placeholders in
	 * 
	 * @return a <code>Resource</code> with replaced placeholders
	 */
	protected org.springframework.core.io.Resource replacePlaceholders(
			final org.springframework.core.io.Resource res) {
		org.springframework.core.io.Resource resultRes = res;

		// replace the values
		if (corePropertyHolder != null && xmlReplacer != null) {

			// load the Document specified by the resource
			final Document doc = loadDocument(res);

			// get the properties
			final Properties properties = getProperties();

			// get the document with the replacements
			final Document resDoc = xmlReplacer.replacePlaceholders(doc,
					properties);

			// get the content of the new document
			final byte[] content = Xml.createByteArray(resDoc);

			// now create the resource from the content
			resultRes = new ByteArrayResource(content);
		}

		// return the new resource
		return resultRes;
	}

	/**
	 * Gets all the <code>Properties</code> defined for <code>this</code>
	 * configuration.
	 * 
	 * @return the <code>Properties</code> of <code>this</code> configuration
	 */
	public Properties getProperties() {
		try {
			return corePropertyHolder.getProperties();
		} catch (final IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(
						"Unable to load the properties of the configuration.",
						e);
			}

			return new Properties();
		}
	}

	@Override
	public <T> T createInstance(final Class<T> clazz) {
		@SuppressWarnings("unchecked")
		final T bean = (T) moduleFactory.autowire(clazz,
				AutowireCapableBeanFactory.AUTOWIRE_NO, false);

		return bean;
	}

	@Override
	public <T> T wireInstance(final T bean) {
		moduleFactory.autowireBeanProperties(bean,
				AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		return bean;
	}
}
