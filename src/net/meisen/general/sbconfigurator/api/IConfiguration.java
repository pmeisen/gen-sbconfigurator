package net.meisen.general.sbconfigurator.api;

import java.util.Map;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.config.exception.InvalidConfigurationException;

/**
 * Interface which defines the loaded <code>Configuration</code>. The
 * implementation to use is defined by the <code>sbconfigurator-core.xml</code>.
 * 
 * @author pmeisen
 * 
 */
public interface IConfiguration {
	/**
	 * The id used to represent the <code>coreSettings</code>
	 */
	public final static String coreSettingsId = "coreSettings";
	/**
	 * The id used to represent the <code>coreConfiguration</code>, which can be
	 * retrieved from the <code>coreSettings</code>.
	 * 
	 * @see ConfigurationCoreSettings#getConfiguration()
	 */
	public final static String coreConfigurationId = "coreConfiguration";

	/**
	 * Loads the <code>Configuration</code> of the application. This method is
	 * called by the core-context (i.e. the one and only
	 * <code>sbconfigurator-core.xml</code>) right after everything is set up
	 * (i.e. as <code>init-method</code>).
	 * 
	 * @throws InvalidConfigurationException
	 *           if the <code>DefaultConfiguration</code> could not be loaded
	 */
	public void loadConfiguration() throws InvalidConfigurationException;

	/**
	 * Get the loaded module for a specified <code>name</code>.
	 * 
	 * @param name
	 *          the name of the module to retrieve
	 * 
	 * @return the <code>Object</code> associated to the <code>name</code>
	 *         specified, might be <code>null</code> if no module with the
	 *         specified <code>name</code> could be found
	 */
	public Object getModule(final String name);

	/**
	 * Method to retrieve all the modules loaded for the
	 * <code>Configuration</code>.
	 * 
	 * @return a <code>Map</code> of all the loaded modules
	 */
	public Map<String, Object> getAllModules();
}
