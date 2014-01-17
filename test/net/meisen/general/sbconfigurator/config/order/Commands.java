package net.meisen.general.sbconfigurator.config.order;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to track commands executed.
 */
public class Commands extends ArrayList<String> {
	private static final long serialVersionUID = 1384436000045722818L;
	private final static Logger LOG = LoggerFactory.getLogger(Commands.class);

	@Override
	public boolean add(final String value) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Adding the value '" + value + "'.");
		}

		return super.add(value);
	}

	@Override
	public void add(final int index, final String element) {
		throw new UnsupportedOperationException(
				"Please append each command separatly.");
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends String> c) {
		throw new UnsupportedOperationException(
				"Please add each command separatly.");
	}

	@Override
	public boolean addAll(final Collection<? extends String> c) {
		throw new UnsupportedOperationException(
				"Please add each command separatly.");
	}
}
