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
import javax.xml.bind.annotation.XmlType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;


/**
 * 
 * 				The new table region type - a table is basically a collection of cells
 * 			
 * 
 * <p>Java-Klasse für TableRegionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TableRegionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}RegionType">
 *       &lt;sequence>
 *         &lt;element name="TableCell" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TableCellType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orientation" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="rows" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="columns" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lineColour" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ColourSimpleType" />
 *       &lt;attribute name="bgColour" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}ColourSimpleType" />
 *       &lt;attribute name="lineSeparators" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="embText" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TableRegionType", propOrder = {
    "tableCell"
})
public class TableRegionType
    extends TrpRegionType
{

    @XmlElement(name = "TableCell")
    protected List<TableCellType> tableCell;
    @XmlAttribute(name = "orientation")
    protected Float orientation;
    @XmlAttribute(name = "rows")
    protected Integer rows;
    @XmlAttribute(name = "columns")
    protected Integer columns;
    @XmlAttribute(name = "lineColour")
    protected ColourSimpleType lineColour;
    @XmlAttribute(name = "bgColour")
    protected ColourSimpleType bgColour;
    @XmlAttribute(name = "lineSeparators")
    protected Boolean lineSeparators;
    @XmlAttribute(name = "embText")
    protected Boolean embText;

    /**
     * Gets the value of the tableCell property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableCell property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableCell().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableCellType }
     * 
     * 
     */
    public List<TableCellType> getTableCell() {
        if (tableCell == null) {
            tableCell = new ArrayList<TableCellType>();
        }
        return this.tableCell;
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
     * Ruft den Wert der rows-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * Legt den Wert der rows-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRows(Integer value) {
        this.rows = value;
    }

    /**
     * Ruft den Wert der columns-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getColumns() {
        return columns;
    }

    /**
     * Legt den Wert der columns-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setColumns(Integer value) {
        this.columns = value;
    }

    /**
     * Ruft den Wert der lineColour-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ColourSimpleType }
     *     
     */
    public ColourSimpleType getLineColour() {
        return lineColour;
    }

    /**
     * Legt den Wert der lineColour-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ColourSimpleType }
     *     
     */
    public void setLineColour(ColourSimpleType value) {
        this.lineColour = value;
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
     * Ruft den Wert der lineSeparators-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLineSeparators() {
        return lineSeparators;
    }

    /**
     * Legt den Wert der lineSeparators-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLineSeparators(Boolean value) {
        this.lineSeparators = value;
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
