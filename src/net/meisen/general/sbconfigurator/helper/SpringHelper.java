package net.meisen.general.sbconfigurator.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

			// now create the post processor and set the factory and the resolver
			final AutowiredAnnotationBeanPostProcessor autowiredPostProcessor = new AutowiredAnnotationBeanPostProcessor();
			autowiredPostProcessor.setBeanFactory(factory);
			factory.addBeanPostProcessor(autowiredPostProcessor);
			factory.setAutowireCandidateResolver(resolver);
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
}
