package net.meisen.general.sbconfigurator.config.order.mocks;

import net.meisen.general.sbconfigurator.config.order.TestInstantiationOrder;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * An element of the container which uses the {@code containerUsageBean}.
 * 
 * @author pmeisen
 * 
 */
public class ContainerElementBean {

	@Autowired
	private ContainerUsageBean containerUsageBean;

	/**
	 * Default constructor
	 */
	public ContainerElementBean() {
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".constructor");
	}

	/**
	 * Gets the wired {@code ContainerUsageBean}.
	 * 
	 * @return the wired {@code ContainerUsageBean}
	 */
	public ContainerUsageBean getContainerUsageBean() {
		return containerUsageBean;
	}
}
