package net.meisen.general.sbconfigurator.authoring;

import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.FactoryBean;

public class ConfigurationMapping implements FactoryBean<Object> {

	private String configurationModule;
	private IConfiguration configuration;

	@Override
	public Object getObject() {
		return configuration.getModule(getConfigurationModule());
	}

	@Override
	public Class<?> getObjectType() {

		// as long as no configuration is known, we have to return null
		if (configuration == null) {
			return null;
		}

		// try to get the module and get it's class
		final Object module = getObject();
		if (module == null) {
			return Object.class;
		} else {
			return module.getClass();
		}
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setConfiguration(final IConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getConfigurationModule() {
		return configurationModule;
	}

	public void setConfigurationModule(final String configurationModule) {
		this.configurationModule = configurationModule;
	}
}
