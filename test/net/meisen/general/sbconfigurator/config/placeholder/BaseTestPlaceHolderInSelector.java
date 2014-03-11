package net.meisen.general.sbconfigurator.config.placeholder;

import static org.junit.Assert.assertNotNull;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base implementation to test placeholders in selectors.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
@ContextClass(BaseTestPlaceHolderInSelector.class)
public abstract class BaseTestPlaceHolderInSelector {

	/**
	 * The core {@code SpringPropertyHolder} used.
	 */
	@Autowired
	@Qualifier(IConfiguration.corePropertyHolderId)
	protected SpringPropertyHolder corePropertyHolder;

	/**
	 * The core configuration loaded.
	 */
	@Autowired(required = true)
	@Qualifier(IConfiguration.coreConfigurationId)
	protected IConfiguration configuration;

	/**
	 * Checks if both instances could be loaded
	 */
	@Before
	public void checkInitialization() {
		assertNotNull(configuration);
		assertNotNull(corePropertyHolder);
	}
}
