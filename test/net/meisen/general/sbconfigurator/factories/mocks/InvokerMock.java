package net.meisen.general.sbconfigurator.factories.mocks;

/**
 * Helper mock to test the {@code MethodInvokingFactoryBean}.
 * 
 * @author pmeisen
 * 
 */
public class InvokerMock {
	private String value;

	/**
	 * Get the currently set value.
	 * 
	 * @return the currently set value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the current value.
	 * 
	 * @param value
	 *            the value to be set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets a value and returns {@code this}.
	 * 
	 * @param value
	 *            the value to be set
	 * 
	 * @return {@code this}
	 */
	public InvokerMock setValueAndGetThis(final String value) {
		this.value = value;
		return this;
	}
}
