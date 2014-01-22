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
	private boolean invoked = false;

	@Override
	public Object invoke() throws InvocationTargetException,
			IllegalAccessException {
		if (!invoked) {
			super.invoke();
			invoked = true;
		}

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

	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
	}

	@Override
	public Object getObject() throws Exception {
		return doInvoke();
	}

	/**
	 * Perform the invocation and convert InvocationTargetException into the
	 * underlying target exception.
	 */
	private Object doInvoke() throws Exception {
		try {
			return invoke();
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof Exception) {
				throw (Exception) ex.getTargetException();
			}
			if (ex.getTargetException() instanceof Error) {
				throw (Error) ex.getTargetException();
			}
			throw ex;
		}
	}
}
