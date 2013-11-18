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
	 * Helper class to count a call of a method
	 * 
	 * @author pmeisen
	 * 
	 */
	public static class CallCounter {
		private int counter = 0;

		/**
		 * Increase the counter by 1
		 */
		public void increase() {
			counter++;
		}

		/**
		 * Reset the counter to 0
		 */
		public void reset() {
			this.counter = 0;
		}

		/**
		 * Get the current value of the counter
		 * 
		 * @return the current value of the counter
		 */
		public int getCounter() {
			return counter;
		}
	}

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

		// get the counter and make sure the methods are called
		final CallCounter counter = configuration.getModule("CallCounter");
		assertEquals(3, counter.getCounter());
		assertEquals(1, configuration.getAllModules().size());
	}
}
