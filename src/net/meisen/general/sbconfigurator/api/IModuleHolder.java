package net.meisen.general.sbconfigurator.api;

import java.util.Map;

/**
 * A holder of {@code Modules} retrieved from a configuration.
 * 
 * @author pmeisen
 * 
 */
public interface IModuleHolder {

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
	 * Method called to destroy all the objects (i.e. calling destroy-methods)
	 * of the configuration.
	 */
	public void release();
}
