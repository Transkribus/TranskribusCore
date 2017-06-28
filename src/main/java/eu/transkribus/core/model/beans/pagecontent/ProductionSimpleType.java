//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.28 um 04:53:11 PM CEST 
//


package eu.transkribus.core.model.beans.pagecontent;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ProductionSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ProductionSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="printed"/>
 *     &lt;enumeration value="typewritten"/>
 *     &lt;enumeration value="handwritten-cursive"/>
 *     &lt;enumeration value="handwritten-printscript"/>
 *     &lt;enumeration value="medieval-manuscript"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProductionSimpleType")
@XmlEnum
public enum ProductionSimpleType {

    @XmlEnumValue("printed")
    PRINTED("printed"),
    @XmlEnumValue("typewritten")
    TYPEWRITTEN("typewritten"),
    @XmlEnumValue("handwritten-cursive")
    HANDWRITTEN_CURSIVE("handwritten-cursive"),
    @XmlEnumValue("handwritten-printscript")
    HANDWRITTEN_PRINTSCRIPT("handwritten-printscript"),
    @XmlEnumValue("medieval-manuscript")
    MEDIEVAL_MANUSCRIPT("medieval-manuscript"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    ProductionSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProductionSimpleType fromValue(String v) {
        for (ProductionSimpleType c: ProductionSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
