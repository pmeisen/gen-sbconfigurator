package net.meisen.general.sbconfigurator.config.order.mocks;

/**
 * Marks a bean to be validatable, i.e. has a validate method to execute for
 * validation.
 * 
 * @author pmeisen
 * 
 */
public interface IValidatable {

	/**
	 * Method executed for validation
	 */
	public void validate();
}
