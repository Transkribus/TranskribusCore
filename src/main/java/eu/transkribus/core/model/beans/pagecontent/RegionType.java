//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.07.29 um 03:19:48 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;


/**
 * <p>Java-Klasse für RegionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RegionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tag" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TagType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Property" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Coords" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}CoordsType"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="TextRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextRegionType"/>
 *           &lt;element name="ImageRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ImageRegionType"/>
 *           &lt;element name="LineDrawingRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LineDrawingRegionType"/>
 *           &lt;element name="GraphicRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}GraphicRegionType"/>
 *           &lt;element name="TableRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TableRegionType"/>
 *           &lt;element name="ChartRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ChartRegionType"/>
 *           &lt;element name="SeparatorRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}SeparatorRegionType"/>
 *           &lt;element name="MathsRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}MathsRegionType"/>
 *           &lt;element name="ChemRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ChemRegionType"/>
 *           &lt;element name="MusicRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}MusicRegionType"/>
 *           &lt;element name="AdvertRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}AdvertRegionType"/>
 *           &lt;element name="NoiseRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}NoiseRegionType"/>
 *           &lt;element name="UnknownRegion" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}UnknownRegionType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
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
@XmlType(name = "RegionType", propOrder = {
    "tag",
    "property",
    "link",
    "coords",
    "textRegionOrImageRegionOrLineDrawingRegion"
})
@XmlSeeAlso({
    MusicRegionType.class,
    MathsRegionType.class,
    TableRegionType.class,
    NoiseRegionType.class,
    ChartRegionType.class,
    GraphicRegionType.class,
    ImageRegionType.class,
    ChemRegionType.class,
    UnknownRegionType.class,
    TrpTextRegionType.class,
    AdvertRegionType.class,
    SeparatorRegionType.class,
    LineDrawingRegionType.class
})
public abstract class RegionType {

    @XmlElement(name = "Tag")
    protected List<TagType> tag;
    @XmlElement(name = "Property")
    protected List<PropertyType> property;
    @XmlElement(name = "Link")
    protected List<LinkType> link;
    @XmlElement(name = "Coords", required = true)
    protected CoordsType coords;
    @XmlElements({
        @XmlElement(name = "TextRegion", type = TrpTextRegionType.class),
        @XmlElement(name = "ImageRegion", type = ImageRegionType.class),
        @XmlElement(name = "LineDrawingRegion", type = LineDrawingRegionType.class),
        @XmlElement(name = "GraphicRegion", type = GraphicRegionType.class),
        @XmlElement(name = "TableRegion", type = TableRegionType.class),
        @XmlElement(name = "ChartRegion", type = ChartRegionType.class),
        @XmlElement(name = "SeparatorRegion", type = SeparatorRegionType.class),
        @XmlElement(name = "MathsRegion", type = MathsRegionType.class),
        @XmlElement(name = "ChemRegion", type = ChemRegionType.class),
        @XmlElement(name = "MusicRegion", type = MusicRegionType.class),
        @XmlElement(name = "AdvertRegion", type = AdvertRegionType.class),
        @XmlElement(name = "NoiseRegion", type = NoiseRegionType.class),
        @XmlElement(name = "UnknownRegion", type = UnknownRegionType.class)
    })
    protected List<TrpRegionType> textRegionOrImageRegionOrLineDrawingRegion;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
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
     * Gets the value of the textRegionOrImageRegionOrLineDrawingRegion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textRegionOrImageRegionOrLineDrawingRegion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextRegionOrImageRegionOrLineDrawingRegion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TextRegionType }
     * {@link ImageRegionType }
     * {@link LineDrawingRegionType }
     * {@link GraphicRegionType }
     * {@link TableRegionType }
     * {@link ChartRegionType }
     * {@link SeparatorRegionType }
     * {@link MathsRegionType }
     * {@link ChemRegionType }
     * {@link MusicRegionType }
     * {@link AdvertRegionType }
     * {@link NoiseRegionType }
     * {@link UnknownRegionType }
     * 
     * 
     */
    public List<TrpRegionType> getTextRegionOrImageRegionOrLineDrawingRegion() {
        if (textRegionOrImageRegionOrLineDrawingRegion == null) {
            textRegionOrImageRegionOrLineDrawingRegion = new ArrayList<TrpRegionType>();
        }
        return this.textRegionOrImageRegionOrLineDrawingRegion;
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
