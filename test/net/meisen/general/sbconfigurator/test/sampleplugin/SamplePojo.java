package net.meisen.general.sbconfigurator.test.sampleplugin;

/**
 * Some pojo for testing only
 * 
 * @author pmeisen
 * 
 */
public class SamplePojo {
	private String testValue;
	private String replacedValue;

	/**
	 * Get the test-value.
	 * 
	 * @return the test-value
	 */
	public String getTestValue() {
		return testValue;
	}

	/**
	 * Sets the test-value.
	 * 
	 * @param testValue
	 *          the test-value to be set
	 */
	public void setTestValue(final String testValue) {
		this.testValue = testValue;
	}

	/**
	 * Gets the replaced test-value.
	 * 
	 * @return the replaced test-value
	 */
	public String getReplacedValue() {
		return replacedValue;
	}

	/**
	 * Sets the replaced test-value
	 * 
	 * @param replacedValue
	 *          the replaced test-value
	 */
	public void setReplacedValue(final String replacedValue) {
		this.replacedValue = replacedValue;
	}
}
