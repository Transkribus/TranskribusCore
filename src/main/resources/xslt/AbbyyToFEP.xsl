<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:saxon="http://saxon.sf.net/" extension-element-prefixes="saxon"
    xmlns="http://www.uibk.ac.at/ulb/dea/schemas/fep-1.0.xsd" xmlns:abbyy="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml"
    xpath-default-namespace="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml http://www.uibk.ac.at/ulb/dea/schemas/fep-1.0.xsd http://www.uibk.ac.at/ulb/dea/schemas/fep-1.0.xsd">

    <xsl:output method="xml" encoding="UTF-8" version="1.0"/>
    
    <!--  Kind of array that contains all different styles found within this page -->
    <xsl:key name="allStyles" match="formatting"
        use="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"/>
    
    <xsl:variable name="textIndex2"><xsl:choose><xsl:when test="count(//charRecVariants)>0"><xsl:value-of select="2"></xsl:value-of></xsl:when><xsl:otherwise><xsl:value-of select="1"></xsl:value-of></xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="textIndex" select="number($textIndex2)"></xsl:variable>
    
    
  <!--  
      //formatting[not(@lang=preceding-sibling::formatting/@lang)]/@lang
    -->
    <xsl:template name="languages">
        <languageTable>
                <xsl:for-each select="distinct-values(//formatting/@lang)">
                    <language languageID="{concat('lang_', current())}" languageName="{.}"></language>
                </xsl:for-each>
        </languageTable>
    </xsl:template>
    
    <xsl:template name="styles2">
        <styleTable>
            <xsl:for-each select="//formatting[generate-id() = generate-id(key('allStyles',concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing))[1])]">
                <xsl:variable name="styleId" select="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"></xsl:variable>
                <style styleID="{$styleId}" fontSize="{@fs}" fontFace="{@ff}">
                    <xsl:if test="string-length(@bold) > 0">
                        <xsl:attribute name="bold" select="@bold"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@italic) > 0">
                        <xsl:attribute name="italic" select="@italic"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@underline) > 0">
                        <xsl:attribute name="underline" select="@underline"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@strikeout) > 0">
                        <xsl:attribute name="strikethrough" select="@strikeout"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@subscript) > 0">
                        <xsl:attribute name="subsuperscript" select="'subscript'"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@superscript) > 0">
                        <xsl:attribute name="subsuperscript" select="'superscript'"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@spacing) > 0">
                        <xsl:attribute name="spacing" select="@spacing"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@scaling) > 0">
                        <xsl:attribute name="scaling" select="@scaling"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@color) > 0">
                        <xsl:attribute name="color" select="@color"></xsl:attribute>
                    </xsl:if> 
                </style>
            </xsl:for-each>
        </styleTable>
    </xsl:template>
 
    <xsl:template match="/" xpath-default-namespace="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml">
        <document xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml http://www.uibk.ac.at/ulb/dea/schemas/fep-1.0.xsd http://www.uibk.ac.at/ulb/dea/schemas/fep-1.0.xsd">
            <xsl:apply-templates select="//abbyy:page" xpath-default-namespace="http://www.abbyy.com/FineReader_xml/FineReader8-schema-v2.xml"/>
        </document>
    </xsl:template>

    <xsl:template match="abbyy:page">
        <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
        <page width="{@width}" height="{@height}" resolution="{@resolution}" seq="0">
            <xsl:call-template name="languages"></xsl:call-template>
                <xsl:call-template name="styles2"></xsl:call-template>
            <xsl:apply-templates select="abbyy:block"/>
        </page>
    </xsl:template>
    
    <xsl:template match="abbyy:block">
        <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
        <xsl:variable name="seq">
            <xsl:for-each select="//abbyy:block">
                <xsl:if test="generate-id(.) = $actId">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <block typeName="{@blockType}" xpathToOriginalBlock="block[{position()}]" l="{@l}" r="{@r}" b="{@b}" t="{@t}" seq="0">
            <text>
                <xsl:apply-templates select="abbyy:text/abbyy:par">
                    <xsl:with-param name="current_block" select="$seq"></xsl:with-param>
                </xsl:apply-templates>
            </text>
        </block>
    </xsl:template>

    <xsl:template match="abbyy:par">
        <xsl:param name="current_block"></xsl:param>
        <xsl:variable name="hasDroppedCapitalChar">
            <xsl:choose>
                <xsl:when test="@dropCapCharsCount > 0">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
        <par xpathToOriginalParagraph="block[{$current_block}]/text/par[{position()}]" hasDroppedCapitalChar="{$hasDroppedCapitalChar}" t="{min(.//@t)}" b="{max(.//@b)}" r="{max(.//@r)}" l="{min(.//@l)}" seq="0">
            <xsl:if test="string-length(@startIndent) > 0">
                <xsl:attribute name="startIndent" select="@startIndent"></xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@rightIndent) > 0">
                <xsl:attribute name="rightIndent" select="@rightIndent"></xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@leftIndent) > 0">
                <xsl:attribute name="leftIndent" select="@leftIndent"></xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@lineSpacing) > 0">
                <xsl:attribute name="lineSpacing" select="@lineSpacing"></xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@align) > 0">
                <xsl:attribute name="alignment" select="@align"></xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="abbyy:line"></xsl:apply-templates>
        </par>
    </xsl:template>

    <xsl:template match="abbyy:line">
        <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
        <xsl:variable name="numberOfFormats" select="count(./formatting)"></xsl:variable>
        <line baseline="{@baseline}" l="{@l}" t="{@t}" r="{@r}" b="{@b}" seq="0">
            <xsl:choose>
                <xsl:when test="$numberOfFormats = 1">
                    <xsl:variable name="f" select="./formatting"></xsl:variable>
                    <xsl:attribute name="languageRef" select="concat('lang_', ./formatting/@lang)"></xsl:attribute>
                    <xsl:attribute name="styleRef" select="concat(translate($f/@ff, ' ', '_'), '_', $f/@fs, '_', $f/@bold, '_', $f/@italic,'_', $f/@subscript, '_', $f/@superscript,'_', $f/@underline, '_', $f/@strikeout, '_', $f/@color,'_', $f/@scaling,'_', $f/@spacing)"></xsl:attribute>
                    <xsl:apply-templates select="abbyy:formatting">
                        <xsl:with-param name="styleId" select="concat(translate($f/@ff, ' ', '_'), '_', $f/@fs, '_', $f/@bold, '_', $f/@italic,'_', $f/@subscript, '_', $f/@superscript,'_', $f/@underline, '_', $f/@strikeout, '_', $f/@color,'_', $f/@scaling,'_', $f/@spacing)"></xsl:with-param>
                        <xsl:with-param name="langId" select="concat('lang_', ./formatting/@lang)"></xsl:with-param>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="langIds" select="distinct-values(./formatting/@lang)"></xsl:variable>
                    <xsl:variable name="mostProminentLang">
                        <xsl:call-template name="applyMostProminentLanguageId">
                            <xsl:with-param name="langIds" select="$langIds"></xsl:with-param>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:attribute name="languageRef" select="$mostProminentLang"></xsl:attribute>
                    <xsl:variable name="mostProminentStyle">
                        <xsl:call-template name="applyMostProminentStyleId">
                            <xsl:with-param name="styleIds" select="//formatting[generate-id() = generate-id(key('allStyles',concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing))[1])]"></xsl:with-param>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:attribute name="styleRef" select="$mostProminentStyle"></xsl:attribute>
                    <xsl:apply-templates select="abbyy:formatting">
                        <xsl:with-param name="styleId" select="$mostProminentStyle"></xsl:with-param>
                        <xsl:with-param name="langId" select="$mostProminentLang"></xsl:with-param>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </line>
    </xsl:template>
    
    <!-- creates word nodes -->
    <xsl:template match="abbyy:formatting">
        <xsl:param name="styleId"></xsl:param>
        <xsl:param name="langId"></xsl:param>
        <xsl:variable name="lang" select="concat('lang_', @lang)"></xsl:variable>
        <xsl:choose>
            <xsl:when test="count(.//abbyy:charParams[@wordStart='true']) > 0">
                <xsl:call-template name="generateStrings">
                    <xsl:with-param name="wordstarts" select=".//abbyy:charParams[@wordStart='true']"></xsl:with-param>
                    <xsl:with-param name="langId" select="$langId"></xsl:with-param>
                    <xsl:with-param name="styleId" select="$styleId"></xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise> 
                <xsl:call-template name="generateStrings">
                    <xsl:with-param name="wordstarts" select=".//abbyy:charParams[1] | .//abbyy:charParams[string-length(normalize-space((text()[$textIndex]))) = 0]/following-sibling::charParams[1]"></xsl:with-param>
                    <xsl:with-param name="langId" select="$langId"></xsl:with-param>
                    <xsl:with-param name="styleId" select="$styleId"></xsl:with-param>
                </xsl:call-template> 
            </xsl:otherwise> 
        </xsl:choose> 
    </xsl:template>
    
    <xsl:template name="generateStrings">
        <xsl:param name="wordstarts"></xsl:param>
        <xsl:param name="styleId"></xsl:param>
        <xsl:param name="langId"></xsl:param>
        <xsl:variable name="style" select="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"></xsl:variable>
        <xsl:variable name="lang" select="concat('lang_', @lang)"></xsl:variable>
        
        <xsl:for-each select="$wordstarts">
            <xsl:variable name="actpos" select="position()"/>
            <xsl:variable name="wordlenght">
                <xsl:choose>
                    <xsl:when test="position()=last()">
                        <xsl:value-of select="count(./following-sibling::charParams) + 1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="count(./following-sibling::charParams) - count($wordstarts[$actpos+1]/following-sibling::charParams) - 1"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="wordChars" select=". | (./following-sibling::*)[$wordlenght > position()]"></xsl:variable>
            
            <xsl:variable name="hyphen" select="$wordChars[last() and text() = '-']"/>
            <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
            <xsl:variable name="value">
                <xsl:for-each select="$wordChars">
                    <xsl:value-of select="normalize-space((./text())[$textIndex])"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="rightCoord">
                    <xsl:choose>
                        <xsl:when test="string-length(normalize-space($wordChars[last()])) = 0">
                            <xsl:value-of select="$wordChars[last()-1]//@r"></xsl:value-of>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$wordChars[last()]//@r"></xsl:value-of>
                        </xsl:otherwise>
                    </xsl:choose>
            </xsl:variable>
            <xsl:variable name="leftCoord">
                <xsl:value-of select="$wordChars[1]//@l"></xsl:value-of>    
            </xsl:variable>
            <xsl:if test="string-length($value) > 0">
                <string l="{$leftCoord}" t="{min($wordChars//@t)}" r="{$rightCoord}" b="{max($wordChars//@b)}" seq="0">
                    <xsl:if test="not($style = $styleId)">
                        <xsl:attribute name="styleRef" select="$style"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="not($lang = $langId)">
                        <xsl:attribute name="languageRef" select="$lang"></xsl:attribute>
                    </xsl:if>
                   
                    <xsl:attribute name="value">
                        <xsl:for-each select="$wordChars">
                            <xsl:value-of select="normalize-space((./text())[$textIndex])"/>
                        </xsl:for-each>
                    </xsl:attribute>
                   
                    <xsl:if test="count($hyphen) > 0">
                        <xsl:attribute name="hasHyphen" select="'true'"/>
                    </xsl:if>
                    <xsl:call-template name="applyChars">
                        <xsl:with-param name="charParams" select="$wordChars"/>
                    </xsl:call-template>
                </string>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- creates character nodes -->
    <xsl:template name="applyChars">
        <xsl:param name="charParams"/>
        <xsl:for-each select="$charParams">
            <xsl:if test="string-length(normalize-space(text()[$textIndex]))>0">
                <xsl:variable name="actId" select="generate-id(.)"></xsl:variable>
                <xsl:variable name="suspicious">
                    <xsl:choose>
                        <xsl:when test="@charConfidence > 80">false</xsl:when>
                        <xsl:otherwise>true</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <charParams l="{@l}" t="{@t}" r="{@r}" b="{@b}" seq="0">
                    <xsl:if test="string-length(@suspicious) > 0">
                        <xsl:attribute name="suspicious" select="$suspicious"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@wordFromDictionary) > 0">
                        <xsl:attribute name="wordFromDictionary" select="@wordFromDictionary"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@wordNumeric) > 0">
                        <xsl:attribute name="wordNumeric" select="@wordNumeric"></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="string-length(@charConfidence) > 0">
                        <xsl:attribute name="charConfidence" select="@charConfidence"></xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="applyTextContent">
                        <xsl:with-param name="textContent" select="normalize-space(text()[$textIndex])"/>
                    </xsl:call-template>
                </charParams>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="applyTextContent">
        <xsl:param name="textContent" select="''"/>
        <xsl:value-of select="$textContent"/>
    </xsl:template>
    
    <xsl:template name="applyMostProminentStyleId">
        <xsl:param name="styleIds"></xsl:param>
        <xsl:param name="mostProminentStyle" select="-1"></xsl:param>
        <xsl:param name="mostProminentStyleCount" select="0"></xsl:param>
        <xsl:param name="position" select="1"></xsl:param>
        <xsl:choose>
            <xsl:when test="count($styleIds) >= $position">
                <xsl:variable name="tmpId" select="$styleIds[$position]"></xsl:variable>
                <xsl:variable name="actId">
                    <xsl:for-each select="$tmpId">
                        <xsl:value-of select="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"></xsl:value-of>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="count(./formatting[concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing) = $actId]//charParams) > $mostProminentStyleCount">
                        <xsl:call-template name="applyMostProminentStyleId">
                            <xsl:with-param name="position" select="$position + 1"></xsl:with-param>
                            <xsl:with-param name="styleIds" select="$styleIds"></xsl:with-param>
                            <xsl:with-param name="mostProminentStyle" select="$actId"></xsl:with-param>
                            <xsl:with-param name="mostProminentStyleCount" select="count(./formatting[concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing) = $actId]//charParams)"></xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="applyMostProminentStyleId">
                            <xsl:with-param name="position" select="$position + 1"></xsl:with-param>
                            <xsl:with-param name="styleIds" select="$styleIds"></xsl:with-param>
                            <xsl:with-param name="mostProminentStyle" select="$mostProminentStyle"></xsl:with-param>
                            <xsl:with-param name="mostProminentStyleCount" select="$mostProminentStyleCount"></xsl:with-param>
                        </xsl:call-template> 
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>  
            <xsl:otherwise>
                <xsl:value-of select="$mostProminentStyle"></xsl:value-of>
            </xsl:otherwise>
        </xsl:choose> 
    </xsl:template>

    <xsl:template name="applyMostProminentLanguageId">
        <xsl:param name="langIds"></xsl:param>
        <xsl:param name="mostProminentLang" select="-1"></xsl:param>
        <xsl:param name="mostProminentLangCount" select="0"></xsl:param>
        <xsl:param name="position" select="1"></xsl:param>
        <xsl:choose>
            <xsl:when test="count($langIds) >= $position">
                <xsl:variable name="actId" select="$langIds[$position]"></xsl:variable>
                <xsl:choose>
                    <xsl:when test="count(./formatting[@lang = $actId]//charParams) > $mostProminentLangCount">
                        <xsl:call-template name="applyMostProminentLanguageId">
                            <xsl:with-param name="position" select="$position + 1"></xsl:with-param>
                            <xsl:with-param name="langIds" select="$langIds"></xsl:with-param>
                            <xsl:with-param name="mostProminentLang" select="$actId"></xsl:with-param>
                            <xsl:with-param name="mostProminentLangCount" select="count(./formatting[@lang = $actId]//charParams)"></xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="applyMostProminentLanguageId">
                            <xsl:with-param name="position" select="$position + 1"></xsl:with-param>
                            <xsl:with-param name="langIds" select="$langIds"></xsl:with-param>
                            <xsl:with-param name="mostProminentLang" select="$mostProminentLang"></xsl:with-param>
                            <xsl:with-param name="mostProminentLangCount" select="$mostProminentLangCount"></xsl:with-param>
                        </xsl:call-template> 
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>  
            <xsl:otherwise>
                <xsl:value-of select="concat('lang_', $mostProminentLang)"></xsl:value-of>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
