package net.meisen.general.sbconfigurator.config.transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.api.transformer.IXsdValidator;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsdException;
import net.meisen.general.sbconfigurator.config.exception.ValidationFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Default implementation of a <code>IXsdValidator</code>. The default
 * implementation offers the possibility to validate against an
 * <code>InputStream</code>, <code>File</code>, path to a file on the classpath
 * or the filesystem.
 * 
 * @author pmeisen
 * 
 */
public class DefaultXsdValidator implements IXsdValidator {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(DefaultXsdValidator.class);

	private Schema xsdSchema = null;
	private DocumentLoader documentLoader = new DefaultDocumentLoader();
	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(LOGGER);

	/**
	 * Creates a <code>DefaultXsdValidator</code>, which uses the by default the
	 * schema of the XML.
	 * 
	 * @see #setXsdSchema(File)
	 * @see #setXsdSchema(InputStream)
	 * @see #setXsdSchema(String)
	 */
	public DefaultXsdValidator() {
		// nothing to do but offering
	}

	/**
	 * Creates a <code>DefaultXsdValidator</code>, which uses the XSD specified by
	 * it's classpath.
	 * 
	 * @param xsdClassPath
	 *          the classpath to the XSD to be used for validation
	 * 
	 * @throws InvalidXsdException
	 *           if the passed schema cannot be used, e.g. because it's null,
	 *           invalid, cannot be found, ...
	 */
	public DefaultXsdValidator(final String xsdClassPath)
			throws InvalidXsdException {
		setXsdSchema(xsdClassPath);
	}

	/**
	 * Creates a <code>DefaultXsdValidator</code>, which uses the XSD specified by
	 * the passed <code>xsdFile</code>.
	 * 
	 * @param xsdFile
	 *          the <code>File</code> which contains the XSD
	 * 
	 * @throws InvalidXsdException
	 *           if the passed schema cannot be used, e.g. because it's null, the
	 *           <code>File</code> cannot be found or read, ...
	 */
	public DefaultXsdValidator(final File xsdFile) throws InvalidXsdException {
		setXsdSchema(xsdFile);
	}

	/**
	 * Creates a <code>DefaultXsdValidator</code>, which uses the XSD read from
	 * the passed <code>xsdStream</code>.
	 * 
	 * @param xsdStream
	 *          the <code>InputStream</code> to read the XSD from
	 * 
	 * @throws InvalidXsdException
	 *           if the passed schema cannot be used, e.g. because the
	 *           <code>InputStream</code> is closed, not accessible, the schema is
	 *           invalid, ...
	 */
	public DefaultXsdValidator(final InputStream xsdStream)
			throws InvalidXsdException {
		setXsdSchema(xsdStream);
	}

	/**
	 * This method is used to set the schema to be used for the validation. It
	 * reads the schema from the passed <code>xsdClassPath</code>.
	 * 
	 * @param xsdClassPath
	 *          the <code>String</code> pointing to the XSD on the classpath
	 * 
	 * @throws InvalidXsdException
	 *           if the XSD cannot be found on the classpath, the XSD is invalid,
	 *           ...
	 */
	public void setXsdSchema(final String xsdClassPath)
			throws InvalidXsdException {
		try {
			setXsdSchema(new ClassPathResource(xsdClassPath).getInputStream());
		} catch (final IOException e) {
			throw new InvalidXsdException("", e);
		}
	}

	/**
	 * This method is used to set the schema to be used for the validation. It
	 * reads the schema from the passed <code>xsdFile</code>.
	 * 
	 * @param xsdFile
	 *          the <code>File</code> containing the XSD
	 * 
	 * @throws InvalidXsdException
	 *           if the <code>xsdFile</code> cannot be read, the XSD is invalid,
	 *           ...
	 */
	public void setXsdSchema(final File xsdFile) throws InvalidXsdException {
		try {
			setXsdSchema(new FileInputStream(xsdFile));
		} catch (final FileNotFoundException e) {
			throw new InvalidXsdException("", e);
		}
	}

	@Override
	public void setXsdSchema(final InputStream xsdStream)
			throws InvalidXsdException {

		if (xsdStream == null) {
			this.xsdSchema = null;
		} else {

			// create the schemaFactory
			final SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// get the schema and it's validator
			try {
				this.xsdSchema = schemaFactory.newSchema(new StreamSource(xsdStream));
			} catch (final SAXException e) {
				this.xsdSchema = null;
				throw new InvalidXsdException("Invalid schema passed", e);
			}
		}

		// close the stream
		Streams.closeIO(xsdStream);
	}

	@Override
	public void resetSchema() {
		this.xsdSchema = null;
	}

	/**
	 * Validates the specified <code>xmlPath</code> against the current schema.
	 * The <code>xmlPath</code> must be located on the classpath to be found. An
	 * <code>ValidationFailedException</code> is thrown if no validation was
	 * possible.
	 * 
	 * @param xmlPath
	 *          the path on the classpath pointing to the XML file to be validated
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed
	 */
	public void validateFromClasspath(final String xmlPath)
			throws ValidationFailedException {

		final ClassPathResource xmlRes = new ClassPathResource(xmlPath);

		InputStream xmlStream = null;
		try {

			// get the streams for the files
			xmlStream = xmlRes.getInputStream();

			// validate the file
			validate(xmlStream);
		} catch (IOException e) {
			throw new ValidationFailedException("", e);
		} finally {

			// close the stream
			Streams.closeIO(xmlStream);
		}
	}

	/**
	 * Validates the specified <code>xmlFile</code> against the current schema. An
	 * <code>ValidationFailedException</code> is thrown if no validation was
	 * possible.
	 * 
	 * @param xmlFile
	 *          the <code>String</code> pointing to a valid file to be validated
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed
	 */
	public void validate(final String xmlFile) throws ValidationFailedException {
		validate(new File(xmlFile));
	}

	/**
	 * Validates the specified <code>xmlFile</code> against the current schema. An
	 * <code>ValidationFailedException</code> is thrown if not validation was
	 * possible.
	 * 
	 * @param xmlFile
	 *          the <code>File</code> to be validated
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed
	 */
	public void validate(final File xmlFile) throws ValidationFailedException {

		InputStream xmlStream = null;
		try {

			// get the stream for the file
			xmlStream = new FileInputStream(xmlFile);

			// validate the file
			validate(xmlStream);
		} catch (final FileNotFoundException e) {
			throw new ValidationFailedException("", e);
		} finally {

			// close the stream
			Streams.closeIO(xmlStream);
		}
	}

	@Override
	public void validate(final InputStream xmlStream)
			throws ValidationFailedException {

		try {
			if (xsdSchema == null) {

				// get the source
				final InputSource inputSource = new InputSource(xmlStream);
				final EntityResolver resolver = new DelegatingEntityResolver(getClass()
						.getClassLoader());

				// use the DocumentLoader for validation, the DocumentLoader uses the
				// defined XSD of the document, it tries to use the one distributed via
				// the jar, otherwise it looks up the URL
				documentLoader.loadDocument(inputSource, resolver, errorHandler,
						XmlValidationModeDetector.VALIDATION_XSD, true);
			} else {

				// get the sources
				final Source xmlSource = new StreamSource(xmlStream);
				final Validator validator = xsdSchema.newValidator();

				// validate the document using the default Validator
				validator.validate(xmlSource);
			}
		} catch (final Exception e) {
			throw new ValidationFailedException("", e);
		}
	}
}
