<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns="http://www.loc.gov/standards/alto/ns-v2#"
    xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd  http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto-v2.0.xsd"
    xpath-default-namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15">
    
    <xsl:output omit-xml-declaration="yes"/>
    
    <xsl:template match="/">
        <xsl:for-each select="//page:TextLine">
            <xsl:variable name="lineString">
                <xsl:value-of select="./TextEquiv/Unicode/text()"/>
            </xsl:variable>
            <xsl:value-of select="replace($lineString, '(^.*)[&#x00AC;|&#x002D;|&#x2212;|&#x201E;|&#x003D;]$', '$1&#x2010;')" />
            <xsl:text>&#xa;</xsl:text>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
