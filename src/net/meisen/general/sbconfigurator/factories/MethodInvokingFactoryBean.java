package net.meisen.general.sbconfigurator.factories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.support.ArgumentConvertingMethodInvoker;

/**
 * An advanced {@code MethodInvokingFactoryBean} which is capable to invoke a
 * method after the real {@code targetMethod} is invoked.
 * 
 * @author pmeisen
 * 
 * @see org.springframework.beans.factory.config.MethodInvokingFactoryBean
 *      MethodInvokingFactoryBean (Spring)
 * 
 */
@SuppressWarnings("unused")
public class MethodInvokingFactoryBean extends
		org.springframework.beans.factory.config.MethodInvokingFactoryBean {
	private String postExecutionMethod;
	private Object[] postArguments = new Object[0];

	private ArgumentConvertingMethodInvoker postMethodInvoker;

	@Override
	public Object invoke() throws InvocationTargetException,
			IllegalAccessException {
		final Object bean = super.invoke();

		if (postMethodInvoker != null) {
			postMethodInvoker.invoke();
		}

		return bean;
	}

	@Override
	public void prepare() throws ClassNotFoundException, NoSuchMethodException {
		super.prepare();

		if (postExecutionMethod == null) {
			postMethodInvoker = null;
		} else {
			postMethodInvoker = new ArgumentConvertingMethodInvoker();
			postMethodInvoker.setArguments(postArguments);
			postMethodInvoker.setTargetMethod(postExecutionMethod);
			postMethodInvoker.setTargetObject(getTargetObject());

			postMethodInvoker.prepare();
		}
	}

	/**
	 * Set arguments for the post-method invocation (i.e. the method invocated
	 * after the real invocation took place). If this property is not set, or
	 * the Object array is of length 0, a method with no arguments is assumed.
	 * 
	 * @param postArguments
	 *            the arguments for the post-method invocation
	 */
	public void setPostArguments(Object[] postArguments) {
		this.postArguments = (postArguments != null ? postArguments
				: new Object[0]);
	}

	/**
	 * Return the arguments for the post-method invocation.
	 * 
	 * @return the arguments for the post-method invocation
	 */
	public Object[] getPostArguments() {
		return this.postArguments;
	}

	/**
	 * Gets the method of the {@code targetObject} to be executed after the
	 * defined {@code targetMethod} is invoked.
	 * 
	 * @return the method of the {@code targetObject} to be executed after the
	 *         defined {@code targetMethod} is invoked
	 */
	public String getPostExecutionMethod() {
		return postExecutionMethod;
	}

	/**
	 * Sets the method of the {@code targetObject} to be executed after the
	 * defined {@code targetMethod} is invoked.
	 * 
	 * @param postExecutionMethod
	 *            the method of the {@code targetObject} to be executed after
	 *            the defined {@code targetMethod} is invoked
	 */
	public void setPostExecutionMethod(String postExecutionMethod) {
		this.postExecutionMethod = postExecutionMethod;
	}
}
