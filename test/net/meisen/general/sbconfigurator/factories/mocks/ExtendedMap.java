package net.meisen.general.sbconfigurator.factories.mocks;

import java.util.HashMap;

/**
 * Map with additional constructors.
 * 
 * @author pmeisen
 * 
 */
public class ExtendedMap extends HashMap<String, Object> {
	private static final long serialVersionUID = 2454849132158895872L;

	/**
	 * Constructor with a key and value to be added to the map.
	 * 
	 * @param key
	 *            the key of the value to be added
	 * @param value
	 *            the value to be added
	 */
	public ExtendedMap(final String key, final Object value) {
		this.put(key, value);
	}

	/**
	 * Constructor to add the specified keys and values to the map.
	 * 
	 * @param keys
	 *            the keys to be added
	 * @param values
	 *            the values for the keys
	 */
	public ExtendedMap(final String[] keys, final Object[] values) {
		final int max = Math.max(keys.length, values.length);
		for (int i = 0; i < max; i++) {
			this.put(keys[i], values[i]);
		}
	}
}
