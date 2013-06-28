package net.meisen.general.sbconfigurator.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

/**
 * Some helper classes which help to work with Spring.
 * 
 * @author pmeisen
 * 
 */
public class SpringHelper {
	private final static Logger LOG = LoggerFactory.getLogger(SpringHelper.class);

	/**
	 * Creates a <code>DefaultListableBeanFactory</code> with some default
	 * settings, i.e.
	 * <ul>
	 * <li>support of auto-wiring annotation</li>
	 * <li>disabled bean-overriding</li>
	 * </ul>
	 * 
	 * @param enableAutoWiring
	 *          <code>true</code> if auto-wiring for the factory should be
	 *          enabled, otherwise <code>false</code>
	 * @param allowBeanOverriding
	 *          <code>true</code> if a bean can override another bean with the
	 *          same id, otherwise <code>false</code>
	 * 
	 * @return a <code>DefaultListableBeanFactory</code> with some default
	 *         settings
	 * 
	 * @see AutowiredAnnotationBeanPostProcessor
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public static DefaultListableBeanFactory createBeanFactory(
			final boolean enableAutoWiring, final boolean allowBeanOverriding) {

		// create the factory
		final DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.setAllowBeanDefinitionOverriding(allowBeanOverriding);

		// enable auto-wiring
		if (enableAutoWiring) {
			// get the resolver used for autowiring, we want the qualifier to be used
			// when resolving
			final AutowireCandidateResolver resolver = new QualifierAnnotationAutowireCandidateResolver();
			factory.setAutowireCandidateResolver(resolver);

			// now create the post processor and set the factory and the resolver
			final AutowiredAnnotationBeanPostProcessor autowiredPostProcessor = new AutowiredAnnotationBeanPostProcessor();
			autowiredPostProcessor.setBeanFactory(factory);
			factory.addBeanPostProcessor(autowiredPostProcessor);
		}

		return factory;
	}

	/**
	 * Gets all the <code>BeanDefinition</code> instances of the passed
	 * <code>beanFactory</code>.
	 * 
	 * @param beanFactory
	 *          the <code>DefaultListableBeanFactory</code> to get all the
	 *          <code>BeanDefinitions</code> from
	 * @param excludes
	 *          a array of <code>Classes</code> which should be excluded, i.e. the
	 *          classes are checked to be a super-class or super-interface as well
	 * 
	 * @return all the <code>BeanDefinition</code> of the <code>beanFactory</code>
	 *         which do not define excluded classes.
	 */
	public static Map<String, BeanDefinition> getBeanDefinitions(
			final DefaultListableBeanFactory beanFactory, final Class<?>... excludes) {
		final Map<String, BeanDefinition> defs = new HashMap<String, BeanDefinition>();

		// if there is a factory add all the definitions available
		if (beanFactory != null) {

			// add all the defs defined
			for (final String defName : beanFactory.getBeanDefinitionNames()) {
				final BeanDefinition def = beanFactory.getBeanDefinition(defName);

				// get the class of the bean
				Class<?> beanClass;
				try {
					beanClass = ClassUtils.forName(def.getBeanClassName(),
							ClassUtils.getDefaultClassLoader());
				} catch (final ClassNotFoundException e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(
								"Unable to determine exclusion of bean of type '"
										+ def.getBeanClassName()
										+ "', because the resolving led to an error.", e);
					}

					beanClass = null;
				}

				// check if we have to skip it
				boolean skip = false;
				if (excludes != null && beanClass != null) {
					for (final Class<?> exclude : excludes) {
						if (exclude.isAssignableFrom(beanClass)) {
							skip = true;
							break;
						}
					}
				}

				// add it if it isn't skipped
				if (!skip) {
					defs.put(defName, def);
				}
			}
		}

		return defs;
	}

	/**
	 * Gets the <code>InputStream</code> of the specified <code>res</code>. Wraps
	 * the exceptions away and makes handling easier.
	 * 
	 * @param res
	 *          the <code>Resource</code> to get the <code>InputStream</code> for
	 * 
	 * @return the <code>InputStream</code> for the <code>ByteArrayResource</code>
	 *         or <code>null</code> if an exception was thrown while creating the
	 *         <code>InputStream</code>
	 * 
	 * @see ByteArrayResource#getInputStream()
	 */
	public static InputStream getInputStream(final Resource res) {

		try {
			return res.getInputStream();
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Failed to create InputStream for ByteArrayResource", e);
			}

			return null;
		}
	}

	/**
	 * 
	 * @param exception
	 *          the <code>Exception</code> to find the first
	 *          none-SpringBeanException in the stack
	 * @param exceptionClazz
	 *          the type of the expected exception to be found
	 * 
	 * @throws IllegalArgumentException
	 *           if the specified <code>exceptionClazz</code> is <code>null</code>
	 *           or if the found none-SpringBeanException is not of the specified
	 *           type (i.e. cannot be assigned to the <code>exceptionClazz</code>)
	 * 
	 * @return the first none-SpringBeanException found within the stack; will
	 *         return <code>null</code> if the stack consists only of
	 *         SpringBeanException or if the passed <code>exception</code> is
	 *         <code>null</code>
	 */
	public static <T extends Throwable> T getNoneSpringBeanException(
			final Throwable exception, final Class<T> exceptionClazz) {

		if (exceptionClazz == null
				|| !Throwable.class.isAssignableFrom(exceptionClazz)) {
			throw new IllegalArgumentException(
					"The exceptionClazz cannot be null and must be an instance of Throwable.");
		} else if (exception == null) {
			return null;
		} else if (exception instanceof BeanCreationException) {
			return getNoneSpringBeanException(exception.getCause(), exceptionClazz);
		} else if (exceptionClazz.isAssignableFrom(exception.getClass())) {
			@SuppressWarnings("unchecked")
			final T tException = (T) exception;
			return tException;
		} else if (exceptionClazz.isAssignableFrom(RuntimeException.class)) {
			@SuppressWarnings("unchecked")
			final T wrapperException = (T) new RuntimeException(
					exception.getMessage() + " ('" + exception.getClass().getName()
							+ "')", exception.getCause());
			return wrapperException;
		} else {
			throw new IllegalArgumentException("The exception '"
					+ exception.getMessage() + " (" + exception.getClass()
					+ ") is not assignable to the expected exceptionClazz '"
					+ exceptionClazz.getName() + "'");
		}
	}
}
