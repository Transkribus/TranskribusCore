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
 * <p>Java-Klasse für ColourSimpleType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ColourSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="black"/>
 *     &lt;enumeration value="blue"/>
 *     &lt;enumeration value="brown"/>
 *     &lt;enumeration value="cyan"/>
 *     &lt;enumeration value="green"/>
 *     &lt;enumeration value="grey"/>
 *     &lt;enumeration value="indigo"/>
 *     &lt;enumeration value="magenta"/>
 *     &lt;enumeration value="orange"/>
 *     &lt;enumeration value="pink"/>
 *     &lt;enumeration value="red"/>
 *     &lt;enumeration value="turquoise"/>
 *     &lt;enumeration value="violet"/>
 *     &lt;enumeration value="white"/>
 *     &lt;enumeration value="yellow"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ColourSimpleType")
@XmlEnum
public enum ColourSimpleType {

    @XmlEnumValue("black")
    BLACK("black"),
    @XmlEnumValue("blue")
    BLUE("blue"),
    @XmlEnumValue("brown")
    BROWN("brown"),
    @XmlEnumValue("cyan")
    CYAN("cyan"),
    @XmlEnumValue("green")
    GREEN("green"),
    @XmlEnumValue("grey")
    GREY("grey"),
    @XmlEnumValue("indigo")
    INDIGO("indigo"),
    @XmlEnumValue("magenta")
    MAGENTA("magenta"),
    @XmlEnumValue("orange")
    ORANGE("orange"),
    @XmlEnumValue("pink")
    PINK("pink"),
    @XmlEnumValue("red")
    RED("red"),
    @XmlEnumValue("turquoise")
    TURQUOISE("turquoise"),
    @XmlEnumValue("violet")
    VIOLET("violet"),
    @XmlEnumValue("white")
    WHITE("white"),
    @XmlEnumValue("yellow")
    YELLOW("yellow"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    ColourSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColourSimpleType fromValue(String v) {
        for (ColourSimpleType c: ColourSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
