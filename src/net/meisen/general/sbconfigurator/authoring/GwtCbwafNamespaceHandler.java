package net.meisen.general.sbconfigurator.authoring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class GwtCbwafNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new GwtCbwafFormatParser());
	}

}
