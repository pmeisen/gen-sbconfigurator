package net.meisen.general.sbconfigurator.api.placeholder;

import java.util.Properties;

import org.w3c.dom.Document;

/**
 * A <code>XmlPropertyReplacer</code> is used to replace all occurrences of
 * placeholders within an xml <code>Document</code>.
 * 
 * @author pmeisen
 * 
 * @see Document
 * 
 */
public interface IXmlPropertyReplacer {
	/**
	 * The id used to represent the <code>XmlPropertyPlaceholderHelper</code> to
	 * be used.
	 */
	public final static String xmlReplacerId = "xmlPropertyReplacer";

	/**
	 * Replaces all the occurrences of defined properties within the passed
	 * <code>Document</code>. The implementation must not guaranty that the passed
	 * <code>doc</code> won't be modified.
	 * 
	 * @param doc
	 *          the <code>Document</code> to replace the properties in
	 * @param properties
	 *          the properties to be replaced
	 * 
	 * @return the <code>Document</code> with the replaced properties, depending
	 *         on the implementation this might be the same as the passed
	 *         <code>doc</code>
	 */
	public Document replacePlaceholders(final Document doc,
			final Properties properties);
}
