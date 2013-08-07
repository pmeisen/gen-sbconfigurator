package net.meisen.general.sbconfigurator.runners.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A property which should be set as system-property.
 * 
 * @author pmeisen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SystemProperty {

	/**
	 * The property to be set, cannot be <code>null</code>.
	 * 
	 * @return property to be set
	 */
	String property();

	/**
	 * The value of the property to be set, cannot be <code>null</code>.
	 * 
	 * @return value of the property
	 */
	String value();
}
