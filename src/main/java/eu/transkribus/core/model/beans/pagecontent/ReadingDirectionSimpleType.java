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
 * <p>Java-Klasse für ReadingDirectionSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ReadingDirectionSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="left-to-right"/>
 *     &lt;enumeration value="right-to-left"/>
 *     &lt;enumeration value="top-to-bottom"/>
 *     &lt;enumeration value="bottom-to-top"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReadingDirectionSimpleType")
@XmlEnum
public enum ReadingDirectionSimpleType {

    @XmlEnumValue("left-to-right")
    LEFT_TO_RIGHT("left-to-right"),
    @XmlEnumValue("right-to-left")
    RIGHT_TO_LEFT("right-to-left"),
    @XmlEnumValue("top-to-bottom")
    TOP_TO_BOTTOM("top-to-bottom"),
    @XmlEnumValue("bottom-to-top")
    BOTTOM_TO_TOP("bottom-to-top");
    private final String value;

    ReadingDirectionSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReadingDirectionSimpleType fromValue(String v) {
        for (ReadingDirectionSimpleType c: ReadingDirectionSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
