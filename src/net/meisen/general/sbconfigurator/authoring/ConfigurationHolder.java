package net.meisen.general.sbconfigurator.authoring;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} which is used to create the {@link IConfiguration}.
 * 
 * @author pmeisen
 * 
 */
public class ConfigurationHolder implements FactoryBean<IConfiguration> {
	private IConfiguration c;

	private Class<?> contextClass = null;
	private String configXml = null;

	/**
	 * Helper method to get the {@link IConfiguration} wrapped by this factory.
	 * 
	 * @return the {@link IConfiguration} wrapped by this factory
	 */
	public IConfiguration getConfiguration() {
		if (c == null) {
			final ConfigurationCoreSettings c = ConfigurationCoreSettings
					.loadCoreSettings(configXml, contextClass);
			this.c = c.getConfiguration();
		}

		return c;
	}

	@Override
	public IConfiguration getObject() throws Exception {
		return getConfiguration();
	}

	@Override
	public Class<IConfiguration> getObjectType() {
		return IConfiguration.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Gets the class which is used to resolve the {@code configXml}.
	 * 
	 * @return the class which is used to resolve the {@code configXml}
	 */
	public Class<?> getContextClass() {
		return contextClass;
	}

	/**
	 * Sets the class which is used to resolve the {@code configXml}.
	 * 
	 * @param contextClass
	 *            the class which is used to resolve the {@code configXml}
	 */
	public void setContextClass(final Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	/**
	 * Gets the configuration file to load the configuration from.
	 * 
	 * @return the configuration file to load the configuration from
	 */
	public String getConfigXml() {
		return configXml;
	}

	/**
	 * Sets the configuration file to load the configuration from.
	 * 
	 * @param configXml
	 *            the configuration file to load the configuration from
	 */
	public void setConfigXml(final String configXml) {
		this.configXml = configXml;
	}
}
