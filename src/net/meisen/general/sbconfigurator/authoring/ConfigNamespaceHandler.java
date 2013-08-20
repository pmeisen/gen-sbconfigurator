package net.meisen.general.sbconfigurator.authoring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Handler to register the parsers.
 * 
 * @author pmeisen
 * 
 */
public class ConfigNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new ConfigParser());
		registerBeanDefinitionParser("loader", new LoaderParser());
	}
}
