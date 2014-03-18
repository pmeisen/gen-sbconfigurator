package net.meisen.general.sbconfigurator.authoring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
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
	private final static String XML_ATTRIBUTE_LOADID = "loadFromId";
	private final static String XML_ELEMENT_MODULE = "module";
	private final static String XML_ELEMENT_INJECT = "inject";
	private final static String XML_ELEMENT_PROPERTIES = "properties";

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
		builder.addPropertyValue("injections",
				createInjectionsMap(element, parserContext));
		builder.addPropertyValue("properties",
				createPropertiesList(element, parserContext));

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
			createAndRegisterModuleMapping(id, el, parserContext);
		}

		return null;
	}

	/**
	 * Creates a <code>Map</code> which contains all the defined injections. The
	 * map is a <code>ManagedMap</code>, so that the references will be
	 * resolved.
	 * 
	 * @param element
	 *            the parent element which contains all the injections
	 * @param parserContext
	 *            the <code>ParserContext</code>
	 * 
	 * @return the <code>Map</code> containing the beans to be injected
	 * 
	 * @see ParserContext
	 * @see ManagedMap
	 */
	protected Map<?, ?> createInjectionsMap(final Element element,
			final ParserContext parserContext) {
		final List<Element> injEls = DomUtils.getChildElementsByTagName(
				element, XML_ELEMENT_INJECT);

		// create the map
		final ManagedMap<Object, Object> map = new ManagedMap<Object, Object>(
				injEls.size());
		map.setSource(parserContext.extractSource(element));
		map.setKeyTypeName(String.class.getName());
		map.setValueTypeName(Object.class.getName());
		map.setMergeEnabled(false);

		// insert the values
		for (final Element injEl : injEls) {
			final String innerId = injEl.getAttribute(XML_ATTRIBUTE_INNERID);
			final String outerId = injEl.getAttribute(XML_ATTRIBUTE_OUTERID);

			// generate the reference
			final RuntimeBeanReference ref = new RuntimeBeanReference(outerId);
			ref.setSource(parserContext.extractSource(injEl));

			// add it
			map.put(innerId, ref);
		}

		return map;
	}

	/**
	 * Creates the list of the properties to be used within the configuration
	 * from the outer world.
	 * 
	 * @param element
	 *            the parent element which contains all the properties
	 * @param parserContext
	 *            the <code>ParserContext</code>
	 * 
	 * @return the <code>List</code> containing the beans with properties
	 */
	protected List<?> createPropertiesList(final Element element,
			final ParserContext parserContext) {
		final List<Element> propEls = DomUtils.getChildElementsByTagName(
				element, XML_ELEMENT_PROPERTIES);

		// create the list
		final ManagedList<Object> list = new ManagedList<Object>(propEls.size());
		list.setSource(parserContext.extractSource(element));
		list.setElementTypeName(PropertiesLoaderSupport.class.getName());
		list.setMergeEnabled(false);

		for (final Element propEl : propEls) {
			final String loadId = propEl.getAttribute(XML_ATTRIBUTE_LOADID);

			final RuntimeBeanReference ref = new RuntimeBeanReference(loadId);
			ref.setSource(parserContext.extractSource(propEl));

			list.add(ref);
		}

		return list;
	}

	/**
	 * Creates the mapping for a module between the outer- and
	 * configuration-context using internal references.
	 * 
	 * @param configurationHolderId
	 *            the id of the configuration
	 * @param element
	 *            the {@link Element} defining the mapping
	 * @param parserContext
	 *            the {@link ParserContext}
	 */
	protected void createAndRegisterModuleMapping(
			final String configurationHolderId, final Element element,
			final ParserContext parserContext) {

		// get the defined attributes
		final String innerId = element.getAttribute(XML_ATTRIBUTE_INNERID);
		final String outerId = element.getAttribute(XML_ATTRIBUTE_OUTERID);

		// get a builder for the mapping to be build
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition();

		// specify the type which is returned by this definition
		builder.getRawBeanDefinition().setBeanClass(
				ConfigurationModuleMapping.class);

		// read some attributes and add those to the definition
		builder.addPropertyValue("configurationModule", innerId);
		builder.addPropertyReference("configuration", configurationHolderId);

		// create the final beanDefinition
		final AbstractBeanDefinition definition = builder.getBeanDefinition();

		// bind the definition and the id
		final BeanDefinitionHolder holder = new BeanDefinitionHolder(
				definition, outerId);

		/*
		 * register the mapping (i.e. bound definition and id) within the
		 * registry
		 */
		final BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}
}
