package net.meisen.general.sbconfigurator.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of {@code BeanReference}.
 * 
 * @author pmeisen
 * 
 */
public class TestBeanReference {
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
	 * Tests some sample references.
	 */
	@Test
	public void testReferences() {
		final DefaultListableBeanFactory f = f(pre + "beanReference.xml");

		Object o;

		// check the reference creation
		o = f.getBean("beanReference");
		assertNotNull(o);
		assertTrue(o instanceof Date);
		assertEquals(new Date().getTime(), ((Date) o).getTime(), 1000);

		// check the definition of a synonym
		o = f.getBean("beanSynonymReference");
		assertNotNull(o);
		assertTrue(o instanceof Integer);
		assertEquals(new Integer(5000), o);

		// check the definition based on a simple value
		o = f.getBean("beanByValueReference");
		assertNotNull(o);
		assertTrue(o instanceof String);
		assertEquals("Just a Simple Value", o);

		// check null bean
		o = f.getBean("nullBeanWithoutType");
		assertNull(o);
		final Map<String, Object> map = f.getBeansOfType(Object.class);
		assertTrue(map.containsKey("nullBeanWithoutType"));

		o = f.getBean("nullBeanWithType");
		assertNull(o);
		o = f.getBean(Long.class);
		assertNull(o);
	}
}
