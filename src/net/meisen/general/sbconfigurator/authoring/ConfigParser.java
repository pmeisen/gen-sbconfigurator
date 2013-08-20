package net.meisen.general.sbconfigurator.authoring;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parser to parse {@code config} elements within the bean-configuration.
 * 
 * @author pmeisen
 * 
 */
public class ConfigParser extends AbstractBeanDefinitionParser {
	private final static String XML_ATTRIBUTE_CONFIGXML = "configXml";
	private final static String XML_ATTRIBUTE_CONTEXTCLASS = "contextClass";
	private final static String XML_ATTRIBUTE_INNERID = "innerId";
	private final static String XML_ATTRIBUTE_OUTERID = "outerId";
	private final static String XML_ELEMENT_MODULE = "module";

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element,
			final ParserContext parserContext) {

		// get a builder for the definition to be build
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition();

		// specify the type which is returned by this definition
		builder.getRawBeanDefinition().setBeanClass(ConfigurationHolder.class);

		// read some attributes and add those to the definition
		final String contextClassName = element
				.getAttribute(XML_ATTRIBUTE_CONTEXTCLASS);
		if (StringUtils.hasText(contextClassName)) {
			builder.addPropertyValue("contextClass", contextClassName);
		}
		final String configXml = element.getAttribute(XML_ATTRIBUTE_CONFIGXML);
		if (StringUtils.hasText(configXml)) {
			builder.addPropertyValue("configXml", configXml);
		}

		// create the final beanDefinition
		final AbstractBeanDefinition definition = builder.getBeanDefinition();

		// get the defined id or generate one
		final String id = resolveId(element, definition, parserContext);

		// bind the definition and the id
		final BeanDefinitionHolder holder = new BeanDefinitionHolder(
				definition, id);

		/*
		 * register the holder (i.e. bound definition and id) within the
		 * registry
		 */
		final BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

		// add all the mappings
		final NodeList moduleEls = element.getElementsByTagNameNS(
				element.getNamespaceURI(), XML_ELEMENT_MODULE);
		for (int i = 0; i < moduleEls.getLength(); i++) {
			final Element el = (Element) moduleEls.item(i);
			createAndRegisterMappings(id, el, parserContext);
		}

		return null;
	}

	/**
	 * Creates the mappings between the outer- and configuration-context using
	 * internal references.
	 * 
	 * @param configurationHolderId
	 *            the id of the configuration
	 * @param element
	 *            the {@link Element} defining the mapping
	 * @param parserContext
	 *            the {@link ParserContext}
	 */
	protected void createAndRegisterMappings(
			final String configurationHolderId, final Element element,
			final ParserContext parserContext) {

		// get a builder for the mapping to be build
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition();

		// specify the type which is returned by this definition
		builder.getRawBeanDefinition().setBeanClass(ConfigurationMapping.class);

		// read some attributes and add those to the definition
		builder.addPropertyValue("configurationModule",
				element.getAttribute(XML_ATTRIBUTE_INNERID));
		builder.addPropertyReference("configuration", configurationHolderId);

		// create the final beanDefinition
		final AbstractBeanDefinition definition = builder.getBeanDefinition();

		// get the defined id or generate one
		final String id = element.getAttribute(XML_ATTRIBUTE_OUTERID);

		// bind the definition and the id
		final BeanDefinitionHolder holder = new BeanDefinitionHolder(
				definition, id);

		/*
		 * register the mapping (i.e. bound definition and id) within the
		 * registry
		 */
		final BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}
}
