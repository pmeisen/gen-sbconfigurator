package net.meisen.general.sbconfigurator.authoring;

import static org.junit.Assert.*;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.authoring.mock.SimpleConfigurationWiring;
import net.meisen.general.sbconfigurator.config.DefaultConfiguration;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringXMLAuthoring {

	@Test
	public void testSimpleAuthering() {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"net/meisen/general/sbconfigurator/test/authoring/sbconfigurator-simple-authoring-test.xml");

		// force Spring to give us the holder
		final ConfigurationHolder ch = context
				.getBean(ConfigurationHolder.class);

		// check that a configuration is available
		assertNotNull(ch.getConfiguration());

		// get the configuration via the defined identifier and check that both
		// configurations are equal
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
		
		// cleanUp
		context.close();
	}

}