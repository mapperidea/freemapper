<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    <xsl:variable name="indent-spaces">§§§§</xsl:variable>

    <xsl:template match="/map">
        <xsl:apply-templates select="node"/>
    </xsl:template>

    <xsl:template match="node">
        <xsl:param name="indent" select="''"/>

        <xsl:value-of select="$indent"/>

        <xsl:choose>
            <xsl:when test="icon/@BUILTIN = 'element'">[e] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'tag_green'">[v] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Descriptor.grouping'">[g] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'bricks'">[g] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Package'">[p] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Descriptor.class'">[c] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Descriptor.bean'">[b] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Mapping.directToField'">[d] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Mapping.oneToOne'">[r] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Mapping.oneToMany'">[o] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Mapping.manyToOne'">[m] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Method.public'">[x] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'tag_yellow'">[y] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'elementOutput'">[x] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'Mapping.directMap'">[h] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'textNode'">[t] </xsl:when>
            <xsl:when test="icon/@BUILTIN = 'bullet_key'">[k] </xsl:when>
            <xsl:otherwise>
                <xsl:if test="icon/@BUILTIN">
                    <xsl:text>[</xsl:text>
                    <xsl:value-of select="icon/@BUILTIN"/>
                    <xsl:text>] </xsl:text>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:variable name="nodeText" select="@TEXT"/>
        
        <xsl:choose>
            <xsl:when test="contains($nodeText, '&#xA;')">
                <xsl:value-of select="substring-before($nodeText, '&#xA;')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$nodeText"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>&#xA;</xsl:text> <xsl:if test="contains($nodeText, '&#xA;')">
            <xsl:call-template name="print-remaining-lines">
                <xsl:with-param name="text" select="substring-after($nodeText, '&#xA;')"/>
                <xsl:with-param name="indent" select="concat($indent, $indent-spaces)"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates select="node">
            <xsl:with-param name="indent" select="concat($indent, $indent-spaces)"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template name="print-remaining-lines">
        <xsl:param name="text"/>
        <xsl:param name="indent"/>
        
        <xsl:value-of select="substring($indent,1,string-length($indent)-2)"/>
        <xsl:text>| </xsl:text>
        
        <xsl:variable name="current-line">
            <xsl:choose>
                <xsl:when test="contains($text, '&#xA;')">
                    <xsl:value-of select="substring-before($text, '&#xA;')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$text"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:value-of select="$current-line"/>
        <xsl:text>&#xA;</xsl:text> <xsl:if test="contains($text, '&#xA;')">
            <xsl:call-template name="print-remaining-lines">
                <xsl:with-param name="text" select="substring-after($text, '&#xA;')"/>
                <xsl:with-param name="indent" select="$indent"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>