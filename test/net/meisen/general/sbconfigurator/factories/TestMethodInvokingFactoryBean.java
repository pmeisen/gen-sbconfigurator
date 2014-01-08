package net.meisen.general.sbconfigurator.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.meisen.general.sbconfigurator.factories.mocks.InvokerMock;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of a {@code MethodInvokingFactoryBean}.
 * 
 * @author pmeisen
 * 
 */
public class TestMethodInvokingFactoryBean {
	private static DefaultListableBeanFactory factory;

	/**
	 * Initialize the tests beans via Spring.
	 */
	@BeforeClass
	public static void init() {

		// create the factory with auto-wiring
		factory = SpringHelper.createBeanFactory(true, true);

		// create the reader and load the Xml into the Factory
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
				factory);
		final Resource testXml = new ClassPathResource(
				"/net/meisen/general/sbconfigurator/factories/methodInvokingFactoryBean.xml");
		reader.loadBeanDefinitions(testXml);
	}

	/**
	 * Tests the default functionality of a {@code MethodInvokingFactoryBean}.
	 */
	@Test
	public void testSimpleMethodInvokation() {
		final Object oSubject = factory.getBean("simpleMethodInvoker");
		assertTrue(oSubject == null ? "null" : oSubject.getClass().getName(),
				oSubject instanceof InvokerMock);

		// get the list
		final InvokerMock subject = (InvokerMock) oSubject;
		assertEquals("firstValue", subject.getValue());
	}

	/**
	 * Tests the advanced functionality of a {@code MethodInvokingFactoryBean},
	 * i.e. calling a post-method.
	 */
	@Test
	public void testMethodInvokationWithPost() {
		final Object subject = factory.getBean("postMethodInvoker");
		assertTrue(subject == null ? "null" : subject.getClass().getName(),
				subject instanceof String);
		assertEquals("initValue", subject);

		// get the list
		final InvokerMock invoked = (InvokerMock) factory
				.getBean("postMethodInvokerObject");
		assertEquals("postValue", invoked.getValue());
	}

	/**
	 * CleanUp
	 */
	@AfterClass
	public static void tearDown() {
		factory.destroySingletons();
	}
}
