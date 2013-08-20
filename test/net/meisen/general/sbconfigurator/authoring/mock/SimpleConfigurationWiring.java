package net.meisen.general.sbconfigurator.authoring.mock;

import java.util.Date;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Mock to test the autowiring when using the authoring.
 * 
 * @author pmeisen
 * 
 */
public class SimpleConfigurationWiring {

	@Autowired(required = true)
	@Qualifier("testConfig")
	private IConfiguration config;

	@Autowired(required = true)
	@Qualifier("b")
	private SamplePlugIn settings;

	@Autowired(required = true)
	@Qualifier("creationTime")
	private Date creationTime;

	/**
	 * Access the wired settings
	 * 
	 * @return the wired settings
	 */
	public SamplePlugIn getSettings() {
		return settings;
	}

	/**
	 * Setter for the settings.
	 * 
	 * @param settings
	 *            settings to be set
	 */
	public void setSettings(SamplePlugIn settings) {
		this.settings = settings;
	}

	/**
	 * Get the wired configuration.
	 * 
	 * @return the wired configuration
	 */
	public IConfiguration getConfig() {
		return config;
	}

	/**
	 * Setter for the configuration.
	 * 
	 * @param config
	 *            the configuration to be set
	 */
	public void setConfig(IConfiguration config) {
		this.config = config;
	}

	/**
	 * Setter for the creation time.
	 * 
	 * @return the time the object was created
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Setter to set the creation time
	 * 
	 * @param creationTime
	 *            the time the object was created
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
}
