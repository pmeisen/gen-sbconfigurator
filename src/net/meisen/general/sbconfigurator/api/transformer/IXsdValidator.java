package net.meisen.general.sbconfigurator.api.transformer;

import java.io.InputStream;

import net.meisen.general.sbconfigurator.config.exception.InvalidXsdException;
import net.meisen.general.sbconfigurator.config.exception.ValidationFailedException;

/**
 * The interface defines a validator used to validate an XML-document using XSD.
 * 
 * @author pmeisen
 * 
 */
public interface IXsdValidator {

	/**
	 * This method is used to set the schema to be used for the validation. It
	 * reads the schema from the passed <code>xsdStream</code>.
	 * 
	 * <br/>
	 * <br/>
	 * <i>Note:</i><br/>
	 * The <code>InputStream</code> might be <b>closed</b> after the schema is
	 * set!
	 * 
	 * @param xsdStream
	 *          the <code>InputStream</code> to read the schema from
	 * 
	 * @throws InvalidXsdException
	 *           if the schema definition is invalid, the <code>xsdStream</code>
	 *           cannot be read, ...
	 * 
	 * @see #resetSchema()
	 */
	public void setXsdSchema(final InputStream xsdStream)
			throws InvalidXsdException;

	/**
	 * Validates the passed <code>xmlStream</code> against the currently defined
	 * schema or the defined schema of the XML (see {@link #setXsdSchema}).
	 * 
	 * @param xmlStream
	 *          the <code>InputStream</code> to read the XML from
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed, e.g. the <code>xmlStream</code> is
	 *           invalid, the XML is invalid against the defined XSD, ...
	 */
	public void validate(final InputStream xmlStream)
			throws ValidationFailedException;

	/**
	 * Reset schema is used to disable any set schema (i.e. XSD). Use this method
	 * to validate the XML against the internal schema (i.e. the one defined in
	 * the XML).
	 * 
	 * @see #setXsdSchema(InputStream)
	 */
	public void resetSchema();
}
