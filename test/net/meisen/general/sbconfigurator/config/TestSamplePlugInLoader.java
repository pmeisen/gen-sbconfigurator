package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Map;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IModuleHolder;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePojo;

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

		// load the configuration
		final ConfigurationCoreSettings coreSettings = ConfigurationCoreSettings
				.loadCoreSettings("loaderExtended-core.xml",
						TestSamplePlugInLoader.class);
		final DefaultConfiguration configuration = (DefaultConfiguration) coreSettings
				.getConfiguration();

		// check the loaded modules
		assertEquals(4, configuration.getAllModules().size());

		// check the pojo
		final SamplePojo moduleSamplePojo = configuration
				.getModule("testSamplePojo");

		// do some tests for the pojo
		assertNotNull(moduleSamplePojo);
		assertEquals(moduleSamplePojo.getTestValue(), "value");
		assertEquals(moduleSamplePojo.getReplacedValue(), "replacedvalue");

		// check another pojo
		final SamplePojo moduleAnotherPojo = configuration
				.getModule("testAnotherPojo");

		// do some tests for the pojo
		assertNotNull(moduleAnotherPojo);
		assertEquals(moduleAnotherPojo.getTestValue(), "value");
		assertEquals(moduleAnotherPojo.getReplacedValue(), "replacedvalue");

		// check the module of the loading via loaderDefinition
		final Object moduleSamplePlugIn = configuration
				.getModule("samplePlugIn");

		// do some tests
		assertNotNull(moduleSamplePlugIn);
		assertTrue(moduleSamplePlugIn instanceof SamplePlugIn);

		// now check the plugin
		final SamplePlugIn plugIn = (SamplePlugIn) moduleSamplePlugIn;
		assertEquals(plugIn.coreSettings, coreSettings);
		assertEquals(plugIn.configuration, coreSettings.getConfiguration());

		// check the loading of the beanModule
		final Object beanModule = configuration
				.getModule("testSampleBeanPlugIn");

		// do some tests
		assertNotNull(moduleSamplePlugIn);
		assertTrue("beanModule is instanceof '"
				+ (beanModule == null ? null : beanModule.getClass().getName())
				+ "'", beanModule instanceof SamplePlugIn);

		// now check the plugin
		final SamplePlugIn beanPlugIn = (SamplePlugIn) beanModule;
		assertEquals(beanPlugIn.coreSettings, coreSettings);
		assertEquals(beanPlugIn.configuration, coreSettings.getConfiguration());
	}

	/**
	 * Test the delayed loading
	 */
	@Test
	public void testDelayedLoading() {

		// load the configuration
		final ConfigurationCoreSettings coreSettings = ConfigurationCoreSettings
				.loadCoreSettings("loaderExtended-core.xml",
						TestSamplePlugInLoader.class);
		final DefaultConfiguration configuration = (DefaultConfiguration) coreSettings
				.getConfiguration();
		final int allModulesSize = configuration.getAllModules().size();

		// get the resource to be loaded delayed
		final InputStream ios = getClass()
				.getResourceAsStream(
						"/net/meisen/general/sbconfigurator/test/sampleplugin/sbconfigurator-testDelayedSample.xml");
		assertNotNull(ios);

		// load it delayed
		final IModuleHolder modulesHolder = configuration.loadDelayed(
				"testSampleLoader", ios);
		final Map<String, Object> modules = modulesHolder.getAllModules();
		assertNotNull(modules);
		assertEquals(1, modules.size());
		assertTrue(modules.values().iterator().next() instanceof SamplePlugIn);

		// get the plugin
		final SamplePlugIn sample = (SamplePlugIn) modules.values().iterator()
				.next();
		assertEquals(sample, modules.get("samplePlugIn"));
		assertNotNull(sample);
		assertFalse(sample.equals(configuration.getModule("samplePlugIn")));

		// make sure no new modules are loaded
		assertEquals(allModulesSize, configuration.getAllModules().size());
	}
}
