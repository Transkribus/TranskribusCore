//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.07.29 um 03:19:48 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Definition of the reading order within the page. To express a reading order between elements they have to be included in an OrderedGroup. Groups may contain further groups.
 * 
 * <p>Java-Klasse für ReadingOrderType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReadingOrderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="OrderedGroup" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}OrderedGroupType"/>
 *         &lt;element name="UnorderedGroup" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}UnorderedGroupType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadingOrderType", propOrder = {
    "orderedGroup",
    "unorderedGroup"
})
public class ReadingOrderType {

    @XmlElement(name = "OrderedGroup")
    protected OrderedGroupType orderedGroup;
    @XmlElement(name = "UnorderedGroup")
    protected UnorderedGroupType unorderedGroup;

    /**
     * Ruft den Wert der orderedGroup-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderedGroupType }
     *     
     */
    public OrderedGroupType getOrderedGroup() {
        return orderedGroup;
    }

    /**
     * Legt den Wert der orderedGroup-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderedGroupType }
     *     
     */
    public void setOrderedGroup(OrderedGroupType value) {
        this.orderedGroup = value;
    }

    /**
     * Ruft den Wert der unorderedGroup-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UnorderedGroupType }
     *     
     */
    public UnorderedGroupType getUnorderedGroup() {
        return unorderedGroup;
    }

    /**
     * Legt den Wert der unorderedGroup-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UnorderedGroupType }
     *     
     */
    public void setUnorderedGroup(UnorderedGroupType value) {
        this.unorderedGroup = value;
    }

}
