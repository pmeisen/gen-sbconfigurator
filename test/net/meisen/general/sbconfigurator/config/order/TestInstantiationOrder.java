package net.meisen.general.sbconfigurator.config.order;

import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.junit.Test;

/**
 * Tests the instantiation order of beans.
 * 
 * @author pmeisen
 * 
 */
public class TestInstantiationOrder {

	/**
	 * Helper to fill the commands executed for the test
	 */
	public static Commands COMMANDS = new Commands();

	/**
	 * Runs a specified tests and resets the commands prior to start the
	 * execution.
	 * 
	 * @param test
	 *            the test to run
	 * 
	 * @return the {@code configuration} loaded
	 */
	protected IConfiguration runExecution(final String test) {
		COMMANDS.clear();

		// set the property to point to the file
		System.setProperty("testBeans.selector",
				"net/meisen/general/sbconfigurator/config/order/" + test
						+ ".xml");

		// load the configuration
		final ConfigurationCoreSettings configCore = ConfigurationCoreSettings
				.loadCoreSettings(
						"/net/meisen/general/sbconfigurator/authoring/sbconfigurator-core-placeholder.xml",
						TestInstantiationOrder.class);

		// get the loaded configuration
		return configCore.getConfiguration();
	}

	/**
	 * Tests some simple usage of a factory.
	 */
	@Test
	public void testSimpleFactory() {
		runExecution("testSimpleFactory");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 3, COMMANDS.size());
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "AutowiredBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "AutowiredBean.create", COMMANDS.get(2));
	}

	/**
	 * Tests the usage of a parametrized factory.
	 */
	@Test
	public void testParameterizedFactory() {
		runExecution("testParameterizedFactory");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 3, COMMANDS.size());
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "AutowiredBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "AutowiredBean.create", COMMANDS.get(2));
	}

	/**
	 * Tests a nested wired factory.
	 */
	@Test
	public void testNestedWiredFactory() {
		runExecution("testNestedWiredFactory");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 4, COMMANDS.size());
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "AutowiredBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "ComplexAutowiredBean.constructor", COMMANDS.get(2));
		assertEquals(msg, "ComplexAutowiredBean.create", COMMANDS.get(3));
	}

	/**
	 * Tests a nested bean within a factory.
	 */
	@Test
	public void testNestedBeanInFactory() {
		runExecution("testNestedBeanInFactory");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 6, COMMANDS.size());
		assertEquals(msg, "AutowiredBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "ComplexAutowiredBean.constructor", COMMANDS.get(2));
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(3));
		assertEquals(msg, "ComplexAutowiredBean.constructor", COMMANDS.get(4));
		assertEquals(msg, "ComplexAutowiredBean.create", COMMANDS.get(5));
	}

	/**
	 * Tests the usage of a method invocation.
	 */
	@Test
	public void testSimpleMethodInvocation() {
		runExecution("testSimpleMethodInvocation");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 5, COMMANDS.size());
		assertEquals(msg, "ComplexAutowiredBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "AutowiredBean.constructor", COMMANDS.get(2));
		assertEquals(msg, "ComplexAutowiredBean.invoke", COMMANDS.get(3));
		assertEquals(msg, "ComplexAutowiredBean.invoke", COMMANDS.get(4));
	}

	/**
	 * Adds a method invoker before the reference is available
	 */
	@Test
	public void testPreMethodInvocation() {
		runExecution("testPreMethodInvocation");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 2, COMMANDS.size());

		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "SimpleBean.invoke", COMMANDS.get(1));
	}

	/**
	 * Tests the usage of a defined methode execution
	 */
	@Test
	public void testCircularMethodExecution() {
		runExecution("testCircularMethodInvocation");

		final String msg = COMMANDS.toString();
		assertEquals(msg, 12, COMMANDS.size());

		assertEquals(msg, "ContainerBean.constructor", COMMANDS.get(0));
		assertEquals(msg, "SimpleBean.constructor", COMMANDS.get(1));
		assertEquals(msg, "ContainerElementBean.constructor", COMMANDS.get(2));
		assertEquals(msg, "ContainerUsageBean.constructor", COMMANDS.get(3));
		assertEquals(msg, "ContainerBean.add", COMMANDS.get(4));
		assertEquals(msg, "ContainerElementBean.constructor", COMMANDS.get(5));
		assertEquals(msg, "ContainerBean.add", COMMANDS.get(6));
		assertEquals(msg, "ContainerElementBean.constructor", COMMANDS.get(7));
		assertEquals(msg, "ContainerBean.add", COMMANDS.get(8));
		assertEquals(msg, "ContainerElementBean.constructor", COMMANDS.get(9));
		assertEquals(msg, "ContainerBean.add", COMMANDS.get(10));
		assertEquals(msg, "ContainerUsageBean.init", COMMANDS.get(11));
	}
}
