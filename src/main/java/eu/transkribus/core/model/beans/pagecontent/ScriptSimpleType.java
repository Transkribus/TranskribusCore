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
 * <p>Java-Klasse für ScriptSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ScriptSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Arabic"/>
 *     &lt;enumeration value="Bengali"/>
 *     &lt;enumeration value="Chinese-simplified"/>
 *     &lt;enumeration value="Chinese-traditional"/>
 *     &lt;enumeration value="Cyrillic"/>
 *     &lt;enumeration value="Devangari"/>
 *     &lt;enumeration value="Ethiopic"/>
 *     &lt;enumeration value="Greek"/>
 *     &lt;enumeration value="Gujarati"/>
 *     &lt;enumeration value="Gurmukhi"/>
 *     &lt;enumeration value="Hebrew"/>
 *     &lt;enumeration value="Latin"/>
 *     &lt;enumeration value="Thai"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScriptSimpleType")
@XmlEnum
public enum ScriptSimpleType {

    @XmlEnumValue("Arabic")
    ARABIC("Arabic"),
    @XmlEnumValue("Bengali")
    BENGALI("Bengali"),
    @XmlEnumValue("Chinese-simplified")
    CHINESE_SIMPLIFIED("Chinese-simplified"),
    @XmlEnumValue("Chinese-traditional")
    CHINESE_TRADITIONAL("Chinese-traditional"),
    @XmlEnumValue("Cyrillic")
    CYRILLIC("Cyrillic"),
    @XmlEnumValue("Devangari")
    DEVANGARI("Devangari"),
    @XmlEnumValue("Ethiopic")
    ETHIOPIC("Ethiopic"),
    @XmlEnumValue("Greek")
    GREEK("Greek"),
    @XmlEnumValue("Gujarati")
    GUJARATI("Gujarati"),
    @XmlEnumValue("Gurmukhi")
    GURMUKHI("Gurmukhi"),
    @XmlEnumValue("Hebrew")
    HEBREW("Hebrew"),
    @XmlEnumValue("Latin")
    LATIN("Latin"),
    @XmlEnumValue("Thai")
    THAI("Thai"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    ScriptSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScriptSimpleType fromValue(String v) {
        for (ScriptSimpleType c: ScriptSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
