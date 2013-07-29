package net.meisen.general.sbconfigurator.config.mocks;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class which has a wired attribute which cannot be resolved but which isn't
 * required.
 * 
 * @author pmeisen
 * 
 */
public class SatisfiableWiredClass {

	/**
	 * The wired core settings
	 */
	@Autowired(required = false)
	@Qualifier("I.Do.NOT.EXIST")
	public ConfigurationCoreSettings coreSettings;
}
