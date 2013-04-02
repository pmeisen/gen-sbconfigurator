package net.meisen.general.sbconfigurator.test.sampleplugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

/**
 * Just some sample to check the loading of additional modules
 * 
 * @author pmeisen
 * 
 */
public class SamplePlugIn {

	/**
	 * The <code>ConfigurationCoreSettings</code> should be wired
	 */
	@Autowired
	@Qualifier(IConfiguration.coreSettingsId)
	public ConfigurationCoreSettings coreSettings;

	/**
	 * The loaded <code>Configuration</code>
	 */
	@Autowired
	@Qualifier(IConfiguration.coreConfigurationId)
	public IConfiguration configuration;
}
