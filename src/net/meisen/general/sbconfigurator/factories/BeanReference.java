package net.meisen.general.sbconfigurator.factories;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Reference to another bean. Can be used i.e. to create synonyms or beans which
 * don't have any identifier can be referenced by an identifier using this
 * method.<br/>
 * <br/>
 * 
 * <b>Examples:</b>
 * 
 * <pre>
 *   &lt;bean id=&quote;simpleInteger&quote; class=&quote;java.lang.Integer&quote;&gt;
 *     &lt;constructor-arg value=&quote;5000&quote; /&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id=&quote;beanReference&quote; class=&quote;net.meisen.general.sbconfigurator.factories.BeanReference&quote;&gt;
 *     &lt;property name=&quote;bean&quote;&gt;
 *       &lt;bean class=&quote;java.util.Date&quote; /&gt;
 *     &lt;/property&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id=&quote;beanSynonymReference&quote; class=&quote;net.meisen.general.sbconfigurator.factories.BeanReference&quote;&gt;
 *     &lt;property name=&quote;bean&quote; ref=&quote;simpleInteger&quote; /&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id=&quote;beanByValueReference&quote; class=&quote;net.meisen.general.sbconfigurator.factories.BeanReference&quote;&gt;
 *     &lt;property name=&quote;bean&quote; value=&quote;Just a Simple Value&quote; /&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id=&quote;nullBeanWithoutType&quote; class=&quote;net.meisen.general.sbconfigurator.factories.BeanReference&quote;&gt;
 *   	&lt;property name=&quote;bean&quote;&gt;&lt;null /&gt;&lt;/property&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id=&quote;nullBeanWithType&quote; class=&quote;net.meisen.general.sbconfigurator.factories.BeanReference&quote;&gt;
 *     &lt;property name=&quote;class&quote; value=&quote;java.lang.Long&quote; /&gt;
 *   &lt;/bean&gt;
 * </pre>
 * 
 * @author pmeisen
 * 
 */
public class BeanReference implements FactoryBean<Object>, InitializingBean {

	private boolean isSet = false;
	private Object bean = null;
	private Class<?> clazz = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (bean != null && clazz != null
				&& !clazz.isAssignableFrom(bean.getClass())) {
			throw new IllegalArgumentException("The bean '" + bean
					+ "' of type '" + bean.getClass().getName()
					+ "' is not an instance of '" + clazz.getName() + "'");
		}
	}

	@Override
	public Object getObject() throws Exception {
		return bean;
	}

	@Override
	public Class<?> getObjectType() {
		if (isSet) {
			if (bean == null) {
				return clazz == null ? Object.class : clazz;
			} else {
				return bean.getClass();
			}
		} else {
			return clazz;
		}
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets the bean instance of the reference.
	 * 
	 * @param bean
	 *            the bean instance to be referred by this reference
	 */
	public void setBean(final Object bean) {
		this.isSet = true;
		this.bean = bean;
	}

	/**
	 * Sets the class of the reference, needed i.e. if the value is {@code null}
	 * .
	 * 
	 * @param clazz
	 *            the class of the reference
	 */
	public void setClass(final Class<?> clazz) {
		this.clazz = clazz;
	}
}
