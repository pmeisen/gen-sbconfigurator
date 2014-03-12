package net.meisen.general.sbconfigurator.runners;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.config.DefaultConfiguration;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.annotations.ContextFile;
import net.meisen.general.sbconfigurator.runners.annotations.SystemProperty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests the implementation of the {@code JUnitConfigurationRunner}.
 * 
 * @author pmeisen
 * 
 * @see JUnitConfigurationRunner
 * 
 */
public class TestJUnitConfigurationRunner {

	/**
	 * Base implementation of a test to be tested by this
	 * {@code TestJUnitConfigurationRunner}.
	 * 
	 * @author pmeisen
	 * 
	 */
	@RunWith(JUnitConfigurationRunner.class)
	@ContextClass(BaseTest.class)
	@ContextFile("emptyBeans.xml")
	public static abstract class BaseTest {

		/**
		 * The wired {@code Configuration}.
		 */
		@Autowired(required = true)
		@Qualifier("coreConfiguration")
		protected DefaultConfiguration configuration;

		/**
		 * The test to run.
		 */
		@Test
		public abstract void test();
	}

	/**
	 * Tests no usage of any system property.
	 * 
	 * @author pmeisen
	 * 
	 */
	public static class TestWithoutSystemProperty extends BaseTest {

		@Override
		public void test() {
			assertNull(System.getProperty("test.placeholder"));
			assertNull(configuration.getProperties().get("test.placeholder"));
			assertNull(System.getProperty("test.placeholder"));
		}
	}

	/**
	 * Tests the usage of a value.
	 * 
	 * @author pmeisen
	 * 
	 */
	@SystemProperty(property = "test.placeholder", value = "?")
	public static class TestAFirstValue extends BaseTest {

		@Override
		public void test() {
			assertNull(System.getProperty("test.placeholder"));
			assertEquals("?",
					configuration.getProperties().get("test.placeholder"));
			assertNull(System.getProperty("test.placeholder"));
		}
	}

	/**
	 * Tests the usage of a value after one was set.
	 * 
	 * @author pmeisen
	 * 
	 */
	@SystemProperty(property = "test.placeholder", value = "someValue")
	public static class TestSomeValue extends BaseTest {

		@Override
		public void test() {
			assertNull(System.getProperty("test.placeholder"));
			assertEquals("someValue",
					configuration.getProperties().get("test.placeholder"));
			assertNull(System.getProperty("test.placeholder"));
		}
	}

	/**
	 * Tests the usage of no value after values are set.
	 * 
	 * @author pmeisen
	 * 
	 */
	public static class TestNoValue extends BaseTest {

		@Override
		public void test() {
			assertNull(System.getProperty("test.placeholder"));
			assertNull(configuration.getProperties().get("test.placeholder"));
			assertNull(System.getProperty("test.placeholder"));
		}
	}

	/**
	 * Suite which combines all the classes.
	 * 
	 * @author pmeisen
	 * 
	 */
	@RunWith(Suite.class)
	@Suite.SuiteClasses({ TestWithoutSystemProperty.class,
			TestAFirstValue.class, TestSomeValue.class, TestNoValue.class })
	public static class TestSuite {
		// just the suite with all the tests defined here
	}
}
