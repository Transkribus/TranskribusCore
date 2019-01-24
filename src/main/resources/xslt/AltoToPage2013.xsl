<?xml version="1.0" encoding="UTF-8"?>
<!--    xmlns:saxon="http://saxon.sf.net/" extension-element-prefixes="saxon"-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15"
    xmlns:alto="http://www.loc.gov/standards/alto/ns-v2#"
    xmlns:abbyy="http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"
    xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15"
    xpath-default-namespace="http://www.loc.gov/standards/alto/ns-v2#"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd">
    
    <xsl:output method="xml" encoding="UTF-8" version="1.0" indent="yes"/>
    
    <!-- if set to false, the text styles recognized by finereader are ignored -->
    <xsl:param name="preserveTextStyles" select="true()"/>
    <!-- if set to false, the Finereader FontFamily output is omitted. If preserveTextStyles==false this has no effect -->
    <xsl:param name="preserveFontFam" select="true()"/>
    <!-- if set to false, text will be propagated also to line and text regions -->
    <xsl:param name="textToWordsOnly" select="false()"/>
    
     <xsl:variable name="primLang">
         <xsl:call-template name="determinePrimaryLanguage"/>
     </xsl:variable> 
    
    <xsl:template match="/">
        <PcGts xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd">
            <xsl:apply-templates select="//Page"/>
        </PcGts>
    </xsl:template>

    <xsl:template match="Page">
        <xsl:call-template name="metadata"/>
        <Page imageWidth="{@WIDTH}" imageHeight="{@HEIGHT}" imageFilename="">
            <!-- store pagination as custom tag -->
            <xsl:if test="./@PRINTED_IMG_NR">
                <xsl:attribute name="custom">
                    <xsl:value-of select="concat('pagination{value:',./@PRINTED_IMG_NR,';}')"/>
                </xsl:attribute>
            </xsl:if>
            <!--            <xsl:call-template name="styles2"/>-->
            <xsl:call-template name="printspace"/>
            <xsl:apply-templates select="./PrintSpace/TextBlock | ./PrintSpace/Illustration 
                | ./PrintSpace/GraphicalElement | ./PrintSpace/ComposedBlock"/>
        </Page>
    </xsl:template>

    <xsl:template match="Illustration">
        <xsl:variable name="actId" select="@ID"/>
        <xsl:call-template name="OtherRegion">
            <xsl:with-param name="elemName" select="'GraphicRegion'"/>
            <xsl:with-param name="id" select="$actId"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="GraphicalElement">
        <xsl:variable name="actId" select="@ID"/>
        <xsl:call-template name="OtherRegion">
            <xsl:with-param name="elemName" select="'SeparatorRegion'"/>
            <xsl:with-param name="id" select="$actId"/>
        </xsl:call-template>
    </xsl:template>
    
    
    <!-- PAGE metadata -->
    <xsl:template name="metadata">
        <xsl:variable name="time" select="current-dateTime()"/>
        <Metadata>
            <Creator/>
            <Created><xsl:value-of  select="$time"/></Created>
            <LastChange><xsl:value-of  select="$time"/></LastChange>
            <Comments>
                Measurement unit: pixel
                PrimaryLanguage: <xsl:value-of select="$primLang"/> 
                <xsl:for-each select="distinct-values(//TextBlock/@language)"> 
                Language: <xsl:value-of select="current()"/> 
                </xsl:for-each> 
<!--                 Producer: <xsl:value-of select="/document/@producer"/> -->
            </Comments>
        </Metadata>
    </xsl:template>
    
    <!-- PAGE printspace -->
    <xsl:template name="printspace">
        <PrintSpace>
            <xsl:choose>
                <xsl:when test="count(//PrintSpace) > 0">
                 <xsl:call-template name="writeCoords">
                     <xsl:with-param name="l" select="//PrintSpace/@HPOS"/>
                     <xsl:with-param name="t" select="//PrintSpace/@VPOS"/>
                     <xsl:with-param name="r" select="//PrintSpace/@HPOS+//PrintSpace/@WIDTH"/>
                     <xsl:with-param name="b" select="//PrintSpace/@VPOS+//PrintSpace/@HEIGHT"/>
                 </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="0"/>
                        <xsl:with-param name="t" select="0"/>
                        <xsl:with-param name="r" select="//Page/@WIDTH"/>
                        <xsl:with-param name="b" select="//Page/@HEIGHT"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </PrintSpace>
    </xsl:template>
    
    <xsl:template match="ComposedBlock">
        <xsl:variable name="actId" select="@ID"/>
        <xsl:choose>
            <xsl:when test="@TYPE='table'">
                <!-- produce TableRegion with included TextRegion per TextBlock -->
                <TableRegion>
                    <xsl:attribute name="id" select="$actId"/>
                    <xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="./@HPOS - 1"/>
                        <xsl:with-param name="t" select="./@VPOS - 1"/>
                        <xsl:with-param name="r" select="(./@HPOS+./@WIDTH)+1"/>
                        <xsl:with-param name="b" select="(./@VPOS+./@HEIGHT)+1"/>
                    </xsl:call-template>
                    <!-- TODO check for more ComposedBlocks within Type=table ?-->
                    <xsl:apply-templates select="./TextBlock">
                    	<xsl:with-param name="isTable" select="true()"/>
                    </xsl:apply-templates>
                </TableRegion>
            </xsl:when>
            <xsl:when test="@TYPE='container'">
                <!-- recurse on inner ComposedBlocks and transform TextBlocks on this level to TextRegions -->
                <xsl:apply-templates select="./ComposedBlock | ./TextBlock | ./GraphicalElement | ./Illustration"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO not implemented! Check for other types that are written by finereader (arbitrary string is possible due to schema!) -->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- blocks to Regions -->    
    <xsl:template match="TextBlock">
      	<xsl:param name="isTable" select="false()"/>
        <xsl:variable name="actId" select="@ID"/>
        <xsl:variable name="seq" select="number(substring-after($actId,'Page1_Block')) - 1"/>
        <xsl:variable name="font" select="./@STYLEREFS"/>
        <xsl:variable name="fontFamily" select="//TextStyle[@ID=$font]/@FONTFAMILY"/>
        <xsl:variable name="fontSize" select="//TextStyle[@ID=$font]/@FONTSIZE"/>
        <xsl:choose>
        <xsl:when test="$isTable">
	        <TableCell>      
		        <xsl:attribute name="id" select="$actId"/>
		        <xsl:attribute name="custom" select="concat('readingOrder ', (concat((concat('{index:', $seq)), ';}') ) )"/>
		<!--         We don't know the type from ALTO...    <xsl:attribute name="type" select="'paragraph'"/> -->
		        <xsl:choose>
		            <xsl:when test="count(./Shape/Polygon) > 0">
		                <Coords points="{./Shape/Polygon/@POINTS}"/>
		            </xsl:when>
		            <xsl:otherwise>
		                <xsl:call-template name="writeCoords">
		                    <xsl:with-param name="l" select="./@HPOS - 1"/>
		                    <xsl:with-param name="t" select="./@VPOS - 1"/>
		                    <xsl:with-param name="r" select="(./@HPOS+./@WIDTH)+1"/>
		                    <xsl:with-param name="b" select="(./@VPOS+./@HEIGHT)+1"/>
		                </xsl:call-template>
		            </xsl:otherwise>
		        </xsl:choose>
		        <xsl:apply-templates select="./TextLine"/>                
		        <xsl:if test="not($textToWordsOnly)">  
		         <TextEquiv>
		             <Unicode>
		                 <xsl:variable name="regionLineCount" select="count(./TextLine)"/>
		                 <xsl:for-each select="./TextLine">
		                     <xsl:value-of select="concat(string-join(.//String/@CONTENT, ' '),.//HYP/@CONTENT)"/>
		                     <xsl:if test="position() &lt; $regionLineCount">
		                         <xsl:text>&#10;</xsl:text>
		                     </xsl:if>
		                 </xsl:for-each>
		             </Unicode>
		         </TextEquiv>
		        </xsl:if>
		        <xsl:if test="$preserveTextStyles">
		            <xsl:choose>
		                <xsl:when test="$font">
		                    <!-- apply format to textLine -->
		                    <TextStyle>
		                        <xsl:call-template name="writeStyleAttribs">
		                            <xsl:with-param name="fontFam" select="$fontFamily"/>
		                            <xsl:with-param name="fontSize" select="$fontSize"/>
		                        </xsl:call-template>
		                    </TextStyle>
		                </xsl:when>
		            </xsl:choose>
		        </xsl:if>
		        <CornerPts>0 1 2 3</CornerPts>
	    	</TableCell>
        </xsl:when>
        <xsl:otherwise>
	        <TextRegion>
		        <xsl:attribute name="id" select="$actId"/>
		        <xsl:attribute name="custom" select="concat('readingOrder ', (concat((concat('{index:', $seq)), ';}') ) )"/>
		<!--         We don't know the type from ALTO...    <xsl:attribute name="type" select="'paragraph'"/> -->
		        <xsl:choose>
		            <xsl:when test="count(./Shape/Polygon) > 0">
		                <Coords points="{./Shape/Polygon/@POINTS}"/>
		            </xsl:when>
		            <xsl:otherwise>
		                <xsl:call-template name="writeCoords">
		                    <xsl:with-param name="l" select="./@HPOS - 1"/>
		                    <xsl:with-param name="t" select="./@VPOS - 1"/>
		                    <xsl:with-param name="r" select="(./@HPOS+./@WIDTH)+1"/>
		                    <xsl:with-param name="b" select="(./@VPOS+./@HEIGHT)+1"/>
		                </xsl:call-template>
		            </xsl:otherwise>
		        </xsl:choose>
		        <xsl:apply-templates select="./TextLine"/>                
		        <xsl:if test="not($textToWordsOnly)">  
		         <TextEquiv>
		             <Unicode>
		                 <xsl:variable name="regionLineCount" select="count(./TextLine)"/>
		                 <xsl:for-each select="./TextLine">
		                     <xsl:value-of select="concat(string-join(.//String/@CONTENT, ' '),.//HYP/@CONTENT)"/>
		                     <xsl:if test="position() &lt; $regionLineCount">
		                         <xsl:text>&#10;</xsl:text>
		                     </xsl:if>
		                 </xsl:for-each>
		             </Unicode>
		         </TextEquiv>
		        </xsl:if>
		        <xsl:if test="$preserveTextStyles">
		            <xsl:choose>
		                <xsl:when test="$font">
		                    <!-- apply format to textLine -->
		                    <TextStyle>
		                        <xsl:call-template name="writeStyleAttribs">
		                            <xsl:with-param name="fontFam" select="$fontFamily"/>
		                            <xsl:with-param name="fontSize" select="$fontSize"/>
		                        </xsl:call-template>
		                    </TextStyle>
		                </xsl:when>
		            </xsl:choose>
		        </xsl:if>
	        </TextRegion>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
        
    <!-- Simple Region template with ID and coordinates but no text content -->
    <xsl:template name="OtherRegion">
        <xsl:param name="id"/>
        <xsl:param name="elemName"/>
        <xsl:variable name="seq" select="number(substring-after($id,'Page1_Block'))-1"/>
        <xsl:element name="{$elemName}">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="custom" select="concat('readingOrder ', (concat((concat('{index:', $seq)), ';}') ) )"/>
            <xsl:call-template name="writeCoords"/>
        </xsl:element>
    </xsl:template>
    
    <!-- custom="readingOrder {index:0;}" -->
    <xsl:template match="TextLine">
        <xsl:variable name="actId" select="generate-id(.)"/>
        <xsl:variable name="seq">
            <xsl:for-each select="//TextLine">
                <xsl:if test="generate-id(.) = $actId">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="seq2" select="number($seq)-1"/>
        <xsl:variable name="font" select="./@STYLEREFS"/>
        <xsl:variable name="fontFamily" select="//TextStyle[@ID=$font]/@FONTFAMILY"/>
        <xsl:variable name="fontSize" select="//TextStyle[@ID=$font]/@FONTSIZE"/>
            <TextLine id="{concat('tl_', $seq)}" custom="readingOrder {concat(concat('{index:', $seq2), ';}')}">
                <xsl:call-template name="writeCoords">
                    <xsl:with-param name="l" select="./@HPOS - 1"/>
                    <xsl:with-param name="t" select="./@VPOS - 1"/>
                    <xsl:with-param name="r" select="(./@HPOS+./@WIDTH)+1"/>
                    <xsl:with-param name="b" select="(./@VPOS+./@HEIGHT)+1"/>
                </xsl:call-template> 
                <Baseline points="{./@HPOS - 1},{(./@VPOS+./@HEIGHT)+1} {(./@HPOS+./@WIDTH)+1},{(./@VPOS+./@HEIGHT)+1}"/>                               
                <!-- produce Word nodes -->
                <xsl:apply-templates select="./String"/>
                <xsl:if test="not($textToWordsOnly)">  
	                <TextEquiv>
	                    <Unicode><xsl:value-of select="concat(string-join(.//String/@CONTENT, ' '),.//HYP/@CONTENT)"/></Unicode>
	                </TextEquiv>
                </xsl:if>
                <xsl:if test="$preserveTextStyles">
                   <xsl:choose>
                       <xsl:when test="$font">
                           <!-- apply format to textLine -->
                           <TextStyle>
                               <xsl:call-template name="writeStyleAttribs">
                                   <xsl:with-param name="fontFam" select="$fontFamily"/>
                                   <xsl:with-param name="fontSize" select="$fontSize"/>
                               </xsl:call-template>
                           </TextStyle>
                       </xsl:when>
                       <xsl:otherwise>
                           <!-- what TODO if several formattings!? -->
                           <!-- styles -->
                       </xsl:otherwise>
                   </xsl:choose>
                </xsl:if>
            </TextLine>
    </xsl:template>
    
    <!-- creates word nodes -->
    <xsl:template match="String">
        <xsl:variable name="actId" select="generate-id(.)"/>
        <!-- counting strings is verrrrry slow!! -->
        <xsl:variable name="seq">
            <xsl:for-each select="//String">
                <xsl:if test="generate-id(.) = $actId">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="seq2" select="number($seq)-1"/>
        <xsl:variable name="font" select="./@STYLEREFS"/>
        <xsl:variable name="fontFamily" select="//TextStyle[@ID=$font]/@FONTFAMILY"/>
        <xsl:variable name="fontSize" select="//TextStyle[@ID=$font]/@FONTSIZE"/>
        <xsl:variable name="hyp">
       		<xsl:if test="following-sibling::*[1][self::HYP]">
       			<xsl:value-of select="following-sibling::*[1][self::HYP]/@CONTENT"/>
       	    </xsl:if>
      	</xsl:variable>
      	
        <Word id="{concat('w_', $seq)}">
        <!-- reading order does not start with 0 for each new text region - so leave this to the Transkribus import  -->
        <!-- Word id="{concat('w_', $seq)}" custom="readingOrder {concat(concat('{index:', $seq2), ';}')}"-->
<!--        <Word id="{$actId}"> fast variant -->
        <xsl:call-template name="writeCoords">
                <xsl:with-param name="l" select="./@HPOS"/>
                <xsl:with-param name="t" select="./@VPOS"/>
                <xsl:with-param name="r" select="./@HPOS+./@WIDTH"/>
                <xsl:with-param name="b" select="./@VPOS+./@HEIGHT"/>
            </xsl:call-template>
            <TextEquiv>
                <Unicode>
                    <!--  xsl:value-of select="./@CONTENT"/-->
                    <xsl:value-of select="concat(./@CONTENT, $hyp)"/>
                </Unicode>
            </TextEquiv>
            <xsl:if test="$preserveTextStyles">
                <xsl:variable name="bold">
                    <xsl:if test="contains(./@STYLE, 'bold')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="italics">
                    <xsl:if test="contains(./@STYLE, 'italics')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="subscript">
                    <xsl:if test="contains(./@STYLE, 'subscript')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>
                <xsl:variable name="superscript">
                    <xsl:if test="contains(./@STYLE, 'superscript')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>
               <!-- <xsl:variable name="smallcaps">
                    <xsl:if test="contains(./@STYLE, 'smallcaps')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>-->
                <xsl:variable name="underline">
                    <xsl:if test="contains(./@STYLE, 'underline')">
                        <xsl:text>true</xsl:text>
                    </xsl:if>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$font or ./@STYLE">        
                        <TextStyle>
                            <xsl:call-template name="writeStyleAttribs">
                                <xsl:with-param name="fontFam" select="$fontFamily"/>
                                <xsl:with-param name="fontSize" select="$fontSize"/>
                                <xsl:with-param name="bold" select="$bold"/>
                                <xsl:with-param name="ital" select="$italics"/>
                                <xsl:with-param name="subScr" select="$subscript"/>
                                <xsl:with-param name="superScr" select="$superscript"/>
                                <xsl:with-param name="underline" select="$underline"/>                                
                            </xsl:call-template>
                        </TextStyle>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- what TODO if several formattings!? -->
                        <!-- styles -->
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </Word>
    </xsl:template>
    
    <!-- Helper for writing rectangle coordinates with points-string -->
    <xsl:template name="writeCoords">
        <xsl:param name="l" select="./@HPOS"/>
        <xsl:param name="t" select="./@VPOS"/>
        <xsl:param name="r" select="./@HPOS+./@WIDTH"/>
        <xsl:param name="b" select="./@VPOS+./@HEIGHT"/>
        <xsl:variable name="left">
            <xsl:choose>
                <xsl:when test="$l&lt;0"><xsl:value-of select="0"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$l"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="top">
            <xsl:choose>
                <xsl:when test="$t&lt;0"><xsl:value-of select="0"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$t"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="right">
            <xsl:choose>
                <xsl:when test="$r&lt;0"><xsl:value-of select="0"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$r"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="bottom">
            <xsl:choose>
                <xsl:when test="$b&lt;0"><xsl:value-of select="0"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$b"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <Coords points="{$left},{$top} {$right},{$top} {$right},{$bottom} {$left},{$bottom}"/>
    </xsl:template>
    
    <xsl:template name="writeStyleAttribs">
        <xsl:param name="fontFam" select="./@ff"/>
        <xsl:param name="fontSize" select="./@fs"/>
        <xsl:param name="bold" select="./@bold"/>
        <xsl:param name="ital" select="./@italic"/>
        <xsl:param name="subScr" select="./@subscript"/>
        <xsl:param name="superScr" select="./@superscript"/>
        <xsl:param name="underline" select="./@underline"/>
        <xsl:param name="strikeout" select="./@strikeout"/>
        <!-- FIXME Text colour comes as number in abbyy!! -->
        <xsl:param name="color" select="./@color"/>
        <xsl:param name="scaling" select="./@scaling"/>
<!--        <xsl:param name="spacing" select="./@spacing"/>-->
        <xsl:if test="(string-length($fontFam) > 0) and $preserveFontFam">
            <xsl:attribute name="fontFamily" select="$fontFam"/>
        </xsl:if>
        <xsl:if test="string-length($fontSize) > 0">
            <xsl:attribute name="fontSize" select="$fontSize"/>
        </xsl:if>
        <xsl:if test="string-length($bold) > 0">
            <xsl:attribute name="bold" select="$bold"/>
        </xsl:if>
        <xsl:if test="string-length($ital) > 0">    
            <xsl:attribute name="italic" select="$ital"/> 
        </xsl:if>
        <xsl:if test="string-length($subScr) > 0">
            <xsl:attribute name="subscript" select="$subScr"/>
        </xsl:if>
        <xsl:if test="string-length($superScr) > 0">
            <xsl:attribute name="superscript" select="$superScr"/>
        </xsl:if>
        <xsl:if test="string-length($underline) > 0">
            <xsl:attribute name="underlined" select="$underline"/>
        </xsl:if>
        <xsl:if test="string-length($strikeout) > 0">
            <xsl:attribute name="strikethrough" select="$strikeout"/>
        </xsl:if>
       <!-- <xsl:if test="string-length($color) > 0">
            <xsl:attribute name="textColour" select="$color"/>
        </xsl:if>-->
        <!--        <xsl:attribute name="scaling" select="./@scaling"/>-->
        <!--<xsl:if test="string-length($spacing) > 0">
            <xsl:attribute name="spacing" select="$spacing"/>
        </xsl:if>-->
    </xsl:template>
    
    <xsl:template name="determinePrimaryLanguage">
        <xsl:param name="blocks" select="//TextBlock"/>
        <xsl:value-of>
            <xsl:for-each select="$blocks/@language">
                <xsl:sort select="count($blocks[@lang=current()])" order="descending"/>
                <xsl:if test="position() = 1">
                    <xsl:call-template name="resolveLang">
                        <xsl:with-param name="frLang">
                            <xsl:value-of select="current()"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:value-of>
    </xsl:template>

    <xsl:template name="resolveLang">
        <xsl:param name="frLang"/>
        <xsl:choose>
            <xsl:when test="$frLang = 'de'">
                <xsl:text>German</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>English</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
