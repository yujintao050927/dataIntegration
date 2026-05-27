<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一课程XML -> C院系课程XML（表3-17） -->
  <xsl:template match="/">
    <classes>
      <xsl:for-each select="//*[local-name()='class']">
        <class>
          <Cno><xsl:value-of select="id"/></Cno>
          <Cnm><xsl:value-of select="name"/></Cnm>
          <Cpt><xsl:value-of select="score"/></Cpt>
          <Tec><xsl:value-of select="teacher"/></Tec>
          <Pla><xsl:value-of select="location"/></Pla>
        </class>
      </xsl:for-each>
    </classes>
  </xsl:template>
</xsl:stylesheet>
