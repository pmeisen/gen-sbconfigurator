package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.config.DefaultConfiguration;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;

import org.junit.Test;

public class TestSamplePlugInLoader {

	@Test
	public void testLoadingOfSamplePlugIn() {

		// load the server
		final ConfigurationCoreSettings coreSettings = ConfigurationCoreSettings
				.loadCoreSettings();
		final DefaultConfiguration configuration = (DefaultConfiguration) coreSettings
				.getConfiguration();
		final Object module = configuration.getModule("samplePlugIn");

		// do some tests
		assertNotNull(module);
		assertTrue(module instanceof SamplePlugIn);

		// now check the plugin
		final SamplePlugIn plugIn = (SamplePlugIn) module;
		assertEquals(plugIn.coreSettings, coreSettings);
	}
}
