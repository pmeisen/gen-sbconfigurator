package net.meisen.general.sbconfigurator.runners.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;

/**
 * The annotation is used to define the class-context of the configuration, when
 * used with a {@link JUnitConfigurationRunner}. The context of the
 * configuration is determined by a class and the filename, the first one is
 * specified with this annotation.
 * 
 * @see ConfigurationCoreSettings#loadCoreSettings(String, Class)
 * 
 * @author pmeisen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ContextClass {

	/**
	 * The class which defines the context.
	 * 
	 * @return the class-context of the configuration, can be <code>null</code>,
	 *         if so the <code>ConfigurationCoreSettings</code> will be used as
	 *         context
	 */
	Class<?> value();
}
