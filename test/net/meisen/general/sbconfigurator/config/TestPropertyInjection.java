package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePojo;

import org.junit.Test;

/**
 * Tests the implementation of {@code PropertyInjection} during configuration.
 * 
 * @author pmeisen
 * 
 */
public class TestPropertyInjection {

	/**
	 * Tests the implementation of {@code PropertyInjection} during
	 * configuration.
	 */
	@Test
	public void testExtendedConfiguration() {
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings("propertyInjection-core.xml", getClass());
		final IConfiguration config = configCore.getConfiguration();

		final SamplePojo module = config.getModule("testPojo");
		assertNotNull(module);
		assertEquals("replacement", module.getReplacedValue());
	}
}
