package net.meisen.general.sbconfigurator.config.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;
import net.meisen.general.sbconfigurator.config.transformer.DefaultXsltTransformer;

import org.junit.Test;

/**
 * Tests the implementation of the <code>DefaultXsltTransformer</code>.
 * 
 * @author pmeisen
 * 
 */
public class TestDefaultXsltTransformer {
	private final static String classPathToThis = TestDefaultXsltTransformer.class
			.getPackage().getName().replace(".", "/");

	// the xsltTransformer used for testing
	private final DefaultXsltTransformer xsltTransformer;
	private ByteArrayOutputStream outputStream = null;

	/**
	 * Creates the <code>DefaultXsltTransformer</code> which is used for testing
	 * purposes.
	 * 
	 * @throws InvalidXsltException
	 *           if the XSLT was invalid
	 */
	public TestDefaultXsltTransformer() throws InvalidXsltException {
		xsltTransformer = new DefaultXsltTransformer();

		// set the Xslt transformer
		xsltTransformer
				.setXsltTransformer("net/meisen/general/sbconfigurator/config/transformer/sbconfigurator-loaderToSpringContext.xslt");
	}

	/**
	 * Tests the transformation of an empty loaderDefinition file.
	 * 
	 * @throws TransformationFailedException
	 *           if the transformation failed
	 */
	@Test
	public void testLoaderEmptyTransformation()
			throws TransformationFailedException {

		// transform it
		xsltTransformer.transformFromClasspath(classPathToThis
				+ "/loaderDefinitionEmpty.xml", openStream());

		final String output = closeStream();
		final String[] lines = output.split("\n");

		assertEquals(lines.length, 2);
		assertEquals(lines[0].trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		assertEquals(
				lines[1].trim(),
				"<beans xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sbc=\"http://dev.meisen.net/sbconfigurator\" xmlns=\"http://www.springframework.org/schema/beans\"/>");
	}

	/**
	 * Tests the transformation of a single loaderDefinition file.
	 * 
	 * @throws TransformationFailedException
	 *           if the transformation failed
	 */
	@Test
	public void testLoaderSingleTransformation()
			throws TransformationFailedException {

		// transform it
		xsltTransformer.transformFromClasspath(classPathToThis
				+ "/loaderDefinitionSingle.xml", openStream());

		final String output = closeStream();
		final String[] lines = output.split("\n");

		assertEquals(lines.length, 14);
		assertEquals(lines[0].trim(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		assertEquals(
				lines[1].trim(),
				"<beans xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:sbc=\"http://dev.meisen.net/sbconfigurator\" xmlns=\"http://www.springframework.org/schema/beans\">");

		// check the sample1 bean
		assertTrue(lines[2].trim().startsWith("<bean id=\"sample1\" "));
		assertEquals(lines[3].trim(),
				"<property name=\"selector\" value=\"sbconfigurator-sample1.xml\"/>");
		assertEquals(lines[4].trim(), "</bean>");

		// check the sample2 bean
		assertTrue(lines[5].trim().startsWith("<bean id=\"sample2\" "));
		assertEquals(lines[6].trim(),
				"<property name=\"selector\" value=\"sbconfigurator-sample2.xml\"/>");
		assertEquals(lines[7].trim(),
				"<property name=\"beanOverridingAllowed\" value=\"false\"/>");
		assertEquals(lines[8].trim(), "</bean>");

		// check the sample3 bean
		assertTrue(lines[9].trim().startsWith("<bean id=\"sample3\" "));
		assertEquals(lines[10].trim(),
				"<property name=\"selector\" value=\"sbconfigurator-sample3.xml\"/>");
		assertEquals(lines[11].trim(), "<property name=\"xslt\" value=\"myXSLT\"/>");
		assertEquals(lines[12].trim(), "</bean>");

		assertEquals(lines[13].trim(), "</beans>");
	}

	private OutputStream openStream() {
		if (outputStream != null) {
			Streams.closeIO(outputStream);
		}

		// create a new one
		outputStream = new ByteArrayOutputStream();

		return outputStream;
	}

	private String closeStream() {
		if (outputStream != null) {
			Streams.closeIO(outputStream);
		}

		final String content = outputStream.toString();
		outputStream = null;

		return content;
	}
}
