//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.21 um 11:17:10 AM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AlignSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="AlignSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="left"/>
 *     &lt;enumeration value="centre"/>
 *     &lt;enumeration value="right"/>
 *     &lt;enumeration value="justify"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AlignSimpleType")
@XmlEnum
public enum AlignSimpleType {

    @XmlEnumValue("left")
    LEFT("left"),
    @XmlEnumValue("centre")
    CENTRE("centre"),
    @XmlEnumValue("right")
    RIGHT("right"),
    @XmlEnumValue("justify")
    JUSTIFY("justify");
    private final String value;

    AlignSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlignSimpleType fromValue(String v) {
        for (AlignSimpleType c: AlignSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
