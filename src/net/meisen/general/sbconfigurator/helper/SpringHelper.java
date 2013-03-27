package net.meisen.general.sbconfigurator.helper;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ByteArrayResource;

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
	 * @return a <code>DefaultListableBeanFactory</code> with some default
	 *         settings
	 * 
	 * @see AutowiredAnnotationBeanPostProcessor
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public static DefaultListableBeanFactory createBeanFactory() {

		// create the factory
		final DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.setAllowBeanDefinitionOverriding(false);

		// enable auto-wiring
		final AutowiredAnnotationBeanPostProcessor autowiredPostProcessor = new AutowiredAnnotationBeanPostProcessor();
		autowiredPostProcessor.setBeanFactory(factory);
		factory.addBeanPostProcessor(autowiredPostProcessor);

		return factory;
	}

	/**
	 * Gets the <code>InputStream</code> of the specified <code>res</code>. Wraps
	 * the exceptions away and makes handling easier.
	 * 
	 * @param res
	 *          the <code>ByteArrayResource</code> to get the
	 *          <code>InputStream</code> for
	 * 
	 * @return the <code>InputStream</code> for the <code>ByteArrayResource</code>
	 *         or <code>null</code> if an exception was thrown while creating the
	 *         <code>InputStream</code>
	 * 
	 * @see ByteArrayResource#getInputStream()
	 */
	public static InputStream getInputStream(final ByteArrayResource res) {

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
