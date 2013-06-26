package net.meisen.general.sbconfigurator.config.placeholder;

import java.util.Properties;

import net.meisen.general.genmisc.resources.Xml;
import net.meisen.general.sbconfigurator.api.placeholder.IPropertyReplacer;
import net.meisen.general.sbconfigurator.api.placeholder.IXmlPropertyReplacer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Default implementation of a <code>XmlPropertyReplacer</code> which replaces
 * all the placeholders within an xml document. Placeholders in comments are not
 * replaced.
 * 
 * @author pmeisen
 * 
 */
public class DefaultXmlPropertyReplacer implements IXmlPropertyReplacer {
	private final static Logger LOG = LoggerFactory
			.getLogger(DefaultXmlPropertyReplacer.class);

	@Autowired
	@Qualifier(IPropertyReplacer.replacerId)
	private IPropertyReplacer replacer;

	/**
	 * Default constructor which counts on an auto-wired
	 * <code>PropertyReplacer</code>. Or a delayed setting via
	 * {@link #setReplacer(IPropertyReplacer)}.
	 */
	public DefaultXmlPropertyReplacer() {
		// default constructor
	}

	/**
	 * Constructor which specifies the <code>PropertyReplacer</code> to be used.
	 * 
	 * @param replacer
	 *          the <code>PropertyReplacer</code> to use
	 */
	public DefaultXmlPropertyReplacer(final IPropertyReplacer replacer) {
		setReplacer(replacer);
	}

	/**
	 * This implementation creates a clone for the <code>Document</code> prior to
	 * modifying it. Therefore the input won't be modified at all.
	 */
	@Override
	public Document replacePlaceholders(final Document doc,
			final Properties properties) {

		// create a clone
		final Document clonedDoc = Xml.cloneDocument(doc);

		// get the defined replacer
		final IPropertyReplacer replacer = getReplacer();

		// if we don't have any replacer don't do anything
		if (replacer == null) {

			// log the situation that we don't have one
			if (LOG.isInfoEnabled()) {
				LOG.info("Could not replace any properties within the specified xml document, because no replacer was defined. Please specify one via a bean definition (id='"
						+ IPropertyReplacer.replacerId
						+ "'), change your constructor call or set it via setReplacer(...) prior to calling this method.");
			}
		} else if (properties == null || properties.size() < 1) {
			// nothing to do
		} else {

			// get the root and start the replacement
			final Element root = clonedDoc.getDocumentElement();
			replaceInNode(root, properties);
		}

		return clonedDoc;
	}

	/**
	 * Replaces the values in a <code>Node</code>, it's attributes and children.
	 * 
	 * @param node
	 *          the node to replace the placeholders in
	 * @param properties
	 *          the properties to replace
	 */
	public void replaceInNode(final Node node, final Properties properties) {

		final short nodeType = node == null ? -1 : node.getNodeType();

		if (nodeType == -1) {
			// skip it was a null node which should never happen
		} else if (nodeType == Node.ATTRIBUTE_NODE
				|| nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE) {
			final String replaced = getReplacer().replacePlaceholders(
					node.getTextContent(), properties);
			node.setNodeValue(replaced);
		} else if (nodeType == Node.COMMENT_NODE || nodeType == Node.ELEMENT_NODE) {
			// nothing more to do
		} else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Found a node type, which is not specified concerning the replacement of properties. The found node has the type '"
						+ nodeType + "' and prints '" + node.toString() + "'");
			}
		}

		// replace the attributes
		if (node.hasAttributes()) {
			final NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				final Node attribute = attributes.item(i);
				replaceInNode(attribute, properties);
			}
		}

		// keep on going for all the children
		if (node.hasChildNodes()) {
			final NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				final Node child = children.item(i);

				// do the child replacement
				replaceInNode(child, properties);
			}
		}
	}

	/**
	 * Gets the used <code>PropertyReplacer</code> used by this
	 * <code>XmlPropertyReplacer</code>.
	 * 
	 * @return the used <code>PropertyReplacer</code>
	 */
	public IPropertyReplacer getReplacer() {
		return replacer;
	}

	/**
	 * Sets the <code>PropertyReplacer</code> to be used.
	 * 
	 * @param replacer
	 *          the <code>PropertyReplacer</code> to be used
	 */
	public void setReplacer(final IPropertyReplacer replacer) {
		this.replacer = replacer;
	}
}
