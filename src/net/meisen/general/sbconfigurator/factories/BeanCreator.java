package net.meisen.general.sbconfigurator.factories;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.validation.DataBinder;

/**
 * Factory to create beans.
 * 
 * @author pmeisen
 * 
 */
public class BeanCreator implements FactoryBean<Object>, InitializingBean,
		BeanClassLoaderAware {

	// properties to be set via the configuration
	private String beanClass = null;
	private boolean singleton = true;
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	private Object[] constArgs = new Object[0];
	private Map<String, Object> properties;

	// objects to be created
	private Class<?> clazz = null;
	private Object created = null;

	/**
	 * Gets the classloader used to create an instance of the bean.
	 * 
	 * @return the classloader used to create an instance of the bean.
	 */
	public ClassLoader getBeanClassLoader() {
		return beanClassLoader;
	}

	@Override
	public void setBeanClassLoader(final ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/**
	 * Gets the arguments used to construct the instance.
	 * 
	 * @return the arguments used to construct the instance
	 */
	public Object[] getConstArgs() {
		return constArgs;
	}

	/**
	 * Sets the arguments used to construct the instance.
	 * 
	 * @param constArgs
	 *            the arguments used to construct the instance
	 */
	public void setConstArgs(final Object[] constArgs) {
		this.constArgs = constArgs;
	}

	@Override
	public Object getObject() throws Exception {
		if (clazz == null) {
			throw new FactoryBeanNotInitializedException(
					"The factory is not fully initialized yet, i.e. no class is defined by '"
							+ beanClass + "'.");
		}

		if (singleton) {
			if (created == null) {
				created = createInstance();
			}

			return created;
		} else {
			return createInstance();
		}
	}

	/**
	 * Creates an instance of the bean.
	 * 
	 * @return the created instance
	 * 
	 * @throws Exception
	 *             if the instance cannot be created
	 */
	protected Object createInstance() throws Exception {
		final Constructor<?> constructor = findMatchingConstructor();
		final Object object = constructor.newInstance(constArgs);

		// check if we have to apply properties
		if (properties != null && properties.size() > 0) {
			final DataBinder binder = new DataBinder(object);
			final MutablePropertyValues values = new MutablePropertyValues(
					properties);
			binder.bind(values);
		}

		return object;
	}

	@Override
	public Class<?> getObjectType() {
		return clazz;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * Specifies if a bean should be created on every call.
	 * 
	 * @param singleton
	 *            if set to {@code false} a bean will be created every time
	 *            {@link #getObject()} is called, otherwise there will be only
	 *            one object created
	 */
	public void setSingleton(final boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.clazz = ClassUtils.forName(beanClass, this.beanClassLoader);
	}

	/**
	 * Gets the class of the bean which is created.
	 * 
	 * @return the class of the bean to be created
	 */
	public String getBeanClass() {
		return beanClass;
	}

	/**
	 * Sets the class of the bean to be created.
	 * 
	 * @param beanClass
	 *            the class of the bean to be created
	 */
	public void setBeanClass(final String beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Find a matching constructor with the specified arguments.
	 * 
	 * @return a matching constructor, or {@code null} if none
	 */
	protected Constructor<?> findMatchingConstructor() {
		final int argCount = constArgs.length;

		// get all the available constructors
		Constructor<?>[] candidates = clazz.getConstructors();

		// check the constructors
		Constructor<?> matchingConstructor = null;
		int minTypeDiffWeight = Integer.MAX_VALUE;
		for (final Constructor<?> candidate : candidates) {
			final Class<?>[] paramTypes = candidate.getParameterTypes();

			if (paramTypes.length == argCount) {
				int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(
						paramTypes, constArgs);
				if (typeDiffWeight < minTypeDiffWeight) {
					minTypeDiffWeight = typeDiffWeight;
					matchingConstructor = candidate;
				}
			}
		}

		return matchingConstructor;
	}

	/**
	 * Gets the defined properties.
	 * 
	 * @return the defined properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Defines the properties to be set using a {@code DataBinder} on the
	 * created object.
	 * 
	 * @param properties
	 *            the properties to be set
	 * 
	 * @see DataBinder
	 */
	public void setProperties(final Map<String, Object> properties) {
		this.properties = properties;
	}
}
