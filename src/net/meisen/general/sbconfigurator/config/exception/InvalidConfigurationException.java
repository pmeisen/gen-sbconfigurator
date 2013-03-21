package net.meisen.general.sbconfigurator.config.exception;

public class InvalidConfigurationException extends RuntimeException {

	public InvalidConfigurationException(final String message) {
		super(message);
	}

	public InvalidConfigurationException(final String message, final Exception e) {
		super(message, e);
	}
}

