<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns="http://www.loc.gov/standards/alto/ns-v2#"
    xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd  http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto-v2.0.xsd"
    xpath-default-namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15">

    <xsl:output method="xml" encoding="UTF-8" version="1.0"/>

    <xsl:key name="allStyles" match="TextStyle"
        use="concat(translate(@fontFamily, ' ', '_'), '_', @fontSize, '_', @serif, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underlined, '_', @textColour)"/>

    <xsl:template name="languageTable">
        <languageTable> </languageTable>
    </xsl:template>
    
    <xsl:variable name="language">
        <xsl:call-template name="determinePrimaryLanguage"/>
    </xsl:variable>
    
    <xsl:template name="description">
        <Description>
            <MeasurementUnit>pixel</MeasurementUnit>
        </Description>
    </xsl:template>

    <!--  styles are collected at the beginning in alto   -->
    <xsl:template name="styleTable">
        <Styles>
            <xsl:for-each
                select="//TextStyle[generate-id() = generate-id(key('allStyles',concat(translate(@fontFamily, ' ', '_'), '_', @fontSize, '_', @serif, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underlined, '_', @textColour))[1])]">
                <xsl:variable name="styleId"
                    select="concat(translate(@fontFamily, ' ', '_'), '_', @fontSize, '_', @serif, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underlined, '_', @textColour)"/>
                <TextStyle ID="{$styleId}" FONTSIZE="{@fontSize}" FONTFAMILY="{@fontFamily}">                  
                    <xsl:variable name="FONTTYPE">
                        <xsl:choose>
                            <xsl:when test="@serif='true'">serif</xsl:when>
                            <xsl:otherwise>sans-serif</xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="fontStyle">
                        <xsl:choose>
                            <xsl:when test="string-length(@bold) > 0">bold </xsl:when>
                            <xsl:when test="string-length(@italic) > 0">italic </xsl:when>
                            <xsl:when test="string-length(@underline) > 0">underline </xsl:when>
                            <xsl:when test="string-length(@subscript) > 0">subscript </xsl:when>
                            <xsl:when test="string-length(@superscript) > 0">superscript </xsl:when>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:if test="string-length($fontStyle)>0">
                        <xsl:attribute name="FONTSTYLE" select="normalize-space($fontStyle)"/>
                    </xsl:if>
                    <xsl:if test="string-length(@textColour) > 0">
                        <xsl:attribute name="FONTCOLOR" select="@textColour"/>
                    </xsl:if>
                </TextStyle>
            </xsl:for-each>
        </Styles>
    </xsl:template>

    <xsl:template match="/">
        <xsl:apply-templates select="page:PcGts"/>
    </xsl:template>


    <xsl:template match="page:PcGts">
        <alto xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto-v2.0.xsd">
            <xsl:apply-templates select="page:Page"/>
        </alto>
    </xsl:template>


    <xsl:template match="page:Page">
        <xsl:variable name="actId" select="generate-id(.)"/>
        <xsl:variable name="actIdInt" select="position()"/>
        <xsl:variable name="maxXFromPrintspace">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:PrintSpace/page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="maxYFromPrintspace">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:PrintSpace/page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minXFromPrintspace">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="./page:PrintSpace/page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minYFromPrintspace">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="./page:PrintSpace/page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="w">
            <xsl:choose>
                <xsl:when test="string-length(@imageWidth)>0">
                    <xsl:value-of select="@imageWidth"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$maxXFromPrintspace"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="h">
            <xsl:choose>
                <xsl:when test="string-length(@imageHeight)>0">
                    <xsl:value-of select="@imageHeight"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$maxYFromPrintspace"/>
<!--                    <xsl:call-template name="getMaxFromPointList">
                        <xsl:with-param name="coords" select="./page:PrintSpace/page:Coords/@points"/>
                        <xsl:with-param name="ordinate" select="'Y'"/>
                    </xsl:call-template>-->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="description"/>
        <xsl:call-template name="styleTable"/>
        <Layout>
            <Page ID="Page{$actIdInt}" PHYSICAL_IMG_NR="{$actIdInt}" HEIGHT="{$h}" WIDTH="{$w}">
                <!--   TODO: at the moment the margins are all 0,0,0,0 and printspace is the whole page; maybe better to calculate is but first check how this is handled in TRP  -->
                <TopMargin>
                    <xsl:attribute name="HEIGHT" select="$minYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="WIDTH" select="$w"></xsl:attribute> 
                    <xsl:attribute name="VPOS" select="0"></xsl:attribute> 
                    <xsl:attribute name="HPOS" select="0"></xsl:attribute> 
                </TopMargin>
                <LeftMargin>
                    <xsl:attribute name="HEIGHT" select="$maxYFromPrintspace - $minYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="WIDTH" select="$minXFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="VPOS" select="$minYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="HPOS" select="0"></xsl:attribute> 
                </LeftMargin>
                <RightMargin>
                    <xsl:attribute name="HEIGHT" select="$maxYFromPrintspace - $minYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="WIDTH" select="$w - $maxXFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="VPOS" select="$minYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="HPOS" select="$maxXFromPrintspace"></xsl:attribute> </RightMargin>
                <BottomMargin>
                    <xsl:attribute name="HEIGHT" select="$h - $maxYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="WIDTH" select="$w"></xsl:attribute> 
                    <xsl:attribute name="VPOS" select="$maxYFromPrintspace"></xsl:attribute> 
                    <xsl:attribute name="HPOS" select="0"></xsl:attribute> </BottomMargin>
                <PrintSpace>
                    <xsl:attribute name="HEIGHT" select="$maxYFromPrintspace - $minYFromPrintspace"></xsl:attribute>
                    <xsl:attribute name="WIDTH" select="$maxXFromPrintspace - $minXFromPrintspace"></xsl:attribute>
                    <xsl:attribute name="VPOS" select="$minYFromPrintspace"></xsl:attribute>
                    <xsl:attribute name="HPOS" select="$minXFromPrintspace"></xsl:attribute>
                    <xsl:apply-templates select="//page:TextRegion"/>
                    <xsl:apply-templates select="//page:SeparatorRegion"/>
                    <xsl:apply-templates select="//page:GraphicRegion"/>
                    <xsl:apply-templates select="//page:ImageRegion"/>
                    <xsl:apply-templates select="//page:TableRegion"/>
                </PrintSpace>
            </Page>
        </Layout>
    </xsl:template>


    <xsl:template match="page:TextRegion">
        <xsl:variable name="maxX">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minX">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>

        <TextBlock ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
            <xsl:attribute name="language" select="$language"></xsl:attribute>
            <Shape>  
                <Polygon>
                    <xsl:attribute name="POINTS" select="./page:Coords/@points"></xsl:attribute>
                </Polygon> 
            </Shape>
                <xsl:if test="count(./page:TextLine)>0">
                    <xsl:apply-templates select="page:TextLine">
                        <xsl:with-param name="current_block" select="position()"/>
                        <xsl:with-param name="stringsBefore" select="count(.//preceding-sibling::TextRegion//page:Word)"/>
                        <xsl:with-param name="linesBefore" select="count(.//preceding-sibling::TextRegion//page:TextLine)"/>
                    </xsl:apply-templates>
                </xsl:if>
        </TextBlock>
    </xsl:template>

    <xsl:template match="page:SeparatorRegion">
        <GraphicalElement ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
        </GraphicalElement>
    </xsl:template>

    <xsl:template match="page:GraphicRegion">
        <Illustration ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
        </Illustration>
    </xsl:template>

    <xsl:template match="page:ImageRegion">
        <Illustration ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
        </Illustration>
    </xsl:template>

    <!-- produce ComposedBlock with @TYPE='table' and included TextBlock per TextRegion -->
    <xsl:template match="page:TableRegion">
        <ComposedBlock ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
            <xsl:attribute name="TYPE">table</xsl:attribute>
            <xsl:choose>
                <xsl:when test="count(.//page:TextRegion) > 0">
                    <xsl:apply-templates select=".//page:TextRegion"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="var1" select="@id" />
                    <xsl:variable name="var2">
                        <xsl:number count="TableRegion" format="1"/>
                    </xsl:variable>
                    <xsl:variable name="tb_id">
                        <xsl:value-of select="concat($var1, '_', $var2)"/>
                    </xsl:variable>
                    <TextBlock ID="{$tb_id}">
                        <xsl:call-template name="applyCoordinates"/>
                    </TextBlock>
                </xsl:otherwise>
            </xsl:choose>
        </ComposedBlock>
    </xsl:template>



    <xsl:template match="page:TextLine">
        <xsl:param name="current-block"/>
        <xsl:param name="stringsBefore"/>
        <xsl:param name="linesBefore"/>

        <xsl:variable name="maxY">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>

        <TextLine ID="{@id}" BASELINE="{$maxY}">
            <xsl:call-template name="applyCoordinates"/>
            <xsl:call-template name="applyStyleId"/>
            <!--  if textline without words then we create a new String with the total textline as CONTENT  -->
            <xsl:choose>
                <xsl:when test="count(./page:Word)>0">
                    <xsl:apply-templates select="page:Word">
                        <xsl:with-param name="current_block" select="$current-block"/>
                        <xsl:with-param name="stringsBefore" select="count(.//preceding-sibling::TextRegion//page:Word) + $stringsBefore"/>
                        <xsl:with-param name="linesBefore" select="count(.//preceding-sibling::TextRegion//page:TextLine) + $linesBefore"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <String ID="string_{@id}">
                        <xsl:call-template name="applyCoordinates"/>
                        <xsl:call-template name="applyStyleId"/>
                        <xsl:attribute name="CONTENT" select="./TextEquiv/Unicode/text()"/>
                    </String>
                </xsl:otherwise>
            </xsl:choose>
        </TextLine>
    </xsl:template>


    <xsl:template match="page:Word">
        <xsl:param name="stringsBefore"/>
        <String ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
            <xsl:call-template name="applyStyleId"/>
            <xsl:attribute name="CONTENT" select="./TextEquiv/Unicode/text()"/>
        </String>
    </xsl:template>
   
   
    <xsl:template name="applyCoordinates">
        <xsl:variable name="maxX">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="maxY">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minX">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minY">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:attribute name="HEIGHT" select="$maxY - $minY"/>
        <xsl:attribute name="WIDTH" select="$maxX - $minX"/>
        <xsl:attribute name="VPOS" select="$minY"/>
        <xsl:attribute name="HPOS" select="$minX"/>
    </xsl:template>
    
    
    <xsl:template name="getMaxFromPointList">
        <xsl:param name="actMax" select="0"/>
        <xsl:param name="coords" select="'0,0 '"/>
        <xsl:param name="ordinate" select="'X'"/>
        
        <xsl:variable name="actPoint" select="substring-before($coords, ' ')"/>
        <xsl:variable name="actRest" select="substring-after($coords, ' ')"/>
        <xsl:variable name="actOrdinate">
            <xsl:choose>
                <xsl:when test="$ordinate='X'">
                    <xsl:value-of select="substring-before($actPoint, ',')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="substring-after($actPoint, ',')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="newMax">
            <xsl:choose>
                <xsl:when test="number($actOrdinate) > number($actMax)">
                    <xsl:value-of select="number($actOrdinate)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$actMax"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string-length($actRest)>0 and contains($actRest,',')">
                <xsl:call-template name="getMaxFromPointList">
                    <xsl:with-param name="actMax" select="$newMax"/>
                    <xsl:with-param name="coords" select="$actRest"/>
                    <xsl:with-param name="ordinate" select="$ordinate"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$newMax"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template name="getMinFromPointList">
        <xsl:param name="actMin" select="9999999"/>
        <xsl:param name="coords" select="'0,0 '"/>
        <xsl:param name="ordinate" select="'X'"/>
        
        <xsl:variable name="actPoint" select="substring-before($coords, ' ')"/>
        <xsl:variable name="actRest" select="substring-after($coords, ' ')"/>
        <xsl:variable name="actOrdinate">
            <xsl:choose>
                <xsl:when test="$ordinate='X'">
                    <xsl:value-of select="substring-before($actPoint, ',')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="substring-after($actPoint, ',')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="newMin">
            <xsl:choose>
                <xsl:when test="number($actMin) > number($actOrdinate)">
                    <xsl:value-of select="number($actOrdinate)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$actMin"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string-length($actRest)>0 and contains($actRest,',')">
                <xsl:call-template name="getMinFromPointList">
                    <xsl:with-param name="actMin" select="$newMin"/>
                    <xsl:with-param name="coords" select="$actRest"/>
                    <xsl:with-param name="ordinate" select="$ordinate"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$newMin"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="applyStyleId">
        <xsl:for-each select="./TextStyle">
            <xsl:variable name="styleId"
                select="concat(translate(@fontFamily, ' ', '_'), '_', @fontSize, '_', @serif, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underlined, '_', @textColour)"/>
            <xsl:attribute name="STYLEREFS" select="$styleId"/>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="determinePrimaryLanguage">
        <xsl:param name="comments" select="//Metadata/Comments"/>
        <xsl:value-of>
            <xsl:if test="contains($comments, 'PrimaryLanguage:')">
                <xsl:call-template name="resolveLang">
                    <xsl:with-param name="frLang">
                        <xsl:value-of select="normalize-space(substring-after($comments, 'PrimaryLanguage:'))"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:value-of>
    </xsl:template>
    
    
    <xsl:template name="resolveLang">
        <xsl:param name="frLang"/>
        <xsl:choose>
            <xsl:when test="starts-with($frLang, 'German')">
                <xsl:text>de</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>en</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
