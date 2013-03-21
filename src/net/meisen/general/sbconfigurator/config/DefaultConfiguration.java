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

	@Autowired
	private ConfigurationCoreSettings coreSettings;

	@Autowired
	private Map<String, ILoaderDefinition> loaderDefinitions;

	private final Map<String, Object> modules = new HashMap<String, Object>();

	@Autowired(required = false)
	private IXsdValidator xsdValidator;

	@Autowired
	private IXsltTransformer xsltTransformer;

	/**
	 * Loads the <code>DefaultConfiguration</code> of the application. This method
	 * should be called by the core-context (i.e. the one and only
	 * <code>sbconfigurator-core.xml</code>) right after everything is set up
	 * (i.e. as <code>init-method</code>).
	 * 
	 * @throws InvalidConfigurationException
	 *           if the <code>DefaultConfiguration</code> could not be loaded
	 */
	public void loadConfiguration() throws InvalidConfigurationException {

		// register the LoaderDefinitions which are defined by the core
		// implementation
		final Map<String, ILoaderDefinition> userLoaderDefinitions = registerCoreLoaderDefinitions();

		// the core engine is up and running now
		if (LOG.isTraceEnabled()) {
			LOG.trace("Core implementation is up and running, found "
					+ userLoaderDefinitions.size()
					+ " more LoaderDefinition(s) to be loaded.");
		}

		// load the additional definitions which are defined by users
		registerUserLoaderDefinitions(userLoaderDefinitions);
	}

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
						throw new InvalidConfigurationException("");
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
					throw new InvalidConfigurationException("");
				} else {
					userLoaderDefinitions.put(loaderId, userEntry.getValue());
				}
			}
		}

		return userLoaderDefinitions;
	}

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
				modules.put(loadedEntry.getKey(), loadedEntry.getValue());
			}
		}
	}

	public Object getModule(final String name) {
		return modules.get(name);
	}

	public ListableBeanFactory loadBeanFactory(
			final ILoaderDefinition loaderDefinition) {
		return loadBeanFactory(loaderDefinition.getSelector(),
				loaderDefinition.getXsltTransformerInputStream(),
				loaderDefinition.getContext(), loaderDefinition.isValidationEnabled(),
				loaderDefinition.isBeanOverridingAllowed());
	}

	public ListableBeanFactory loadBeanFactory(final String xmlFileName,
			final InputStream xsltTransformer, final boolean validate,
			final boolean beanOverriding) {
		return loadBeanFactory(xmlFileName, xsltTransformer, null, validate,
				beanOverriding);
	}

	public ListableBeanFactory loadBeanFactory(final String xmlFileName,
			final InputStream xsltStream, final Class<?> context,
			final boolean validate, final boolean beanOverriding) {

		// create the factory
		final DefaultListableBeanFactory factory = SpringHelper.createBeanFactory();
		factory.setAllowBeanDefinitionOverriding(beanOverriding);

		// register the Singleton of the ConfigurationCoreSettings for auto-wiring
		factory.registerSingleton("coreSettings", coreSettings);

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
					xmlFileName, true, true);

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
