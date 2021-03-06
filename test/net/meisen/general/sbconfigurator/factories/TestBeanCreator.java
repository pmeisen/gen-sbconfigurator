package net.meisen.general.sbconfigurator.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.meisen.general.sbconfigurator.factories.mocks.AutowiredInstance;
import net.meisen.general.sbconfigurator.factories.mocks.Setter;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of {@code BeanCreator}.
 * 
 * @author pmeisen
 * 
 */
public class TestBeanCreator {
	private final static String pre = "net/meisen/general/sbconfigurator/factories/";

	/**
	 * Helper method to create a factory.
	 * 
	 * @param xml
	 *            the xml file to load the beans from
	 * @return a loaded factory
	 */
	protected DefaultListableBeanFactory f(final String xml) {

		// create the factory with auto-wiring
		final DefaultListableBeanFactory factory = SpringHelper
				.createBeanFactory(true, true);

		// create the reader and load the Xml into the Factory
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
				factory);
		final Resource resXml = new ClassPathResource(xml);
		reader.loadBeanDefinitions(resXml);

		return factory;
	}

	/**
	 * Tests the creation of singletons.
	 */
	@Test
	public void testSingletons() {
		final DefaultListableBeanFactory f = f(pre + "beanCreator.xml");

		Object o;

		// test date creation
		o = f.getBean("beanDateCreator");
		assertNotNull(o);
		assertTrue(o instanceof Date);
		assertEquals(o, f.getBean("beanDateCreator"));

		// test the string creation with constructor arguments
		o = f.getBean("beanStringCreator");
		assertNotNull(o);
		assertTrue(o instanceof String);
		assertEquals("java.lang.Integer", o);
		assertTrue(o == f.getBean("beanStringCreator"));

		// test usage of references
		o = f.getBean("beanClassByStringCreator");
		assertNotNull(o);
		assertTrue(o instanceof Integer);
		assertEquals(5, o);
		assertTrue(o == f.getBean("beanClassByStringCreator"));

		// test multiple constructor args
		o = f.getBean("beanClassWithProperties");
		assertNotNull(o);
		assertTrue(o instanceof Setter);
		final Setter s = (Setter) o;
		assertNotNull(s.getDateValue());
		assertNotNull(s.getIntNumber());
		assertNotNull(s.getLongNumber());
		assertNotNull(s.getStringValue());
		assertNotNull(s.getAnyObject());
		assertNotNull(s.isSomethingCalled());
		assertEquals(new Integer(1), s.getIntNumber());
		assertEquals(new Long(1000), s.getLongNumber());
		assertEquals("7000", s.getStringValue());
		assertTrue(s.getDateValue() instanceof Date);
		assertTrue(s.getAnyObject() instanceof Object);
		assertTrue(s.isSomethingCalled());

		// test auto-wiring
		o = f.getBean("beanAutowiredInstance");
		assertNotNull(o);
		assertTrue(o instanceof AutowiredInstance);
		assertNotNull(((AutowiredInstance) o).getDate());
	}
}
