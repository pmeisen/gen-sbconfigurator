package net.meisen.general.sbconfigurator.authoring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConfigNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new ConfigParser());
		registerBeanDefinitionParser("loader", new LoaderParser());
	}
}
