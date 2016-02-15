<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    >

    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>

    <!-- Configuration -->

    <xsl:variable name="patterns" select="(

                                        (: [YYYY] :)
                                           '^\s*[\[(]\d{4}[\])][^?\w]*$'

                                        (: YYYY, YYYY-MM, YYYY-MM-DD :)
                                         , '^\s*\d{4}(\s*[-/.]\d{1,2}){0,2}[^?\w]*$'

                                        (: MM-YYYY, DD-MM-YYYY :)
                                         , '^(\s*\d{1,2}\s*[-/.]){1,2}\s*\d{4}[^?\w]*$'

                                        (: Annotated dates :)
                                         , '^\s*\d{4}([-/.]\d{1,2}){0,2}\s*\[[Pp]ublication\][^?\w]*$'
                                         , '^\s*\d{4}([-/.]\d{1,2}){0,2}\s*\(first performance\)[^?\w]*$'

                                        (: RANGES :)
                                         , '^\s*\d{4}\s*[-=/.]\s*\d{4}[^?\w]*$'
                                         , '^\s*\d{4}[-/.]\d{1,2}\s*[-=/]\s*\d{4}[-/.]\d{1,2}[^?\w]*$'
                                         , '^\s*\d{4}[-/.]\d{1,2}[-/.]\d{1,2}\s*[-=/]\s*\d{4}[-/.]\d{1,2}[-/.]\d{1,2}[^?\w]*$'

                                        (: TIMESTAMPS: 2011-11-29T14:49:21Z :)
                                         , '^\s*\d{4}-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}Z?[^?\w]*$'
                                         )"
    />

    <!-- Implementation -->

    <xsl:template match="/">
        <xsl:text>pattern,count,matched&#xa;</xsl:text>
        <xsl:apply-templates select="response/lst/lst[@name='facet_fields']"/>
    </xsl:template>

    <xsl:template match="lst[@name='facet_fields']">

        <xsl:variable name="nodes" select="./lst/int"/> 

        <xsl:for-each select="$patterns">
            <xsl:variable name="p" select="."/>
	        <xsl:variable name="s" select="$nodes[fn:matches(string(@name),$p)]"/>

            <xsl:text>"</xsl:text><xsl:value-of select="$p"/><xsl:text>"</xsl:text>

            <xsl:text>,</xsl:text>
            <xsl:value-of select="sum($s/text())"/>
            <xsl:text>&#xa;</xsl:text>

            <xsl:for-each select="$s">
                <xsl:text>,</xsl:text>
                <xsl:value-of select="./text()"/>
                <xsl:text>,"</xsl:text>
                <xsl:value-of select="./@name"/>
                <xsl:text>"&#xa;</xsl:text>

            </xsl:for-each>

        </xsl:for-each>

        <xsl:text>&#xa;</xsl:text>

        <xsl:text>Unmatched patterns&#xa;</xsl:text>
        <xsl:variable name="s" select="$nodes[not (some $x in $patterns satisfies fn:matches(string(@name),$x))]"/>
        <xsl:for-each select="$s">
            <xsl:value-of select="./@name"/><xsl:text>&#xa;</xsl:text>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
