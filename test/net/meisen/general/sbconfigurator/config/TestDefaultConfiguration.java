package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.api.IModuleHolder;
import net.meisen.general.sbconfigurator.config.mocks.DelayedBean;
import net.meisen.general.sbconfigurator.config.mocks.NotWiredClass;
import net.meisen.general.sbconfigurator.config.mocks.SatisfiableWiredClass;
import net.meisen.general.sbconfigurator.config.mocks.SetterClass;
import net.meisen.general.sbconfigurator.config.mocks.TestPropertyHolder;
import net.meisen.general.sbconfigurator.config.mocks.UnsatisfiableWiredClass;
import net.meisen.general.sbconfigurator.config.mocks.WiredClass;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

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
					System.getProperty(prop.toString()),
					properties.getProperty(prop.toString()));
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

		final NotWiredClass notWired = config
				.createInstance(NotWiredClass.class);
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
	 * Tests the creation of an instance of a class which can be satisfied, even
	 * if the attributes cannot be wired (i.e. not the attributes are not
	 * required).
	 */
	@Test
	public void testAutowiringForSatisfiableWiredClass() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance
		final SatisfiableWiredClass wired = config
				.createInstance(SatisfiableWiredClass.class);
		assertNotNull(wired);
		assertNull(wired.coreSettings);
	}

	/**
	 * Tests the creation of an instance of a class which cannot be satisfied
	 * concerning it's required attributes.
	 */
	@Test(expected = BeanCreationException.class)
	public void testAutowiringForUnsatisfiableWiredClass() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance
		config.createInstance(UnsatisfiableWiredClass.class);
	}

	/**
	 * Tests the wiring of an object, which has setter methods (which should be
	 * ignored, the object should still be wired). With dependency checking set
	 * to <code>true</code>, Spring would throw an exception here.
	 * 
	 * @see DefaultListableBeanFactory#autowire(Class, int, boolean)
	 */
	@Test
	public void testAutowiringForSetterClass() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance
		final SetterClass wired = config.createInstance(SetterClass.class);
		assertNotNull(wired);
		assertNotNull(wired.coreSettings);
		assertNull(wired.something);
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

	/**
	 * Tests the wiring of an object which has satisfiable (i.e. not required)
	 * attributes.
	 */
	@Test
	public void testAutowiringForSatisfiableWiredObject() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance (not wired)
		final SatisfiableWiredClass wired = new SatisfiableWiredClass();
		assertNotNull(wired);
		assertNull(wired.coreSettings);

		// wire the instance
		config.wireInstance(wired);
		assertNull(wired.coreSettings);
	}

	/**
	 * Tests the creation of a wired object, with an unresolvable dependency.
	 */
	@Test(expected = BeanCreationException.class)
	public void testAutowiringForUnsatisfiableWiredObject() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance (not wired)
		final UnsatisfiableWiredClass wired = new UnsatisfiableWiredClass();
		assertNotNull(wired);
		assertNull(wired.coreSettings);

		// wire the instance
		config.wireInstance(wired);
	}

	/**
	 * Tests the wiring of an object, which has setter methods (which should be
	 * ignored, the object should still be wired). With dependency checking set
	 * to <code>true</code>, Spring would throw an exception here.
	 * 
	 * @see DefaultListableBeanFactory#autowireBeanProperties(Object, int,
	 *      boolean)
	 */
	@Test
	public void testAutowiringForSetterObject() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings();
		final IConfiguration config = configCore.getConfiguration();

		// create the instance (not wired)
		final SetterClass wired = new SetterClass();
		assertNotNull(wired);
		assertNull(wired.coreSettings);
		assertNull(wired.something);

		// wire the instance
		config.wireInstance(wired);
		assertNotNull(wired.coreSettings);
		assertNull(wired.something);
	}

	/**
	 * Tests the delayed loading and the release
	 */
	@Test
	public void testReleaseAndDelayedLoading() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings("sbconfigurator-addBeansLoader.xml",
						getClass());
		final IConfiguration config = configCore.getConfiguration();
		assertNotNull(config);
		assertEquals(3, config.getAllModules().size());

		final IModuleHolder moduleHolder = config.loadDelayed("addBeans",
				getClass().getResourceAsStream("delayedBeans-test.xml"));
		assertNotNull(moduleHolder);
		assertEquals(1, moduleHolder.getAllModules().size());

		// get a specific module and check auto-wiring
		final DelayedBean db = moduleHolder.getModule("testDelayedBean");
		assertNotNull(db);
		assertNotNull(db.sp);

		// release the modules
		moduleHolder.release();

		// load again
		final IModuleHolder moduleHolderAfterRelease = config.loadDelayed(
				"addBeans",
				getClass().getResourceAsStream("delayedBeans-test.xml"));
		assertNotNull(moduleHolderAfterRelease);
		assertEquals(1, moduleHolderAfterRelease.getAllModules().size());

		// get a specific module and check auto-wiring
		final DelayedBean dbAfterRelease = moduleHolder
				.getModule("testDelayedBean");
		assertNotNull(dbAfterRelease);
		assertNotNull(dbAfterRelease.sp);
	}

	/**
	 * Tests the definition of a default selector which is used if the other
	 * selector didn't select anything.
	 */
	@Test
	public void testDefaultSelector() {
		ConfigurationCoreSettings configCore;
		IConfiguration config;

		configCore = ConfigurationCoreSettings.loadCoreSettings(
				"sbconfigurator-noDefaultSelector.xml", getClass());
		config = configCore.getConfiguration();
		assertNotNull(config);
		assertEquals(0, config.getAllModules().size());

		configCore = ConfigurationCoreSettings.loadCoreSettings(
				"sbconfigurator-defaultSelector.xml", getClass());
		config = configCore.getConfiguration();
		assertNotNull(config);
		assertEquals(3, config.getAllModules().size());

		configCore = ConfigurationCoreSettings.loadCoreSettings(
				"sbconfigurator-defaultSelectorFromWorkingDir.xml", getClass());
		config = configCore.getConfiguration();
		assertNotNull(config);
		assertEquals(0, config.getAllModules().size());
	}
}
