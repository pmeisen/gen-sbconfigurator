package net.meisen.general.sbconfigurator.config.transformer;

import org.slf4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Error handler used to output <code>SAXParseException</code>
 * 
 * @author pmeisen
 * 
 */
public class SimpleSaxErrorHandler implements ErrorHandler {

	private final Logger logger;

	/**
	 * Create a new SimpleSaxErrorHandler for the given slf4j <code>Logger</code>
	 * instance.
	 * 
	 * @param logger
	 *          the <code>Logger</code> to use for logging
	 */
	public SimpleSaxErrorHandler(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public void warning(final SAXParseException ex) throws SAXException {
		logger.warn("Ignored XML validation warning", ex);
	}

	@Override
	public void error(final SAXParseException ex) throws SAXException {
		throw ex;
	}

	@Override
	public void fatalError(final SAXParseException ex) throws SAXException {
		throw ex;
	}

}