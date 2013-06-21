package net.meisen.general.sbconfigurator.config.placeholder;

import java.util.Properties;

import net.meisen.general.sbconfigurator.api.placeholder.IPropertyReplacer;

import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * This implementation is based on the Spring
 * <code>PropertyPlaceholderHelper</code> and the default
 * placeholder-definition. Generally it is used to replace a placeholder within
 * a string.
 * 
 * @author pmeisen
 * 
 * @see PlaceholderConfigurerSupport#DEFAULT_PLACEHOLDER_PREFIX
 * @see PlaceholderConfigurerSupport#DEFAULT_PLACEHOLDER_SUFFIX
 * @see PlaceholderConfigurerSupport#DEFAULT_VALUE_SEPARATOR
 * 
 * @see PropertyPlaceholderHelper
 * 
 */
public class SpringPropertyReplacer implements IPropertyReplacer {

	/**
	 * The Spring implementation which is wrapped by this implementation
	 */
	private PropertyPlaceholderHelper phHelper;

	/**
	 * Default constructor
	 */
	public SpringPropertyReplacer() {
		phHelper = new PropertyPlaceholderHelper(
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
				PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, true);
	}

	@Override
	public String replacePlaceholders(final String input,
			final Properties properties) {
		return phHelper.replacePlaceholders(input, properties);
	}
}
