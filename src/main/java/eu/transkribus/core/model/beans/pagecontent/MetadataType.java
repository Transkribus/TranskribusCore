//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.28 um 04:53:11 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für MetadataType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Creator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Created" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="LastChange" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TranskribusMetadata" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TranskribusMetadataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataType", propOrder = {
    "creator",
    "created",
    "lastChange",
    "comments",
    "transkribusMetadata"
})
public class MetadataType {

    @XmlElement(name = "Creator", required = true)
    protected String creator;
    @XmlElement(name = "Created", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    @XmlElement(name = "LastChange", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastChange;
    @XmlElement(name = "Comments")
    protected String comments;
    @XmlElement(name = "TranskribusMetadata")
    protected TranskribusMetadataType transkribusMetadata;

    /**
     * Ruft den Wert der creator-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Legt den Wert der creator-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Ruft den Wert der created-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Legt den Wert der created-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Ruft den Wert der lastChange-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastChange() {
        return lastChange;
    }

    /**
     * Legt den Wert der lastChange-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastChange(XMLGregorianCalendar value) {
        this.lastChange = value;
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

    /**
     * Ruft den Wert der transkribusMetadata-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TranskribusMetadataType }
     *     
     */
    public TranskribusMetadataType getTranskribusMetadata() {
        return transkribusMetadata;
    }

    /**
     * Legt den Wert der transkribusMetadata-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TranskribusMetadataType }
     *     
     */
    public void setTranskribusMetadata(TranskribusMetadataType value) {
        this.transkribusMetadata = value;
    }

}
