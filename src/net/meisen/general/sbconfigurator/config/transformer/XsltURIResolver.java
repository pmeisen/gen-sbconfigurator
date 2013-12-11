package net.meisen.general.sbconfigurator.config.transformer;

import java.net.URI;

import javax.xml.transform.Source;

/**
 * Interface to specify additional {@code XsltURIResolver}. Those are
 * automatically auto-wired (if configured in the main-configuration). <br/>
 * <br/>
 * <b>Example:</b><br/>
 * <br/>
 * The {@code ClasspathXsltUriResolver} is registered by default just by adding
 * it as bean to the {@code sbconfigurator-core.xml}.<br/>
 * <br/>
 * 
 * <code>
 * &lt;bean class="net.meisen.[...].ClasspathXsltUriResolver" /&gt;
 * </code>
 * 
 * @author pmeisen
 * 
 */
public interface XsltURIResolver {

	/**
	 * Gets the protocol this {@code XsltURIResolver} should be used for. The
	 * protocol is case-sensitive.
	 * 
	 * @return the protocol this {@code XsltURIResolver} should be used for
	 */
	public String getProtocol();

	/**
	 * Resolves the specified {@code URI}.
	 * 
	 * @param uri
	 *            the requested {@code URI}
	 * @param path
	 *            the host + path of the {@code URI}
	 * 
	 * @return the {@code Source} to be used, if {@code null} the default
	 *         resolver will be used
	 */
	public Source resolve(final URI uri, final String path);
}
