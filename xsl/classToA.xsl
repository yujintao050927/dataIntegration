<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一课程XML -> A院系课程XML（表3-17） -->
  <xsl:template match="/">
    <classes>
      <xsl:for-each select="//*[local-name()='class']">
        <class>
          <课程编号><xsl:value-of select="id"/></课程编号>
          <课程名称><xsl:value-of select="name"/></课程名称>
          <学分><xsl:value-of select="score"/></学分>
          <课程教师><xsl:value-of select="teacher"/></课程教师>
          <上课地点><xsl:value-of select="location"/></上课地点>
        </class>
      </xsl:for-each>
    </classes>
  </xsl:template>
</xsl:stylesheet>
