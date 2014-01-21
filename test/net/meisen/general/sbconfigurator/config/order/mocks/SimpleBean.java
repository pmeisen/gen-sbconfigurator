package net.meisen.general.sbconfigurator.config.order.mocks;

import static org.junit.Assert.assertNotNull;

import java.util.List;

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

	/**
	 * Method which can be invoked.
	 */
	public void invoke() {
		invoke((List<Object>) null);
	}

	/**
	 * Validate the passed parameters not to be {@code null}.
	 * 
	 * @param parameters
	 *            the parameters to be validated
	 */
	public void validateParameters(final List<Object> parameters) {
		if (parameters != null && parameters.size() > 0) {
			for (final Object p : parameters) {
				assertNotNull(p);

				if (p instanceof IValidatable) {
					((IValidatable) p).validate();
				}
			}
		}
	}

	/**
	 * Method which can be invoked, whereby the parameters are validated (see
	 * {@link #validateParameters(List)}).
	 * 
	 * @param parameters
	 *            parameters to be validated
	 */
	public void invoke(final List<Object> parameters) {
		validateParameters(parameters);

		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".invoke");
	}
}
