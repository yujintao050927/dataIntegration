<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 各院系课程XML -> 统一课程XML（表3-17） -->
  <xsl:template match="/">
    <classes>
      <xsl:for-each select="//*[local-name()='class']">
        <class>
          <id><xsl:value-of select="课程编号 | 课程号 | Cno | id"/></id>
          <name><xsl:value-of select="课程名称 | 课程名 | Cnm | name"/></name>
          <score><xsl:value-of select="学分 | Cpt | score"/></score>
          <teacher><xsl:value-of select="任课教师 | 课程教师 | 教师 | Tec | teacher"/></teacher>
          <location><xsl:value-of select="上课地点 | 地点 | Pla | location"/></location>
        </class>
      </xsl:for-each>
    </classes>
  </xsl:template>
</xsl:stylesheet>
