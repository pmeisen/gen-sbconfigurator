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
	 * The id used to represent the <code>corePropertyHolder</code>
	 */
	public final static String corePropertyHolderId = "corePropertyHolder";

	/**
	 * Loads the <code>Configuration</code> of the application. This method is
	 * called by the core-context (i.e. the one and only
	 * <code>sbconfigurator-core.xml</code>) right after everything is set up
	 * (i.e. as <code>init-method</code>).
	 * 
	 * @param injections
	 *            additional modules which are defined outside and should be
	 *            added to the configuration, cannot be <code>null</code>
	 * 
	 * @throws InvalidConfigurationException
	 *             if the <code>DefaultConfiguration</code> could not be loaded
	 */
	public void loadConfiguration(final Map<String, Object> injections)
			throws InvalidConfigurationException;

	/**
	 * Get the loaded module for a specified <code>name</code>.
	 * 
	 * @param name
	 *            the name of the module to retrieve
	 * 
	 * @return the <code>Object</code> associated to the <code>name</code>
	 *         specified, might be <code>null</code> if no module with the
	 *         specified <code>name</code> could be found
	 */
	public <T> T getModule(final String name);

	/**
	 * Method to retrieve all the modules loaded for the
	 * <code>Configuration</code>.
	 * 
	 * @return a <code>Map</code> of all the loaded modules
	 */
	public Map<String, Object> getAllModules();

	/**
	 * Creates an instance of the specified <code>clazz</code> and wires
	 * annotated fields.
	 * 
	 * @param clazz
	 *            the <code>Class</code> to create an instance from
	 * 
	 * @return the created instance
	 */
	public <T> T createInstance(final Class<T> clazz);

	/**
	 * Wires the passed instance, i.e. all the annotated fields will be wired
	 * afterwards.
	 * 
	 * @param bean
	 *            the instance to be wired
	 * 
	 * @return the wired instance (which is not a clone, it's the same as the
	 *         passed <code>bean</code>)
	 */
	public <T> T wireInstance(final T bean);
}
