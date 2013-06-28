package net.meisen.general.sbconfigurator.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.text.ParseException;

import net.meisen.general.genmisc.types.Dates;

import org.junit.Test;

/**
 * Tests the implementation of the <code>StringParser</code>
 * 
 * @author pmeisen
 * 
 */
public class TestStringParser {

	/**
	 * Tests the parsing of <code>null</code>
	 */
	@Test
	public void testNull() {
		final StringParser parser = new StringParser();
		assertNull(parser.parseString(null));
	}

	/**
	 * Tests the parsing of the empty string
	 */
	@Test
	public void testEmpty() {
		final StringParser parser = new StringParser();
		assertEquals("", parser.parseString(""));
	}

	/**
	 * Tests the parsing of a general string
	 */
	@Test
	public void testString() {
		final StringParser parser = new StringParser();
		assertEquals("This is a value", parser.parseString("This is a value"));
	}

	/**
	 * Tests the parsing of booleans
	 */
	@Test
	public void testBooleans() {
		final StringParser parser = new StringParser();

		assertEquals(true, parser.parseString("true"));
		assertEquals(false, parser.parseString("false"));
	}

	/**
	 * Tests the parsing of numbers
	 */
	@Test
	public void testNumbers() {
		final StringParser parser = new StringParser();

		assertEquals(-1000, parser.parseString("-1000"));
		assertEquals(0, parser.parseString("0"));
		assertEquals(1, parser.parseString("1"));
		assertEquals(5, parser.parseString("5"));
		assertEquals(5.56775, parser.parseString("5.56775"));
		assertEquals(new BigDecimal("5.58092809320973209170298782018307258092809"),
				parser.parseString("5.58092809320973209170298782018307258092809"));
	}

	/**
	 * Tests the parsing of date
	 * 
	 * @throws ParseException
	 *           if the generated test date is invalid
	 */
	@Test
	public void testDate() throws ParseException {
		final StringParser parser = new StringParser("dd.MM.yyyy");
		assertEquals(Dates.createDateFromString("20.01.1981", "dd.MM.yyyy"),
				parser.parseString("20.01.1981"));

		parser.setDateFormat("dd.MM.yyyy HH:mm:ss");
		assertEquals(Dates.createDateFromString("20.01.1981", "dd.MM.yyyy"),
				parser.parseString("20.01.1981 00:00:00"));
	}
}
