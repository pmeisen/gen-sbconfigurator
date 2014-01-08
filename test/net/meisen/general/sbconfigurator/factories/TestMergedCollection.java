package net.meisen.general.sbconfigurator.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import net.meisen.general.sbconfigurator.helper.SpringHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests the implementation of {@code MergedCollection}.
 * 
 * @author pmeisen
 * 
 */
public class TestMergedCollection {
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
				"/net/meisen/general/sbconfigurator/factories/mergedCollection.xml");
		reader.loadBeanDefinitions(testXml);
	}

	/**
	 * Tests a simple merge
	 */
	@Test
	public void testSimpleMerge() {
		final Object oSubject = factory.getBean("simpleMergedCollection");
		assertTrue(oSubject instanceof ArrayList);

		// get the list
		@SuppressWarnings("unchecked")
		final ArrayList<Object> subject = (ArrayList<Object>) oSubject;
		assertEquals(5, subject.size());

		assertTrue(subject.contains(1));
		assertTrue(subject.contains(2));
		assertTrue(subject.contains("VALUE1"));
		assertTrue(subject.contains("VALUE2"));
		assertTrue(subject.contains("VALUE3"));
	}

	/**
	 * Tests a merge with a defined type of the collection to be created.
	 */
	@Test
	public void testDefinedTypeOfMergedCollection() {
		final Object oSubject = factory
				.getBean("definedTypeOfMergedCollection");
		assertTrue(oSubject instanceof HashSet);

		// get the list
		@SuppressWarnings("unchecked")
		final HashSet<Object> subject = (HashSet<Object>) oSubject;
		assertEquals(4, subject.size());

		assertTrue(subject.contains(1));
		assertTrue(subject.contains(2));
		assertTrue(subject.contains("VALUE1"));
		assertTrue(subject.contains("VALUE2"));
	}

	/**
	 * Tests the usage if the collections are defined as {@code null}.
	 */
	@Test
	public void testNullCollections() {
		final Object oSubject = factory.getBean("nullCollections");
		assertTrue(oSubject instanceof TreeSet);

		// get the list
		@SuppressWarnings("unchecked")
		final TreeSet<Object> subject = (TreeSet<Object>) oSubject;
		assertEquals(0, subject.size());
	}

	/**
	 * Tests if only {@code null} values are added as collections.
	 */
	@Test
	public void testOnlyNullCollectionsMerged() {
		final Object oSubject = factory.getBean("onlyNullMergedCollection");
		assertTrue(oSubject instanceof TreeSet);

		// get the list
		@SuppressWarnings("unchecked")
		final TreeSet<Object> subject = (TreeSet<Object>) oSubject;
		assertEquals(0, subject.size());
	}

	/**
	 * Tests if some {@code null} values are defined.
	 */
	@Test
	public void testWithNullCollectionsMerged() {
		final Object oSubject = factory.getBean("nullMergedCollection");
		assertTrue(oSubject instanceof TreeSet);

		// get the list
		@SuppressWarnings("unchecked")
		final TreeSet<Object> subject = (TreeSet<Object>) oSubject;
		assertEquals(2, subject.size());

		assertTrue(subject.contains(1));
		assertTrue(subject.contains(2));
	}

	/**
	 * CleanUp
	 */
	@AfterClass
	public static void tearDown() {
		factory.destroySingletons();
	}
}
