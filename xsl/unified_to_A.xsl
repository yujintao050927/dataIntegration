<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <courses department="A">
            <xsl:for-each select="/courses/course">
                <course>
                    <course_id><xsl:value-of select="course_id"/></course_id>
                    <course_name><xsl:value-of select="course_name"/></course_name>
                    <credit><xsl:value-of select="credit"/></credit>
                    <teacher><xsl:value-of select="teacher"/></teacher>
                    <location><xsl:value-of select="location"/></location>
                    <is_shared><xsl:value-of select="is_shared"/></is_shared>
                </course>
            </xsl:for-each>
        </courses>
    </xsl:template>
</xsl:stylesheet>
