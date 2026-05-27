<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一选课XML -> C院系选课XML（表3-19） -->
  <xsl:template match="/">
    <choices>
      <xsl:for-each select="//*[local-name()='choice']">
        <choice>
          <Sno><xsl:value-of select="sid"/></Sno>
          <Cno><xsl:value-of select="cid"/></Cno>
          <Grd><xsl:value-of select="score"/></Grd>
        </choice>
      </xsl:for-each>
    </choices>
  </xsl:template>
</xsl:stylesheet>
