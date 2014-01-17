package net.meisen.general.sbconfigurator.config.order.mocks;

import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A bean which is based on another auto-wired bean.
 * 
 * @author pmeisen
 * 
 */
public class ComplexAutowiredBean extends AutowiredBean {

	@Autowired
	@Qualifier("wiredBean")
	private AutowiredBean wiredBean;

	@Override
	public void validateAutowiring() {
		super.validateAutowiring();
		assertNotNull(wiredBean);
	}
}
