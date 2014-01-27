package net.meisen.general.sbconfigurator.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.beans.PropertyEditor;
import java.text.ParseException;
import java.util.Date;

import net.meisen.general.genmisc.types.Dates;
import net.meisen.general.sbconfigurator.factories.mocks.DateConstructor;
import net.meisen.general.sbconfigurator.factories.mocks.DateProperty;
import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of a {@link PropertyEditor} which is used to
 * retrieve dates from strings.
 * 
 * @author pmeisen
 * 
 */
public class TestDatePropertyEditor {
	private static DefaultListableBeanFactory factory;

	/**
	 * Initialize the tests beans via Spring.
	 */
	@BeforeClass
	public static void init() {

		// create the factory with auto-wiring
		factory = SpringHelper.createBeanFactory(true, true);

		// create the reader and load the Xml into the Factory
		final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
				factory);
		final Resource testXml = new ClassPathResource(
				"/net/meisen/general/sbconfigurator/factories/dateValue.xml");
		reader.loadBeanDefinitions(testXml);
	}

	/**
	 * Tests some different date formates.
	 * 
	 * @throws ParseException
	 *             if a date cannot be parsed
	 */
	@Test
	public void testConversion() throws ParseException {
		DatePropertyEditor propertyEditor;

		// tests null
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText(null);
		assertNull(propertyEditor.getValue());

		// tests "" with 500ms delta
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("");
		assertEquals(new Date().getTime(),
				((Date) propertyEditor.getValue()).getTime(), 500);

		// tests a simple German date
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("20.01.1981");
		assertEquals(Dates.createDateFromString("20.01.1981", "dd.MM.yyyy"),
				propertyEditor.getValue());

		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("20.1.1981");
		assertEquals(Dates.createDateFromString("20.1.1981", "dd.MM.yyyy"),
				propertyEditor.getValue());

		// tests a full qualified German time
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("10.05.2012 10:00:00");
		assertEquals(Dates.createDateFromString("10.05.2012 10:00:00",
				"dd.MM.yyyy HH:mm:ss"), propertyEditor.getValue());

		// tests a US date
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("22/08/2013");
		assertEquals(Dates.createDateFromString("22.08.2013", "dd.MM.yyyy"),
				propertyEditor.getValue());

		// tests a full qualified US time
		propertyEditor = new DatePropertyEditor();
		propertyEditor.setAsText("22/08/2013 12:22:16");
		assertEquals(Dates.createDateFromString("22.08.2013 12:22:16",
				"dd.MM.yyyy HH:mm:ss"), propertyEditor.getValue());
	}

	/**
	 * Tests the implementation of {@code DatePropertyEditor#setAsText(String)}.
	 * 
	 * @throws ParseException
	 *             if the date cannot be parsed
	 */
	@Test
	public void testDateProperty() throws ParseException {
		final DateProperty dateProperty = (DateProperty) factory
				.getBean("dateProperty");
		assertEquals(Dates.createDateFromString("2010-02-13", "yyyy-MM-dd"),
				dateProperty.getDate());
	}

	/**
	 * Tests the implementation of {@code DatePropertyEditor#setAsText(String)}.
	 * 
	 * @throws ParseException
	 *             if the date cannot be parsed
	 */
	@Test
	public void testDateConstructor() throws ParseException {
		final DateConstructor dateConstructor = (DateConstructor) factory
				.getBean("dateConstructor");
		assertEquals(Dates.createDateFromString("2010-02-13", "yyyy-MM-dd"),
				dateConstructor.getDate());
	}
}
