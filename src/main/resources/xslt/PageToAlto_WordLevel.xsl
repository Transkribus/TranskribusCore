<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns="http://www.loc.gov/standards/alto/ns-v2#"
    xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd  http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto-v2.0.xsd"
    xpath-default-namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15">

    <xsl:output indent="yes" method="xml" encoding="UTF-8" version="1.0"/>
    
    <!-- if set to false, the tags will not be exported -->
    <xsl:param name="includeTags" select="false()"/>

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
            <OCRProcessing ID="IdOcr">
                <ocrProcessingStep>
                    <processingDateTime>
                        <xsl:value-of select="current-dateTime()"/>
                    </processingDateTime>
                    <processingSoftware>
                        <softwareCreator>READ COOP</softwareCreator>
                        <softwareName>Transkribus</softwareName>
                    </processingSoftware>
                </ocrProcessingStep>
            </OCRProcessing>
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

    <xsl:template name="tags">
        <Tags>
            <xsl:for-each select="//page:TextRegion">
                <xsl:for-each select="child::TextLine[contains(@custom, 'person')]">
                    <xsl:call-template name="createNamedEntity">
                        <xsl:with-param name="text" select="./TextEquiv/Unicode/text()"/>
                        <xsl:with-param name="remainingCustomTag" select="@custom"/>
                        <xsl:with-param name="type" select="'person'"/>
                        <xsl:with-param name="shownType" select="'person'"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:for-each select="child::TextLine[contains(@custom, 'place')]">
                    <xsl:call-template name="createNamedEntity">
                        <xsl:with-param name="text" select="./TextEquiv/Unicode/text()"/>
                        <xsl:with-param name="remainingCustomTag" select="@custom"/>
                        <xsl:with-param name="type" select="'place'"/>
                        <xsl:with-param name="shownType" select="'location'"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:for-each>
        </Tags>
    </xsl:template>

    <xsl:template name="createNamedEntity">
        <xsl:param name="text"/>
        <xsl:param name="remainingCustomTag"/>
        <xsl:param name="type"/>
        <xsl:param name="shownType"/>
        <xsl:variable name="value"
            select="substring-before(substring-after($remainingCustomTag, concat($type, ' {')), '}')"/>
        <xsl:variable name="remaining"
            select="substring-after($remainingCustomTag, concat($type, ' {'))"/>
        <xsl:variable name="offset"
            select="substring-before(substring-after($value, 'offset:'), ';')"/>
        <xsl:variable name="length"
            select="substring-before(substring-after($value, 'length:'), ';')"/>
        <xsl:variable name="lineID" select="@id"/>
        <xsl:variable name="id"
            select="concat('Tag_', $lineID, '_', $offset, '_', $length, '_', substring($type,1,3))"/>
        <NamedEntityTag ID="" LABEL="" TYPE="">
            <xsl:attribute name="ID" select="$id"/>
            <xsl:attribute name="LABEL" select="substring($text,number($offset)+1,number($length))"/>
            <xsl:attribute name="TYPE" select="$shownType"/>
        </NamedEntityTag>
        <xsl:choose>
            <xsl:when test="contains($remaining, $type)">
                <xsl:call-template name="createNamedEntity">
                    <xsl:with-param name="text" select="$text"/>
                    <xsl:with-param name="remainingCustomTag" select="$remaining"/>
                    <xsl:with-param name="type" select="$type"/>
                    <xsl:with-param name="shownType" select="$shownType"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="createNamedEntityRef">
        <xsl:param name="remainingCustomTag"/>
        <xsl:param name="tagref" select="''"/>
        <xsl:param name="lineString"/>
        <xsl:param name="wordString"/>
<!--        <xsl:message>
            <xsl:value-of select="$remainingCustomTag"/>
        </xsl:message>-->
        <xsl:choose>
            <xsl:when
                test="starts-with($remainingCustomTag, 'person') or starts-with($remainingCustomTag, 'place')">
                <xsl:variable name="type" select="substring-before($remainingCustomTag, ' ')"/>
                <xsl:variable name="value"
                    select="substring-before(substring-after($remainingCustomTag, concat($type, ' {')), '}')"/>
                <xsl:variable name="remaining" select="substring-after($remainingCustomTag, '} ')"/>
                <xsl:variable name="offset"
                    select="substring-before(substring-after($value, 'offset:'), ';')"/>
                <xsl:variable name="length"
                    select="substring-before(substring-after($value, 'length:'), ';')"/>
                <xsl:variable name="tagValue"
                    select="substring($lineString, number($offset)+1, number($length))"/>
                <!--                <xsl:element name="{$type}"><xsl:value-of select="$remaining"/></xsl:element>-->
                <xsl:variable name="lineID" select="@id"/>
                <xsl:variable name="id"
                    select="concat('Tag_', $lineID, '_', $offset, '_', $length, '_', substring($type,1,3))"/>
                <xsl:variable name="ref">
                    <xsl:choose>
                        <xsl:when test="contains($tagValue, $wordString)">
                            <xsl:value-of select="concat($tagref, $id)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$tagref"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="contains($remaining, 'person') or contains($remaining, 'place')">

                        <xsl:call-template name="createNamedEntityRef">
                            <xsl:with-param name="remainingCustomTag" select="$remaining"/>
                            <xsl:with-param name="tagref" select="concat($ref, ' ')"/>
                            <xsl:with-param name="lineString" select="$lineString"/>
                            <xsl:with-param name="wordString" select="$wordString"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:if test="string-length(normalize-space($ref))>0">
                            <xsl:attribute name="TAGREFS" select="normalize-space($ref)"/>

                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="/">
        <xsl:apply-templates select="page:PcGts"/>
    </xsl:template>


    <xsl:template match="page:PcGts">
        <alto xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto.xsd">
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
        <xsl:variable name="foundMinY">
            <xsl:choose>
                <xsl:when test="$minYFromPrintspace=9999999">
                    <xsl:value-of select="0"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$minYFromPrintspace"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="foundMinX">
            <xsl:choose>
                <xsl:when test="$minXFromPrintspace=9999999">
                    <xsl:value-of select="0"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$minXFromPrintspace"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="foundMaxY">
            <xsl:choose>
                <xsl:when test="$maxYFromPrintspace=0">
                    <xsl:value-of select="$h"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$maxYFromPrintspace"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="foundMaxX">
            <xsl:choose>
                <xsl:when test="$maxXFromPrintspace=0">
                    <xsl:value-of select="$w"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$maxXFromPrintspace"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="description"/>
        <xsl:if test="$includeTags">
            <xsl:call-template name="tags"/>
        </xsl:if>
        <!-- <xsl:call-template name="styleTable"/> -->
        <Layout>
            <Page ID="Page{$actIdInt}" PHYSICAL_IMG_NR="{$actIdInt}" HEIGHT="{$h}" WIDTH="{$w}">
                <!--   TODO: at the moment the margins are all 0,0,0,0 and printspace is the whole page; maybe better to calculate is but first check how this is handled in TRP  -->
                <TopMargin>
                    <xsl:attribute name="HEIGHT" select="$foundMinY"/>
                    <xsl:attribute name="WIDTH" select="$w"/>
                    <xsl:attribute name="VPOS" select="0"/>
                    <xsl:attribute name="HPOS" select="0"/>
                </TopMargin>
                <LeftMargin>
                    <xsl:attribute name="HEIGHT" select="$foundMaxY - $foundMinY"/>
                    <xsl:attribute name="WIDTH" select="$foundMinX"/>
                    <xsl:attribute name="VPOS" select="$foundMinY"/>
                    <xsl:attribute name="HPOS" select="0"/>
                </LeftMargin>
                <RightMargin>
                    <xsl:attribute name="HEIGHT" select="$foundMaxY - $foundMinY"/>
                    <xsl:attribute name="WIDTH" select="$w - $foundMaxX"/>
                    <xsl:attribute name="VPOS" select="$foundMinY"/>
                    <xsl:attribute name="HPOS" select="$foundMaxX"/>
                </RightMargin>
                <BottomMargin>
                    <xsl:attribute name="HEIGHT" select="$h - $foundMaxY"/>
                    <xsl:attribute name="WIDTH" select="$w"/>
                    <xsl:attribute name="VPOS" select="$foundMaxY"/>
                    <xsl:attribute name="HPOS" select="0"/>
                </BottomMargin>
                <PrintSpace>
                    <xsl:attribute name="HEIGHT" select="$foundMaxY - $foundMinY"/>
                    <xsl:attribute name="WIDTH" select="$foundMaxX - $foundMinX"/>
                    <xsl:attribute name="VPOS" select="$foundMinY"/>
                    <xsl:attribute name="HPOS" select="$foundMinX"/>
                    <xsl:apply-templates select="//page:TextRegion"/>
                    <xsl:apply-templates select="//page:SeparatorRegion"/>
                    <xsl:apply-templates select="//page:GraphicRegion"/>
                    <xsl:apply-templates select="//page:ImageRegion"/>
                    <xsl:apply-templates select="//page:TableRegion"/>
                </PrintSpace>
            </Page>
        </Layout>
    </xsl:template>


    <xsl:template match="page:TextRegion | page:TableCell">
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
            <!-- <xsl:attribute name="language" select="$language"></xsl:attribute> -->
            <Shape>
                <Polygon>
                    <xsl:attribute name="POINTS" select="./page:Coords/@points"/>
                </Polygon>
            </Shape>
            <xsl:if test="count(./page:TextLine)>0">
                <xsl:apply-templates select="page:TextLine">
                    <xsl:with-param name="current_block" select="position()"/>
                    <xsl:with-param name="stringsBefore"
                        select="count(.//preceding-sibling::TextRegion//page:Word)"/>
                    <xsl:with-param name="linesBefore"
                        select="count(.//preceding-sibling::TextRegion//page:TextLine)"/>
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
                <xsl:when test="count(.//page:TableCell) > 0">
                    <xsl:apply-templates select=".//page:TableCell"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="var1" select="@id"/>
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

        <xsl:variable name="maxBlY">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="./page:Coords/@points"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>

        <TextLine ID="{@id}" BASELINE="{$maxBlY}">
            <xsl:call-template name="applyCoordinates"/>
            <!-- <xsl:call-template name="applyStyleId"/> -->
            <!--  if textline without words then we create a new String with the total textline as CONTENT  -->
            <xsl:choose>
                <xsl:when test="count(./page:Word)>0">
                    <xsl:apply-templates select="page:Word">
                        <xsl:with-param name="current_block" select="$current-block"/>
                        <xsl:with-param name="stringsBefore"
                            select="count(.//preceding-sibling::TextRegion//page:Word) + $stringsBefore"/>
                        <xsl:with-param name="linesBefore"
                            select="count(.//preceding-sibling::TextRegion//page:TextLine) + $linesBefore"
                        />
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="text" select="./TextEquiv/Unicode/text()"/>
                    <xsl:variable name="coords" select="./page:Coords/@points"/>
                    <xsl:variable name="height">
                        <xsl:call-template name="getHeightFromPointList">
                            <xsl:with-param name="coords" select="$coords"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="width">
                        <xsl:call-template name="getWidthFromPointList">
                            <xsl:with-param name="coords" select="$coords"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="maxX">
                        <xsl:call-template name="getMaxFromPointList">
                            <xsl:with-param name="coords" select="$coords"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="maxY">
                        <xsl:call-template name="getMaxFromPointList">
                            <xsl:with-param name="coords" select="$coords"/>
                            <xsl:with-param name="ordinate" select="'Y'"/>
                        </xsl:call-template>
                    </xsl:variable>
<!--                    <xsl:message>
                        <xsl:value-of select="name()"/>
                    </xsl:message>-->
                    <!--                    <xsl:message><xsl:value-of select="(.//ancestor::TextRegion//page:TextLine)[1]"/></xsl:message>
                    <xsl:message><xsl:value-of select="preceding-sibling::*[1]/@points"/></xsl:message>-->
<!--                    <xsl:message>
                        <xsl:value-of
                            select="string-length(preceding-sibling::*[1]/TextEquiv/Unicode/text())"
                        />
                    </xsl:message>-->

                    <xsl:variable name="predecessorName" select="name(preceding-sibling::*[1])"/>
                    <xsl:variable name="predecessorText">
                        <xsl:choose>
                            <xsl:when
                                test="$predecessorName = 'TextLine' and (string-length(preceding-sibling::*[1]/TextEquiv/Unicode/text())&gt;0)">
                                <xsl:value-of
                                    select="preceding-sibling::*[1]/TextEquiv/Unicode/text()"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'NA'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:variable name="followerName" select="name(following-sibling::*[1])"/>
                    <xsl:variable name="followerText">
                        <xsl:choose>
                            <xsl:when
                                test="($followerName = 'TextLine') and (string-length(following-sibling::*[1]/TextEquiv/Unicode/text())&gt;0)">
                                <xsl:value-of
                                    select="following-sibling::*[1]/TextEquiv/Unicode/text()"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'NA'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <!--                    <xsl:message>
                        <xsl:if test="not($predecessorName = 'NA')">
                            <xsl:value-of select="$predecessorText"></xsl:value-of>
                        </xsl:if>
                    </xsl:message>-->

                    <xsl:variable name="lastWordOfPrevLine">
                        <xsl:choose>
                            <xsl:when
                                test="not($predecessorText = 'NA') and (ends-with($predecessorText, '¬') or ends-with($predecessorText, '&#x2010;') or ends-with($predecessorText, '&#x002D;') or ends-with($predecessorText, '&#x2212;'))">
<!--                                <xsl:message>
                                    <xsl:value-of select="concat('last word of prev line found: ', $predecessorText)"/>
                                </xsl:message>-->
                                <xsl:value-of

                                    select="tokenize(replace($predecessorText, '[\p{Zs}]', ' '), ' ')[last()]"
                                />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'NA'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:variable name="firstWordOfNextLine">
                        <xsl:choose>
                            <xsl:when test="ends-with($text, '¬') or ends-with($text, '&#x2010;') or ends-with($text, '&#x002D;') or ends-with($text, '&#x2212;')">
<!--                                <xsl:message>
                                    <xsl:value-of
                                        select="substring-before(replace($followerText, '[\p{Zs}]', ' '),' ')"
                                    />
                                </xsl:message>-->
                                <xsl:value-of
                                    select="substring-before(replace($followerText, '[\p{Zs}]', ' '),' ')"
                                />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'NA'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:call-template name="getWords">
                        <xsl:with-param name="lineString" select="replace($text, '[\p{Zs}]', ' ')"/>
                        <xsl:with-param name="remainingContent"
                            select="replace($text, '[\p{Zs}]', ' ')"/>
                        <xsl:with-param name="firstWordOfNextLineIfAny"
                            select="$firstWordOfNextLine"/>
                        <xsl:with-param name="lastWordOfPreviousLineIfAny"
                            select="$lastWordOfPrevLine"/>
                        <xsl:with-param name="lineLength" select="string-length($text)"/>
                        <xsl:with-param name="maxX" select="$maxX"/>
                        <xsl:with-param name="maxY" select="$maxY"/>
                        <xsl:with-param name="height" select="$height"/>
                        <xsl:with-param name="width" select="$width"/>
                        <xsl:with-param name="customTags" select="@custom"/>
                    </xsl:call-template>
                    <!-- 	                <xsl:call-template name="applyCoordinates"/>
	                <xsl:call-template name="applyStyleId"/> -->

                </xsl:otherwise>
            </xsl:choose>
        </TextLine>
    </xsl:template>


    <xsl:template match="page:Word">
        <xsl:param name="stringsBefore"/>
        <String ID="{@id}">
            <xsl:call-template name="applyCoordinates"/>
            <!-- <xsl:call-template name="applyStyleId"/> -->
            <xsl:attribute name="CONTENT" select="./TextEquiv/Unicode/text()"/>
        </String>
    </xsl:template>

    <xsl:template name="getWords">
        <xsl:param name="lineString"/>
        <xsl:param name="remainingContent"/>
        <xsl:param name="firstWordOfNextLineIfAny"/>
        <xsl:param name="lastWordOfPreviousLineIfAny"/>
        <xsl:param name="lineLength"/>
        <xsl:param name="maxX"/>
        <xsl:param name="maxY"/>
        <xsl:param name="height"/>
        <xsl:param name="width"/>
        <xsl:param name="customTags"/>
        <xsl:variable name="stringId" select="@id"/>
        <!--   		<xsl:variable name="text" select="./TextEquiv/Unicode/text()"/>
  		<xsl:variable name="coords" select="./page:Coords/@points"/> -->

<!--        <xsl:message>
            <xsl:value-of select="concat('lineString is ', $lineString)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('remainingContent is ', $remainingContent)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('first word is ', $firstWordOfNextLineIfAny)"/>
        </xsl:message>
        <xsl:message>
            <xsl:value-of select="concat('previous word is ', $lastWordOfPreviousLineIfAny)"/>
        </xsl:message>-->

        <xsl:choose>
            <xsl:when test="contains($remainingContent,' ')">
                <xsl:variable name="actString" select="substring-before($remainingContent,' ')"/>
                <xsl:variable name="restString" select="substring-after($remainingContent,' ')"/>
                <xsl:variable name="actStringLength" select="string-length($actString)"/>
                <xsl:variable name="restStringLength" select="string-length($restString)"/>
                <String>
                    <!-- <xsl:attribute name="ID" select="$stringId"/> -->
                    <xsl:call-template name="getCoordinates">
                        <xsl:with-param name="lineLength" select="$lineLength"/>
                        <xsl:with-param name="maxX" select="$maxX"/>
                        <xsl:with-param name="maxY" select="$maxY"/>
                        <xsl:with-param name="height" select="$height"/>
                        <xsl:with-param name="width" select="$width"/>
                        <xsl:with-param name="actStringLength" select="string-length($actString)+4"/>
                        <xsl:with-param name="restStringLength"
                            select="string-length($restString)-2"/>
                    </xsl:call-template>
                    <!-- 	            <xsl:call-template name="applyStyleId"/> -->
                    <xsl:attribute name="CONTENT" select="$actString"/>
                    <!-- so we know that the last word in the previous line has been separated. --> 
                    <xsl:if test="not($lastWordOfPreviousLineIfAny = 'NA') and not($lastWordOfPreviousLineIfAny = '')">
                        <xsl:attribute name="SUBS_TYPE" select="'HypPart2'"/>
                        <xsl:attribute name="SUBS_CONTENT"
                            select="concat((tokenize($lastWordOfPreviousLineIfAny,'&#x00AC;|&#x002D;|&#x2010;|&#x2212;')[1]),$actString)"/>
                    </xsl:if>
                    <xsl:if test="$includeTags">
                        <xsl:call-template name="createNamedEntityRef">
                            <xsl:with-param name="remainingCustomTag"
                                select="substring-after($customTags, '} ')"/>
                            <xsl:with-param name="lineString" select="$lineString"/>
                            <xsl:with-param name="wordString" select="$actString"/>
                        </xsl:call-template>
                    </xsl:if>
                </String>
                <!-- add the space string -->
                <SP>
                    <xsl:call-template name="getSPAttributes">
                        <xsl:with-param name="maxXOfLine" select="$maxX"></xsl:with-param>
                        <xsl:with-param name="maxYOfLine" select="$maxY"></xsl:with-param>
                        <xsl:with-param name="widthOfLine" select="$width"></xsl:with-param>
                        <xsl:with-param name="heightOfLine" select="$height"></xsl:with-param>
                        <xsl:with-param name="lineLength" select="$lineLength"></xsl:with-param>
                        <xsl:with-param name="prevStringLength" select="string-length($actString)+4"></xsl:with-param>
                        <xsl:with-param name="restStringLength" select="string-length($restString)-2"/>
                    </xsl:call-template>
                </SP>
                <xsl:call-template name="getWords">
                    <xsl:with-param name="lineString" select="$lineString"/>
                    <xsl:with-param name="remainingContent"
                        select="replace($restString, '[\p{Zs}]', ' ')"/>
                    <xsl:with-param name="firstWordOfNextLineIfAny"
                        select="$firstWordOfNextLineIfAny"/>
                    <xsl:with-param name="lastWordOfPreviousLineIfAny"
                        select="'NA'"/>
                    <xsl:with-param name="lineLength" select="$lineLength"/>
                    <xsl:with-param name="maxX" select="$maxX"/>
                    <xsl:with-param name="maxY" select="$maxY"/>
                    <xsl:with-param name="height" select="$height"/>
                    <xsl:with-param name="width" select="$width"/>
                    <xsl:with-param name="customTags" select="$customTags"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <String>
                    <!-- <xsl:attribute name="ID" select="$stringId"/> -->
                    <xsl:call-template name="getCoordinates">
                        <xsl:with-param name="lineLength" select="$lineLength"/>
                        <xsl:with-param name="maxX" select="$maxX"/>
                        <xsl:with-param name="maxY" select="$maxY"/>
                        <xsl:with-param name="height" select="$height"/>
                        <xsl:with-param name="width" select="$width"/>
                        <xsl:with-param name="actStringLength"
                            select="string-length($remainingContent)+2"/>
                        <xsl:with-param name="restStringLength" select="0"/>
                    </xsl:call-template>
                    <!-- 	            <xsl:call-template name="applyStyleId"/> -->
                    <xsl:attribute name="CONTENT" select="tokenize($remainingContent,'&#x00AC;|&#x002D;|&#x2010;|&#x2212;')[1]"/>
                    <xsl:choose>
                        <xsl:when
                            test="not($firstWordOfNextLineIfAny = 'NA') and not($firstWordOfNextLineIfAny = '')">
                            <xsl:attribute name="SUBS_TYPE" select="'HypPart1'"/>
<!--                            <xsl:message>
                                <xsl:value-of
                                    select="concat('try to tokenize ', tokenize($remainingContent,'[¬U+2010]')[1])"
                                />
                            </xsl:message>
                            <xsl:message>
                                <xsl:value-of
                                    select="concat('try to tokenize(2) ', tokenize($remainingContent,'[¬]')[1])"
                                />
                            </xsl:message>-->
                            <!-- different Unicode chars of hyphens -->
                            <xsl:attribute name="SUBS_CONTENT"
                                select="concat((tokenize($remainingContent,'&#x00AC;|&#x002D;|&#x2010;|&#x2212;')[1]),$firstWordOfNextLineIfAny)"
                            />
                        </xsl:when>
                    </xsl:choose>
                    <xsl:if test="$includeTags">
                        <xsl:call-template name="createNamedEntityRef">
                            <xsl:with-param name="remainingCustomTag"
                                select="substring-after($customTags, '} ')"/>
                            <xsl:with-param name="lineString" select="$lineString"/>
                            <xsl:with-param name="wordString" select="$remainingContent"/>
                        </xsl:call-template>
                    </xsl:if>
                </String>
                <xsl:if
                    test="not($firstWordOfNextLineIfAny = 'NA') and not($firstWordOfNextLineIfAny = '')">
                    <HYP CONTENT="&#x002D;"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="getCoordinates">
        <xsl:param name="maxX"/>
        <xsl:param name="maxY"/>
        <xsl:param name="height"/>
        <xsl:param name="width"/>
        <xsl:param name="lineLength"/>
        <xsl:param name="actStringLength"/>
        <xsl:param name="restStringLength"/>

        <xsl:variable name="ratioRest">
            <xsl:choose>
                <xsl:when test="$lineLength>0">
                    <xsl:value-of select="$restStringLength div $lineLength"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="0"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="ratioAct">
            <xsl:choose>
                <xsl:when test="$lineLength>0">
                    <xsl:value-of select="$actStringLength div $lineLength"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="0"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="end" select="$maxX - ($width * $ratioRest)"/>
        <xsl:variable name="begin" select="$end - ($width * $ratioAct)"/>

        <xsl:attribute name="HEIGHT" select="$height"/>
        <xsl:attribute name="WIDTH" select="round($end - $begin)"/>
        <xsl:attribute name="VPOS" select="$maxY - $height"/>
        <xsl:attribute name="HPOS" select="round($begin)"/>
    </xsl:template>
    
    <xsl:template name="getSPAttributes">
        <xsl:param name="maxXOfLine"/>
        <xsl:param name="maxYOfLine"/>
        <xsl:param name="heightOfLine"/>
        <xsl:param name="widthOfLine"/>
        <xsl:param name="lineLength"/>
        <xsl:param name="prevStringLength"/>
        <xsl:param name="restStringLength"/>
        
        <xsl:variable name="actStringLength" select="number($prevStringLength)+number(1)"/>
        
        <xsl:variable name="ratioRest">
            <xsl:choose>
                <xsl:when test="$lineLength>0">
                    <xsl:value-of select="(number($restStringLength)-number(1)) div $lineLength"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="0"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="ratioAct">
            <xsl:choose>
                <xsl:when test="$lineLength>0">
                    <xsl:value-of select="number(1) div $lineLength"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="0"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="end" select="$maxXOfLine - ($widthOfLine * $ratioRest)"/>
        <xsl:variable name="begin" select="$end - ($widthOfLine * $ratioAct)"/>
                        
                        
        <xsl:attribute name="HEIGHT" select="$heightOfLine"/>
        <xsl:attribute name="WIDTH" select="round($end - $begin)"/>
        <xsl:attribute name="VPOS" select="$maxYOfLine - $heightOfLine"/>
        <xsl:attribute name="HPOS" select="round($begin)"/>
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

    <xsl:template name="getHeightFromPointList">
        <xsl:param name="coords" select="'0,0 '"/>
        <xsl:variable name="maxY">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="$coords"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minY">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="$coords"/>
                <xsl:with-param name="ordinate" select="'Y'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$maxY - $minY"/>
    </xsl:template>

    <xsl:template name="getWidthFromPointList">
        <xsl:param name="coords" select="'0,0 '"/>
        <xsl:variable name="maxX">
            <xsl:call-template name="getMaxFromPointList">
                <xsl:with-param name="coords" select="$coords"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="minX">
            <xsl:call-template name="getMinFromPointList">
                <xsl:with-param name="coords" select="$coords"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$maxX - $minX"/>
    </xsl:template>


    <xsl:template name="getMaxFromPointList">
        <xsl:param name="actMax" select="0"/>
        <xsl:param name="coords" select="'0,0 '"/>
        <xsl:param name="ordinate" select="'X'"/>

        <xsl:variable name="actPoint" select="substring-before($coords, ' ')"/>
        <xsl:variable name="actRest" select="substring-after($coords, ' ')"/>

        <xsl:variable name="currPoint">
            <xsl:choose>
                <xsl:when
                    test="string-length($coords)>0 and contains($coords,',') and string-length($actRest)=0">
                    <xsl:value-of select="$coords"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$actPoint"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="actOrdinate">
            <xsl:choose>
                <xsl:when test="$ordinate='X'">
                    <xsl:value-of select="substring-before($currPoint, ',')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="substring-after($currPoint, ',')"/>
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

        <xsl:variable name="currPoint">
            <xsl:choose>
                <xsl:when
                    test="string-length($coords)>0 and contains($coords,',') and string-length($actRest)=0">
                    <xsl:value-of select="$coords"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$actPoint"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="actOrdinate">
            <xsl:choose>
                <xsl:when test="$ordinate='X'">
                    <xsl:value-of select="substring-before($currPoint, ',')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="substring-after($currPoint, ',')"/>
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
        <xsl:if test="contains($comments, 'PrimaryLanguage:')">
            <xsl:call-template name="resolveLang">
                <xsl:with-param name="frLang">
                    <xsl:value-of
                        select="normalize-space(substring-after($comments, 'PrimaryLanguage:'))"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
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
