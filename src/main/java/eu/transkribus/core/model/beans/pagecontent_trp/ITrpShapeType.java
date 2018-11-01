package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Rectangle;
import java.util.List;
import java.util.Observer;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.util.PointStrUtils;

public interface ITrpShapeType {
	public static final int PRINTSPACE_BASE_LEVEL = -1;
	public static final int REGION_BASE_LEVEL = 0;
	public static final int LINE_BASE_LEVEL = 100;
	public static final int BASELINE_BASE_LEVEL = 200;
	public static final int WORD_BASE_LEVEL = 300;
	
	static int getMaxLevel() {
		return WORD_BASE_LEVEL;
	}
	
	ITrpShapeType copy();
	void copyFields(ITrpShapeType src);
	
	String getName();
	String getId();
	void setId(String id);
	int getLevel();
	
	TrpPageType getPage();
	ITrpShapeType getParentShape();
//	int getNestingLevel();
	void setParent(Object parent);
	Object getParent();
	ITrpShapeType getSiblingShape(boolean previous);
	
	Object getData();
	void setData(Object data);
	
	TextStyleType getTextStyle();
	void setTextStyle(TextStyleType s, boolean recursive, Object who);
	/** Is only used internally - does not fire events etc.!*/
	void setTextStyle(TextStyleType s);
	
	void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who);
	List<TextStyleTag> getTextStyleTags();
	
	void setReadingOrder(Integer readingOrder, Object who);
	Integer getReadingOrder();
	default int getReadingOrderAsInt() {
		Integer ro = getReadingOrder();
		return ro != null ? ro : -1;
	}
	
	void setStructure(String structureType, boolean recursive, Object who);
	String getStructure();
	
	void setCoordinates(String value, Object who);
	String getCoordinates();
	default Rectangle getBoundingBox() {
		return PointStrUtils.getBoundingBox(getCoordinates());
	}
	
	void setUnicodeText(String unicode, Object who);
	void editUnicodeText(int start, int end, String replacement, Object who);
	
	String getUnicodeText();
	
	List<ITrpShapeType> getChildren(boolean recursive);
	boolean hasChildren();
	void reInsertIntoParent();
	void reInsertIntoParent(int index);
	void removeFromParent();
	void removeChildren();
	
	String print();
	
	String getCustom();
	void setCustom(String custom);
	
	void rotate(double degrees) throws Exception;
	void translate(int x, int y) throws Exception;
	
	// The observable stuff:
	TrpObservable getObservable();
    void addObserver(Observer o);
    void deleteObserver(Observer o);
    void deleteObservers();
    
    CustomTagList getCustomTagList();

	void setTextEquiv(TextEquivType te);
	void sortChildren(boolean recursive);
	void swap(int direction);
}
