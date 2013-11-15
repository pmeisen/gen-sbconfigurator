package net.meisen.general.sbconfigurator.config;

import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

import org.junit.Test;

/**
 * Tests the exclusion of specific modules (i.e. the one injected by Spring)
 * from the module list
 * 
 * @author pmeisen
 * 
 */
public class TestSpringExclusions {

	/**
	 * Tests the exclusion of <code>MethodInvocations</code>.
	 */
	@Test
	public void testExclusionOfMethodInvocations() {

		// load the server
		final ConfigurationCoreSettings coreSettings = ConfigurationCoreSettings
				.loadCoreSettings("springExclusions-core.xml",
						TestSpringExclusions.class);
		final DefaultConfiguration configuration = (DefaultConfiguration) coreSettings
				.getConfiguration();

		assertEquals(0, configuration.getAllModules().size());
	}
}
