package net.meisen.general.sbconfigurator.config.exception;

/**
 * This exception is thrown whenever the <code>Configuration</code> could not be
 * loaded. Reasons could be:
 * <ul>
 * <li>an invalid XSLT,</li>
 * <li>an identifier for a <code>LoaderDefinitions</code> is used by a
 * core-loader or a user-loader (and the settings doesn't allow id overridding)</li>
 * </ul>
 * 
 * @author pmeisen
 * 
 */
public class InvalidConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 6155619843596735894L;

	/**
	 * Generates an exception with a specified message, the exception should be
	 * the root-cause.
	 * 
	 * @param message
	 *          the message which explains the exception
	 */
	public InvalidConfigurationException(final String message) {
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
	public InvalidConfigurationException(final String message, final Exception e) {
		super(message, e);
	}
}
