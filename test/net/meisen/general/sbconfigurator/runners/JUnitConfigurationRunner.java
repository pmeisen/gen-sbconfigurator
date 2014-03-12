package net.meisen.general.sbconfigurator.runners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.annotations.ContextFile;
import net.meisen.general.sbconfigurator.runners.annotations.SystemProperties;
import net.meisen.general.sbconfigurator.runners.annotations.SystemProperty;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * JUnit <code>Runner</code> which can be used to create the test within the
 * configuration's context, i.e. within the test auto-wiring is possible.<br/>
 * The context and filename from which the configuration should be loaded can be
 * defined using the annotations {@link ContextClass} and/or {@link ContextFile}
 * .
 * 
 * @author pmeisen
 * 
 */
public class JUnitConfigurationRunner extends BlockJUnit4ClassRunner {

	private final IConfiguration configuration;
	private final Map<String, String> properties = new HashMap<String, String>();

	/**
	 * The default constructor for the runner, which specifies the
	 * <code>Class</code> of the test to be run.
	 * 
	 * @param clazz
	 *            the <code>Class</code> of the test to be run
	 * 
	 * @throws InitializationError
	 *             if the test cannot be initialized, i.e. invalid constructor,
	 *             invalid annotations, ...
	 */
	public JUnitConfigurationRunner(final Class<?> clazz)
			throws InitializationError {
		super(clazz);

		// get the annotations
		final ContextClass contextClassAnnotation = clazz
				.getAnnotation(ContextClass.class);
		final ContextFile contextFileAnnotation = clazz
				.getAnnotation(ContextFile.class);
		final SystemProperties systemProperties = clazz
				.getAnnotation(SystemProperties.class);
		final SystemProperty systemProperty = clazz
				.getAnnotation(SystemProperty.class);

		// define the values of the annotation
		final Class<?> contextClazz = contextClassAnnotation == null ? null
				: contextClassAnnotation.value();
		final String contextFile = contextFileAnnotation == null ? null
				: contextFileAnnotation.value();

		// get the System-Properties
		if (systemProperties != null) {
			for (final SystemProperty p : systemProperties.value()) {
				properties.put(p.property(), p.value());
			}
		}
		if (systemProperty != null) {
			properties.put(systemProperty.property(), systemProperty.value());
		}

		// set the properties
		final Map<String, String> oldProperties = new HashMap<String, String>();
		for (final Entry<String, String> entry : properties.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();

			// keep the old property
			oldProperties.put(key, System.getProperty(key));

			// set the new one
			if (value == null) {
				System.clearProperty(key);
			} else {
				System.setProperty(key, value);
			}
		}

		// now we need the ConfigurationCoreSettings
		try {
			final ConfigurationCoreSettings settings = ConfigurationCoreSettings
					.loadCoreSettings(contextFile, contextClazz);
			configuration = settings.getConfiguration();
		} catch (final RuntimeException e) {
			throw e;
		} finally {
			// now we should reset the properties
			for (final Entry<String, String> entry : oldProperties.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();

				if (value == null) {
					System.clearProperty(key);
				} else {
					System.setProperty(key, value);
				}
			}
		}
	}

	@Override
	protected Object createTest() throws Exception {
		return configuration.createInstance(getTestClass().getJavaClass());
	}

	@Override
	public Description getDescription() {
		return super.getDescription();
	}

	@Override
	protected Statement classBlock(final RunNotifier notifier) {
		final Statement statement = super.classBlock(notifier);
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				try {
					statement.evaluate();
				} catch (final Throwable t) {
					throw t;
				} finally {
					configuration.release();
				}
			}
		};
	}

}
