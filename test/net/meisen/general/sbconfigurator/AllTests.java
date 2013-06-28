package net.meisen.general.sbconfigurator;

import net.meisen.general.sbconfigurator.config.TestDefaultConfiguration;
import net.meisen.general.sbconfigurator.config.TestSamplePlugInLoader;
import net.meisen.general.sbconfigurator.config.placeholder.TestDefaultXmlPropertyReplacer;
import net.meisen.general.sbconfigurator.config.placeholder.TestSpringPropertyHolder;
import net.meisen.general.sbconfigurator.config.transformer.TestDefaultXsdValidator;
import net.meisen.general.sbconfigurator.config.transformer.TestDefaultXsltTransformer;
import net.meisen.general.sbconfigurator.helper.TestSpringHelper;
import net.meisen.general.sbconfigurator.helper.TestStringParser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All tests together as a {@link Suite}
 * 
 * @author pmeisen
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestSpringPropertyHolder.class, TestStringParser.class,
		TestDefaultXmlPropertyReplacer.class, TestDefaultXsdValidator.class,
		TestDefaultXsltTransformer.class, TestDefaultConfiguration.class,
		TestSamplePlugInLoader.class, TestSpringHelper.class })
public class AllTests {
	// nothing more to do here
}
