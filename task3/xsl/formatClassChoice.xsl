<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 各院系选课XML -> 统一选课XML（表3-19） -->
  <xsl:template match="/">
    <choices>
      <xsl:for-each select="//*[local-name()='choice']">
        <choice>
          <sid><xsl:value-of select="学号 | Sno | sid"/></sid>
          <cid><xsl:value-of select="课程编号 | 课程号 | Cno | cid"/></cid>
          <score><xsl:value-of select="成绩 | 得分 | Grd | score"/></score>
        </choice>
      </xsl:for-each>
    </choices>
  </xsl:template>
</xsl:stylesheet>
