package net.meisen.general.sbconfigurator;

import net.meisen.general.sbconfigurator.config.TestDefaultConfiguration;
import net.meisen.general.sbconfigurator.config.TestSamplePlugInLoader;
import net.meisen.general.sbconfigurator.config.placeholder.TestDefaultXmlPropertyReplacer;
import net.meisen.general.sbconfigurator.config.placeholder.TestSpringPropertyHolder;
import net.meisen.general.sbconfigurator.config.transformer.TestDefaultXsdValidator;
import net.meisen.general.sbconfigurator.config.transformer.TestDefaultXsltTransformer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All tests together as a {@link Suite}
 * 
 * @author pmeisen
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestSpringPropertyHolder.class,
		TestDefaultXmlPropertyReplacer.class, TestDefaultXsdValidator.class,
		TestDefaultXsltTransformer.class, TestDefaultConfiguration.class,
		TestSamplePlugInLoader.class })
public class AllTests {
	// nothing more to do here
}
