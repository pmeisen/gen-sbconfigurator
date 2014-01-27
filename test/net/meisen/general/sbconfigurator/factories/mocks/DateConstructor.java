package net.meisen.general.sbconfigurator.factories.mocks;

import java.util.Date;

/**
 * Class which sets a date property during construction.
 * 
 * @author pmeisen
 * 
 */
public class DateConstructor {
	private final Date date;

	/**
	 * Constructor to set the date property.
	 * 
	 * @param date
	 *            the date
	 */
	public DateConstructor(final Date date) {
		this.date = date;
	}

	/**
	 * Get the date.
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
}
