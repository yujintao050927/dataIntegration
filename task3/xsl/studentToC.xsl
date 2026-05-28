<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一学生XML -> C院系学生XML（表3-18） -->
  <xsl:template match="/">
    <students>
      <xsl:for-each select="//*[local-name()='student']">
        <student>
          <Sno><xsl:value-of select="id"/></Sno>
          <Snm><xsl:value-of select="name"/></Snm>
          <Sex><xsl:value-of select="sex"/></Sex>
          <Sde><xsl:value-of select="major"/></Sde>
        </student>
      </xsl:for-each>
    </students>
  </xsl:template>
</xsl:stylesheet>
