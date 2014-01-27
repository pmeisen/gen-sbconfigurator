package net.meisen.general.sbconfigurator.factories;

import java.util.Date;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

/**
 * A {@code PropertyEditorRegistrar} used to register the additional
 * {@code PropertyEditor} defined by the configurator.
 * 
 * @author pmeisen
 * 
 */
public class ConfiguratorPropertyEditorRegistrar implements
		PropertyEditorRegistrar {

	@Override
	public void registerCustomEditors(final PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Date.class, new DatePropertyEditor());
	}
}
