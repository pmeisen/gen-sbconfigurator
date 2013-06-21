package net.meisen.general.sbconfigurator.config.mocks;

import java.io.IOException;
import java.util.Properties;

import net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder;

/**
 * Just a test mock to check if it gets loaded instead of the originally defined
 * one.
 * 
 * @author pmeisen
 * 
 */
public class TestPropertyHolder extends SpringPropertyHolder {

	@Override
	public Properties getProperties(final boolean inclOtherReplacer)
			throws IOException {
		
		// get the super result
		final Properties result = super.getProperties(inclOtherReplacer);

		// add an additional property
		result.setProperty("class", getClass().getName());
		
		return result;
	}
}
