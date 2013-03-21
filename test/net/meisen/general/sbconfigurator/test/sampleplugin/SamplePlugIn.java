package net.meisen.general.sbconfigurator.test.sampleplugin;

import org.springframework.beans.factory.annotation.Autowired;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

/**
 * Just some sample to check the loading of additional modules
 * 
 * @author pmeisen
 * 
 */
public class SamplePlugIn {

	/**
	 * The <code>ConfigurationCoreSettings</code> should be wired
	 */
	@Autowired
	public ConfigurationCoreSettings coreSettings;
}
