//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.08.04 um 05:12:39 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PageTypeSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PageTypeSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="front-cover"/>
 *     &lt;enumeration value="back-cover"/>
 *     &lt;enumeration value="title"/>
 *     &lt;enumeration value="table-of-contents"/>
 *     &lt;enumeration value="index"/>
 *     &lt;enumeration value="content"/>
 *     &lt;enumeration value="blank"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PageTypeSimpleType")
@XmlEnum
public enum PageTypeSimpleType {

    @XmlEnumValue("front-cover")
    FRONT_COVER("front-cover"),
    @XmlEnumValue("back-cover")
    BACK_COVER("back-cover"),
    @XmlEnumValue("title")
    TITLE("title"),
    @XmlEnumValue("table-of-contents")
    TABLE_OF_CONTENTS("table-of-contents"),
    @XmlEnumValue("index")
    INDEX("index"),
    @XmlEnumValue("content")
    CONTENT("content"),
    @XmlEnumValue("blank")
    BLANK("blank"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    PageTypeSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PageTypeSimpleType fromValue(String v) {
        for (PageTypeSimpleType c: PageTypeSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
