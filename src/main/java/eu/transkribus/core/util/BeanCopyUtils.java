package eu.transkribus.core.util;

import eu.transkribus.core.model.beans.pagecontent.CellCoordsType;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;

/** Provides functions to copy beans from the Page JAXB. */
public class BeanCopyUtils {
	
	public static CoordsType copyCellCoordsType(CoordsType coords) {
		if (coords == null)
			return null;
		
		CoordsType copy = new CoordsType();
		copy.setPoints(coords.getPoints());
		return copy;
	}
	
	public static CellCoordsType copyCellCoordsType(CellCoordsType coords) {
		if (coords == null)
			return null;
		
		CellCoordsType copy = new CellCoordsType();
		copy.setPoints0(coords.getPoints0());
		copy.setPoints1(coords.getPoints1());
		copy.setPoints2(coords.getPoints2());
		copy.setPoints3(coords.getPoints3());
		return copy;
	}
	
	public static TextEquivType copyTextEquivType(TextEquivType text) {
		if (text == null)
			return null;
		
		TextEquivType copy = new TextEquivType();
		copy.setConf(text.getConf());
		copy.setPlainText(text.getPlainText());
		copy.setUnicode(text.getUnicode());
		
		return copy;
	}

	public static TextStyleType copyTextStyleType(TextStyleType style) {
		if (style == null)
			return null;
		
		TextStyleType copy = new TextStyleType();
		TextStyleTypeUtils.copyTextStyleTypeFields(style, copy);
		
		return copy;
	}
	


}
