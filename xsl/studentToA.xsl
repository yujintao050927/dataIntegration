<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一学生XML -> A院系学生XML（表3-18） -->
  <xsl:template match="/">
    <students>
      <xsl:for-each select="//*[local-name()='student']">
        <student>
          <学号><xsl:value-of select="id"/></学号>
          <姓名><xsl:value-of select="name"/></姓名>
          <性别><xsl:value-of select="sex"/></性别>
          <院系><xsl:value-of select="major"/></院系>
        </student>
      </xsl:for-each>
    </students>
  </xsl:template>
</xsl:stylesheet>
