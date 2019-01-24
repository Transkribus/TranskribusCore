package eu.transkribus.core.model.beans.rest;

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
    	if(arr == null) {
    		return;
    	}
        for(MapEntry entry : arr) {
            this.map.put(entry.key, entry.value);
        }
    }
 
 
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AJaxbMap other = (AJaxbMap) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}


	public static class MapEntry {
        @XmlElement
        public String key;
        @XmlElement
        public String value;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MapEntry other = (MapEntry) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
    }
}