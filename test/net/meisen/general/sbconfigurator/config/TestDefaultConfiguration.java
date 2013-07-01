package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.config.mocks.NotWiredClass;
import net.meisen.general.sbconfigurator.config.mocks.TestPropertyHolder;
import net.meisen.general.sbconfigurator.config.mocks.WiredClass;

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

	/**
	 * Tests the creation of a not-wired bean using the
	 * {@link IConfiguration#createInstance(Class)} implementation.
	 */
	@Test
	public void testAutowiringForNotWiredClass() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		final NotWiredClass notWired = config.createInstance(NotWiredClass.class);
		assertNotNull(notWired);
	}

	/**
	 * Tests the wiring when creating an instance using the
	 * {@link IConfiguration#createInstance(Class)} implementation.
	 */
	@Test
	public void testAutowiringForWiredClass() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		final WiredClass wired = config.createInstance(WiredClass.class);
		assertNotNull(wired);
		assertNotNull(wired.coreSettings);
		assertEquals(configCore, wired.coreSettings);

	}

	/**
	 * Tests the wiring when wiring an instance using the
	 * {@link IConfiguration#wireInstance(Object)} implementation.
	 */
	@Test
	public void testAutowiringForWiredObject() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance (not wired)
		final WiredClass wired = new WiredClass();
		assertNotNull(wired);
		assertNull(wired.coreSettings);

		// wire the instance
		config.wireInstance(wired);
		assertNotNull(wired.coreSettings);
		assertEquals(configCore, wired.coreSettings);

	}
}
