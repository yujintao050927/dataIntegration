<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <!-- 统一课程XML -> B院系课程XML（表3-17） -->
  <xsl:template match="/">
    <classes>
      <xsl:for-each select="//*[local-name()='class']">
        <class>
          <课程号><xsl:value-of select="id"/></课程号>
          <课程名><xsl:value-of select="name"/></课程名>
          <学分><xsl:value-of select="score"/></学分>
          <教师><xsl:value-of select="teacher"/></教师>
          <地点><xsl:value-of select="location"/></地点>
          <学时/>
          <属性/>
        </class>
      </xsl:for-each>
    </classes>
  </xsl:template>
</xsl:stylesheet>
