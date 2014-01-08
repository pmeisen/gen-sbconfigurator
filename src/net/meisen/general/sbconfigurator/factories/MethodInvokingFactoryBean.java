package net.meisen.general.sbconfigurator.factories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.support.ArgumentConvertingMethodInvoker;

@SuppressWarnings("unused")
public class MethodInvokingFactoryBean extends
		org.springframework.beans.factory.config.MethodInvokingFactoryBean {
	private String postExecutionMethod;
	private Object[] postArguments = new Object[0];

	private ArgumentConvertingMethodInvoker methodInvoker;

	@Override
	public Object invoke() throws InvocationTargetException,
			IllegalAccessException {
		final Object bean = super.invoke();

		if (methodInvoker != null) {
			methodInvoker.invoke();
		}

		return bean;
	}

	@Override
	public void prepare() throws ClassNotFoundException, NoSuchMethodException {
		super.prepare();

		if (postExecutionMethod == null) {
			methodInvoker = null;
		} else {
			methodInvoker = new ArgumentConvertingMethodInvoker();
			methodInvoker.setArguments(postArguments);
			methodInvoker.setTargetMethod(postExecutionMethod);
			methodInvoker.setTargetObject(getTargetObject());

			methodInvoker.prepare();
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

	public String getPostExecutionMethod() {
		return postExecutionMethod;
	}

	public void setPostExecutionMethod(String postExecutionMethod) {
		this.postExecutionMethod = postExecutionMethod;
	}
}
