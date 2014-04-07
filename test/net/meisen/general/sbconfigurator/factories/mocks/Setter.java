package net.meisen.general.sbconfigurator.factories.mocks;

import java.util.Date;

/**
 * Just a setter class to set some values
 * 
 * @author pmeisen
 * 
 */
public class Setter {
	private Integer intNumber = null;
	private Long longNumber = null;
	private Object anyObject = null;
	private String stringValue = null;
	private Date dateValue = null;

	/**
	 * Get an integer number.
	 * 
	 * @return the integer number
	 */
	public Integer getIntNumber() {
		return intNumber;
	}

	/**
	 * Set a int number.
	 * 
	 * @param intNumber
	 *            the int number
	 */
	public void setIntNumber(final Integer intNumber) {
		this.intNumber = intNumber;
	}

	/**
	 * Get an long number.
	 * 
	 * @return the long number
	 */
	public Long getLongNumber() {
		return longNumber;
	}

	/**
	 * Set a long number.
	 * 
	 * @param longNumber
	 *            the long number
	 */
	public void setLongNumber(final Long longNumber) {
		this.longNumber = longNumber;
	}

	/**
	 * Get a string value.
	 * 
	 * @return the string value
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * Set a string value.
	 * 
	 * @param stringValue
	 *            the string value
	 */
	public void setStringValue(final String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * Get a date value.
	 * 
	 * @return the date value
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * Set a date value.
	 * 
	 * @param dateValue
	 *            the date value
	 */
	public void setDateValue(final Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * Get the defined any object.
	 * 
	 * @return the defined any object
	 */
	public Object getAnyObject() {
		return anyObject;
	}

	/**
	 * Set the any object.
	 * 
	 * @param anyObject
	 *            the object to be set
	 */
	public void setAnyObject(Object anyObject) {
		this.anyObject = anyObject;
	}
}
