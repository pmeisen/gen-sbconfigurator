package net.meisen.general.sbconfigurator.config.exception;

/**
 * This exception is thrown whenever a XML could not be transformed using a
 * XSLT. Reasons could be:
 * <ul>
 * <li>a specified XSLT file could not be found,</li>
 * <li>a specified XSLT could not be read (i.e. from the classpath),</li>
 * <li>a XSLT's stream could not be read correctly</li>
 * </ul>
 * 
 * @author pmeisen
 * 
 */
public class ValidationFailedException extends Exception {
	private static final long serialVersionUID = -5003610964155237758L;

	/**
	 * Generates an exception with a specified message, the exception should be
	 * the root-cause.
	 * 
	 * @param message
	 *          the message which explains the exception
	 */
	public ValidationFailedException(final String message) {
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
	public ValidationFailedException(final String message, final Exception e) {
		super(message, e);
	}
}
