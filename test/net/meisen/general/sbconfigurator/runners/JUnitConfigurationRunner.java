package net.meisen.general.sbconfigurator.runners;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.annotations.ContextFile;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * JUnit <code>Runner</code> which can be used to create the test within the
 * configuration's context, i.e. within the text auto-wiring is possible.<br/>
 * The context and filename from which the configuration should be loaded can be
 * defined using the annotations {@link ContextClass} and/or {@link ContextFile}
 * .
 * 
 * @author pmeisen
 * 
 */
public class JUnitConfigurationRunner extends BlockJUnit4ClassRunner {

	private final IConfiguration configuration;

	/**
	 * The default constructor for the runner, which specifies the
	 * <code>Class</code> of the test to be run.
	 * 
	 * @param clazz
	 *          the <code>Class</code> of the test to be run
	 * 
	 * @throws InitializationError
	 *           if the test cannot be initialized, i.e. invalid constructor,
	 *           invalid annotations, ...
	 */
	public JUnitConfigurationRunner(final Class<?> clazz)
			throws InitializationError {
		super(clazz);

		// get the annotations
		final ContextClass contextClassAnnotation = clazz
				.getAnnotation(ContextClass.class);
		final ContextFile contextFileAnnotation = clazz
				.getAnnotation(ContextFile.class);

		// define the values of the annotation
		final Class<?> contextClazz = contextClassAnnotation == null ? null
				: contextClassAnnotation.value();
		final String contextFile = contextFileAnnotation == null ? null
				: contextFileAnnotation.value();

		// final String coreSettingsContext, final Class<?> clazz

		// now we need the ConfigurationCoreSettings
		final ConfigurationCoreSettings settings = ConfigurationCoreSettings
				.loadCoreSettings(contextFile, contextClazz);
		configuration = settings.getConfiguration();
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
	public void run(final RunNotifier notifier) {
		super.run(notifier);
	}

}
