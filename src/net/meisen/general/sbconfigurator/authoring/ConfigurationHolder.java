package net.meisen.general.sbconfigurator.authoring;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.FactoryBean;

public class ConfigurationHolder implements FactoryBean<IConfiguration> {
	private IConfiguration c;

	private Class<?> contextClass = null;
	private String configXml = null;

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

	public Class<?> getContextClass() {
		return contextClass;
	}

	public void setContextClass(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	public String getConfigXml() {
		return configXml;
	}

	public void setConfigXml(String configXml) {
		this.configXml = configXml;
	}
}
