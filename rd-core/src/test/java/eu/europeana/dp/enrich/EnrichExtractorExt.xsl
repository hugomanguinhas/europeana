<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"

    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:edm="http://www.europeana.eu/schemas/edm/"

    exclude-result-prefixes="xsl fn xs"
    >

    <xsl:output omit-xml-declaration="no" indent="yes"/>

    <!-- Configuration -->

    <xsl:variable name="prefixes" select="(
        'http://nauji.aruodai.lt/duomenys/main/terminai/objects'
        )"
    />

    <!-- Implementation -->

    <xsl:template match="rdf:RDF">

        <xsl:copy>
            <xsl:copy-of select="@*"/>

            <xsl:apply-templates select="edm:ProvidedCHO" mode="split"/>
    
            <xsl:copy-of select="not (edm:ProvidedCHO or ore:Aggregation)"/>
        </xsl:copy>

    </xsl:template>

    <xsl:template match="node()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="edm:ProvidedCHO" mode="split">

        <xsl:variable name="a"   select="../ore:Aggregation"/>
        <xsl:variable name="eid" select="fn:concat(@rdf:about, '_e')"/>
        <xsl:variable name="pid" select="fn:concat($a/@rdf:about, '_e')"/>

        <edm:ProvidedCHO>
            <xsl:copy-of select="@*"/>
        </edm:ProvidedCHO>

        <ore:Proxy rdf:about="">
            <xsl:copy-of select="node()[not (some $x in $prefixes satisfies fn:starts-with(@rdf:resource,$x))]"/>
        </ore:Proxy>

        <xsl:copy-of select="$a"/>

        <ore:Proxy rdf:about="{$id}">
            <xsl:copy-of select="dc:title|edm:type"/>
            <xsl:copy-of select="node()[some $x in $prefixes satisfies fn:starts-with(@rdf:resource,$x)]"/>
        </ore:Proxy>

        <xsl:for-each select="$a">
            <ore:Aggregation rdf:about="{$aid}">
                <edm:aggregatedCHO rdf:resource="{$id}"/>
                <xsl:copy-of select="edm:dataProvider|edm:shownAt|edm:shownBy|edm:provider|edm:rights"/>
            </ore:Aggregation>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
