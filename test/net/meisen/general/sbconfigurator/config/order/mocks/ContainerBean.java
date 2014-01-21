package net.meisen.general.sbconfigurator.config.order.mocks;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.meisen.general.sbconfigurator.config.order.TestInstantiationOrder;

/**
 * Mock which is used as a container.
 * 
 * @author pmeisen
 * 
 */
public class ContainerBean {

	@Autowired
	@Qualifier("simpleBean")
	private SimpleBean bean;

	private List<Object> item = new ArrayList<Object>();

	/**
	 * Default constructor
	 */
	public ContainerBean() {
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".constructor");
	}

	/**
	 * Adds an element to the container
	 * 
	 * @param item
	 *            the item to be added
	 */
	public void add(final Object item) {
		assertNotNull(item);

		this.item.add(item);

		TestInstantiationOrder.COMMANDS
				.add(getClass().getSimpleName() + ".add");
	}

	/**
	 * The amount of items added.
	 * 
	 * @return the amount of items added
	 */
	public int getSize() {
		return item.size();
	}

	/**
	 * The auto-wired bean.
	 * 
	 * @return the auto-wired bean
	 */
	public SimpleBean getBean() {
		return bean;
	}
}
