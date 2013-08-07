package net.meisen.general.sbconfigurator.runners.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An array of properties which should be set as system-properties prior to
 * create the <code>Configuration</code>.
 * 
 * @author pmeisen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SystemProperties {
	/**
	 * The array of defined <code>SystemProperty</code> to be set.
	 * 
	 * @return array of defined <code>SystemProperty</code> to be set
	 */
	SystemProperty[] value();
}
