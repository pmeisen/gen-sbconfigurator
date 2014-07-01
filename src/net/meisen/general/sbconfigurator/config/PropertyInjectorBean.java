package net.meisen.general.sbconfigurator.config;

import java.util.Map;
import java.util.Properties;

/**
 * A bean used to inject properties within the {@code SpringPropertyHolder}
 * during the configuration loading.
 * 
 * @author pmeisen
 * 
 */
public class PropertyInjectorBean {

	private final Properties properties = new Properties();

	/**
	 * Gets the set properties.
	 * 
	 * @return the set properties
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Sets some properties.
	 * 
	 * @param properties
	 *            the final properties to be set
	 */
	public void setProperties(final Properties properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Sets some properties.
	 * 
	 * @param properties
	 *            the final properties to be set
	 */
	public void setProperties(final Map<String, String> properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Sets a property.
	 * 
	 * @param key
	 *            the key of the property
	 * @param value
	 *            the value of the property
	 * 
	 */
	public void setProperty(final String key, final String value) {
		this.properties.setProperty(key, value);
	}
}
