package net.meisen.general.sbconfigurator.config.placeholder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.meisen.general.sbconfigurator.runners.annotations.ContextFile;
import net.meisen.general.sbconfigurator.runners.annotations.SystemProperty;

import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PropertiesLoaderSupport;

/**
 * Tests the usage of a placeholder within the selector
 * 
 * @author pmeisen
 * 
 */
@ContextFile("loader/loaderUsingPlaceholder.xml")
@SystemProperty(property = "sbconfigurator.loader.selector", value = "net/meisen/general/sbconfigurator/config/placeholder/loader/sampleBeansToBeLoaded.xml")
public class TestPlaceholderInSelectorSimple extends
		BaseTestPlaceHolderInSelector {

	/**
	 * Tests the usage of a placeholder within the selector
	 * 
	 * @throws IOException
	 *             if the properties cannot be read
	 */
	@Test
	public void testPlaceHolderInSelector() throws IOException {

		// check that there is onle ony defined
		assertEquals(1, corePropertyHolder.getOtherPropertyHolder().size());

		// get the one defined in the file
		final PropertiesLoaderSupport propertyHolder = corePropertyHolder
				.getOtherPropertyHolder().get(0);
		assertTrue(propertyHolder instanceof SpringPropertyHolder);
		final SpringPropertyHolder innerHolder = (SpringPropertyHolder) propertyHolder;

		assertEquals(false, innerHolder.isOtherHolderOverride());
		assertEquals(false, innerHolder.isLocalOverride());
		assertEquals(
				PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE,
				innerHolder.getSystemPropertiesMode());
		assertEquals(
				"net/meisen/general/sbconfigurator/config/placeholder/loader/sampleBeansToBeLoaded.xml",
				innerHolder.getProperties().get(
						"sbconfigurator.loader.selector"));
		assertEquals(
				"net/meisen/general/sbconfigurator/config/placeholder/loader/sampleBeansToBeLoaded.xml",
				corePropertyHolder.getProperties().get(
						"sbconfigurator.loader.selector"));

		// check if the configuration was able to load the
		assertNotNull(configuration.getModule("dateModule"));
	}
}
