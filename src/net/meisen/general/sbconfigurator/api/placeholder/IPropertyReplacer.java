package net.meisen.general.sbconfigurator.api.placeholder;

import java.util.Properties;

/**
 * A <code>PropertyReplacer</code> is used to replace properties within strings.
 * How a property is defined (i.e. prefix and suffix) is defined by the concrete
 * implementation of the replacer.
 * 
 * @author pmeisen
 * 
 */
public interface IPropertyReplacer {
	/**
	 * The id used to represent the <code>IPropertyReplacer</code> to be used.
	 */
	public final static String replacerId = "propertyReplacer";

	/**
	 * Replaces all the defined properties (incl. nested properties) within the
	 * <code>input</code>.
	 * 
	 * @param input
	 *          the string to replace the properties in
	 * @param properties
	 *          the properties to be replaced
	 * 
	 * @return the string with the replaced properties (incl. nested properties)
	 */
	public String replacePlaceholders(final String input,
			final Properties properties);
}
