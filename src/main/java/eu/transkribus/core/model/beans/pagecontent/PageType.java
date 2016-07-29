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
import javax.xml.bind.annotation.XmlType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;


/**
 * <p>Java-Klasse für PageType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tag" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TagType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Property" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AlternativeImage" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}AlternativeImageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Border" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}BorderType" minOccurs="0"/>
 *         &lt;element name="PrintSpace" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}PrintSpaceType" minOccurs="0"/>
 *         &lt;element name="ReadingOrder" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ReadingOrderType" minOccurs="0"/>
 *         &lt;element name="Layers" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}LayersType" minOccurs="0"/>
 *         &lt;element name="Relations" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}RelationsType" minOccurs="0"/>
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
 *       &lt;attribute name="imageFilename" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="imageWidth" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="imageHeight" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="custom" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}PageTypeSimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageType", propOrder = {
    "tag",
    "property",
    "link",
    "alternativeImage",
    "border",
    "printSpace",
    "readingOrder",
    "layers",
    "relations",
    "textRegionOrImageRegionOrLineDrawingRegion"
})
public class PageType {

    @XmlElement(name = "Tag")
    protected List<TagType> tag;
    @XmlElement(name = "Property")
    protected List<PropertyType> property;
    @XmlElement(name = "Link")
    protected List<LinkType> link;
    @XmlElement(name = "AlternativeImage")
    protected List<AlternativeImageType> alternativeImage;
    @XmlElement(name = "Border")
    protected BorderType border;
    @XmlElement(name = "PrintSpace")
    protected PrintSpaceType printSpace;
    @XmlElement(name = "ReadingOrder")
    protected ReadingOrderType readingOrder;
    @XmlElement(name = "Layers")
    protected LayersType layers;
    @XmlElement(name = "Relations")
    protected RelationsType relations;
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
    @XmlAttribute(name = "imageFilename", required = true)
    protected String imageFilename;
    @XmlAttribute(name = "imageWidth", required = true)
    protected int imageWidth;
    @XmlAttribute(name = "imageHeight", required = true)
    protected int imageHeight;
    @XmlAttribute(name = "custom")
    protected String custom;
    @XmlAttribute(name = "type")
    protected PageTypeSimpleType type;

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
     * Gets the value of the alternativeImage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativeImage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativeImage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlternativeImageType }
     * 
     * 
     */
    public List<AlternativeImageType> getAlternativeImage() {
        if (alternativeImage == null) {
            alternativeImage = new ArrayList<AlternativeImageType>();
        }
        return this.alternativeImage;
    }

    /**
     * Ruft den Wert der border-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BorderType }
     *     
     */
    public BorderType getBorder() {
        return border;
    }

    /**
     * Legt den Wert der border-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BorderType }
     *     
     */
    public void setBorder(BorderType value) {
        this.border = value;
    }

    /**
     * Ruft den Wert der printSpace-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PrintSpaceType }
     *     
     */
    public PrintSpaceType getPrintSpace() {
        return printSpace;
    }

    /**
     * Legt den Wert der printSpace-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PrintSpaceType }
     *     
     */
    public void setPrintSpace(PrintSpaceType value) {
        this.printSpace = value;
    }

    /**
     * Ruft den Wert der readingOrder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReadingOrderType }
     *     
     */
    public ReadingOrderType getReadingOrder() {
        return readingOrder;
    }

    /**
     * Legt den Wert der readingOrder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadingOrderType }
     *     
     */
    public void setReadingOrder(ReadingOrderType value) {
        this.readingOrder = value;
    }

    /**
     * Ruft den Wert der layers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LayersType }
     *     
     */
    public LayersType getLayers() {
        return layers;
    }

    /**
     * Legt den Wert der layers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LayersType }
     *     
     */
    public void setLayers(LayersType value) {
        this.layers = value;
    }

    /**
     * Ruft den Wert der relations-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RelationsType }
     *     
     */
    public RelationsType getRelations() {
        return relations;
    }

    /**
     * Legt den Wert der relations-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationsType }
     *     
     */
    public void setRelations(RelationsType value) {
        this.relations = value;
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
     * Ruft den Wert der imageFilename-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageFilename() {
        return imageFilename;
    }

    /**
     * Legt den Wert der imageFilename-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageFilename(String value) {
        this.imageFilename = value;
    }

    /**
     * Ruft den Wert der imageWidth-Eigenschaft ab.
     * 
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Legt den Wert der imageWidth-Eigenschaft fest.
     * 
     */
    public void setImageWidth(int value) {
        this.imageWidth = value;
    }

    /**
     * Ruft den Wert der imageHeight-Eigenschaft ab.
     * 
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Legt den Wert der imageHeight-Eigenschaft fest.
     * 
     */
    public void setImageHeight(int value) {
        this.imageHeight = value;
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
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PageTypeSimpleType }
     *     
     */
    public PageTypeSimpleType getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PageTypeSimpleType }
     *     
     */
    public void setType(PageTypeSimpleType value) {
        this.type = value;
    }

}
