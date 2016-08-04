//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.08.04 um 05:12:39 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;


/**
 * 
 * 				Pure text is represented as a text region. This includes
 * 				drop capitals, but practically ornate text may be
 * 				considered as a graphic.
 * 			
 * 
 * <p>Java-Klasse für TextRegionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TextRegionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}RegionType">
 *       &lt;sequence>
 *         &lt;element name="TextLine" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextLineType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TextEquiv" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextEquivType" minOccurs="0"/>
 *         &lt;element name="TextStyle" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextStyleType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orientation" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="type" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextTypeSimpleType" />
 *       &lt;attribute name="leading" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="readingDirection" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ReadingDirectionSimpleType" />
 *       &lt;attribute name="readingOrientation" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="indented" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="align" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}AlignSimpleType" />
 *       &lt;attribute name="primaryLanguage" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LanguageSimpleType" />
 *       &lt;attribute name="secondaryLanguage" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LanguageSimpleType" />
 *       &lt;attribute name="primaryScript" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ScriptSimpleType" />
 *       &lt;attribute name="secondaryScript" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ScriptSimpleType" />
 *       &lt;attribute name="production" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ProductionSimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextRegionType", propOrder = {
    "textLine",
    "textEquiv",
    "textStyle"
})
@XmlSeeAlso({
    TableCellType.class
})
public class TextRegionType
    extends TrpRegionType
{

    @XmlElement(name = "TextLine")
    protected List<TextLineType> textLine;
    @XmlElement(name = "TextEquiv")
    protected TextEquivType textEquiv;
    @XmlElement(name = "TextStyle")
    protected TextStyleType textStyle;
    @XmlAttribute(name = "orientation")
    protected Float orientation;
    @XmlAttribute(name = "type")
    protected TextTypeSimpleType type;
    @XmlAttribute(name = "leading")
    protected Integer leading;
    @XmlAttribute(name = "readingDirection")
    protected ReadingDirectionSimpleType readingDirection;
    @XmlAttribute(name = "readingOrientation")
    protected Float readingOrientation;
    @XmlAttribute(name = "indented")
    protected Boolean indented;
    @XmlAttribute(name = "align")
    protected AlignSimpleType align;
    @XmlAttribute(name = "primaryLanguage")
    protected LanguageSimpleType primaryLanguage;
    @XmlAttribute(name = "secondaryLanguage")
    protected LanguageSimpleType secondaryLanguage;
    @XmlAttribute(name = "primaryScript")
    protected ScriptSimpleType primaryScript;
    @XmlAttribute(name = "secondaryScript")
    protected ScriptSimpleType secondaryScript;
    @XmlAttribute(name = "production")
    protected ProductionSimpleType production;

    /**
     * Gets the value of the textLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TextLineType }
     * 
     * 
     */
    public List<TextLineType> getTextLine() {
        if (textLine == null) {
            textLine = new ArrayList<TextLineType>();
        }
        return this.textLine;
    }

    /**
     * Ruft den Wert der textEquiv-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TextEquivType }
     *     
     */
    public TextEquivType getTextEquiv() {
        return textEquiv;
    }

    /**
     * Legt den Wert der textEquiv-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TextEquivType }
     *     
     */
    public void setTextEquiv(TextEquivType value) {
        this.textEquiv = value;
    }

    /**
     * Ruft den Wert der textStyle-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TextStyleType }
     *     
     */
    public TextStyleType getTextStyle() {
        return textStyle;
    }

    /**
     * Legt den Wert der textStyle-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TextStyleType }
     *     
     */
    public void setTextStyle(TextStyleType value) {
        this.textStyle = value;
    }

    /**
     * Ruft den Wert der orientation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getOrientation() {
        return orientation;
    }

    /**
     * Legt den Wert der orientation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setOrientation(Float value) {
        this.orientation = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TextTypeSimpleType }
     *     
     */
    public TextTypeSimpleType getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TextTypeSimpleType }
     *     
     */
    public void setType(TextTypeSimpleType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der leading-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeading() {
        return leading;
    }

    /**
     * Legt den Wert der leading-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeading(Integer value) {
        this.leading = value;
    }

    /**
     * Ruft den Wert der readingDirection-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReadingDirectionSimpleType }
     *     
     */
    public ReadingDirectionSimpleType getReadingDirection() {
        return readingDirection;
    }

    /**
     * Legt den Wert der readingDirection-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadingDirectionSimpleType }
     *     
     */
    public void setReadingDirection(ReadingDirectionSimpleType value) {
        this.readingDirection = value;
    }

    /**
     * Ruft den Wert der readingOrientation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getReadingOrientation() {
        return readingOrientation;
    }

    /**
     * Legt den Wert der readingOrientation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setReadingOrientation(Float value) {
        this.readingOrientation = value;
    }

    /**
     * Ruft den Wert der indented-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndented() {
        return indented;
    }

    /**
     * Legt den Wert der indented-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndented(Boolean value) {
        this.indented = value;
    }

    /**
     * Ruft den Wert der align-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AlignSimpleType }
     *     
     */
    public AlignSimpleType getAlign() {
        return align;
    }

    /**
     * Legt den Wert der align-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AlignSimpleType }
     *     
     */
    public void setAlign(AlignSimpleType value) {
        this.align = value;
    }

    /**
     * Ruft den Wert der primaryLanguage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LanguageSimpleType }
     *     
     */
    public LanguageSimpleType getPrimaryLanguage() {
        return primaryLanguage;
    }

    /**
     * Legt den Wert der primaryLanguage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageSimpleType }
     *     
     */
    public void setPrimaryLanguage(LanguageSimpleType value) {
        this.primaryLanguage = value;
    }

    /**
     * Ruft den Wert der secondaryLanguage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LanguageSimpleType }
     *     
     */
    public LanguageSimpleType getSecondaryLanguage() {
        return secondaryLanguage;
    }

    /**
     * Legt den Wert der secondaryLanguage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageSimpleType }
     *     
     */
    public void setSecondaryLanguage(LanguageSimpleType value) {
        this.secondaryLanguage = value;
    }

    /**
     * Ruft den Wert der primaryScript-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ScriptSimpleType }
     *     
     */
    public ScriptSimpleType getPrimaryScript() {
        return primaryScript;
    }

    /**
     * Legt den Wert der primaryScript-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ScriptSimpleType }
     *     
     */
    public void setPrimaryScript(ScriptSimpleType value) {
        this.primaryScript = value;
    }

    /**
     * Ruft den Wert der secondaryScript-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ScriptSimpleType }
     *     
     */
    public ScriptSimpleType getSecondaryScript() {
        return secondaryScript;
    }

    /**
     * Legt den Wert der secondaryScript-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ScriptSimpleType }
     *     
     */
    public void setSecondaryScript(ScriptSimpleType value) {
        this.secondaryScript = value;
    }

    /**
     * Ruft den Wert der production-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProductionSimpleType }
     *     
     */
    public ProductionSimpleType getProduction() {
        return production;
    }

    /**
     * Legt den Wert der production-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductionSimpleType }
     *     
     */
    public void setProduction(ProductionSimpleType value) {
        this.production = value;
    }

}
