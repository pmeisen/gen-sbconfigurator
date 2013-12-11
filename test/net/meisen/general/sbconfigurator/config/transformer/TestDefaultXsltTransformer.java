package net.meisen.general.sbconfigurator.config.transformer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.meisen.general.sbconfigurator.config.DefaultConfiguration;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests the implementation of {@code DefaultXsltTransformer} and the additional
 * {@code ClasspathXsltUriResolver}.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
public class TestDefaultXsltTransformer {

	@Autowired(required = true)
	@Qualifier("coreConfiguration")
	private DefaultConfiguration configuration;

	/**
	 * Simple usage just transformation.
	 * 
	 * @throws InvalidXsltException
	 *             if the xslt is invalid
	 * @throws TransformationFailedException
	 *             if the transformation failed
	 */
	@Test
	public void testSimple() throws InvalidXsltException,
			TransformationFailedException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final DefaultXsltTransformer transformer = new DefaultXsltTransformer();

		// set a sample
		transformer
				.setXsltTransformer("net/meisen/general/sbconfigurator/config/transformer/testSample.xslt");

		// transform it
		transformer
				.transformFromClasspath(
						"net/meisen/general/sbconfigurator/config/transformer/testSample.xml",
						outputStream);

		final String out = outputStream.toString();
		assertTrue(out.contains("<beans"));
	}

	/**
	 * Simple usage of includes from the classpath.
	 * 
	 * @throws InvalidXsltException
	 *             if the xslt is invalid
	 * @throws TransformationFailedException
	 *             if the transformation failed
	 */
	@Test
	public void testOtherIncludes() throws InvalidXsltException,
			TransformationFailedException {
		final DefaultXsltUriResolver resolver = new DefaultXsltUriResolver(
				new ClasspathXsltUriResolver());
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final DefaultXsltTransformer transformer = new DefaultXsltTransformer(
				resolver);

		// set a sample
		transformer
				.setXsltTransformer("net/meisen/general/sbconfigurator/config/transformer/testSampleWithInclude.xslt");

		// transform it
		transformer
				.transformFromClasspath(
						"net/meisen/general/sbconfigurator/config/transformer/testSample.xml",
						outputStream);

		final String out = outputStream.toString();
		assertTrue(out
				.contains("<bean id=\"testString\" class=\"java.lang.String\"/>"));
	}

	/**
	 * Tests the auto-wiring of everything.
	 * 
	 * @throws IOException
	 *             if the test xml-file cannot be found
	 */
	@Test
	public void testAutowiring() throws IOException {
		final String xmlSelector = "net/meisen/general/sbconfigurator/config/transformer/testSample.xml";
		final InputStream xsltStream = new ClassPathResource(
				"net/meisen/general/sbconfigurator/config/transformer/testSampleWithInclude.xslt")
				.getInputStream();

		final DefaultListableBeanFactory factory = configuration
				.loadBeanFactory(xmlSelector, xsltStream, null, false, true,
						true, false);
		assertNotNull(factory.getBean("testString"));
	}
}
