package net.meisen.general.sbconfigurator.authoring.mock;

import java.util.Date;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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

	public SamplePlugIn getSettings() {
		return settings;
	}

	public void setSettings(SamplePlugIn settings) {
		this.settings = settings;
	}

	public IConfiguration getConfig() {
		return config;
	}

	public void setConfig(IConfiguration config) {
		this.config = config;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
}
