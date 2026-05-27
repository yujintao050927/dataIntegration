<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 各院系学生XML -> 统一学生XML（表3-18） -->
  <xsl:template match="/">
    <students>
      <xsl:for-each select="//*[local-name()='student']">
        <student>
          <id><xsl:value-of select="学号 | Sno | id"/></id>
          <name><xsl:value-of select="姓名 | Snm | name"/></name>
          <sex><xsl:value-of select="性别 | Sex | sex"/></sex>
          <major><xsl:value-of select="院系 | 专业 | Sde | major"/></major>
        </student>
      </xsl:for-each>
    </students>
  </xsl:template>
</xsl:stylesheet>
