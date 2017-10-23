package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Abstract wrapper for a map object. XML representation of a class implementing this will be like:
 * <pre>
   &lt;root&gt;
   	&lt;entry&gt;
   	  &lt;key>test&lt;/key&gt;
   	  &lt;value>testValue&lt;/value&gt;
   	&lt;/entry&gt;
   &lt;/root&gt;
 * </pre>
 * 
 * @author philip
 *
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AJaxbMap {
 
    protected Map<String, String> map = new HashMap<String, String>();
 
    @XmlElement(name = "entry")
    public MapEntry[] getMap() {
        List<MapEntry> list = new ArrayList<MapEntry>();
        for (Entry<String, String> entry : map.entrySet()) {
            MapEntry mapEntry =new MapEntry();
            mapEntry.key = entry.getKey();
            mapEntry.value = entry.getValue();
            list.add(mapEntry);
        }
        return list.toArray(new MapEntry[list.size()]);
    }
     
    public void setMap(MapEntry[] arr) {
        for(MapEntry entry : arr) {
            this.map.put(entry.key, entry.value);
        }
    }
 
 
    public static class MapEntry {
        @XmlElement
        public String key;
        @XmlElement
        public String value;
    }
}