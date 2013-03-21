package net.meisen.general.sbconfigurator.config.exception;

public class ValidationFailedException extends Exception {

	public ValidationFailedException(final String message) {
		super(message);
	}

	public ValidationFailedException(final String message, final Exception e) {
		super(message, e);
	}
}
