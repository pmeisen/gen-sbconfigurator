package net.meisen.general.sbconfigurator.factories;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import net.meisen.general.genmisc.types.Dates;

/**
 * A {@code PropertyEditor} used to parse strings to dates.
 * 
 * @see Date
 * 
 * @author pmeisen
 * 
 */
public class DatePropertyEditor extends PropertyEditorSupport {

	private static final String[] PATTERNS = new String[] { null,
			"dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy", "yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy",
			"yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd" };

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		if (text == null) {
			setValue(null);
		} else if ("".equals(text)) {
			setValue(new Date());
		} else {

			// we have to validate the format of the String
			final SimpleDateFormat formatter = new SimpleDateFormat();
			for (final String pattern : PATTERNS) {
				final Date parsedDate = tryParse(formatter, pattern, text);
				if (parsedDate != null) {
					setValue(parsedDate);
					return;
				}
			}

			throw new IllegalArgumentException("Unable to parse the text '"
					+ text + "' to a valid date. Please use a valid format: "
					+ Arrays.asList(PATTERNS));
		}
	}

	private Date tryParse(final SimpleDateFormat formatter,
			final String pattern, final String text) {

		if (pattern != null) {
			try {
				formatter.applyPattern(pattern);
			} catch (final IllegalArgumentException e) {
				return null;
			}
		}

		try {
			return formatter.parse(text);
		} catch (final ParseException e) {
			return null;
		}
	}

	@Override
	public String getAsText() {
		final Date value = (Date) getValue();
		final String format = "";

		return Dates.createStringFromDate(value, format);
	}
}
