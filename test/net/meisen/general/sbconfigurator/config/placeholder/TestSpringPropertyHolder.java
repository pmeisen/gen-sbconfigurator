package net.meisen.general.sbconfigurator.config.placeholder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of the <code>SpringPropertyHolder</code>.
 * 
 * @author pmeisen
 * 
 */
public class TestSpringPropertyHolder {

	/**
	 * Helper method to load the property bean of the specified
	 * <code>xmlFile</code>.
	 * 
	 * @param xmlFile
	 *          the <code>xmlFile</code> to load the
	 *          <code>SpringPropertyHolder</code> from
	 * 
	 * @return the read <code>Properties</code> from the
	 *         <code>SpringPropertyHolder</code>
	 */
	protected Properties loadPropertyBean(final String xmlFile) {
		return loadPropertyBean(xmlFile, null);
	}

	/**
	 * Helper method to load the property bean with the specified <code>id</code>
	 * of the specified <code>xmlFile</code>.
	 * 
	 * @param xmlFile
	 *          the <code>xmlFile</code> to load the
	 *          <code>SpringPropertyHolder</code> from
	 * @param id
	 *          the id of the bean to be loaded, can be <code>null</code> if no
	 *          specific <code>SpringPropertyHolder</code> should be used
	 * 
	 * @return the read <code>Properties</code> from the
	 *         <code>SpringPropertyHolder</code> with the specified
	 *         <code>id</code>
	 */
	protected Properties loadPropertyBean(final String xmlFile, final String id) {
		final SpringPropertyHolder springProperties = getSpringPropertyHolder(
				xmlFile, id);

		// get the properties
		try {
			return springProperties.getProperties();
		} catch (final IOException e) {
			throw new IllegalStateException("Invalid definition for properties.", e);
		}
	}

	/**
	 * Helper method to retrieve the bean which holds the properties.
	 * 
	 * @param xmlFile
	 *          the <code>xmlFile</code> to load the
	 *          <code>SpringPropertyHolder</code> from
	 * @param id
	 *          the id of the bean to be loaded, can be <code>null</code> if no
	 *          specific <code>SpringPropertyHolder</code> should be used
	 * 
	 * @return the loaded <code>SpringPropertyHolder</code>
	 */
	protected SpringPropertyHolder getSpringPropertyHolder(final String xmlFile,
			final String id) {
		// factory for the beans
		final DefaultListableBeanFactory factory = SpringHelper.createBeanFactory(
				true, false);

		// create the reader
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

		// load the xml definition
		final Resource res = new ClassPathResource("propertyBeans/" + xmlFile,
				getClass());
		reader.loadBeanDefinitions(res);

		// get the bean
		return id == null ? factory.getBean(SpringPropertyHolder.class)
				: (SpringPropertyHolder) factory.getBean(id);
	}

	/**
	 * Loads the <code>localPropertiesByValues.xml</code> and checks the result.
	 */
	@Test
	public void testLocalPropertiesByValue() {
		final Properties properties = loadPropertyBean("localPropertiesByValues.xml");
		assertEquals(2, properties.size());
		assertEquals("1st-value1", properties.get("testValue1"));
		assertEquals("1st-value2", properties.get("testValue2"));
	}

	/**
	 * Loads the <code>localPropertiesAsArray.xml</code> and checks the result.
	 */
	@Test
	public void testLocalPropertiesAsArray() {
		final Properties properties = loadPropertyBean("localPropertiesAsArray.xml");
		assertEquals(4, properties.size());
		assertEquals("1st-value1", properties.get("first.testValue1"));
		assertEquals("1st-value2", properties.get("first.testValue2"));
		assertEquals("2nd-value1", properties.get("second.testValue1"));
		assertEquals("2nd-value2", properties.get("second.testValue2"));
	}

	/**
	 * Loads the <code>propertyFileByLocation.xml</code> and checks the result.
	 */
	@Test
	public void testPropertyFileByLocation() {
		final Properties properties = loadPropertyBean("propertyFileByLocation.xml");
		assertEquals(3, properties.size());
		assertEquals("sample1", properties.get("location.property.value1"));
		assertEquals("sample2", properties.get("location.property.value2"));
		assertEquals("sample3,sample4", properties.get("location.property.value3"));
	}

	/**
	 * Loads the <code>propertyFilesByLocations.xml</code> and checks the result.
	 */
	@Test
	public void testPropertyFilesByLocations() {
		final Properties properties = loadPropertyBean("propertyFilesByLocations.xml");
		assertEquals(7, properties.size());
		assertEquals("sample1", properties.get("test.properties.value1"));
		assertEquals("sample2", properties.get("test.properties.value2"));
		assertEquals("sample3", properties.get("test.properties.value3"));
		assertEquals("anothersample1", properties.get("test.properties.newvalue1"));
		assertEquals("anothersample2", properties.get("test.properties.newvalue2"));
		assertEquals("anothersample3", properties.get("test.properties.newvalue3"));
		assertEquals("anothersample4", properties.get("test.properties.value4"));
	}

	/**
	 * Loads the <code>propertyFilesBySelector.xml</code> and checks the result.
	 */
	@Test
	public void testPropertyFileBySelector() {
		final Properties properties = loadPropertyBean("propertyFilesBySelector.xml");
		assertEquals("value1", properties.get("simpleselector.value1"));
		assertEquals("value2", properties.get("simpleselector.value2"));
		assertEquals("value3", properties.get("simpleselector.value3"));
	}

	/**
	 * Loads the <code>propertyFilesBySelectors.xml</code> and checks the result.
	 */
	@Test
	public void testPropertyFileBySelectors() {
		final Properties properties = loadPropertyBean("propertyFilesBySelectors.xml");
		assertEquals("value0", properties.get("multiselector.value1"));
		assertEquals("value2", properties.get("multiselector.value2"));
		assertEquals("value3", properties.get("multiselector.value3"));
		assertEquals("value4", properties.get("multiselector.value4"));
		assertEquals("value5", properties.get("multiselector.value5"));
	}

	/**
	 * Loads the <code>otherPropertyHolderNoOverride.xml</code> and checks the
	 * result of the bean 'mainPropertyHolder' bean.
	 */
	@Test
	public void testOtherPropertyHolderNoOverride() {
		final Properties properties = loadPropertyBean(
				"otherPropertyHolderNoOverride.xml", "mainPropertyHolder");
		assertEquals(4, properties.size());
		assertEquals("main", properties.get("global.testValue"));
		assertEquals("notmain_value1", properties.get("noname.testValue1"));
		assertEquals("notmain_value2", properties.get("noname.testValue2"));
		assertEquals("main_value1", properties.get("main.testValue1"));
	}

	/**
	 * Loads the <code>otherPropertyHolderOverride.xml</code> and checks the
	 * result of the bean 'mainPropertyHolder' bean.
	 */
	@Test
	public void testOtherPropertyHolderOverride() {
		final Properties properties = loadPropertyBean(
				"otherPropertyHolderOverride.xml", "mainPropertyHolder");
		assertEquals(4, properties.size());
		assertEquals("notmain", properties.get("global.testValue"));
		assertEquals("notmain_value1", properties.get("noname.testValue1"));
		assertEquals("notmain_value2", properties.get("noname.testValue2"));
		assertEquals("main_value1", properties.get("main.testValue1"));
	}

	/**
	 * Loads the <code>propertyFullExample.xml</code> and checks the result.
	 */
	@Test
	public void testFullExample() {
		final Properties properties = loadPropertyBean("propertyFullExample.xml");
		assertEquals(3, properties.size());
		assertEquals("local-value1", properties.get("testValue1"));
		assertEquals("local-value2", properties.get("testValue2"));
		assertEquals("file-value3", properties.get("testValue3"));
	}

	/**
	 * Loads the <code>propertyFullExampleNoOverride.xml</code> and checks the
	 * result.
	 */
	@Test
	public void testFullExampleNoOverride() {
		final Properties properties = loadPropertyBean("propertyFullExampleNoOverride.xml");
		assertEquals(3, properties.size());
		assertEquals("file-value1", properties.get("testValue1"));
		assertEquals("local-value2", properties.get("testValue2"));
		assertEquals("file-value3", properties.get("testValue3"));
	}

	/**
	 * Tests to read the properties retrieved by a selector several times. This
	 * test-case was added because of a bug-report: <br/>
	 * Accessing properties retrieved by selectors several times failed, because
	 * the InputStream can only be read once.
	 * 
	 * @throws IOException
	 *           if the properties cannot be read the first time
	 */
	@Test
	public void testSeveralLoadingOfPropertiesFromSelector() throws IOException {
		final SpringPropertyHolder holder = getSpringPropertyHolder(
				"propertyFilesBySelectors.xml", null);

		// the first time we should be able to access anyways (already tested)
		holder.getProperties();

		// the properties should be readable a second time
		try {
			holder.getProperties();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Could not access the InputStream several times ('" + e.getMessage()
					+ "')");
		}
	}
}
