package net.meisen.general.sbconfigurator.factories.mocks;

/**
 * Helper mock to test the {@code MethodInvokingFactoryBean}.
 * 
 * @author pmeisen
 * 
 */
public class InvokerMock {
	private String value;
	
	public String getValue() {
		return value;
	}
	
	public void setValue(final String value) {
		this.value = value;
	}

	public InvokerMock setValueAndGetThis(final String value) {
		this.value = value;
		return this;
	}
}
