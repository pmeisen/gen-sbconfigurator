package net.meisen.general.sbconfigurator.config.transformer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import net.meisen.general.sbconfigurator.config.exception.InvalidConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A default {@code URIResolver} which uses registered {@code XsltURIResolver}
 * to resolve an {@code URI}.
 * 
 * @author pmeisen
 * 
 */
public class DefaultXsltUriResolver implements URIResolver {

	@Autowired(required = false)
	private List<IXsltUriResolver> resolvers;

	/**
	 * Default constructor which doesn't add any additional
	 * {@code XsltURIResolver}. Those might have been added using auto-wiring.
	 * The class auto-wired all {@code XsltURIResolver} defined.
	 * 
	 * @see Autowired
	 */
	public DefaultXsltUriResolver() {
		this(new ArrayList<IXsltUriResolver>());
	}

	/**
	 * Constructor used to register additional {@code XsltURIResolver}.
	 * 
	 * @param resolvers
	 *            the {@code XsltURIResolver} to be registered
	 */
	public DefaultXsltUriResolver(final IXsltUriResolver... resolvers) {
		this(resolvers == null ? new ArrayList<IXsltUriResolver>() : Arrays
				.asList(resolvers));
	}

	/**
	 * Constructor used to register additional {@code XsltURIResolver}.
	 * 
	 * @param resolvers
	 *            the {@code XsltURIResolver} to be registered
	 */
	public DefaultXsltUriResolver(final List<IXsltUriResolver> resolvers) {
		this.resolvers = resolvers;

		final Set<String> protocols = new HashSet<String>();
		for (final IXsltUriResolver resolver : resolvers) {
			if (!protocols.add(resolver.getProtocol())) {
				throw new InvalidConfigurationException(
						"The protocol '"
								+ resolver.getProtocol()
								+ "' is used multiple times, which is not allowed within XsltURIResolver.");
			}
		}
	}

	@Override
	public Source resolve(final String href, final String base)
			throws TransformerException {

		// if there aren't any we cannot do anything
		if (resolvers == null) {
			return null;
		}
		
		try {
			final URI uri = new URI(href);
			final String scheme = uri.getScheme();
			final String host = uri.getHost();
			final String path = uri.getPath();
			final String cmbPath = (host == null ? path.replaceFirst("^/", "")
					: host + path);

			for (final IXsltUriResolver resolver : resolvers) {
				final String protocol = resolver.getProtocol();

				// if we found the protol just resolve the URI
				if (scheme.equals(protocol)) {
					return resolver.resolve(uri, cmbPath);
				}
			}
		} catch (final URISyntaxException e) {
			// nothing to do let's hope the underlying system can resolve it
		}

		return null;
	}
}
