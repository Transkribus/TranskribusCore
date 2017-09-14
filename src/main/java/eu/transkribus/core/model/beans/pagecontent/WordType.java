//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.21 um 11:17:10 AM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für WordType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="WordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tag" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TagType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Property" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Coords" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}CoordsType"/>
 *         &lt;element name="Glyph" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}GlyphType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TextEquiv" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextEquivType" minOccurs="0"/>
 *         &lt;element name="TextStyle" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextStyleType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="language" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LanguageSimpleType" />
 *       &lt;attribute name="production" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ProductionSimpleType" />
 *       &lt;attribute name="custom" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comments" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WordType", propOrder = {
    "tag",
    "property",
    "link",
    "coords",
    "glyph",
    "textEquiv",
    "textStyle"
})
public class WordType {

    @XmlElement(name = "Tag")
    protected List<TagType> tag;
    @XmlElement(name = "Property")
    protected List<PropertyType> property;
    @XmlElement(name = "Link")
    protected List<LinkType> link;
    @XmlElement(name = "Coords", required = true)
    protected CoordsType coords;
    @XmlElement(name = "Glyph")
    protected List<GlyphType> glyph;
    @XmlElement(name = "TextEquiv")
    protected TextEquivType textEquiv;
    @XmlElement(name = "TextStyle")
    protected TextStyleType textStyle;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "language")
    protected LanguageSimpleType language;
    @XmlAttribute(name = "production")
    protected ProductionSimpleType production;
    @XmlAttribute(name = "custom")
    protected String custom;
    @XmlAttribute(name = "comments")
    protected String comments;

    /**
     * Gets the value of the tag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TagType }
     * 
     * 
     */
    public List<TagType> getTag() {
        if (tag == null) {
            tag = new ArrayList<TagType>();
        }
        return this.tag;
    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyType }
     * 
     * 
     */
    public List<PropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<PropertyType>();
        }
        return this.property;
    }

    /**
     * Gets the value of the link property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the link property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkType }
     * 
     * 
     */
    public List<LinkType> getLink() {
        if (link == null) {
            link = new ArrayList<LinkType>();
        }
        return this.link;
    }

    /**
     * Ruft den Wert der coords-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CoordsType }
     *     
     */
    public CoordsType getCoords() {
        return coords;
    }

    /**
     * Legt den Wert der coords-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordsType }
     *     
     */
    public void setCoords(CoordsType value) {
        this.coords = value;
    }

    /**
     * Gets the value of the glyph property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glyph property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlyph().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlyphType }
     * 
     * 
     */
    public List<GlyphType> getGlyph() {
        if (glyph == null) {
            glyph = new ArrayList<GlyphType>();
        }
        return this.glyph;
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
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der language-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LanguageSimpleType }
     *     
     */
    public LanguageSimpleType getLanguage() {
        return language;
    }

    /**
     * Legt den Wert der language-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageSimpleType }
     *     
     */
    public void setLanguage(LanguageSimpleType value) {
        this.language = value;
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

    /**
     * Ruft den Wert der custom-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustom() {
        return custom;
    }

    /**
     * Legt den Wert der custom-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustom(String value) {
        this.custom = value;
    }

    /**
     * Ruft den Wert der comments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Legt den Wert der comments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

}
