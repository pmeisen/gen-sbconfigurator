package net.meisen.general.sbconfigurator.authoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.authoring.mock.RecursiveConfigurationWiringInner;
import net.meisen.general.sbconfigurator.authoring.mock.RecursiveConfigurationWiringOuter;
import net.meisen.general.sbconfigurator.authoring.mock.SimpleConfigurationWiring;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePojo;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests the implementation of the authering of the configuration.
 * 
 * @author pmeisen
 * 
 */
public class TestSpringXMLAuthoring {

	/**
	 * Tests the simple authoring with the
	 * {@code sbconfigurator-simple-authoring-test.xml}
	 */
	@Test
	public void testSimpleAuthoring() {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"net/meisen/general/sbconfigurator/authoring/sbconfigurator-simple-authoring-test.xml");

		// force Spring to give us the holder
		final ConfigurationHolder ch = context
				.getBean(ConfigurationHolder.class);

		// check that a configuration is available
		assertNotNull(ch.getConfiguration());

		/*
		 * get the configuration via the defined identifier and check that both
		 * configurations are equal
		 */
		final IConfiguration config = (IConfiguration) context
				.getBean("testConfig");
		assertNotNull(config);
		assertEquals(ch.getConfiguration(), config);

		// get the test instance
		final SimpleConfigurationWiring subject = (SimpleConfigurationWiring) context
				.getBean("simpleTestInstance");
		assertNotNull(subject);
		assertNotNull(subject.getConfig());
		assertEquals(config, subject.getConfig());
		assertNotNull(subject.getSettings());

		// get an outer id
		final SamplePlugIn samplePlugIn = (SamplePlugIn) context.getBean("b");
		assertNotNull(samplePlugIn);
		assertEquals(samplePlugIn, subject.getSettings());

		// get another one that is bound
		final Object c = context.getBean("c");
		assertNotNull(c);
		assertTrue("Invalid type was: " + c.getClass().getName(),
				c instanceof SamplePojo);

		// check the injections
		assertNotNull(config.getModule("date"));
		assertNotNull(config.getModule("instance"));
		assertTrue(config.getModule("date") instanceof Date);
		assertTrue(config.getModule("instance") instanceof SimpleConfigurationWiring);

		// cleanUp
		context.close();
	}

	/**
	 * Tests some recursive authering.
	 */
	@Test
	public void testRecursiveAuthoring() {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"net/meisen/general/sbconfigurator/authoring/sbconfigurator-recursive-authoring-test.xml");

		// get the configuration
		final IConfiguration config = (IConfiguration) context
				.getBean("config");
		assertNotNull(config);

		// check the outer element
		final RecursiveConfigurationWiringOuter outer = (RecursiveConfigurationWiringOuter) context
				.getBean("outer");
		assertNotNull(outer);
		assertEquals(outer.getConfig(), config);

		// check the outerDate
		final Date outerDate = (Date) context.getBean("outerDate");
		assertNotNull(outerDate);
		assertEquals(outerDate, outer.getOuterDate());

		// check the injected
		final RecursiveConfigurationWiringOuter injected = (RecursiveConfigurationWiringOuter) config
				.getModule("injected");
		assertNotNull(injected);
		assertEquals(outer, injected);

		// check the inner element
		final RecursiveConfigurationWiringInner inner = config
				.getModule("inner");
		assertNotNull(inner);

		// check the innerDate
		final Date innerDate = config.getModule("innerDate");
		assertNotNull(innerDate);

		// check the pushedDate
		final Date pushedDate = (Date) outer.getPushedDate();
		assertNotNull(pushedDate);
		assertEquals(pushedDate, innerDate);

		// check the pushed
		final RecursiveConfigurationWiringInner pushed = (RecursiveConfigurationWiringInner) context
				.getBean("pushed");
		assertNotNull(pushed);
		assertEquals(pushed, inner);

		// do some other checks now
		assertEquals(pushed.getInjected(), injected);
		assertEquals(pushed.getInjectedDate(), outerDate);
		assertEquals(injected.getOuterDate(), outerDate);

		// cleanUp
		context.close();
	}
}
