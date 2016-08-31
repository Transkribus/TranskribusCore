//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.08.31 um 10:07:11 AM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ColourDepthSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ColourDepthSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="bilevel"/>
 *     &lt;enumeration value="greyscale"/>
 *     &lt;enumeration value="colour"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ColourDepthSimpleType")
@XmlEnum
public enum ColourDepthSimpleType {

    @XmlEnumValue("bilevel")
    BILEVEL("bilevel"),
    @XmlEnumValue("greyscale")
    GREYSCALE("greyscale"),
    @XmlEnumValue("colour")
    COLOUR("colour"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    ColourDepthSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColourDepthSimpleType fromValue(String v) {
        for (ColourDepthSimpleType c: ColourDepthSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
