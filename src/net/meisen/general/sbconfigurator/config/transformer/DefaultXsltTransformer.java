package net.meisen.general.sbconfigurator.config.transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.api.transformer.IXsltTransformer;
import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;

import org.springframework.core.io.ClassPathResource;

/**
 * Default implementation of the <code>IXsltTransformer</code> interface.
 * 
 * @author pmeisen
 * 
 */
public class DefaultXsltTransformer implements IXsltTransformer {

	private Transformer xsltTransformer = null;

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
		try {
			setXsltTransformer(new ClassPathResource(xsltClassPath).getInputStream());
		} catch (final IOException e) {
			throw new InvalidXsltException("", e);
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
		try {
			setXsltTransformer(new FileInputStream(xsltFile));
		} catch (final FileNotFoundException e) {
			throw new InvalidXsltException("", e);
		}
	}

	@Override
	public void setXsltTransformer(final InputStream xsltStream)
			throws InvalidXsltException {

		// create an instance of TransformerFactory
		final TransformerFactory transFact = TransformerFactory.newInstance();

		// create the source
		final Source xsltSource = new StreamSource(xsltStream);

		try {
			xsltTransformer = transFact.newTransformer(xsltSource);
		} catch (TransformerConfigurationException e) {
			throw new InvalidXsltException("", e);
		} finally {
			Streams.closeIO(xsltStream);
		}
	}

	/**
	 * Transforms the specified <code>xmlPath</code> using the specified
	 * transformer (XSLT). The <code>xmlPath</code> must be located on the
	 * classpath to be found. An <code>TransformationFailedException</code> is
	 * thrown if not transformation was possible.
	 * 
	 * @param xmlPath
	 *          the path on the classpath pointing to the XML file to be
	 *          transformed
	 * @param outputStream
	 *          the <code>OutputStream</code> to write the transformation to
	 * 
	 * @throws TransformationFailedException
	 *           if the transformation failed
	 */
	public void transformFromClasspath(final String xmlPath,
			final OutputStream outputStream) throws TransformationFailedException {

		final ClassPathResource xmlRes = new ClassPathResource(xmlPath);

		InputStream xmlStream = null;
		try {

			// get the streams for the files
			xmlStream = xmlRes.getInputStream();

			// validate the file
			transform(xmlStream, outputStream);
		} catch (final IOException e) {
			throw new TransformationFailedException("", e);
		} finally {

			// close the stream
			Streams.closeIO(xmlStream);
		}
	}

	/**
	 * Transforms the specified <code>xmlFile</code> using the current
	 * transformer. An <code>TransformationFailedException</code> is thrown if the
	 * transformation failed.
	 * 
	 * @param xmlFile
	 *          the <code>String</code> pointing to a valid file to be transformed
	 * @param outputStream
	 *          the <code>OutputStream</code> to write the transformation to
	 * 
	 * @throws TransformationFailedException
	 *           if the transformation failed
	 */
	public void transform(final String xmlFile, final OutputStream outputStream)
			throws TransformationFailedException {
		transform(new File(xmlFile), outputStream);
	}

	/**
	 * Transforms the specified <code>xmlFile</code> using the current
	 * transformer. An <code>TransformationFailedException</code> is thrown if the
	 * transformation failed.
	 * 
	 * @param xmlFile
	 *          the <code>File</code> to be transformed
	 * @param outputStream
	 *          the <code>OutputStream</code> to write the transformation to
	 * 
	 * @throws TransformationFailedException
	 *           if the transformation failed
	 */
	public void transform(final File xmlFile, final OutputStream outputStream)
			throws TransformationFailedException {

		InputStream xmlStream = null;
		try {

			// get the stream for the file
			xmlStream = new FileInputStream(xmlFile);

			// validate the file
			transform(xmlStream, outputStream);
		} catch (final FileNotFoundException e) {
			throw new TransformationFailedException("", e);
		} finally {

			// close the stream
			Streams.closeIO(xmlStream);
		}
	}

	@Override
	public void transform(final InputStream xmlStream,
			final OutputStream outputStream) throws TransformationFailedException {

		final Source xmlSource = new StreamSource(xmlStream);
		final Result result = new StreamResult(outputStream);

		try {
			xsltTransformer.transform(xmlSource, result);
		} catch (final TransformerException e) {
			throw new TransformationFailedException("", e);
		}

		// close the stream - just to be sure
		Streams.closeIO(outputStream);
	}
}
