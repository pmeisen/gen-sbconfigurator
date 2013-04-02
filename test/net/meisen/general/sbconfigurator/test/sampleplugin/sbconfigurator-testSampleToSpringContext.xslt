<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                
  <xsl:output method="xml" indent="yes" />
                
  <xsl:template match="/testSample">
    <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
      <bean id="samplePlugIn" class="net.meisen.general.sbconfigurator.test.sampleplugin.SamplePlugIn" />
    </beans>
  </xsl:template>
</xsl:stylesheet>