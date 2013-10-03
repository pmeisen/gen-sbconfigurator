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
public class RecursiveConfigurationWiringOuter {

	@Autowired(required = true)
	@Qualifier("config")
	private IConfiguration config;

	@Autowired(required = true)
	@Qualifier("pushed")
	private RecursiveConfigurationWiringInner pushed;

	@Autowired(required = true)
	@Qualifier("pushedDate")
	private Date pushedDate;

	@Autowired(required = true)
	@Qualifier("outerDate")
	private Date outerDate;

	public IConfiguration getConfig() {
		return config;
	}

	public void setConfig(IConfiguration config) {
		this.config = config;
	}

	public RecursiveConfigurationWiringInner getPushed() {
		return pushed;
	}

	public void setPushed(RecursiveConfigurationWiringInner pushed) {
		this.pushed = pushed;
	}

	public Date getPushedDate() {
		return pushedDate;
	}

	public void setPushedDate(Date pushedDate) {
		this.pushedDate = pushedDate;
	}

	public Date getOuterDate() {
		return outerDate;
	}

	public void setOuterDate(Date outerDate) {
		this.outerDate = outerDate;
	}
}
