package net.meisen.general.sbconfigurator.config.exception;

/**
 * This exception is thrown whenever a XML could not be transformed using a
 * XSLT. Reasons could be:
 * <ul>
 * <li>the XML file to be validated could not be found or the stream not read,</li>
 * <li>the XML to be validated is not well-formed,</li>
 * <li>the XML validation failed,</li>
 * </ul>
 * 
 * @author pmeisen
 * 
 */
public class TransformationFailedException extends Exception {
	private static final long serialVersionUID = 7468893263985482035L;

	/**
	 * Generates an exception with a specified message, the exception should be
	 * the root-cause.
	 * 
	 * @param message
	 *          the message which explains the exception
	 */
	public TransformationFailedException(final String message) {
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
	public TransformationFailedException(final String message, final Exception e) {
		super(message, e);
	}
}
