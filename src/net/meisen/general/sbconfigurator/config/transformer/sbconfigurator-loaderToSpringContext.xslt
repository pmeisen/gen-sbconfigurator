<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:sbc="http://dev.meisen.net/sbconfigurator"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                
  <xsl:output method="xml" indent="yes" />
                
  <xsl:template match="/sbc:Config">
    <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

      <xsl:for-each select="sbc:loaderDefinition">
        <xsl:variable name="id"><xsl:value-of select="@id"/></xsl:variable>
        <xsl:variable name="xslt"><xsl:value-of select="@xslt"/></xsl:variable>
        <xsl:variable name="selector"><xsl:value-of select="@selector"/></xsl:variable>
        <xsl:variable name="beanOverridingAllowed"><xsl:value-of select="@beanOverridingAllowed"/></xsl:variable>
        <xsl:variable name="validationEnabled"><xsl:value-of select="@validationEnabled"/></xsl:variable>
    
        <bean id="{$id}" class="net.meisen.general.sbconfigurator.config.transformer.DefaultLoaderDefinition">  
          <property name="selector" value="{$selector}" />
          <xsl:if test="@xslt != ''">
            <property name="xslt" value="{$xslt}" />
          </xsl:if>
          <xsl:if test="@beanOverridingAllowed != ''">
            <property name="beanOverridingAllowed" value="{$beanOverridingAllowed}" />
          </xsl:if>
          <xsl:if test="@validationEnabled != ''">
            <property name="validationEnabled" value="{$validationEnabled}" />
          </xsl:if>
        </bean>
      </xsl:for-each>

    </beans>
  </xsl:template>
</xsl:stylesheet>