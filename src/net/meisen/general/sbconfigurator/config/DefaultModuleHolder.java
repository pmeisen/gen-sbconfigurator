package net.meisen.general.sbconfigurator.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import net.meisen.general.sbconfigurator.api.IModuleHolder;

/**
 * A {@code ModuleHolder} is used to keep modules and the definitions of the
 * modules (i.e. their destroy-methods).
 * 
 * @author pmeisen
 * 
 */
public class DefaultModuleHolder implements IModuleHolder {

	private final DefaultListableBeanFactory factory;
	private final Map<String, Object> factoryModules;

	/**
	 * Constructor to define a holder based on a
	 * {@code DefaultListableBeanFactory}. All the {@code Beans} of the
	 * {@code DefaultListableBeanFactory} are used.
	 * 
	 * @param factory
	 *            the {@code DefaultListableBeanFactory} to retrieve the
	 *            {@code Beans} from
	 */
	public DefaultModuleHolder(final DefaultListableBeanFactory factory) {
		this(factory, factory.getBeansOfType(Object.class));
	}

	/**
	 * Constructor to be used to define a sub-set of modules from a factory.
	 * 
	 * @param factory
	 *            {@code DefaultListableBeanFactory} which contains the
	 *            definitions of the {@code Beans}
	 * @param factoryModules
	 *            the modules retrieved from the factory (this must be a subset
	 *            of the {@code factory.getBeansOfType(Object.class)}
	 * 
	 * @see DefaultListableBeanFactory#getBeansOfType(Class)
	 */
	public DefaultModuleHolder(final DefaultListableBeanFactory factory,
			final Map<String, Object> factoryModules) {
		this.factory = factory;
		this.factoryModules = factory.getBeansOfType(Object.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModule(final String name) {
		return (T) factoryModules.get(name);
	}

	@Override
	public Map<String, Object> getAllModules() {
		return Collections.unmodifiableMap(factoryModules);
	}

	@Override
	public void release() {
		factory.destroySingletons();
	}
}
