package net.meisen.general.sbconfigurator.factories;

import java.lang.reflect.InvocationTargetException;

/**
 * A bean used to execute a method, the {@code type} property can be used to
 * define the execution time. The following values are available:
 * <ul>
 * <li>factory (default), or</li>
 * <li>creation</li>
 * <li>init</li>
 * </ul>
 * 
 * 
 * @author pmeisen
 * 
 */
public class MethodExecutorBean extends MethodInvokingFactoryBean {
	private String type;

	@Override
	public Object invoke() throws InvocationTargetException,
			IllegalAccessException {
		super.invoke();

		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return Object.class;
	}

	/**
	 * Get the defined type, i.e. {@code factory}, {@code creation}, or
	 * {@code init}.
	 * 
	 * @return the defined type
	 */
	public String getType() {
		return type == null ? "factory" : type;
	}

	/**
	 * Sets the type when this method should be executed.
	 * 
	 * @param type
	 *            the type
	 */
	public void setType(final String type) {
		this.type = type;
	}
}
