<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一选课XML -> B院系选课XML（表3-19） -->
  <xsl:template match="/">
    <choices>
      <xsl:for-each select="//*[local-name()='choice']">
        <choice>
          <学号><xsl:value-of select="sid"/></学号>
          <课程编号><xsl:value-of select="cid"/></课程编号>
          <得分><xsl:value-of select="score"/></得分>
        </choice>
      </xsl:for-each>
    </choices>
  </xsl:template>
</xsl:stylesheet>
