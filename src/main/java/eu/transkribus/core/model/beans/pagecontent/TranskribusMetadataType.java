//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.21 um 11:17:10 AM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TranskribusMetadataType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TranskribusMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="docId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pageId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pageNr" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tsid" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="userId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="imgUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xmlUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="imageId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TranskribusMetadataType")
public class TranskribusMetadataType {

    @XmlAttribute(name = "docId", required = true)
    protected int docId;
    @XmlAttribute(name = "pageId", required = true)
    protected int pageId;
    @XmlAttribute(name = "pageNr", required = true)
    protected int pageNr;
    @XmlAttribute(name = "tsid")
    protected Integer tsid;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "userId")
    protected Integer userId;
    @XmlAttribute(name = "imgUrl")
    protected String imgUrl;
    @XmlAttribute(name = "xmlUrl")
    protected String xmlUrl;
    @XmlAttribute(name = "imageId")
    protected Integer imageId;

    /**
     * Ruft den Wert der docId-Eigenschaft ab.
     * 
     */
    public int getDocId() {
        return docId;
    }

    /**
     * Legt den Wert der docId-Eigenschaft fest.
     * 
     */
    public void setDocId(int value) {
        this.docId = value;
    }

    /**
     * Ruft den Wert der pageId-Eigenschaft ab.
     * 
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Legt den Wert der pageId-Eigenschaft fest.
     * 
     */
    public void setPageId(int value) {
        this.pageId = value;
    }

    /**
     * Ruft den Wert der pageNr-Eigenschaft ab.
     * 
     */
    public int getPageNr() {
        return pageNr;
    }

    /**
     * Legt den Wert der pageNr-Eigenschaft fest.
     * 
     */
    public void setPageNr(int value) {
        this.pageNr = value;
    }

    /**
     * Ruft den Wert der tsid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTsid() {
        return tsid;
    }

    /**
     * Legt den Wert der tsid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTsid(Integer value) {
        this.tsid = value;
    }

    /**
     * Ruft den Wert der status-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Legt den Wert der status-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Ruft den Wert der userId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Legt den Wert der userId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUserId(Integer value) {
        this.userId = value;
    }

    /**
     * Ruft den Wert der imgUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * Legt den Wert der imgUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImgUrl(String value) {
        this.imgUrl = value;
    }

    /**
     * Ruft den Wert der xmlUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlUrl() {
        return xmlUrl;
    }

    /**
     * Legt den Wert der xmlUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlUrl(String value) {
        this.xmlUrl = value;
    }

    /**
     * Ruft den Wert der imageId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImageId() {
        return imageId;
    }

    /**
     * Legt den Wert der imageId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImageId(Integer value) {
        this.imageId = value;
    }

}
