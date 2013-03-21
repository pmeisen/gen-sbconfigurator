package net.meisen.general.sbconfigurator.config.exception;

public class InvalidXsltException extends Exception {

	public InvalidXsltException(final String message) {
		super(message);
	}

	public InvalidXsltException(final String message, final Exception e) {
		super(message, e);
	}
}
