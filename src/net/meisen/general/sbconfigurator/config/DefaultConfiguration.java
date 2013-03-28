package net.meisen.general.sbconfigurator.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.genmisc.resources.Resource;
import net.meisen.general.genmisc.resources.ResourceInfo;
import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.api.transformer.ILoaderDefinition;
import net.meisen.general.sbconfigurator.api.transformer.IXsdValidator;
import net.meisen.general.sbconfigurator.api.transformer.IXsltTransformer;
import net.meisen.general.sbconfigurator.config.exception.InvalidConfigurationException;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;
import net.meisen.general.sbconfigurator.config.exception.ValidationFailedException;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ByteArrayResource;

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
	 * The id used to represent the <code>coreSettings</code>
	 */
	private final static String coreSettingsId = "coreSettings";

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

	/**
	 * This <code>Collection</code> is auto-wired with all the
	 * <code>LoaderDefintions</code>, which are configured via the
	 * <code>sbconfigurator-core.xml</code> context.
	 */
	@Autowired
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
	 * The <code>XsltTransformer</code> is used to transform XML definitions into
	 * bean XML definitions. The validator must be defined in the
	 * <code>sbconfigurator-core.xml</code> context and must use the id
	 * <code>xsltTransformer</code>.
	 */
	@Autowired
	@Qualifier("xsltTransformer")
	private IXsltTransformer xsltTransformer;

	/**
	 * The list of all the loaded modules. A module can be anything which is
	 * defined to be loaded via a <code>LoaderDefinition</code>. Each module is
	 * represented by its bean-id.
	 */
	private final Map<String, Object> modules = new HashMap<String, Object>();

	@Override
	public void loadConfiguration() throws InvalidConfigurationException {

		// register the LoaderDefinitions which are defined by the core
		// implementation
		final Map<String, ILoaderDefinition> coreLoaderDefinitions = registerCoreLoaderDefinitions();

		// the core engine is up and running now
		if (LOG.isTraceEnabled()) {
			LOG.trace("Core implementation is up and running, found "
					+ coreLoaderDefinitions.size()
					+ " more LoaderDefinition(s) to be loaded.");
		}

		// load the additional definitions which are defined by users
		registerUserLoaderDefinitions(coreLoaderDefinitions);
	}

	/**
	 * Registers all the others <code>LoaderDefintions</code> which are defined by
	 * default, i.e. by the <code>sbconfigurator-core.xml</code> context.
	 * 
	 * @return the <code>Collection</code> of <code>LoaderDefinitions</code>
	 *         defined by the core configuration
	 */
	protected Map<String, ILoaderDefinition> registerCoreLoaderDefinitions() {
		final Map<String, ILoaderDefinition> userLoaderDefinitions = new HashMap<String, ILoaderDefinition>();

		// load the default loader definitions
		for (final Entry<String, ILoaderDefinition> entry : loaderDefinitions
				.entrySet()) {
			final ILoaderDefinition loaderDefinition = entry.getValue();

			// do some logging
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading configuration from loader '" + entry.getKey()
						+ "'; " + loaderDefinition);
			}

			// now load the definition
			final ListableBeanFactory beanFactory = loadBeanFactory(loaderDefinition);
			final Map<String, ILoaderDefinition> beans = beanFactory
					.getBeansOfType(ILoaderDefinition.class);

			// load the core LoaderDefinitions
			for (final Entry<String, ILoaderDefinition> userEntry : beans.entrySet()) {
				final String loaderId = userEntry.getKey();

				if (userLoaderDefinitions.containsKey(loaderId)) {
					if (!isUserLoaderOverridingAllowed()) {
						throw new InvalidConfigurationException(
								"The id of the loader '"
										+ loaderId
										+ "' is used multiple times, which is not allowed via the coreSettings.");
					}

					// override the current one
					final ILoaderDefinition newDef = entry.getValue();
					final ILoaderDefinition oldDef = userLoaderDefinitions.put(loaderId,
							userEntry.getValue());

					// log the overriding
					if (LOG.isDebugEnabled()) {
						LOG.debug("The loader '" + loaderId + "' ('"
								+ oldDef.getClass().getName() + "' was overridden by '"
								+ newDef.getClass().getName());
					}
				} else if (loaderDefinitions.containsKey(loaderId)) {
					throw new InvalidConfigurationException("The id of the loader '"
							+ loaderId + "' is already used by a core LoaderDefinition.");
				} else {
					userLoaderDefinitions.put(loaderId, userEntry.getValue());
				}
			}
		}

		return userLoaderDefinitions;
	}

	/**
	 * Registers all the user <code>LoaderDefintions</code> defined via the
	 * <code>coreLoaderDefinition</code>.
	 * 
	 * @param userLoaderDefinitions
	 *          the <code>LoaderDefinition</code> instances which are provided via
	 *          the <code>coreLoaderDefinition</code>
	 */
	protected void registerUserLoaderDefinitions(
			final Map<String, ILoaderDefinition> userLoaderDefinitions) {

		// lets load the userDefinitions now
		for (final Entry<String, ILoaderDefinition> entry : userLoaderDefinitions
				.entrySet()) {
			final ILoaderDefinition loaderDefinition = entry.getValue();

			// load the definitions
			final ListableBeanFactory beanFactory = loadBeanFactory(loaderDefinition);

			// add it to the loaded ones
			loaderDefinitions.put(entry.getKey(), loaderDefinition);

			// get the beans of the loader
			final Map<String, Object> beans = beanFactory
					.getBeansOfType(Object.class);

			// add the module
			for (final Entry<String, Object> loadedEntry : beans.entrySet()) {

				// the coreSettings should not be added as module, they are added for
				// wiring-purposes only
				if (coreSettingsId.equals(loadedEntry.getKey())) {
					continue;
				} else if (loadedEntry.getValue() instanceof ConfigurationCoreSettings) {
					continue;
				}

				// log it
				if (LOG.isDebugEnabled()) {
					LOG.debug("Loaded the module '" + loadedEntry.getKey()
							+ "' from loaderDefinition '" + entry.getKey() + "'");
				}

				// add it and warn if we overwrite something
				if (modules.put(loadedEntry.getKey(), loadedEntry.getValue()) != null) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Overloading the module '" + loadedEntry.getKey()
								+ "' with the one from the loaderDefinition '" + entry.getKey()
								+ "'");
					}
				}
			}
		}
	}

	/**
	 * Get the loaded module for a specified <code>name</code>
	 * 
	 * @param name
	 *          the name of the module to retrieve
	 * 
	 * @return the <code>Object</code> associated to the <code>name</code>
	 *         specified, might be <code>null</code> if no module with the
	 *         specified <code>name</code> could be found
	 */
	public Object getModule(final String name) {
		return modules.get(name);
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>LoaderDefinition</code>.
	 * 
	 * @param loaderDefinition
	 *          the <code>LoaderDefinition</code> which specifies which data to be
	 *          loaded
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         <code>LoaderDefinition</code>
	 */
	public ListableBeanFactory loadBeanFactory(
			final ILoaderDefinition loaderDefinition) {		
		return loadBeanFactory(loaderDefinition.getSelector(),
				loaderDefinition.getXsltTransformerInputStream(),
				loaderDefinition.getContext(), loaderDefinition.isValidationEnabled(),
				loaderDefinition.isBeanOverridingAllowed(),
				loaderDefinition.isLoadFromClassPath(),
				loaderDefinition.isLoadFromWorkingDir());
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>xmlFileName</code>, using the passed <code>xsltTransformer</code> to
	 * transform the data into a bean XML definition. The XML might be validated
	 * if <code>validate</code> is set to <code>true</code>. Furthermore the
	 * <code>beanOverriding</code> specifies if beans can be overwritten within
	 * the context, i.e. all the XML files found with the specified
	 * <code>xmlFileName</code>.
	 * 
	 * @param xmlFileName
	 *          the XML files to be loaded
	 * @param xsltTransformer
	 *          the XSLT transformer to be used to transform the XML files into
	 *          bean definitions
	 * @param validate
	 *          <code>true</code> if the XML of the <code>xmlFileName</code>
	 *          should be validated, otherwise <code>false</code>
	 * @param beanOverriding
	 *          <code>true</code> if beans of the context can be overwritten,
	 *          otherwise <code>false</code>
	 * @param loadFromClasspath
	 *          <code>true</code> if the <code>xmlFileName</code> should be
	 *          searched on the classpath, otherwise <code>false</code>
	 * @param loadFromWorkingDir
	 *          <code>true</code> if the <code>xmlFileName</code> should be
	 *          searched in the current working-directory (and all
	 *          sub-directories), otherwise <code>false</code>
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         parameters
	 */
	public ListableBeanFactory loadBeanFactory(final String xmlFileName,
			final InputStream xsltTransformer, final boolean validate,
			final boolean beanOverriding, final boolean loadFromClasspath,
			final boolean loadFromWorkingDir) {
		return loadBeanFactory(xmlFileName, xsltTransformer, null, validate,
				beanOverriding, loadFromClasspath, loadFromWorkingDir);
	}

	/**
	 * Loads the <code>BeanFactory</code> which is specified by the passed
	 * <code>xmlFileName</code>, using the passed <code>xsltTransformer</code> to
	 * transform the data into a bean XML definition. The XML might be validated
	 * if <code>validate</code> is set to <code>true</code>. Furthermore the
	 * <code>beanOverriding</code> specifies if beans can be overwritten within
	 * the context, i.e. all the XML files found with the specified
	 * <code>xmlFileName</code>.
	 * 
	 * @param xmlFileName
	 *          the XML files to be loaded
	 * @param xsltStream
	 *          the stream of the XSLT used for transformation
	 * @param context
	 *          the context to look for the specified <code>xmlFileName</code>,
	 *          might be <code>null</code> if all files on the class-path with the
	 *          specified <code>xmlFileName</code> should be loaded
	 * @param validate
	 *          <code>true</code> if the XML of the <code>xmlFileName</code>
	 *          should be validated, otherwise <code>false</code>
	 * @param beanOverriding
	 *          <code>true</code> if beans of the context can be overwritten,
	 *          otherwise <code>false</code>
	 * @param loadFromClasspath
	 *          <code>true</code> if the <code>xmlFileName</code> should be
	 *          searched on the classpath, otherwise <code>false</code>
	 * @param loadFromWorkingDir
	 *          <code>true</code> if the <code>xmlFileName</code> should be
	 *          searched in the current working-directory (and all
	 *          sub-directories), otherwise <code>false</code>
	 * 
	 * @return the <code>ListableBeanFactory</code> loaded by the specified
	 *         parameters
	 */
	public ListableBeanFactory loadBeanFactory(final String xmlFileName,
			final InputStream xsltStream, final Class<?> context,
			final boolean validate, final boolean beanOverriding,
			final boolean loadFromClasspath, final boolean loadFromWorkingDir) {

		// create the factory
		final DefaultListableBeanFactory factory = SpringHelper.createBeanFactory();
		factory.setAllowBeanDefinitionOverriding(beanOverriding);

		// register the Singleton of the ConfigurationCoreSettings for auto-wiring
		factory.registerSingleton(coreSettingsId, coreSettings);

		// create the reader
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		// create the transformer
		if (xsltTransformer != null) {
			try {
				xsltTransformer.setXsltTransformer(xsltStream);
			} catch (final InvalidXsltException e) {
				throw new InvalidConfigurationException(
						"The specified XSLT cannot be used.", e);
			}
		}

		// check if we have a context
		if (context == null) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Creating factory for files '" + xmlFileName
						+ "' without context");
			}

			// read all the bean.xmls on the classpath
			final Collection<ResourceInfo> resInfos = Resource.getResources(
					xmlFileName, loadFromClasspath, loadFromWorkingDir);

			// read all the loaded resources
			for (final ResourceInfo resInfo : resInfos) {
				final InputStream resIo = Resource.getResourceAsStream(resInfo);

				// log the current resource
				if (LOG.isTraceEnabled()) {
					LOG.trace("Loading '" + xmlFileName + "' at location '"
							+ resInfo.getFullPath() + "'");
				}

				// add the resource
				addResourceToReader(reader, xsltTransformer, resIo, validate);
			}
		} else {
			final String contextPath = context.getPackage().getName()
					.replace(".", "/");
			final String fileClassPath = contextPath + "/" + xmlFileName;

			if (LOG.isTraceEnabled()) {
				LOG.trace("Creating factory for file '" + xmlFileName
						+ "' using context '" + fileClassPath + "'");
			}

			// get the resource
			final InputStream resIo = context.getClassLoader().getResourceAsStream(
					fileClassPath);
			addResourceToReader(reader, xsltTransformer, resIo, validate);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Loaded factory for files '" + xmlFileName + "' (size: "
					+ factory.getBeanDefinitionCount() + ")");
		}

		return factory;
	}

	/**
	 * Adds a specific resource to the <code>reader</code>
	 * 
	 * @param reader
	 *          the <code>XmlBeanDefinitionReader</code> to which the resource
	 *          should be added
	 * @param xsltTransformer
	 *          the XSLT transformer used to transform the XML stream into a XML
	 *          bean definition
	 * @param resStream
	 *          the resource to be added to the <code>reader</code>
	 * @param validate
	 *          <code>true</code> if the streamed XML should be validated,
	 *          otherwise <code>false</code>
	 */
	protected void addResourceToReader(final XmlBeanDefinitionReader reader,
			final IXsltTransformer xsltTransformer, final InputStream resStream,
			final boolean validate) {

		// get the content of the stream
		final byte[] content;
		try {
			content = Streams.copyStreamToByteArray(resStream);
		} catch (final IOException e) {
			throw new BeanDefinitionStoreException("The resource could not be read",
					e);
		} finally {

			// close the stream
			Streams.closeIO(resStream);
		}

		// now let's get the resource
		ByteArrayResource res = new ByteArrayResource(content);

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
						+ System.getProperty("line.separator") + outputStream.toString());
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
	 * user can be overridden by another user's <code>ILoaderDefinition</code>. If
	 * no <code>ConfigurationCoreSettings</code> are defined, the default return
	 * value is <code>false</code>, otherwise the value defined by the
	 * <code>ConfigurationCoreSettings</code> is used.
	 * 
	 * @return <code>true</code> if a user's <code>ILoaderDefinition</code> can be
	 *         overridden, otherwise <code>false</code>
	 */
	public boolean isUserLoaderOverridingAllowed() {
		return coreSettings != null && coreSettings.isUserLoaderOverridingAllowed();
	}
}
