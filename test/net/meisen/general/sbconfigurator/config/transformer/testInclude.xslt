<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
        xmlns="http://www.springframework.org/schema/beans"  
        xmlns:uuid="java.util.UUID"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="subelement">
    <bean id="testString" class="java.lang.String" />
  </xsl:template>
</xsl:stylesheet>