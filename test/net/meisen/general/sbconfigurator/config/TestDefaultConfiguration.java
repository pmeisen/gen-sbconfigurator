package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.config.mocks.TestPropertyHolder;

/**
 * Tests the implementation of the <code>DefaultConfiguration</code>.
 * 
 * @author pmeisen
 * 
 * @see DefaultConfiguration
 */
public class TestDefaultConfiguration {

	/**
	 * Tests the default implementation
	 */
	@Test
	public void testDefaultConfiguration() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// check the default stuff
		assertTrue(config instanceof DefaultConfiguration);

		// check the concrete implementation
		final DefaultConfiguration defaultConfig = (DefaultConfiguration) config;

		// get the defined properties
		final Properties properties = defaultConfig.getProperties();
		assertNotNull(properties);

		// all the system properties should be available
		for (final Object prop : System.getProperties().keySet()) {
			assertEquals("Checking System property '" + prop.toString() + "'",
					properties.getProperty(prop.toString()),
					System.getProperty(prop.toString()));
		}
	}

	/**
	 * Tests an extended configuration
	 */
	@Test
	public void testExtendedConfiguration() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings("propertyExtended-core.xml", getClass());
		final IConfiguration config = configCore.getConfiguration();

		// check the default stuff
		assertTrue(config instanceof DefaultConfiguration);

		// check the concrete implementation
		final DefaultConfiguration defaultConfig = (DefaultConfiguration) config;

		// get the defined properties
		final Properties properties = defaultConfig.getProperties();
		assertNotNull(properties);

		// all the system properties should be available
		for (final Object prop : System.getProperties().keySet()) {
			assertEquals("Checking System property '" + prop.toString() + "'",
					properties.getProperty(prop.toString()),
					System.getProperty(prop.toString()));
		}

		// check if the overriding worked
		assertEquals(properties.getProperty("class"),
				TestPropertyHolder.class.getName());
	}
}
