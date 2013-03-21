package net.meisen.general.sbconfigurator.config.transformer;

import net.meisen.general.sbconfigurator.config.exception.InvalidXsdException;
import net.meisen.general.sbconfigurator.config.exception.ValidationFailedException;
import net.meisen.general.sbconfigurator.config.transformer.DefaultXsdValidator;

import org.junit.Test;

/**
 * Tests the default implementation of the <code>IXsdValidator</code>.
 * 
 * @author pmeisen
 * 
 */
public class TestDefaultXsdValidator {

	private final static String classPathToThis = TestDefaultXsdValidator.class
			.getPackage().getName().replace(".", "/");

	private final DefaultXsdValidator validator;

	/**
	 * Default constructor for the test to create a
	 * <code>DefaultXsdValidator</code> instance
	 */
	public TestDefaultXsdValidator() {

		// create the validator we use for testing
		validator = new DefaultXsdValidator();
	}

	/**
	 * Tests the validation of an XML-file without any loader definitions
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed
	 * @throws InvalidXsdException
	 *           if the XSD could not be found
	 */
	@Test
	public void testLoaderEmptyValidation() throws ValidationFailedException,
			InvalidXsdException {

		validator.validateFromClasspath(classPathToThis
				+ "/loaderDefinitionEmpty.xml");
	}

	/**
	 * Tests the validation of an XML-file with a simple loader definition
	 * 
	 * @throws ValidationFailedException
	 *           if the validation failed
	 * @throws InvalidXsdException
	 *           if the XSD could not be found
	 */
	@Test
	public void testLoaderSimpleValidation() throws ValidationFailedException,
			InvalidXsdException {

		validator.validateFromClasspath(classPathToThis
				+ "/loaderDefinitionSingle.xml");
	}
}
