package net.meisen.general.sbconfigurator.config.transformer;

import java.net.URI;
import java.util.Collection;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.meisen.general.genmisc.resources.Resource;
import net.meisen.general.genmisc.resources.ResourceInfo;

/**
 * A {@code XsltURIResolver} which can be used to resolve resources in xslt on
 * the classpath. The protocol to be used is {@code classpath}.
 * 
 * @author pmeisen
 * 
 */
public class ClasspathXsltUriResolver implements XsltURIResolver {

	@Override
	public Source resolve(final URI uri, final String path) {
		final Collection<ResourceInfo> resources = Resource.getResources(path,
				true, false);

		final int size = resources.size();
		if (size == 0) {
			return null;
		} else {
			// get the first one
			final ResourceInfo resInfo = resources.iterator().next();
			final Source src = new StreamSource(
					Resource.getResourceAsStream(resInfo));

			return src;
		}
	}

	@Override
	public String getProtocol() {
		return "classpath";
	}
}
