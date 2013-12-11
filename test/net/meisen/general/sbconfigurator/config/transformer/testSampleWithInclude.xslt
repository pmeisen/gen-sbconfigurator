<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
        xmlns="http://www.springframework.org/schema/beans"  
        xmlns:uuid="java.util.UUID"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:import href="classpath://net/meisen/general/sbconfigurator/config/transformer/testInclude.xslt" />

  <xsl:template match="/root">
    <beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                               http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd">
      <xsl:apply-imports />
    </beans>
  </xsl:template>
  
</xsl:stylesheet>