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
 * <p>Java-Klasse für TextTypeSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="TextTypeSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="paragraph"/>
 *     &lt;enumeration value="heading"/>
 *     &lt;enumeration value="caption"/>
 *     &lt;enumeration value="header"/>
 *     &lt;enumeration value="footer"/>
 *     &lt;enumeration value="page-number"/>
 *     &lt;enumeration value="drop-capital"/>
 *     &lt;enumeration value="credit"/>
 *     &lt;enumeration value="floating"/>
 *     &lt;enumeration value="signature-mark"/>
 *     &lt;enumeration value="catch-word"/>
 *     &lt;enumeration value="marginalia"/>
 *     &lt;enumeration value="footnote"/>
 *     &lt;enumeration value="footnote-continued"/>
 *     &lt;enumeration value="endnote"/>
 *     &lt;enumeration value="TOC-entry"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TextTypeSimpleType")
@XmlEnum
public enum TextTypeSimpleType {

    @XmlEnumValue("paragraph")
    PARAGRAPH("paragraph"),
    @XmlEnumValue("heading")
    HEADING("heading"),
    @XmlEnumValue("caption")
    CAPTION("caption"),
    @XmlEnumValue("header")
    HEADER("header"),
    @XmlEnumValue("footer")
    FOOTER("footer"),
    @XmlEnumValue("page-number")
    PAGE_NUMBER("page-number"),
    @XmlEnumValue("drop-capital")
    DROP_CAPITAL("drop-capital"),
    @XmlEnumValue("credit")
    CREDIT("credit"),
    @XmlEnumValue("floating")
    FLOATING("floating"),
    @XmlEnumValue("signature-mark")
    SIGNATURE_MARK("signature-mark"),
    @XmlEnumValue("catch-word")
    CATCH_WORD("catch-word"),
    @XmlEnumValue("marginalia")
    MARGINALIA("marginalia"),
    @XmlEnumValue("footnote")
    FOOTNOTE("footnote"),
    @XmlEnumValue("footnote-continued")
    FOOTNOTE_CONTINUED("footnote-continued"),
    @XmlEnumValue("endnote")
    ENDNOTE("endnote"),
    @XmlEnumValue("TOC-entry")
    TOC_ENTRY("TOC-entry"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    TextTypeSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TextTypeSimpleType fromValue(String v) {
        for (TextTypeSimpleType c: TextTypeSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
