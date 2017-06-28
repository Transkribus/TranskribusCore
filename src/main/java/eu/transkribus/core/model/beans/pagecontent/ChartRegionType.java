//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.28 um 04:53:11 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;


/**
 * 
 * 				Regions containing charts or graphs of any type, should
 * 				be marked as chart regions.
 * 			
 * 
 * <p>Java-Klasse für ChartRegionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ChartRegionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}RegionType">
 *       &lt;attribute name="orientation" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="type" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ChartTypeSimpleType" />
 *       &lt;attribute name="numColours" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="bgColour" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ColourSimpleType" />
 *       &lt;attribute name="embText" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChartRegionType")
public class ChartRegionType
    extends TrpRegionType
{

    @XmlAttribute(name = "orientation")
    protected Float orientation;
    @XmlAttribute(name = "type")
    protected ChartTypeSimpleType type;
    @XmlAttribute(name = "numColours")
    protected Integer numColours;
    @XmlAttribute(name = "bgColour")
    protected ColourSimpleType bgColour;
    @XmlAttribute(name = "embText")
    protected Boolean embText;

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
     *     {@link ChartTypeSimpleType }
     *     
     */
    public ChartTypeSimpleType getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChartTypeSimpleType }
     *     
     */
    public void setType(ChartTypeSimpleType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der numColours-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumColours() {
        return numColours;
    }

    /**
     * Legt den Wert der numColours-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumColours(Integer value) {
        this.numColours = value;
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
     * Ruft den Wert der embText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEmbText() {
        return embText;
    }

    /**
     * Legt den Wert der embText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmbText(Boolean value) {
        this.embText = value;
    }

}
