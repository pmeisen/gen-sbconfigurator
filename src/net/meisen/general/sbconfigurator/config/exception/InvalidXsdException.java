package net.meisen.general.sbconfigurator.config.exception;

/**
 * This exception is thrown whenever a XML could not be validated or the XSD is
 * invalid. More detailed reasons could be:
 * <ul>
 * <li>a specified XSD file could not be found,</li>
 * <li>a specified XSD could not be read (i.e. from the classpath),</li>
 * <li>a XSD's stream could not be read correctly,</li>
 * <li>a specified schema is invalid against the XSD</li>
 * </ul>
 * 
 * @author pmeisen
 * 
 */
public class InvalidXsdException extends Exception {
	private static final long serialVersionUID = 3037124066698926414L;

	/**
	 * Generates an exception with a specified message, the exception should be
	 * the root-cause.
	 * 
	 * @param message
	 *          the message which explains the exception
	 */
	public InvalidXsdException(final String message) {
		super(message);
	}

	/**
	 * The exception is not the root for the problem and wraps another exception.
	 * 
	 * @param message
	 *          the message which explains the exception
	 * @param e
	 *          the root for the exception
	 */
	public InvalidXsdException(final String message, final Exception e) {
		super(message, e);
	}
}
