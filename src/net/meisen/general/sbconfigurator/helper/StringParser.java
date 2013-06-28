package net.meisen.general.sbconfigurator.helper;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.xml.sax.InputSource;

/**
 * Helper method to parse strings without knowing the type of the string.
 * 
 * @author pmeisen
 * 
 */
public class StringParser {
	private Map<Class<?>, PropertyEditor> editors = new LinkedHashMap<Class<?>, PropertyEditor>();
	private Map<Class<?>, PropertyEditor> extEditors = new LinkedHashMap<Class<?>, PropertyEditor>();

	private DateFormat dateFormat;

	/**
	 * Default constructor which registers the <code>PropertyEditor</code>
	 * instances, the default and extensions.
	 */
	public StringParser() {
		setDefaultEditors();
		setExtendedEditors();
	}

	/**
	 * Constructor to specify a <code>dateFormat</code> as well.
	 * 
	 * @param dateFormat
	 *          the format to be used
	 */
	public StringParser(final String dateFormat) {
		this();

		setDateFormat(dateFormat);
	}

	/**
	 * Parses the string to the next most common data-type, i.e. <code>true</code>
	 * is parsed to a boolean, <code>5</code> to an integer, <code>5.5</code> to
	 * double, etc.
	 * 
	 * @param input
	 *          the string to be parsed
	 * 
	 * @return the result, can only be <code>null</code> if the <code>input</code>
	 *         was <code>null</code>
	 */
	public Object parseString(final String input) {

		if (input == null) {
			return null;
		} else if ("".equals(input)) {
			return input;
		} else {

			for (final PropertyEditor editor : editors.values()) {
				final Object value = parse(editor, input);
				if (value != null) {
					return value;
				}
			}
		}

		// so far means, we have to use input
		return input;
	}

	/**
	 * Parses a string to the specified <code>clazz</code>.
	 * 
	 * @param clazz
	 *          the type to parse the string to
	 * @param input
	 *          the string to be parsed, should never be <code>null</code>,
	 *          because <code>null</code> is only returned if no parsing was
	 *          possible
	 * 
	 * @return <code>null</code> if the parsing failed, otherwise the
	 *         <code>input</code> parsed to the specified <code>clazz</code>
	 */
	public Object parseString(final Class<?> clazz, final String input) {

		// that makes sense...
		if (String.class.equals(clazz)) {
			return input;
		}

		// otherwise look for the editor to use
		PropertyEditor editor = editors.get(clazz);

		// if there isn't a default let's check the extensions
		if (editor == null) {
			editor = extEditors.get(clazz);
		}

		// there is no editor so we return null as error
		if (editor == null) {
			return null;
		} else {
			return parse(editor, input);
		}
	}

	/**
	 * Internal helper method to parse the string with the specified
	 * <code>editor</code>.
	 * 
	 * @param editor
	 *          the editor used to parse the string
	 * @param input
	 *          the string to be parsed
	 * 
	 * @return <code>null</code> if the editor could not parse the string,
	 *         otherwise the parsed value
	 */
	protected Object parse(final PropertyEditor editor, final String input) {
		try {
			editor.setAsText(input);
		} catch (final Exception e) {
			return null;
		}

		// get the value and if the textual representation matches, return it
		final Object value = editor.getValue();
		if (input.equals(editor.getAsText())) {
			return value;
		}

		return null;
	}

	/**
	 * Set the extended <code>PropertyEditor</code> instances, which are only used
	 * if explicitly called (i.e. {@link #parseString(Class, String)}.
	 */
	protected void setExtendedEditors() {
		extEditors.put(InputStream.class, new InputStreamEditor());
		extEditors.put(InputSource.class, new InputSourceEditor());

		extEditors.put(Class[].class, new ClassArrayEditor());

		extEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		extEditors.put(TimeZone.class, new TimeZoneEditor());

		extEditors.put(byte[].class, new ByteArrayPropertyEditor());
		extEditors.put(char[].class, new CharArrayPropertyEditor());

		extEditors.put(Properties.class, new PropertiesEditor());
		extEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		extEditors
				.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		extEditors.put(List.class, new CustomCollectionEditor(List.class));
		extEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
		extEditors.put(Collection.class, new CustomCollectionEditor(
				Collection.class));

		extEditors.put(char.class, new CharacterEditor(false));
		extEditors.put(Character.class, new CharacterEditor(true));

		extEditors.put(boolean.class, new CustomBooleanEditor(false));
		extEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		extEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		extEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		extEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		extEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		extEditors.put(double.class, new CustomNumberEditor(Double.class, false));

		extEditors.put(Charset.class, new CharsetEditor());
		extEditors.put(Currency.class, new CurrencyEditor());
		extEditors.put(Class.class, new ClassEditor());
		extEditors.put(URL.class, new URLEditor());
		extEditors.put(UUID.class, new UUIDEditor());

		extEditors.put(Pattern.class, new PatternEditor());
		extEditors.put(URI.class, new URIEditor());
		extEditors.put(File.class, new FileEditor());

		extEditors.put(Locale.class, new LocaleEditor());

		extEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		extEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
		extEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
	}

	/**
	 * Set the default <code>PropertyEditor</code> instances, which are used to
	 * parse a string, even without any known type.
	 */
	protected void setDefaultEditors() {
		editors.put(Boolean.class, new CustomBooleanEditor(true));
		editors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		editors.put(Long.class, new CustomNumberEditor(Long.class, true));
		editors.put(Double.class, new CustomNumberEditor(Double.class, true));
		editors.put(BigInteger.class,
				new CustomNumberEditor(BigInteger.class, true));
		editors.put(BigDecimal.class,
				new CustomNumberEditor(BigDecimal.class, true));
		editors.put(Date.class, new CustomDateEditor(getDateFormat(), true));
	}

	/**
	 * Gets the currently used <code>DateFormat</code>.
	 * 
	 * @return the currently used <code>DateFormat</code>
	 */
	public DateFormat getDateFormat() {
		if (this.dateFormat == null) {
			return new SimpleDateFormat();
		} else {
			return dateFormat;
		}
	}

	/**
	 * Sets the <code>dateFormat</code> to be used when parsing a
	 * <code>Date</code>.
	 * 
	 * @param dateFormat
	 *          the format to be used
	 * 
	 * @see Date
	 * @see SimpleDateFormat
	 */
	public void setDateFormat(final String dateFormat) {
		setDateFormat(new SimpleDateFormat(dateFormat));
	}

	/**
	 * Sets the <code>dateFormat</code> to be used when parsing a
	 * <code>Date</code>.
	 * 
	 * @param dateFormat
	 *          the format to be used
	 * 
	 * @see Date
	 * @see SimpleDateFormat
	 */
	public void setDateFormat(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;

		// update the editor if there is one
		final PropertyEditor editor = editors.get(Date.class);
		if (editor != null && editor instanceof CustomDateEditor) {
			editors.put(Date.class, new CustomDateEditor(getDateFormat(), true));
		}
	}
}
