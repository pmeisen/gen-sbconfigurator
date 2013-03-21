package net.meisen.general.sbconfigurator.config.exception;

public class TransformationFailedException extends Exception {

	public TransformationFailedException(final String message) {
		super(message);
	}

	public TransformationFailedException(final String message, final Exception e) {
		super(message, e);
	}
}
