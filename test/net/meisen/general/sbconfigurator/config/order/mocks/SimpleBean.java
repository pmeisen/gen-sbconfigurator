package net.meisen.general.sbconfigurator.config.order.mocks;

import net.meisen.general.sbconfigurator.config.order.TestInstantiationOrder;

/**
 * Just a simple bean.
 * 
 * @author pmeisen
 * 
 */
public class SimpleBean {

	/**
	 * Default constructor
	 */
	public SimpleBean() {
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".constructor");
	}
}
