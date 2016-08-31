//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.08.31 um 10:07:11 AM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *     			Monospace (fixed-pitch, non-proportional) or
 *     			proportional font
 *     		
 * 
 * <p>Java-Klasse für TextStyleType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TextStyleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fontFamily" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="serif" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="monospace" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="fontSize" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="kerning" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="textColour" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ColourSimpleType" />
 *       &lt;attribute name="bgColour" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ColourSimpleType" />
 *       &lt;attribute name="reverseVideo" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="bold" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="italic" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="underlined" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="subscript" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="superscript" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="strikethrough" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="smallCaps" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="letterSpaced" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextStyleType")
public class TextStyleType {

    @XmlAttribute(name = "fontFamily")
    protected String fontFamily;
    @XmlAttribute(name = "serif")
    protected Boolean serif;
    @XmlAttribute(name = "monospace")
    protected Boolean monospace;
    @XmlAttribute(name = "fontSize")
    protected Float fontSize;
    @XmlAttribute(name = "kerning")
    protected Integer kerning;
    @XmlAttribute(name = "textColour")
    protected ColourSimpleType textColour;
    @XmlAttribute(name = "bgColour")
    protected ColourSimpleType bgColour;
    @XmlAttribute(name = "reverseVideo")
    protected Boolean reverseVideo;
    @XmlAttribute(name = "bold")
    protected Boolean bold;
    @XmlAttribute(name = "italic")
    protected Boolean italic;
    @XmlAttribute(name = "underlined")
    protected Boolean underlined;
    @XmlAttribute(name = "subscript")
    protected Boolean subscript;
    @XmlAttribute(name = "superscript")
    protected Boolean superscript;
    @XmlAttribute(name = "strikethrough")
    protected Boolean strikethrough;
    @XmlAttribute(name = "smallCaps")
    protected Boolean smallCaps;
    @XmlAttribute(name = "letterSpaced")
    protected Boolean letterSpaced;

    /**
     * Ruft den Wert der fontFamily-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Legt den Wert der fontFamily-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFontFamily(String value) {
        this.fontFamily = value;
    }

    /**
     * Ruft den Wert der serif-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSerif() {
        return serif;
    }

    /**
     * Legt den Wert der serif-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSerif(Boolean value) {
        this.serif = value;
    }

    /**
     * Ruft den Wert der monospace-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMonospace() {
        return monospace;
    }

    /**
     * Legt den Wert der monospace-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMonospace(Boolean value) {
        this.monospace = value;
    }

    /**
     * Ruft den Wert der fontSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getFontSize() {
        return fontSize;
    }

    /**
     * Legt den Wert der fontSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setFontSize(Float value) {
        this.fontSize = value;
    }

    /**
     * Ruft den Wert der kerning-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKerning() {
        return kerning;
    }

    /**
     * Legt den Wert der kerning-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKerning(Integer value) {
        this.kerning = value;
    }

    /**
     * Ruft den Wert der textColour-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ColourSimpleType }
     *     
     */
    public ColourSimpleType getTextColour() {
        return textColour;
    }

    /**
     * Legt den Wert der textColour-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ColourSimpleType }
     *     
     */
    public void setTextColour(ColourSimpleType value) {
        this.textColour = value;
    }

    /**
     * Ruft den Wert der bgColour-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ColourSimpleType }
     *     
     */
    public ColourSimpleType getBgColour() {
        return bgColour;
    }

    /**
     * Legt den Wert der bgColour-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ColourSimpleType }
     *     
     */
    public void setBgColour(ColourSimpleType value) {
        this.bgColour = value;
    }

    /**
     * Ruft den Wert der reverseVideo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReverseVideo() {
        return reverseVideo;
    }

    /**
     * Legt den Wert der reverseVideo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReverseVideo(Boolean value) {
        this.reverseVideo = value;
    }

    /**
     * Ruft den Wert der bold-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBold() {
        return bold;
    }

    /**
     * Legt den Wert der bold-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBold(Boolean value) {
        this.bold = value;
    }

    /**
     * Ruft den Wert der italic-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isItalic() {
        return italic;
    }

    /**
     * Legt den Wert der italic-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setItalic(Boolean value) {
        this.italic = value;
    }

    /**
     * Ruft den Wert der underlined-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnderlined() {
        return underlined;
    }

    /**
     * Legt den Wert der underlined-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnderlined(Boolean value) {
        this.underlined = value;
    }

    /**
     * Ruft den Wert der subscript-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubscript() {
        return subscript;
    }

    /**
     * Legt den Wert der subscript-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubscript(Boolean value) {
        this.subscript = value;
    }

    /**
     * Ruft den Wert der superscript-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuperscript() {
        return superscript;
    }

    /**
     * Legt den Wert der superscript-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuperscript(Boolean value) {
        this.superscript = value;
    }

    /**
     * Ruft den Wert der strikethrough-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * Legt den Wert der strikethrough-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStrikethrough(Boolean value) {
        this.strikethrough = value;
    }

    /**
     * Ruft den Wert der smallCaps-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmallCaps() {
        return smallCaps;
    }

    /**
     * Legt den Wert der smallCaps-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmallCaps(Boolean value) {
        this.smallCaps = value;
    }

    /**
     * Ruft den Wert der letterSpaced-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLetterSpaced() {
        return letterSpaced;
    }

    /**
     * Legt den Wert der letterSpaced-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLetterSpaced(Boolean value) {
        this.letterSpaced = value;
    }

}
