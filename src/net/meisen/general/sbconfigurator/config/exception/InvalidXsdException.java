package net.meisen.general.sbconfigurator.config.exception;

public class InvalidXsdException extends Exception {

	public InvalidXsdException(final String message) {
		super(message);
	}

	public InvalidXsdException(final String message, final Exception e) {
		super(message, e);
	}
}
