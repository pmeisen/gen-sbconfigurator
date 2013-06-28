package net.meisen.general.sbconfigurator.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

/**
 * Tests the implementations provided by the {@link SpringHelper}.
 * 
 * @author pmeisen
 * 
 */
public class TestSpringHelper {

	private class RootRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public RootRuntimeException(final String msg) {
			super(msg);
		}
	}

	private class RootException extends Exception {
		private static final long serialVersionUID = 1L;

		public RootException(final String msg) {
			super(msg);
		}
	}

	private class SampleException extends Exception {
		private static final long serialVersionUID = 1L;

		public SampleException(final String msg) {
			super(msg);
		}

		public SampleException(final String msg, final Throwable t) {
			super(msg, t);
		}
	}

	/**
	 * Tests the implementation of the method
	 * {@link SpringHelper#getNoneSpringBeanException(Throwable, Class)}
	 */
	@Test
	public void testGetNoneSpringBeanException() {

		// let's create some exception
		final RootException rootException = new RootException("I am the cause");
		final RootRuntimeException rootRuntimeException = new RootRuntimeException(
				"I am the cause, runtime");
		final SampleException sampleException = new SampleException(
				"I am the sample");
		final SampleException causeException = new SampleException(
				"I am the sample with a cause", rootException);

		// check the identity
		assertEquals(rootException,
				SpringHelper.getNoneSpringBeanException(rootException, Throwable.class));
		assertEquals(rootRuntimeException, SpringHelper.getNoneSpringBeanException(
				rootRuntimeException, Throwable.class));
		assertEquals(sampleException, SpringHelper.getNoneSpringBeanException(
				sampleException, Throwable.class));

		// check the expectedClazz
		assertEquals(rootException, SpringHelper.getNoneSpringBeanException(
				rootException, RootException.class));
		assertEquals(rootRuntimeException, SpringHelper.getNoneSpringBeanException(
				rootRuntimeException, RootRuntimeException.class));
		assertEquals(sampleException, SpringHelper.getNoneSpringBeanException(
				sampleException, SampleException.class));

		// check the cast to a runtime method
		RuntimeException wrapped;
		wrapped = SpringHelper.getNoneSpringBeanException(rootException,
				RuntimeException.class);
		assertTrue(wrapped instanceof RuntimeException);
		assertNull(wrapped.getCause());
		assertEquals("I am the cause ('" + rootException.getClass().getName()
				+ "')", wrapped.getMessage());

		wrapped = SpringHelper.getNoneSpringBeanException(causeException,
				RuntimeException.class);
		assertTrue(wrapped instanceof RuntimeException);
		assertNotNull(wrapped.getCause());
		assertEquals("I am the sample with a cause ('"
				+ causeException.getClass().getName() + "')", wrapped.getMessage());

		// check the removing of the SpringBeans
		Throwable result;
		for (int i = 1; i < 100; i++) {
			result = SpringHelper.getNoneSpringBeanException(
					create(1, rootException), Exception.class);
			assertEquals(rootException, result);
		}

		for (int i = 1; i < 100; i++) {
			result = SpringHelper.getNoneSpringBeanException(
					create(1, rootRuntimeException), RuntimeException.class);
			assertEquals(rootRuntimeException, result);
		}

		for (int i = 1; i < 100; i++) {
			result = SpringHelper.getNoneSpringBeanException(
					create(1, causeException), SampleException.class);
			assertEquals(causeException, result);
		}
	}

	private BeanCreationException create(final int level, final Throwable cause) {

		Throwable last = cause;
		for (int i = 1; i <= level; i++) {
			final BeanCreationException bean = new BeanCreationException("" + i,
					cause);
			last = bean;
		}

		return (BeanCreationException) last;
	}
}
