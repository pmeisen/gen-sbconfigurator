package net.meisen.general.sbconfigurator.config.placeholder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests the implementation ofthe default <code>XmlPropertyReplacer</code>.
 * 
 * @author pmeisen
 * 
 */
public class TestDefaultXmlPropertyReplacer {

	private XPath xp = XPathFactory.newInstance().newXPath();

	/**
	 * Tests the replacement of properties in comments (which should not happen)
	 * 
	 * @throws XPathExpressionException
	 *           if the specified xpath is invalid
	 */
	@Test
	public void testCommentReplacement() throws XPathExpressionException {

		// define the properties for the test
		final Properties properties = new Properties();
		properties.setProperty("prop.name", "propertyName");

		// write the documents content
		String xml = "";
		xml += "<root>";
		xml += "<!-- This is a comment with a property ${prop.name} -->";
		xml += "<subelement>";
		xml += "</subelement>";
		xml += "</root>";

		// define the where to look
		final String xPath[] = { "/root/comment()" };

		// define the expected result
		final String expected[] = { "This is a comment with a property ${prop.name}" };

		// do the test
		testXmlReplacer(xml, xPath, expected, properties);
	}

	/**
	 * Tests the replacement of properties within text.
	 * 
	 * @throws XPathExpressionException
	 *           if the specified xpath is invalid
	 */
	@Test
	public void testTextReplacement() throws XPathExpressionException {

		// define the properties for the test
		final Properties properties = new Properties();
		properties.setProperty("prop.name", "propertyName");

		// write the documents content
		String xml = "";
		xml += "<root>";
		xml += "<subelement>";
		xml += "This is a text with '${prop.name}'";
		xml += "<inlineElement value=\"value\" />";
		xml += "This is another text with '${prop.name}'";
		xml += "</subelement>";
		xml += "</root>";

		// define the where to look
		final String xPath[] = { "/root/subelement/text()[1]",
				"/root/subelement/text()[2]" };

		// define the expected result
		final String expected[] = { "This is a text with 'propertyName'",
				"This is another text with 'propertyName'" };

		// do the test
		testXmlReplacer(xml, xPath, expected, properties);
	}

	/**
	 * Tests the replacement of properties within attributes.
	 * 
	 * @throws XPathExpressionException
	 *           if the specified xpath is invalid
	 */
	@Test
	public void testAttributeReplacement() throws XPathExpressionException {

		// define the properties for the test
		final Properties properties = new Properties();
		properties.setProperty("prop.name", "propertyName");
		properties.setProperty("prop.value", "propertyValue");

		// write the documents content
		String xml = "";
		xml += "<root sample=\"${prop.name}\">";
		xml += "<subelement attribute=\"${prop.value}\" />";
		xml += "</root>";

		// define the where to look
		final String xPath[] = { "/root/@sample", "/root/subelement/@attribute" };

		// define the expected result
		final String expected[] = { "propertyName", "propertyValue" };

		// do the test
		testXmlReplacer(xml, xPath, expected, properties);
	}

	/**
	 * Tests the replacement of properties within CDATA elements.
	 * 
	 * @throws XPathExpressionException
	 *           if the specified xpath is invalid
	 */
	@Test
	public void testCDATAReplacement() throws XPathExpressionException {

		// define the properties for the test
		final Properties properties = new Properties();
		properties.setProperty("prop.name", "propertyName");

		// write the documents content
		String xml = "";
		xml += "<root>";
		xml += "<![CDATA[";
		xml += "The same property will be used within this text twice. Once here '${prop.name}' and here '${prop.name}'.";
		xml += "]]>";
		xml += "</root>";

		// define the where to look
		final String xPath = "/root/text()";

		// define the expected result
		final String expected = "The same property will be used within this text twice. Once here 'propertyName' and here 'propertyName'.";

		// do the test
		testXmlReplacer(xml, xPath, expected, properties);
	}

	/**
	 * Tests the replacement of nested properties.
	 * 
	 * @throws XPathExpressionException
	 *           if the specified xpath is invalid
	 */
	@Test
	public void testNestedReplacement() throws XPathExpressionException {

		// define the properties for the test
		final Properties properties = new Properties();
		properties.setProperty("prop.name", "${prop.value}");
		properties.setProperty("prop.final", "propertyFinal");
		properties.setProperty("prop.value", "${prop.final}");

		// write the documents content
		String xml = "";
		xml += "<root>";
		xml += "<![CDATA[";
		xml += "The same property will be used within this text twice. Once here '${prop.name}' and here '${prop.value}'.";
		xml += "]]>";
		xml += "</root>";

		// define the where to look
		final String xPath = "/root/text()";

		// define the expected result
		final String expected = "The same property will be used within this text twice. Once here 'propertyFinal' and here 'propertyFinal'.";

		// do the test
		testXmlReplacer(xml, xPath, expected, properties);
	}

	/**
	 * Helper method to replace the passed <code>properties</code> within the
	 * passed <code>xml</code> and check the <code>expectations</code> against the
	 * specified <code>xPath</code>-expressions.
	 * 
	 * @param xml
	 *          the xml to replace the properties in
	 * @param xPaths
	 *          the expressions to look for the expected results at
	 * @param expectations
	 *          the expected results for the passed <code>xPaths</code> (compared
	 *          by index)
	 * @param properties
	 *          the properties to replace
	 * 
	 * @throws XPathExpressionException
	 *           if one of the specified xpath is invalid
	 */
	protected void testXmlReplacer(final String xml, final String[] xPaths,
			final String[] expectations, final Properties properties)
			throws XPathExpressionException {
		final DefaultXmlPropertyReplacer testSubject = new DefaultXmlPropertyReplacer();

		// just use the Spring replacer it should work as normal replacement
		testSubject.setReplacer(new SpringPropertyReplacer());

		// let's create the document
		final Document doc = createDoc(xml);
		final Document replacedDoc = testSubject.replacePlaceholders(doc,
				properties);

		for (int i = 0; i < xPaths.length; i++) {
			final String xPath = xPaths[i];
			final String expected = expectations[i];

			final String result = xp.evaluate(xPath, replacedDoc);
			assertEquals(result.trim(), expected);
		}
	}

	/**
	 * Helper method to replace the passed <code>properties</code> within the
	 * passed <code>xml</code> and check the <code>expectation</code> against the
	 * specified <code>xPath</code>-expressions.
	 * 
	 * @param xml
	 *          the xml to replace the properties in
	 * @param xPath
	 *          the expression to look for the expected result at
	 * @param expected
	 *          the expected result for the passed <code>xPath</code>
	 * @param properties
	 *          the properties to replace
	 * 
	 * @throws XPathExpressionException
	 *           if one of the specified xpath is invalid
	 */
	public void testXmlReplacer(final String xml, final String xPath,
			final String expected, final Properties properties)
			throws XPathExpressionException {
		testXmlReplacer(xml, new String[] { xPath }, new String[] { expected },
				properties);
	}

	/**
	 * Helper method to create a <code>Document</code> based on the passed
	 * <code>xml</code> fragment.
	 * 
	 * @param xml
	 *          the xml of the document to be created
	 * 
	 * @return the created <code>Document</code>
	 */
	public static Document createDoc(final String xml) {
		final String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + xml;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();

			return builder.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			fail("Invalid Xml: " + System.getProperty("line.separator") + xml);

			// never reached but has to be done
			return null;
		}
	}
}
