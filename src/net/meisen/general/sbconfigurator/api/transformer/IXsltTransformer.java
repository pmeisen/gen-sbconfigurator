package net.meisen.general.sbconfigurator.api.transformer;

import java.io.InputStream;
import java.io.OutputStream;

import net.meisen.general.sbconfigurator.config.exception.InvalidXsltException;
import net.meisen.general.sbconfigurator.config.exception.TransformationFailedException;

/**
 * The interface defines the transformer used to transform configuration
 * documents to bean-context definitions.
 * 
 * @author pmeisen
 * 
 */
public interface IXsltTransformer {

	/**
	 * This method is used to set the transformer to be used for the
	 * transformation. It reads the transformer from the passed
	 * <code>xsltStream</code> .
	 * 
	 * <br/>
	 * <br/>
	 * <i>Note:</i><br/>
	 * The <code>InputStream</code> might be <b>closed</b> after the transformer
	 * is set!
	 * 
	 * @param xsltStream
	 *            the <code>InputStream</code> to read the transformer from
	 * 
	 * @throws InvalidXsltException
	 *             if the transformer definition is invalid, the
	 *             <code>xsltStream</code> cannot be read, ...
	 */
	public void setXsltTransformer(final InputStream xsltStream)
			throws InvalidXsltException;

	/**
	 * Uses a cached xslt-transformer or caches one if non was found for the id
	 * so far.
	 * 
	 * @param id
	 *            the id to cache the template under
	 * @param xsltStream
	 *            the stream if no cached version was found
	 * 
	 * @throws InvalidXsltException
	 *             if the transformer definition is invalid, the
	 *             <code>xsltStream</code> cannot be read, ...
	 */
	public void setCachedXsltTransformer(final String id,
			final InputStream xsltStream) throws InvalidXsltException;

	/**
	 * Transforms the passed <code>xmlStream</code> using the currently defined
	 * transformer (see {@link #setXsltTransformer}). If no transformer is
	 * defined the method will fail with an
	 * <code>TransformationFailedException</code>.
	 * 
	 * @param xmlStream
	 *            the <code>InputStream</code> of the XML to be transformed
	 * @param outputStream
	 *            the <code>OutputStream</code> to write the transformation to
	 * 
	 * @throws TransformationFailedException
	 *             if the transformation failed, e.g. the <code>xmlStream</code>
	 *             is invalid, the XML cannot be transformed, ...
	 */
	public void transform(final InputStream xmlStream,
			final OutputStream outputStream)
			throws TransformationFailedException;
}
