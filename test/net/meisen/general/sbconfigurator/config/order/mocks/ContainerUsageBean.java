package net.meisen.general.sbconfigurator.config.order.mocks;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.config.order.TestInstantiationOrder;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A mock which uses the {@code ContainerBean}. Used for circular references.
 * 
 * @author pmeisen
 * 
 */
public class ContainerUsageBean {

	@Autowired
	private ContainerBean container;

	/**
	 * Default constructor
	 */
	public ContainerUsageBean() {
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".constructor");
	}

	/**
	 * A init-method to be called by a {@code MethodInvoker}.
	 */
	public void init() {
		assertNotNull(container);
		assertNotNull(container.getBean());
		assertEquals(4, container.getSize());

		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".init");
	}

	/**
	 * Gets the {@code ContainerBean}.
	 * 
	 * @return the {@code ContainerBean}
	 */
	public ContainerBean getContainer() {
		return container;
	}
}
