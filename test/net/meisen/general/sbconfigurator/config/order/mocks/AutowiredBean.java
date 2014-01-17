package net.meisen.general.sbconfigurator.config.order.mocks;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.config.order.TestInstantiationOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A bean which is autowired.
 * 
 * @author pmeisen
 * 
 */
public class AutowiredBean implements IValidatable {

	@Autowired
	@Qualifier("simpleBean")
	private SimpleBean bean;

	@Autowired
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	/**
	 * Default constructor.
	 */
	public AutowiredBean() {

		// within the Constructor no auto-wiring could have taken place
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".constructor");
	}

	/**
	 * Validates the auto-wiring.
	 */
	public void validateAutowiring() {
		assertNotNull(bean);
		assertNotNull(configuration);
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
	 * Create some dummy element.
	 * 
	 * @return a dummy object created
	 */
	public Object create() {
		return create((List<Object>) null);
	}

	/**
	 * Create some dummy object and validates the parameters (see
	 * {@link #validateParameters(List)}).
	 * 
	 * @param parameters
	 *            parameters to be validated
	 * 
	 * @return a dummy object created
	 */
	public Object create(final List<Object> parameters) {
		validateAutowiring();
		validateParameters(parameters);

		// add the command
		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".create");

		// return a new Date
		return new Date();
	}

	/**
	 * Method which can be invoked.
	 */
	public void invoke() {
		invoke((List<Object>) null);
	}

	/**
	 * Method which can be invoked, whereby the parameters are validated (see
	 * {@link #validateParameters(List)}).
	 * 
	 * @param parameters
	 *            parameters to be validated
	 */
	public void invoke(final List<Object> parameters) {
		validateAutowiring();
		validateParameters(parameters);

		TestInstantiationOrder.COMMANDS.add(getClass().getSimpleName()
				+ ".invoke");
	}

	@Override
	public void validate() {
		validateAutowiring();
	}
}
