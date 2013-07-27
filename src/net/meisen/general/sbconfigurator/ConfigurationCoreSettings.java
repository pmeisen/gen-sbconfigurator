package net.meisen.general.sbconfigurator;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The core settings of the configuration should be loaded when the application
 * is started. Those core settings are loaded via the
 * <code>coreSettingsContext</code>, i.e. a Spring-context. The context-file
 * must be located at the same location as the
 * <code>ConfigurationCoreSettings</code> and therefore cannot be overridden.
 * 
 * @author pmeisen
 * 
 */
public class ConfigurationCoreSettings {
	private final static String coreSettingsContext = "sbconfigurator-core.xml";

	private final static Logger LOG = LoggerFactory
			.getLogger(ConfigurationCoreSettings.class);

	@Autowired
	@Qualifier(IConfiguration.coreConfigurationId)
	private IConfiguration configuration;

	private boolean configurationValidationEnabled = true;
	private boolean userLoaderOverridingAllowed = false;

	/**
	 * Method to load the <code>ConfigurationCoreSettings</code> and all the other
	 * modules used by the <code>CoreSettings</code>.
	 * 
	 * @return the loaded <code>ConfigurationCoreSettings</code>
	 */
	public static ConfigurationCoreSettings loadCoreSettings() {
		return loadCoreSettings(null);
	}

	/**
	 * Method to load the <code>ConfigurationCoreSettings</code> and all the other
	 * modules used by the <code>CoreSettings</code>.
	 * 
	 * @param clazz
	 *          using the <code>coreSettingsContext</code> of the specified class
	 * 
	 * @return the loaded <code>ConfigurationCoreSettings</code>
	 */
	public static ConfigurationCoreSettings loadCoreSettings(final Class<?> clazz) {
		return loadCoreSettings(null, clazz);
	}

	/**
	 * Method to load the <code>ConfigurationCoreSettings</code> and all the other
	 * modules used by the <code>CoreSettings</code>.
	 * 
	 * @param coreSettingsContext
	 *          the name of the context file to load the
	 *          <code>ConfigurationCoreSettings</code> from
	 * @param clazz
	 *          using the <code>coreSettingsContext</code> of the specified class
	 * 
	 * @return the loaded <code>ConfigurationCoreSettings</code>
	 */
	public static ConfigurationCoreSettings loadCoreSettings(
			final String coreSettingsContext, final Class<?> clazz) {

		final String fCoreSettingsContext = coreSettingsContext == null ? ConfigurationCoreSettings.coreSettingsContext
				: coreSettingsContext;
		final Class<?> fClazz = clazz == null ? ConfigurationCoreSettings.class
				: clazz;

		// create the factory with auto-wiring this will bring up the core-system
		final DefaultListableBeanFactory factory = SpringHelper.createBeanFactory(
				true, true);

		// create the reader
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		// load the core resource into the reader
		final Resource coreRes = new ClassPathResource(fCoreSettingsContext, fClazz);
		reader.loadBeanDefinitions(coreRes);

		// get the bean
		final ConfigurationCoreSettings settings = (ConfigurationCoreSettings) factory
				.getBean("coreSettings");

		if (LOG.isTraceEnabled()) {
			LOG.trace("The coreSettings are loaded and auto-wired.");
		}

		// trigger the loading of the Configuration
		settings.getConfiguration().loadConfiguration();

		return settings;
	}

	/**
	 * Defines if any loaded configuration should be validated against it's
	 * defined or specified XSD schema. The default value is <code>true</code>.
	 * 
	 * @return <code>true</code> if any loaded configuration should be validated
	 *         against it's defined or specified XSD schema, otherwise
	 *         <code>false</code>
	 */
	public boolean isConfigurationValidationEnabled() {
		return configurationValidationEnabled;
	}

	/**
	 * Defines if any loaded configuration should be validated against it's
	 * defined or specified XSD schema. The default value is <code>true</code>.
	 * 
	 * @param enable
	 *          <code>true</code> if any loaded configuration should be validated
	 *          against it's defined or specified XSD schema, otherwise
	 *          <code>false</code>
	 */
	public void setConfigurationValidationEnabled(final boolean enable) {
		this.configurationValidationEnabled = enable;
	}

	/**
	 * Defines if an instance of a <code>ILoaderDefinition</code> defined by a
	 * user can be overridden by another user's <code>ILoaderDefinition</code>.
	 * The default value is <code>false</code>.
	 * 
	 * @return <code>true</code> if a user's <code>ILoaderDefinition</code> can be
	 *         overridden, otherwise <code>false</code>
	 */
	public boolean isUserLoaderOverridingAllowed() {
		return userLoaderOverridingAllowed;
	}

	/**
	 * Defines if an instance of a <code>ILoaderDefinition</code> defined by a
	 * user can be overridden by another user's <code>ILoaderDefinition</code>.
	 * The default value is <code>false</code>.
	 * 
	 * @param allow
	 *          <code>true</code> if a user's <code>ILoaderDefinition</code> can
	 *          be overridden, otherwise <code>false</code>
	 */
	public void setUserLoaderOverridingAllowed(final boolean allow) {
		this.userLoaderOverridingAllowed = allow;
	}

	/**
	 * Gets the currently used <code>IConfiguration</code>.
	 * 
	 * @return the <code>IConfiguration</code>
	 */
	public IConfiguration getConfiguration() {
		return configuration;
	}
}
