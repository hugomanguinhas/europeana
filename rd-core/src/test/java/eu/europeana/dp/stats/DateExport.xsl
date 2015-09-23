<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    >

    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>

    <!-- Configuration -->

    <xsl:variable name="dates"    select="(1800 to 2015)"/>
    <xsl:variable name="patterns" select="(
                                        (: [YYYY] :)
                                           '^\s*[\[(]YEAR[\])][^?\w]*$'

                                        (: YYYY, YYYY-MM, YYYY-MM-DD :)
                                         , '^\s*YEAR(\s*[-/.]\d{1,2}){0,2}[^?\w]*$'

                                        (: DD-MM-YYYY :)
                                         , '^\s*\d{1,2}\s*[-=/.]\s*\d{1,2}\s*[-=/.]\s*YEAR[^?\w]*$')

                                        (: Annotated dates :)
                                         , '^\s*YEAR(\s*[-/.]\d{1,2}){0,2}\s*\[[Pp]ublication\][^?\w]*$'
                                         , '^\s*YEAR(\s*[-/.]\d{1,2}){0,2}\s*\(first performance\)[^?\w]*$'

                                        (: RANGES :)
                                         , '^\s*YEAR\s*[-=/]\s*YEAR[^?\w]*$'
                                         , '^\s*YEAR[-/.]\d{1,2}\s*[-=/]\s*YEAR[-/.]\d{1,2}[^?\w]*$'
                                         , '^\s*YEAR[-/.]\d{1,2}[-/.]\d{1,2}\s*[-=/]\s*YEAR[-/.]\d{1,2}[-/.]\d{1,2}[^?\w]*$'

                                        (: TIMESTAMPS: 2011-11-29T14:49:21Z :)
                                         , '^\s*YEAR-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}Z?[^?\w]*$'
                                         "
    />

    <!-- Implementation -->

    <xsl:template match="/">
        <xsl:text>date,count,in,out&#xa;</xsl:text>
        <xsl:apply-templates select="response/lst/lst[@name='facet_fields']"/>
    </xsl:template>

    <xsl:template match="lst[@name='facet_fields']">
        <xsl:variable name="ctx" select="./lst"/>

        <xsl:for-each select="$dates">
            <xsl:call-template name="row">
                <xsl:with-param name="date" select="string(.)"/>
                <xsl:with-param name="node" select="$ctx"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="row">
        <xsl:param name="date"/>
        <xsl:param name="node"/>

        <xsl:variable name="s1" select="$node/int[contains(string(@name),$date)]"/>
        <xsl:variable name="p" select="for $x in $patterns return fn:replace($x,'YEAR',$date)"/>
        <xsl:variable name="s2" select="$s1[some $x in $p satisfies fn:matches(string(@name),$x)]"/>

        <xsl:value-of select="$date"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of select="sum($s2/text())"/>
        <xsl:text>,</xsl:text>
        <xsl:text>"</xsl:text><xsl:value-of select="fn:string-join($s2/@name,'|')"/><xsl:text>"</xsl:text>
        <xsl:text>&#xa;</xsl:text>
    </xsl:template>

</xsl:stylesheet>
