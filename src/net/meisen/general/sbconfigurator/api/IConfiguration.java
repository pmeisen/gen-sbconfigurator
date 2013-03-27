package net.meisen.general.sbconfigurator.api;

import net.meisen.general.sbconfigurator.config.exception.InvalidConfigurationException;

/**
 * Interface which defines the
 * 
 * @author pmeisen
 * 
 */
public interface IConfiguration {

	/**
	 * Loads the <code>Configuration</code> of the application. This method should
	 * be called by the core-context (i.e. the one and only
	 * <code>sbconfigurator-core.xml</code>) right after everything is set up
	 * (i.e. as <code>init-method</code>).
	 * 
	 * @throws InvalidConfigurationException
	 *           if the <code>DefaultConfiguration</code> could not be loaded
	 */
	public void loadConfiguration() throws InvalidConfigurationException;
}
