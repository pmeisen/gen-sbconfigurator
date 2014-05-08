package net.meisen.general.sbconfigurator.factories.mocks;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * An instance which was auto-wired.
 * 
 * @author pmeisen
 * 
 */
public class AutowiredInstance {

	@Autowired
	@Qualifier("Date")
	private Date date;

	/**
	 * Get the auto-wired date.
	 * 
	 * @return the auto-wired date
	 */
	public Date getDate() {
		return date;
	}
}
