package net.meisen.general.sbconfigurator.authoring;

import net.meisen.general.sbconfigurator.config.transformer.DefaultLoaderDefinition;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class LoaderParser extends AbstractBeanDefinitionParser {

	public final static String XML_ATTRIBUTE_SELECTOR = "selector";
	public final static String XML_ATTRIBUTE_XSLT = "xslt";
	public final static String XML_ATTRIBUTE_LOADFROMCLASSPATH = "loadFromClassPath";
	public final static String XML_ATTRIBUTE_LOADFROMWORKINGDIR = "loadFromWorkingDir";
	public final static String XML_ATTRIBUTE_BEANOVERRIDINGALLOWED = "beanOverridingAllowed";
	public final static String XML_ATTRIBUTE_VALIDATIONENABLED = "validationEnabled";

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element,
			final ParserContext parserContext) {

		// get a builder for the definition to be build
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition();

		// specify the type which is returned by this definition
		builder.getRawBeanDefinition().setBeanClass(
				DefaultLoaderDefinition.class);

		// set the attributes required
		builder.addPropertyValue("selector",
				element.getAttribute(XML_ATTRIBUTE_SELECTOR));

		// set the attributes not required
		setValue(builder, element, XML_ATTRIBUTE_XSLT, "xslt", "");
		setValue(builder, element, XML_ATTRIBUTE_LOADFROMCLASSPATH,
				"loadFromClassPath", true);
		setValue(builder, element, XML_ATTRIBUTE_LOADFROMWORKINGDIR,
				"loadFromWorkingDir", true);
		setValue(builder, element, XML_ATTRIBUTE_BEANOVERRIDINGALLOWED,
				"beanOverridingAllowed", false);
		setValue(builder, element, XML_ATTRIBUTE_VALIDATIONENABLED,
				"validationEnabled", true);

		// the outer implementation of AbstractBeanDefinitionParser sets the id
		return builder.getBeanDefinition();
	}

	protected void setValue(final BeanDefinitionBuilder builder,
			final Element element, final String attribute,
			final String property, final Object def) {
		setValue(builder, property, element.getAttribute(attribute), "");
	}

	protected void setValue(final BeanDefinitionBuilder builder,
			final String property, final Object value, final Object def) {

		if (value instanceof String && StringUtils.hasText(value.toString())) {
			builder.addPropertyValue(property, value);
		} else if (value != null) {
			builder.addPropertyValue(property, value);
		} else {
			builder.addPropertyValue(property, def);
		}
	}

}
