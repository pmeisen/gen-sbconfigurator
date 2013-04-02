package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.config.DefaultConfiguration;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;

import org.junit.Test;

/**
 * Creates and loads a sample plug-in
 * 
 * @author pmeisen
 * 
 */
public class TestSamplePlugInLoader {

	/**
	 * Loads the sample plug-in and checks the result
	 */
	@Test
	public void testLoadingOfSamplePlugIn() {

		// load the server
		final ConfigurationCoreSettings coreSettings = ConfigurationCoreSettings
				.loadCoreSettings();
		final DefaultConfiguration configuration = (DefaultConfiguration) coreSettings
				.getConfiguration();

		// check the loaded modules
		assertEquals(configuration.getAllModules().size(), 2);

		// check the module of the loading via loaderDefinition
		final Object module = configuration.getModule("samplePlugIn");

		// do some tests
		assertNotNull(module);
		assertTrue(module instanceof SamplePlugIn);

		// now check the plugin
		final SamplePlugIn plugIn = (SamplePlugIn) module;
		assertEquals(plugIn.coreSettings, coreSettings);
		assertEquals(plugIn.configuration, coreSettings.getConfiguration());

		// check the loading of the beanModule
		final Object beanModule = configuration.getModule("testSampleBeanPlugIn");

		// do some tests
		assertNotNull(module);
		assertTrue("beanModule is instanceof '"
				+ (beanModule == null ? null : beanModule.getClass().getName()) + "'",
				beanModule instanceof SamplePlugIn);

		// now check the plugin
		final SamplePlugIn beanPlugIn = (SamplePlugIn) beanModule;
		assertEquals(beanPlugIn.coreSettings, coreSettings);
		assertEquals(beanPlugIn.configuration, coreSettings.getConfiguration());
	}
}
