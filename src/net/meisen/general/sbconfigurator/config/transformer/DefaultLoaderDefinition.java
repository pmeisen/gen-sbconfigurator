package net.meisen.general.sbconfigurator.config.transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.meisen.general.genmisc.types.Objects;
import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.transformer.ILoaderDefinition;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsdException;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

/**
 * The default implementation of the <code>ILoaderDefinition</code>.
 * 
 * @author pmeisen
 * 
 */
public class DefaultLoaderDefinition implements ILoaderDefinition {
	private final static Logger LOG = LoggerFactory
			.getLogger(DefaultLoaderDefinition.class);

	private String selector = null;
	private byte[] xsd = null;
	private byte[] xslt = null;
	private Class<?> context = null;
	private boolean beanOverridingAllowed = false;
	private boolean validationEnabled = true;
	private boolean loadFromClassPath = true;
	private boolean loadFromWorkingDir = true;

	/**
	 * Default constructor which doesn't specify anything. The setters have to be
	 * used prior to using the definition.
	 */
	public DefaultLoaderDefinition() {
		// nothing to do
	}

	/**
	 * This constructor sets the most important values of the definition, i.e. the
	 * XSLT (if needed) and the selector to select the XML files specifying
	 * whatever has to be loaded.
	 * 
	 * @param xsltClassPath
	 *          the classpath to the <code>XSLT</code> to be used for
	 *          transformation purposes, can be <code>null</code> if the XML is
	 *          already a valid SpringContext definition.
	 * @param selector
	 *          the selector which specifies which XML to be loaded
	 * 
	 * @throws InvalidXsltException
	 *           if the XSLT is invalid
	 */
	public DefaultLoaderDefinition(final String xsltClassPath,
			final String selector) throws InvalidXsltException {
		this(xsltClassPath, selector, null);
	}

	/**
	 * This constructor sets the values of the definition, i.e. the XSLT (if
	 * needed), the selector to select the XML files specifying whatever has to be
	 * loaded and the context.
	 * 
	 * @param xsltClassPath
	 *          the classpath to the <code>XSLT</code> to be used for
	 *          transformation purposes, can be <code>null</code> if the XML is
	 *          already a valid SpringContext definition.
	 * @param selector
	 *          the selector which specifies which XML to be loaded
	 * @param context
	 *          the context to look for the files selected via the selector.
	 * 
	 * @throws InvalidXsltException
	 *           if the XSLT is invalid
	 */
	public DefaultLoaderDefinition(final String xsltClassPath,
			final String selector, final Class<?> context)
			throws InvalidXsltException {
		setXsltTransformer(xsltClassPath);
		setSelector(selector);
		setContext(context);
	}

	@Override
	public boolean hasContext() {
		return context != null;
	}

	@Override
	public Class<?> getContext() {
		return context;
	}

	/**
	 * Sets the context in which the selected files should be chosen.
	 * 
	 * @param context
	 *          the context in which the selected files should be chosen
	 */
	public void setContext(final Class<?> context) {
		this.context = context;
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
			throw new InvalidXsdException("The specified xsd at '" + xsdClassPath
					+ "' could not be accessed.", e);
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
			throw new InvalidXsdException("The specified xsd file '" + xsdFile
					+ "' could not be found.", e);
		}
	}

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
	 */
	public void setXsdSchema(final InputStream xsdStream)
			throws InvalidXsdException {
		try {
			this.xsd = Streams.copyStreamToByteArray(xsdStream);
		} catch (final IOException e) {
			throw new InvalidXsdException("The xsd stream could not be read.", e);
		}
	}

	/**
	 * This method is used to set the transformer to be used for the
	 * transformation. It reads the transformer from the passed
	 * <code>xsltClassPath</code>.
	 * 
	 * @param xsltClassPath
	 *          the <code>String</code> pointing to the XSLT on the classpath
	 * 
	 * @throws InvalidXsltException
	 *           if the XSLT cannot be found on the classpath, the XSLT is
	 *           invalid, ...
	 */
	public void setXslt(final String xsltClassPath) throws InvalidXsltException {
		setXsltTransformer(xsltClassPath);
	}

	/**
	 * This method is used to set the transformer to be used for the
	 * transformation. It reads the transformer from the passed
	 * <code>xsltClassPath</code>.
	 * 
	 * @param xsltClassPath
	 *          the <code>String</code> pointing to the XSLT on the classpath
	 * 
	 * @throws InvalidXsltException
	 *           if the XSLT cannot be found on the classpath, the XSLT is
	 *           invalid, ...
	 */
	public void setXsltTransformer(final String xsltClassPath)
			throws InvalidXsltException {
		if (Objects.empty(xsltClassPath)) {
			setXsltTransformer((InputStream) null);
		} else {
			try {
				setXsltTransformer(new ClassPathResource(xsltClassPath)
						.getInputStream());
			} catch (final IOException e) {
				throw new InvalidXsltException("The xslt stream could not be read.", e);
			}
		}
	}

	/**
	 * This method is used to set the transformer to be used for the
	 * transformation. It reads the transformer from the passed
	 * <code>xsltFile</code>.
	 * 
	 * @param xsltFile
	 *          the <code>File</code> containing the XSLT
	 * @throws InvalidXsltException
	 * 
	 * @throws InvalidXsltException
	 *           if the <code>xsltFile</code> cannot be read, the XSLT is invalid,
	 *           ...
	 */
	public void setXsltTransformer(final File xsltFile)
			throws InvalidXsltException {
		if (Objects.empty(xsltFile)) {
			setXsltTransformer((InputStream) null);
		} else {
			try {
				setXsltTransformer(new FileInputStream(xsltFile));
			} catch (final FileNotFoundException e) {
				throw new InvalidXsltException("The specified xslt file '" + xsltFile
						+ "' could not be found.", e);
			}
		}
	}

	/**
	 * This method is used to set the transformer to be used for the
	 * transformation. It reads the transformer from the passed
	 * <code>xsltStream</code> .
	 * 
	 * <br/>
	 * <br/>
	 * <i>Note:</i><br/>
	 * The <code>InputStream</code> will be <b>closed</b> after the transformer is
	 * read!
	 * 
	 * @param xsltStream
	 *          the <code>InputStream</code> to read the transformer from
	 * 
	 * @throws InvalidXsltException
	 *           if the transformer definition is invalid, the
	 *           <code>xsltStream</code> cannot be read, ...
	 */
	public void setXsltTransformer(final InputStream xsltStream)
			throws InvalidXsltException {
		if (Objects.empty(xsltStream)) {
			this.xslt = null;
		} else {
			try {
				this.xslt = Streams.copyStreamToByteArray(xsltStream);
			} catch (final IOException e) {
				throw new InvalidXsltException("The xslt could not be read.", e);
			}
		}
	}

	@Override
	public InputStream getXsltTransformerInputStream() {
		if (xslt == null) {
			return null;
		}

		try {
			return new ByteArrayResource(xslt).getInputStream();
		} catch (final IOException e) {
			// should never appear, but lets log it
			if (LOG.isErrorEnabled()) {
				LOG.error(
						"The byte array could not be transformed to an InputStream, this error should never occur",
						e);
			}

			return null;
		}
	}

	/**
	 * Checks if a transformer is defined for this definition.
	 * 
	 * @return <code>true</code> if a transformer is defined, otherwise
	 *         <code>false</code>
	 */
	public boolean hasXsltTransformer() {
		return xslt != null;
	}

	@Override
	public InputStream getXsdSchemaInputStream() {
		if (xsd == null) {
			return null;
		}

		try {
			return new ByteArrayResource(xsd).getInputStream();
		} catch (final IOException e) {
			// should never appear, but lets log it
			if (LOG.isErrorEnabled()) {
				LOG.error(
						"The byte array could not be transformed to an InputStream, this error should never occur",
						e);
			}

			return null;
		}
	}

	@Override
	public boolean hasXsdSchema() {
		return xsd != null;
	}

	@Override
	public String getSelector() {
		return selector;
	}

	/**
	 * Sets the selector to be used to select the xml-files to be changed by this
	 * <code>ILoaderDefinition</code>.
	 * 
	 * @param selector
	 *          the selector which defines which xml-files are modified by this
	 *          <code>ILoaderDefinition</code>
	 */
	public void setSelector(final String selector) {
		this.selector = selector;
	}

	@Override
	public boolean isBeanOverridingAllowed() {
		return beanOverridingAllowed;
	}

	/**
	 * Defines if bean overriding is allowed by this
	 * <code>ILoaderDefinition</code>.
	 * 
	 * @param beanOverridingAllowed
	 *          <code>true</code> if bean overriding is allowed, otherwise
	 *          <code>false</code>
	 */
	public void setBeanOverridingAllowed(final boolean beanOverridingAllowed) {
		this.beanOverridingAllowed = beanOverridingAllowed;
	}

	@Override
	public String toString() {
		return "Loader: " + selector + " (context: '"
				+ (context == null ? null : context.getName()) + "', overriding: '"
				+ beanOverridingAllowed + "', hasXsd: '" + hasXsdSchema()
				+ "', hasXslt: '" + hasXsltTransformer() + "')";
	}

	@Override
	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	/**
	 * Defines if the XML read during loading should be validated. This setting
	 * can be overruled by the general configuration setting.
	 * 
	 * @param validationEnabled
	 *          <code>true</code> if the XML should be validated during the
	 *          loading process or not
	 * 
	 * @see ConfigurationCoreSettings#isConfigurationValidationEnabled()
	 */
	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	@Override
	public boolean isLoadFromClassPath() {
		return loadFromClassPath;
	}

	/**
	 * Defines if the selector should be applied against files on the class-path (
	 * <code>true</code>) or not <code>false</code>.
	 * 
	 * @param loadFromClassPath
	 *          <code>true</code> if the selected should search on the class-path,
	 *          otherwise <code>false</code>
	 */
	public void setLoadFromClassPath(final boolean loadFromClassPath) {
		this.loadFromClassPath = loadFromClassPath;
	}

	@Override
	public boolean isLoadFromWorkingDir() {
		return loadFromWorkingDir;
	}

	/**
	 * Defines if the selector should be applied against files in the
	 * working-directory (and all it's sub-directories) or not.
	 * 
	 * @param loadFromWorkingDir
	 *          <code>true</code> if the selector should search in the current
	 *          working-directory (and all sub-directories), otherwise
	 *          <code>false</code>
	 */
	public void setLoadFromWorkingDir(final boolean loadFromWorkingDir) {
		this.loadFromWorkingDir = loadFromWorkingDir;
	}
}
