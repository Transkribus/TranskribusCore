//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.07.29 um 03:19:48 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChartTypeSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ChartTypeSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="bar"/>
 *     &lt;enumeration value="line"/>
 *     &lt;enumeration value="pie"/>
 *     &lt;enumeration value="scatter"/>
 *     &lt;enumeration value="surface"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChartTypeSimpleType")
@XmlEnum
public enum ChartTypeSimpleType {

    @XmlEnumValue("bar")
    BAR("bar"),
    @XmlEnumValue("line")
    LINE("line"),
    @XmlEnumValue("pie")
    PIE("pie"),
    @XmlEnumValue("scatter")
    SCATTER("scatter"),
    @XmlEnumValue("surface")
    SURFACE("surface"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    ChartTypeSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChartTypeSimpleType fromValue(String v) {
        for (ChartTypeSimpleType c: ChartTypeSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
