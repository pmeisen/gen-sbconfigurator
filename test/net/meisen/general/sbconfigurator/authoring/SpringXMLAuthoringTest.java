package net.meisen.general.sbconfigurator.authoring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringXMLAuthoringTest {

	@Test
	public void loadAndTest() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("net/meisen/general/sbconfigurator/test/loader/sbconfigurator-authoring-test.xml");
		GwtCbwafConfig config = context.getBean(GwtCbwafConfig.class);
		assertTrue(config.getLocation().equals("test"));
		context.close();
	}

}
