package net.meisen.general.sbconfigurator.config.mocks;

import net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helper mock to test delayed loading.
 * 
 * @author pmeisen
 * 
 */
public class DelayedBean {

	/**
	 * The {@code SamplePlugIn} which will be auto-wired.
	 */
	@Autowired
	public SamplePlugIn sp;
}
