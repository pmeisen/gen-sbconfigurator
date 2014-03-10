package net.meisen.general.sbconfigurator.authoring;

import net.meisen.general.sbconfigurator.config.transformer.DefaultLoaderDefinition;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser used to parse {@code loader} instances within the XML-bean
 * configuration.
 * 
 * @author pmeisen
 * 
 */
public class LoaderParser extends AbstractBeanDefinitionParser {
	private final static String XML_ATTRIBUTE_SELECTOR = "selector";
	private final static String XML_ATTRIBUTE_DEFAULTSELECTOR = "defaultSelector";
	private final static String XML_ATTRIBUTE_XSLT = "xslt";
	private final static String XML_ATTRIBUTE_LOADFROMCLASSPATH = "loadFromClassPath";
	private final static String XML_ATTRIBUTE_LOADFROMWORKINGDIR = "loadFromWorkingDir";
	private final static String XML_ATTRIBUTE_BEANOVERRIDINGALLOWED = "beanOverridingAllowed";
	private final static String XML_ATTRIBUTE_VALIDATIONENABLED = "validationEnabled";

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
		setValue(builder, element, XML_ATTRIBUTE_DEFAULTSELECTOR,
				"defaultSelector", null);

		// the outer implementation of AbstractBeanDefinitionParser sets the id
		return builder.getBeanDefinition();
	}

	/**
	 * Helper method to set a property of the {@code builder} from the passed
	 * {@code element}. The property to be set is defined by {@code property}
	 * and the attribute read from the {@code element} is specified by the
	 * {@code attribute}. If the specified attribute is not available or not
	 * defined for the {@code element} or empty, the {@code def} is used.
	 * 
	 * @param builder
	 *            the {@link BeanDefinitionBuilder} to set the {@code property}
	 *            for
	 * @param element
	 *            the {@link Element} to get the {@code attribute} from
	 * @param attribute
	 *            the attribute to be read from the {@code element}
	 * @param property
	 *            the property to be set
	 * @param def
	 *            the default value to be used if the attribute is not specified
	 */
	protected void setValue(final BeanDefinitionBuilder builder,
			final Element element, final String attribute,
			final String property, final Object def) {
		setValue(builder, property, element.getAttribute(attribute), def);
	}

	/**
	 * Helper method to set a property of the {@code builder} with the specified
	 * {@code value}. The property to be set is defined by {@code property}. If
	 * the specified {@code value} is empty, the {@code def} is used.
	 * 
	 * @param builder
	 *            the {@link BeanDefinitionBuilder} to set the {@code property}
	 *            for
	 * @param value
	 *            the value to be set for the property
	 * @param property
	 *            the property to be set
	 * @param def
	 *            the default value to be used if the attribute is not specified
	 */
	protected void setValue(final BeanDefinitionBuilder builder,
			final String property, final Object value, final Object def) {

		if (value instanceof String) {
			final String stringValue = (String) value;
			if (StringUtils.hasText(stringValue)) {
				builder.addPropertyValue(property, value);
			} else {
				builder.addPropertyValue(property, def);
			}
		} else if (value != null) {
			builder.addPropertyValue(property, value);
		} else {
			builder.addPropertyValue(property, def);
		}
	}

}
