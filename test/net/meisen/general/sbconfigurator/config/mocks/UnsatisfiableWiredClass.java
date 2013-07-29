package net.meisen.general.sbconfigurator.config.mocks;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Wired class which has an attribute which should be, but cannot be wired.
 * 
 * @author pmeisen
 * 
 */
public class UnsatisfiableWiredClass {

	/**
	 * The wired core settings
	 */
	@Autowired(required = true)
	@Qualifier("I.Do.NOT.EXIST")
	public ConfigurationCoreSettings coreSettings;
}
