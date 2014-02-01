package net.meisen.general.sbconfigurator.factories;

import java.beans.PropertyEditorSupport;
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

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		if (text == null) {
			setValue(null);
		} else if ("".equals(text)) {
			setValue(new Date());
		} else {
			final Date parsedDate = Dates.isDate(text);
			if (parsedDate == null) {
				throw new IllegalArgumentException("Unable to parse the text '"
						+ text
						+ "' to a valid date. Please use a valid format: "
						+ Arrays.asList(Dates.PATTERNS));
			} else {
				setValue(parsedDate);
			}
		}
	}

	@Override
	public String getAsText() {
		final Date value = (Date) getValue();
		final String format = "";

		return Dates.createStringFromDate(value, format);
	}
}
