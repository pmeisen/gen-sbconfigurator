package net.meisen.general.sbconfigurator.config.mocks;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class which is wired
 * 
 * @author pmeisen
 * 
 */
public class WiredClass {

	/**
	 * The wired core settings
	 */
	@Autowired
	@Qualifier(IConfiguration.coreSettingsId)
	public ConfigurationCoreSettings coreSettings;
}
