//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.07.29 um 03:19:48 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;


/**
 * A single cell of a table
 * 
 * <p>Java-Klasse für TableCellType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TableCellType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}TextRegionType">
 *       &lt;sequence>
 *         &lt;element name="CornerPts" type="{http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15}CornerPointsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="row" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="col" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rowSpan" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="colSpan" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="leftBorderVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="rightBorderVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="topBorderVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="bottomBorderVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TableCellType", propOrder = {
    "cornerPts"
})
public class TableCellType
    extends TrpTextRegionType
{

    @XmlElement(name = "CornerPts", required = true, defaultValue = "0 1 2 3")
    protected String cornerPts;
    @XmlAttribute(name = "row", required = true)
    protected int row;
    @XmlAttribute(name = "col", required = true)
    protected int col;
    @XmlAttribute(name = "rowSpan")
    protected Integer rowSpan;
    @XmlAttribute(name = "colSpan")
    protected Integer colSpan;
    @XmlAttribute(name = "leftBorderVisible")
    protected Boolean leftBorderVisible;
    @XmlAttribute(name = "rightBorderVisible")
    protected Boolean rightBorderVisible;
    @XmlAttribute(name = "topBorderVisible")
    protected Boolean topBorderVisible;
    @XmlAttribute(name = "bottomBorderVisible")
    protected Boolean bottomBorderVisible;

    /**
     * Ruft den Wert der cornerPts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCornerPts() {
        return cornerPts;
    }

    /**
     * Legt den Wert der cornerPts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCornerPts(String value) {
        this.cornerPts = value;
    }

    /**
     * Ruft den Wert der row-Eigenschaft ab.
     * 
     */
    public int getRow() {
        return row;
    }

    /**
     * Legt den Wert der row-Eigenschaft fest.
     * 
     */
    public void setRow(int value) {
        this.row = value;
    }

    /**
     * Ruft den Wert der col-Eigenschaft ab.
     * 
     */
    public int getCol() {
        return col;
    }

    /**
     * Legt den Wert der col-Eigenschaft fest.
     * 
     */
    public void setCol(int value) {
        this.col = value;
    }

    /**
     * Ruft den Wert der rowSpan-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRowSpan() {
        if (rowSpan == null) {
            return  1;
        } else {
            return rowSpan;
        }
    }

    /**
     * Legt den Wert der rowSpan-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRowSpan(Integer value) {
        this.rowSpan = value;
    }

    /**
     * Ruft den Wert der colSpan-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getColSpan() {
        if (colSpan == null) {
            return  1;
        } else {
            return colSpan;
        }
    }

    /**
     * Legt den Wert der colSpan-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setColSpan(Integer value) {
        this.colSpan = value;
    }

    /**
     * Ruft den Wert der leftBorderVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLeftBorderVisible() {
        if (leftBorderVisible == null) {
            return false;
        } else {
            return leftBorderVisible;
        }
    }

    /**
     * Legt den Wert der leftBorderVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLeftBorderVisible(Boolean value) {
        this.leftBorderVisible = value;
    }

    /**
     * Ruft den Wert der rightBorderVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRightBorderVisible() {
        if (rightBorderVisible == null) {
            return false;
        } else {
            return rightBorderVisible;
        }
    }

    /**
     * Legt den Wert der rightBorderVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRightBorderVisible(Boolean value) {
        this.rightBorderVisible = value;
    }

    /**
     * Ruft den Wert der topBorderVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTopBorderVisible() {
        if (topBorderVisible == null) {
            return false;
        } else {
            return topBorderVisible;
        }
    }

    /**
     * Legt den Wert der topBorderVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTopBorderVisible(Boolean value) {
        this.topBorderVisible = value;
    }

    /**
     * Ruft den Wert der bottomBorderVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isBottomBorderVisible() {
        if (bottomBorderVisible == null) {
            return false;
        } else {
            return bottomBorderVisible;
        }
    }

    /**
     * Legt den Wert der bottomBorderVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBottomBorderVisible(Boolean value) {
        this.bottomBorderVisible = value;
    }

}
