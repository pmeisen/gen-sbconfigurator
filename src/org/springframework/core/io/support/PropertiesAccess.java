package org.springframework.core.io.support;

import java.io.IOException;
import java.util.Properties;

/**
 * Helper class to access the merge method of a class with
 * {@code PropertiesLoaderSupport}.
 * 
 * @author pmeisen
 * 
 * @see PropertiesLoaderSupport
 * 
 */
public class PropertiesAccess {

	/**
	 * Gets the properties of a class with {@code PropertiesLoaderSupport} using
	 * the {@link PropertiesLoaderSupport#mergeProperties()}.
	 * 
	 * @param propertiesLoader
	 *            the instance to retrieve the properties from
	 * @return the properties loaded by the {@code propertiesLoader}
	 * 
	 * @throws IOException
	 *             if a specified location cannot be read
	 */
	public static Properties getProperties(
			final PropertiesLoaderSupport propertiesLoader) throws IOException {
		return propertiesLoader.mergeProperties();
	}
}
