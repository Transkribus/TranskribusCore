<?xml version="1.0" encoding="UTF-8"?>
<!--    xmlns:saxon="http://saxon.sf.net/" extension-element-prefixes="saxon"-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15"
    xmlns:abbyy="http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"
    xmlns:page="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15"
    xpath-default-namespace="http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd">
    
    <xsl:output method="xml" encoding="UTF-8" version="1.0" indent="yes"/>
    
    <!--xpath-default-namespace="http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml">
    -->
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
            <xsl:apply-templates select="//abbyy:page"/>
        </PcGts>
    </xsl:template>

    <xsl:template match="abbyy:page">
        <xsl:call-template name="metadata"/>
        <Page imageWidth="{@width}" imageHeight="{@height}" imageFilename="">
            <!--            <xsl:call-template name="styles2"/>-->
            <xsl:call-template name="printspace"/>
            <xsl:apply-templates select="abbyy:block"/>
        </Page>
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
                <xsl:for-each select="distinct-values(//formatting/@lang)">
                Language: <xsl:value-of select="current()"/>
                </xsl:for-each>
                Producer: <xsl:value-of select="/document/@producer"/>
            </Comments>
        </Metadata>
    </xsl:template>
    
    <!-- PAGE printspace -->
    <xsl:template name="printspace">
        <PrintSpace>
            <xsl:choose>
                <xsl:when test="count(//abbyy:rect) > 0">
                 <xsl:call-template name="writeCoords">
                     <xsl:with-param name="l" select="min(//abbyy:rect/@l)"/>
                     <xsl:with-param name="t" select="min(//abbyy:rect/@t)"/>
                     <xsl:with-param name="r" select="max(//abbyy:rect/@r)"/>
                     <xsl:with-param name="b" select="max(//abbyy:rect/@b)"/>
                 </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="0"/>
                        <xsl:with-param name="t" select="0"/>
                        <xsl:with-param name="r" select="//abbyy:page/@width"/>
                        <xsl:with-param name="b" select="//abbyy:page/@height"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </PrintSpace>
    </xsl:template>
    
    <!-- blocks to Regions -->    
    <xsl:template match="abbyy:block">
        <xsl:variable name="actId" select="generate-id(.)"/>
        <xsl:variable name="seq">
            <xsl:for-each select="//abbyy:block">
                <xsl:if test="generate-id(.) = $actId">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="@blockType='Text'">
                <xsl:call-template name="TextRegion_from_par">
                    <xsl:with-param name="seq" select="$seq"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="@blockType='Picture'">
                <!-- Use GraphicRegion or ImageRegion? -->
                <xsl:call-template name="OtherRegion">
                    <xsl:with-param name="id" select="concat('r_', $seq)"/>
                    <xsl:with-param name="seq" select="$seq"/>
                    <xsl:with-param name="elemName" select="'GraphicRegion'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="@blockType='Barcode'">
                <xsl:call-template name="OtherRegion">
                    <xsl:with-param name="id" select="concat('r_', $seq)"/>
                    <xsl:with-param name="seq" select="$seq"/>
                    <xsl:with-param name="elemName" select="'GraphicRegion'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="@blockType='Separator'">
                <xsl:call-template name="OtherRegion">
                    <xsl:with-param name="id" select="concat('r_', $seq)"/>
                    <xsl:with-param name="seq" select="$seq"/>
                    <xsl:with-param name="elemName" select="'SeparatorRegion'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="@blockType='SeparatorBox'">
                <xsl:for-each select="./abbyy:separatorsBox/abbyy:separator">
                    <xsl:call-template name="OtherRegion">
                        <xsl:with-param name="id" select="concat('r_', $seq, '_', position())"/>
                        <xsl:with-param name="seq" select="$seq"/>
                        <xsl:with-param name="elemName" select="'SeparatorRegion'"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="@blockType='Table'">
                <xsl:call-template name="TableRegion">
                    <xsl:with-param name="seq" select="$seq"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="@blockType='Checkmark'"></xsl:when>
            <xsl:when test="@blockType='GroupCheckmark'"></xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- UNUSED! -->
    <xsl:template name="TextRegion_from_block">
        <xsl:param name="seq"/>
        <!-- one text region per block -->
        <TextRegion>
            <xsl:attribute name="id" select="concat('r_', $seq)"/>
            <xsl:attribute name="type" select="'paragraph'"/>
            <xsl:call-template name="writeCoords"/>
            <xsl:apply-templates select="./abbyy:text/abbyy:par/abbyy:line">
<!--                <xsl:with-param name="current_block" select="$seq"/>-->
            </xsl:apply-templates>
            <TextEquiv>
                <Unicode>
                    <xsl:variable name="regionLineCount" select="count(.//line)"/>
                    <xsl:for-each select=".//line">
                        <xsl:value-of select="string-join(.//charParams/text(), '')"/>
                        <xsl:if test="position() &lt; $regionLineCount">
                            <xsl:text>&#10;</xsl:text>
                        </xsl:if>
                    </xsl:for-each>
                </Unicode>
            </TextEquiv>
        </TextRegion>
    </xsl:template>
    
    <xsl:template name="TextRegion_for_table">
        <xsl:param name="seq"/>
        <xsl:param name="rowSeq"/>
        <xsl:param name="cellSeq"/>
         <!-- compute region bounds from lines within and add a padding -->
        <xsl:param name="padding" select="number(1)"/>
<!--         <xsl:for-each select="./abbyy:text/abbyy:par"> -->
            <!-- there are pars with @lineSpacing="-1" and no nodes inside -->
<!--              <xsl:if test="not(empty(.) or ./@lineSpacing='-1')"> -->
				<xsl:if test="count(./abbyy:text/abbyy:par/abbyy:line)>0">
                <TableCell>
                	<xsl:attribute name="row" select="$rowSeq - 1"></xsl:attribute>
                	<xsl:attribute name="col" select="$cellSeq - 1"></xsl:attribute>
                	<xsl:attribute name="id" select="concat('r_', $seq)"/>
                    <xsl:attribute name="type" select="'paragraph'"/>
					<xsl:if test="number($seq)">
                   		<xsl:attribute name="custom" select="concat('readingOrder {index:', $seq - 1, ';}')"/>
                   	</xsl:if>
                  	<xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="min((./abbyy:text/abbyy:par/abbyy:line/@l))-$padding"/>
                        <xsl:with-param name="t" select="min((./abbyy:text/abbyy:par/abbyy:line/@t))-$padding"/>
                        <xsl:with-param name="r" select="max((./abbyy:text/abbyy:par/abbyy:line/@r))+$padding"/>
                        <xsl:with-param name="b" select="max((./abbyy:text/abbyy:par/abbyy:line/@b))+$padding"/>
                    </xsl:call-template>
                   	<xsl:for-each select="./abbyy:text/abbyy:par">
	                    <xsl:apply-templates select="./abbyy:line"/> 
	                    <xsl:if test="not($textToWordsOnly)">             
		                    <TextEquiv>
		                        <Unicode>
		                            <xsl:variable name="regionLineCount" select="count(./abbyy:line)"/>
		                            <xsl:for-each select="./abbyy:line">
		                                <xsl:value-of select="string-join(.//abbyy:charParams/text(), '')"/>
		                                <xsl:if test="position() &lt; $regionLineCount">
		                                    <xsl:text>&#10;</xsl:text>
		                                </xsl:if>
		                            </xsl:for-each>
		                        </Unicode>
		                    </TextEquiv>
	                    </xsl:if>
                     </xsl:for-each>
                    <CornerPts>0 1 2 3</CornerPts>
                </TableCell>
            </xsl:if>
<!--         -->
    </xsl:template>
    
     <xsl:template name="TextRegion_from_par">
        <xsl:param name="seq"/>
         <!-- compute region bounds from lines within and add a padding -->
        <xsl:param name="padding" select="number(1)"/>
         <xsl:for-each select="./abbyy:text/abbyy:par">
            <!-- there are pars with @lineSpacing="-1" and no nodes inside-->
             <xsl:if test="not(empty(.) or ./@lineSpacing='-1')">
                <TextRegion>
                    <xsl:attribute name="id" select="concat('r_', $seq, '_', position())"/>
                    <xsl:attribute name="type" select="'paragraph'"/>
					<xsl:if test="number($seq)">
                   		<xsl:attribute name="custom" select="concat('readingOrder {index:', $seq - 1, ';}')"/>
                   	</xsl:if>
                    <xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="min(./abbyy:line/@l)-$padding"/>
                        <xsl:with-param name="t" select="min(./abbyy:line/@t)-$padding"/>
                        <xsl:with-param name="r" select="max(./abbyy:line/@r)+$padding"/>
                        <xsl:with-param name="b" select="max(./abbyy:line/@b)+$padding"/>
                    </xsl:call-template>
                    <xsl:apply-templates select="./abbyy:line"/> 
                    <xsl:if test="not($textToWordsOnly)">             
	                    <TextEquiv>
	                        <Unicode>
	                            <xsl:variable name="regionLineCount" select="count(./abbyy:line)"/>
	                            <xsl:for-each select="./abbyy:line">
	                                <xsl:value-of select="string-join(.//abbyy:charParams/text(), '')"/>
	                                <xsl:if test="position() &lt; $regionLineCount">
	                                    <xsl:text>&#10;</xsl:text>
	                                </xsl:if>
	                            </xsl:for-each>
	                        </Unicode>
	                    </TextEquiv>
                    </xsl:if>
                </TextRegion>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="TableRegion">
        <!--  
                <row>
                <cell leftBorder="White" rightBorder="White" width="560" height="101">
                    <text>
                        <par align="Center" leftIndent="300">
                            <line baseline="507" l="771" t="466" r="1093" b="493">
                                <formatting lang="GermanStandard" ff="Times New Roman" fs="8.5">
                                    <charParams l="771" t="466" r="797" b="490" wordStart="1" -->
        <xsl:param name="seq"/>
        <!-- compute region bounds from lines within and add a padding -->
        <xsl:param name="padding" select="number(1)"/>
                <TableRegion>
                    <xsl:attribute name="id" select="concat('tbl_', $seq, '_', position())"/>
                    <xsl:call-template name="writeCoords"/>  
			        <xsl:for-each select="./abbyy:row">
						<xsl:variable name="row">
						  	<xsl:value-of select="position()"/>
						</xsl:variable>							                
	                    <xsl:for-each select="./abbyy:cell">
	                        <xsl:variable name="pos">
						  		<xsl:value-of select="position()"/>
							</xsl:variable>
	                        <xsl:call-template name="TextRegion_for_table">
	                        	<xsl:with-param name="seq" select="concat($seq, '_', $row, '_', $pos)"/>
	                            <xsl:with-param name="rowSeq" select="$row"/>
	                            <xsl:with-param name="cellSeq" select="$pos"/>
	                        </xsl:call-template>
	                    </xsl:for-each>
                    </xsl:for-each>
                </TableRegion>
    </xsl:template>
    
    <!-- Simple Region template with ID and coordinates but no text content -->
    <xsl:template name="OtherRegion">
        <xsl:param name="id"/>
        <xsl:param name="seq"/>
        <xsl:param name="elemName"/>
        <xsl:element name="{$elemName}">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="custom" select="concat('readingOrder {index:', $seq - 1, ';}')"/>
            <xsl:call-template name="writeCoords"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="abbyy:line">
        <xsl:variable name="actId" select="generate-id(.)"/>
        <xsl:variable name="seq">
            <xsl:for-each select="//abbyy:line">
                <xsl:if test="generate-id(.) = $actId">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="numberOfFormats" select="count(./abbyy:formatting)"/>
            <TextLine id="{concat('tl_', $seq)}">
            	<xsl:attribute name="custom" select="concat('readingOrder {index:', $seq - 1, ';}')"/>
                <xsl:attribute name="primaryLanguage">
                    <xsl:call-template name="determinePrimaryLanguage">
                        <xsl:with-param name="formatting" select="./abbyy:formatting"/>
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:call-template name="writeCoords"/>
                <xsl:call-template name="generateBaselineCoords">  
                    <xsl:with-param name="wordstarts" select=".//abbyy:charParams[1] | .//abbyy:charParams[string-length(normalize-space((text()[$textIndex]))) = 0]/following-sibling::charParams[1]"/>
                </xsl:call-template>           
<!--                 <Baseline points="{./@l},{./@baseline} {./@r},{./@baseline}"/> -->
                
                <!-- produce Word nodes -->
                <!-- FIXME This does not work when charRecVariants and wordRecVariants are present!!! -->
                <!-- TODO remove '¬' within lines and replace it with '-' at the end of lines  -->
                <xsl:apply-templates select="./abbyy:formatting"/>
                <xsl:if test="not($textToWordsOnly)">  
	                <TextEquiv>
	                    <Unicode><xsl:value-of select="string-join(.//charParams/text(), '')"/></Unicode>
	                </TextEquiv>
                </xsl:if>
                <xsl:if test="$preserveTextStyles">
                   <xsl:choose>
                       <xsl:when test="$numberOfFormats = 1">
                           <!-- apply format to textLine -->
                           <TextStyle>
                               <xsl:for-each select="./abbyy:formatting">
                                   <xsl:call-template name="writeStyleAttribs"/>
                               </xsl:for-each>
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
    <xsl:template match="abbyy:formatting">
        <xsl:variable name="thisLang">
            <xsl:call-template name="resolveLang">
                <xsl:with-param name="frLang">
                    <xsl:value-of select="./@lang"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="count(.//abbyy:charParams[@wordStart='true']) > 0">
                <xsl:call-template name="generateWords">
                    <xsl:with-param name="wordstarts"
                        select=".//abbyy:charParams[@wordStart='true']"/>
                    <xsl:with-param name="lang" select="$thisLang"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="generateWords">
                    <xsl:with-param name="wordstarts"
                        select=".//abbyy:charParams[1] | .//abbyy:charParams[string-length(normalize-space((text()[$textIndex]))) = 0]/following-sibling::charParams[1]"/>
                    <xsl:with-param name="lang" select="$thisLang"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="generateWords">
        <xsl:param name="wordstarts"/>
        <xsl:param name="lang" />
        
        <xsl:for-each select="$wordstarts">
            <xsl:variable name="actId" select="generate-id(.)"/>
           <!-- <xsl:variable name="seq">
                <xsl:for-each select="generate-id(//abbyy:charParams[@wordStart='true'])">
                    <xsl:if test="current()=$actId">
                        <xsl:value-of select="position()"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:variable>-->
            <xsl:variable name="actpos" select="position()"/>
            <xsl:variable name="wordlenght">
                <xsl:choose>
                    <xsl:when test="position()=last()">
                        <xsl:value-of select="count(./following-sibling::abbyy:charParams) + 1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of
                            select="count(./following-sibling::abbyy:charParams) - count($wordstarts[$actpos+1]/following-sibling::charParams) - 1"
                        />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="wordChars"
                select=". | (./following-sibling::*)[$wordlenght > position()]"/>
            
            <xsl:variable name="hyphen" select="$wordChars[last() and text() = '-']"/>
            <xsl:variable name="value">
                <xsl:for-each select="$wordChars">
                    <xsl:value-of select="normalize-space((./text())[$textIndex])"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="rightCoord">
                <xsl:choose>
                    <xsl:when test="string-length(normalize-space($wordChars[last()])) = 0">
                        <xsl:value-of select="$wordChars[last()-1]//@r"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$wordChars[last()]//@r"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="leftCoord">
                <xsl:value-of select="$wordChars[1]//@l"/>
            </xsl:variable>
            <xsl:if test="string-length($value) > 0">
                <Word id="{concat('w_', $actId)}">
                    <xsl:if test="$lang">
                        <xsl:attribute name="language" select="$lang"/>
                    </xsl:if>
                    <xsl:call-template name="writeCoords">
                        <xsl:with-param name="l" select="$leftCoord"/>
                        <xsl:with-param name="t" select="min($wordChars//@t)"/>
                        <xsl:with-param name="r" select="$rightCoord"/>
                        <xsl:with-param name="b" select="max($wordChars//@b)"/>
                    </xsl:call-template>
                    <!--<xsl:if test="count($hyphen) > 0">
                        <xsl:attribute name="hasHyphen" select="'true'"/>
                    </xsl:if>-->
                    <!--<xsl:call-template name="applyChars">
                        <xsl:with-param name="charParams" select="$wordChars"/>
                    </xsl:call-template>-->
                    <TextEquiv>
                        <Unicode>
                        <xsl:for-each select="$wordChars">
                            <xsl:value-of select="normalize-space((./text())[$textIndex])"/>
                        </xsl:for-each>
                        </Unicode>
                    </TextEquiv>
                    <xsl:if test="$preserveTextStyles">
                        <TextStyle>
                            <xsl:call-template name="writeStyleAttribs">
                                <xsl:with-param name="fontFam" select="parent::node()/@ff"/>
                                <xsl:with-param name="fontSize" select="parent::node()/@fs"/>
                                <xsl:with-param name="bold" select="parent::node()/@bold"/>
                                <xsl:with-param name="ital" select="parent::node()/@italic"/>
                                <xsl:with-param name="subScr" select="parent::node()/@subscript"/>
                                <xsl:with-param name="superScr" select="parent::node()/@superscript"/>
                                <xsl:with-param name="underline" select="parent::node()/@underline"/>
                                <xsl:with-param name="strikeout" select="parent::node()/@strikeout"/>
                                <xsl:with-param name="color" select="parent::node()/@color"/>
                                <xsl:with-param name="scaling" select="parent::node()/@scaling"/>
    <!--                            <xsl:with-param name="spacing" select="parent::node()/@spacing"/>-->
                            </xsl:call-template>
                        </TextStyle>
                    </xsl:if>
                </Word>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!-- creates baseline for a line: Abbyy has only defined one point for baseline - on oblique lines this
    means that the basline is not correct. So we create the baseline from the beginning of a word to each end of a word respectively
    the last charparams of all words-->
    <xsl:template name="generateBaselineCoords">
        <xsl:param name="wordstarts"/>
        <xsl:variable name="coordPoints">
	        <xsl:for-each select="$wordstarts">
	            <xsl:variable name="actpos" select="position()"/>
	            <xsl:variable name="wordlenght">
	                <xsl:choose>
	                    <xsl:when test="position()=last()">
	                        <xsl:value-of select="count(./following-sibling::abbyy:charParams) + 1"/>
	                    </xsl:when>
	                    <xsl:otherwise>
	                        <xsl:value-of
	                            select="count(./following-sibling::abbyy:charParams) - count($wordstarts[$actpos+1]/following-sibling::charParams) - 1"
	                        />
	                    </xsl:otherwise>
	                </xsl:choose>
	            </xsl:variable>	            
	            <xsl:variable name="wordChars"
	                select=". | (./following-sibling::*)[$wordlenght > position()]"/>	            
	            <xsl:variable name="value">
	                <xsl:for-each select="$wordChars">
	                    <xsl:value-of select="normalize-space((./text())[$textIndex])"/>
	                </xsl:for-each>
	            </xsl:variable>
	            <xsl:variable name="leftCoord">
	                <xsl:value-of select="$wordChars[1]//@l"/>
	            </xsl:variable>
	            <xsl:variable name="leftBottom">
	                <xsl:value-of select="$wordChars[1]//@b"/>
	            </xsl:variable>
	            <xsl:variable name="rightCoord">
	                <xsl:value-of select="$wordChars[last()]//@r"/>
	            </xsl:variable>
	           	<xsl:variable name="rightBottom">
	                <xsl:value-of select="$wordChars[last()]//@b"/>
	            </xsl:variable>
	            <xsl:if test="string-length($value) > 0">
	            	<xsl:choose>
	            		<xsl:when test="position()=1">
	            			<xsl:value-of select="$leftCoord"/>
	                		<xsl:value-of>,</xsl:value-of>
			                <xsl:value-of select="$leftBottom"/>
	            			<xsl:choose>
		            			<xsl:when test="$rightCoord != '0' and $rightBottom != '0'">
		            				<xsl:text> </xsl:text>
				                	<xsl:value-of select="$rightCoord"/>
				                	<xsl:value-of>,</xsl:value-of>
			                		<xsl:value-of select="$rightBottom"/>
		            			</xsl:when>
	            			</xsl:choose>
	            		</xsl:when>
	            		<xsl:otherwise>
	            			<xsl:choose>
		            			<xsl:when test="$rightCoord != '0' and $rightBottom != '0'">
		            				<xsl:text> </xsl:text>
				                	<xsl:value-of select="$rightCoord"/>
				                	<xsl:value-of>,</xsl:value-of>
			                		<xsl:value-of select="$rightBottom"/>
		            			</xsl:when>
	            			</xsl:choose>
	            		</xsl:otherwise>
	            	</xsl:choose>
	            </xsl:if>
	        </xsl:for-each>
        </xsl:variable>
        <Baseline points="{$coordPoints}"/>
    </xsl:template>
    
    <!-- Helper for writing rectangle coordinates with points-string -->
    <xsl:template name="writeCoords">
        <xsl:param name="l" select="./@l"/>
        <xsl:param name="t" select="./@t"/>
        <xsl:param name="r" select="./@r"/>
        <xsl:param name="b" select="./@b"/>
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
        <xsl:param name="formatting" select="//abbyy:formatting"/>
        <xsl:value-of>
            <xsl:for-each select="$formatting/@lang">
                <xsl:sort select="count($formatting[@lang=current()])" order="descending"/>
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
    
   <!-- <xsl:template name="determinePrimaryLanguage">
        <xsl:param name="primLang" />
        <xsl:param name="count" select="number(0)"/>
        <xsl:param name="pos" select="number(0)"/>
        <xsl:choose>
            <xsl:when test="$pos &lt; count(distinct-values(.//abbyy:formatting/@lang))">
                <xsl:variable name="thisLang" select="distinct-values(.//abbyy:formatting/@lang)[$pos+1]"/>
                <xsl:variable name="thisLangCount" select="count(.//abbyy:formatting[@lang=$thisLang]/node())"/>
                <xsl:choose>
                    <xsl:when test="$thisLangCount &gt; $count">
                        <xsl:call-template name="determinePrimaryLanguage">
                            <xsl:with-param name="primLang" select="$thisLang"/>
                            <xsl:with-param name="count" select="$thisLangCount"/>
                            <xsl:with-param name="pos" select="$pos+1"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="determinePrimaryLanguage">
                            <xsl:with-param name="primLang" select="$primLang"/>
                            <xsl:with-param name="count" select="$count"/>
                            <xsl:with-param name="pos" select="$pos+1"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="resolveLang">
                    <xsl:with-param name="frLang">
                        <xsl:value-of select="$primLang"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>    
    </xsl:template>-->
    
    <xsl:template name="resolveLang">
        <xsl:param name="frLang"/>
        <xsl:choose>
            <xsl:when test="$frLang = 'GermanStandard'">
                <xsl:text>German</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>English</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- ============================================================================================ -->
    <!-- ================================                 =========================================== -->
    <!-- ================================     GARBAGE     =========================================== -->
    <!-- ================================                 =========================================== -->
    <!-- ============================================================================================ -->

    <!--  Kind of array that contains all different styles found within this page -->
    <xsl:key name="allStyles" match="formatting"
        use="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"/>

    <xsl:variable name="textIndex2">
        <xsl:choose>
            <xsl:when test="count(//charRecVariants)>0">
                <xsl:value-of select="2"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="1"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="textIndex" select="number($textIndex2)"/>


    <!--  
      //formatting[not(@lang=preceding-sibling::formatting/@lang)]/@lang
    -->
    <xsl:template name="languages">
        <languageTable>
            <xsl:for-each select="distinct-values(//formatting/@lang)">
                <language languageID="{concat('lang_', current())}" languageName="{.}"/>
            </xsl:for-each>
        </languageTable>
    </xsl:template>

    <xsl:template name="styles2">
        <styleTable>
            <xsl:for-each
                select="//formatting[generate-id() = generate-id(key('allStyles',concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing))[1])]">
                <xsl:variable name="styleId"
                    select="concat(translate(@ff, ' ', '_'), '_', @fs, '_', @bold, '_', @italic,'_', @subscript, '_', @superscript,'_', @underline, '_', @strikeout, '_', @color,'_', @scaling,'_', @spacing)"/>
                <style styleID="{$styleId}" fontSize="{@fs}" fontFace="{@ff}">
                    <xsl:if test="string-length(@bold) > 0">
                        <xsl:attribute name="bold" select="@bold"/>
                    </xsl:if>
                    <xsl:if test="string-length(@italic) > 0">
                        <xsl:attribute name="italic" select="@italic"/>
                    </xsl:if>
                    <xsl:if test="string-length(@underline) > 0">
                        <xsl:attribute name="underline" select="@underline"/>
                    </xsl:if>
                    <xsl:if test="string-length(@strikeout) > 0">
                        <xsl:attribute name="strikethrough" select="@strikeout"/>
                    </xsl:if>
                    <xsl:if test="string-length(@subscript) > 0">
                        <xsl:attribute name="subsuperscript" select="'subscript'"/>
                    </xsl:if>
                    <xsl:if test="string-length(@superscript) > 0">
                        <xsl:attribute name="subsuperscript" select="'superscript'"/>
                    </xsl:if>
                    <xsl:if test="string-length(@spacing) > 0">
                        <xsl:attribute name="spacing" select="@spacing"/>
                    </xsl:if>
                    <xsl:if test="string-length(@scaling) > 0">
                        <xsl:attribute name="scaling" select="@scaling"/>
                    </xsl:if>
                    <xsl:if test="string-length(@color) > 0">
                        <xsl:attribute name="color" select="@color"/>
                    </xsl:if> 
                </style>
            </xsl:for-each>
        </styleTable>
    </xsl:template>

    <xsl:template match="abbyy:par">
        <xsl:param name="current_block"/>
        <xsl:variable name="hasDroppedCapitalChar">
            <xsl:choose>
                <xsl:when test="@dropCapCharsCount > 0">true</xsl:when>
                <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="actId" select="generate-id(.)"/>
        <par xpathToOriginalParagraph="block[{$current_block}]/text/par[{position()}]"
            hasDroppedCapitalChar="{$hasDroppedCapitalChar}" t="{min(.//@t)}" b="{max(.//@b)}"
            r="{max(.//@r)}" l="{min(.//@l)}" seq="0">
            <xsl:if test="string-length(@startIndent) > 0">
                <xsl:attribute name="startIndent" select="@startIndent"/>
            </xsl:if>
            <xsl:if test="string-length(@rightIndent) > 0">
                <xsl:attribute name="rightIndent" select="@rightIndent"/>
            </xsl:if>
            <xsl:if test="string-length(@leftIndent) > 0">
                <xsl:attribute name="leftIndent" select="@leftIndent"/>
            </xsl:if>
            <xsl:if test="string-length(@lineSpacing) > 0">
                <xsl:attribute name="lineSpacing" select="@lineSpacing"/>
            </xsl:if>
            <xsl:if test="string-length(@align) > 0">
                <xsl:attribute name="alignment" select="@align"/>
            </xsl:if>
            <xsl:apply-templates select="abbyy:line"/>
        </par>
    </xsl:template>

    <!-- creates character nodes -->
    <xsl:template name="applyChars">
        <xsl:param name="charParams"/>
        <xsl:for-each select="$charParams">
            <xsl:if test="string-length(normalize-space(text()[$textIndex]))>0">
                <xsl:variable name="actId" select="generate-id(.)"/>
                <xsl:variable name="suspicious">
                    <xsl:choose>
                        <xsl:when test="@charConfidence > 80">false</xsl:when>
                        <xsl:otherwise>true</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <charParams l="{@l}" t="{@t}" r="{@r}" b="{@b}" seq="0">
                    <xsl:if test="string-length(@suspicious) > 0">
                        <xsl:attribute name="suspicious" select="$suspicious"/>
                    </xsl:if>
                    <xsl:if test="string-length(@wordFromDictionary) > 0">
                        <xsl:attribute name="wordFromDictionary" select="@wordFromDictionary"/>
                    </xsl:if>
                    <xsl:if test="string-length(@wordNumeric) > 0">
                        <xsl:attribute name="wordNumeric" select="@wordNumeric"/>
                    </xsl:if>
                    <xsl:if test="string-length(@charConfidence) > 0">
                        <xsl:attribute name="charConfidence" select="@charConfidence"/>
                    </xsl:if>
                    <xsl:call-template name="applyTextContent">
                        <xsl:with-param name="textContent"
                            select="normalize-space(text()[$textIndex])"/>
                    </xsl:call-template>
                </charParams>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="applyTextContent">
        <xsl:param name="textContent" select="''"/>
        <xsl:value-of select="$textContent"/>
    </xsl:template>
</xsl:stylesheet>
