package net.meisen.general.sbconfigurator.runners.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

/**
 * Annotation to define the filename-context of a configuration.
 * 
 * @see ConfigurationCoreSettings#loadCoreSettings(String, Class)
 * 
 * @author pmeisen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ContextFile {

	/**
	 * The name of the filename to load the configuration from.
	 * 
	 * @return the name of the filename to load the configuration from, can be
	 *         <code>null</code>
	 */
	String value();
}
