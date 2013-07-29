package net.meisen.general.sbconfigurator.config.mocks;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Setter class which would throw an exception when the dependency checking of
 * Spring would be enabled and the setter could not be satisfied.
 * 
 * @author pmeisen
 * 
 */
public class SetterClass {

	/**
	 * The wired core settings
	 */
	@Autowired
	@Qualifier(IConfiguration.coreSettingsId)
	public ConfigurationCoreSettings coreSettings;

	/**
	 * Setable object
	 */
	public Object something;

	/**
	 * Setter for something
	 * 
	 * @param something
	 *          the value for something
	 */
	public void setSomething(final Object something) {
		this.something = something;
	}

	/**
	 * Getter for something
	 * 
	 * @return something
	 */
	public Object getSomething() {
		return this.something;
	}
}
