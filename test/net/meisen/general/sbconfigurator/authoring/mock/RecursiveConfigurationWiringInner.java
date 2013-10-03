package net.meisen.general.sbconfigurator.authoring.mock;

import java.util.Date;

import net.meisen.general.sbconfigurator.api.IConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Mock to test the autowiring when using the authoring.
 * 
 * @author pmeisen
 * 
 */
@SuppressWarnings("javadoc")
public class RecursiveConfigurationWiringInner {

	@Autowired(required = true)
	@Qualifier(IConfiguration.coreConfigurationId)
	private IConfiguration config;

	@Autowired(required = true)
	@Qualifier("injected")
	private RecursiveConfigurationWiringOuter injected;

	@Autowired(required = true)
	@Qualifier("injectedDate")
	private Date injectedDate;

	@Autowired(required = true)
	@Qualifier("innerDate")
	private Date innerDate;
	
	public IConfiguration getConfig() {
		return config;
	}

	public void setConfig(IConfiguration config) {
		this.config = config;
	}

	public RecursiveConfigurationWiringOuter getInjected() {
		return injected;
	}

	public void setInjected(RecursiveConfigurationWiringOuter injected) {
		this.injected = injected;
	}

	public Date getInjectedDate() {
		return injectedDate;
	}

	public void setInjectedDate(Date injectedDate) {
		this.injectedDate = injectedDate;
	}

	public Date getInnerDate() {
		return innerDate;
	}

	public void setInnerDate(Date innerDate) {
		this.innerDate = innerDate;
	}

}
