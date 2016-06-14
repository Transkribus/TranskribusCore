package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.management.RuntimeErrorException;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.TableCellType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.util.BeanCopyUtils;

public class TrpTableCellType extends TableCellType implements ITrpShapeType {
	
	private final static Logger logger = LoggerFactory.getLogger(TrpTableCellType.class);
	
 	@XmlTransient
 	protected TrpObservable observable;
 	@XmlTransient
 	protected TrpTableRegionType parent=null;
 	@XmlTransient
	protected CustomTagList customTagList;
 	
 	@XmlTransient
	Object data;
	
	public TrpTableCellType() {
		
		logger.debug("created TrpTableCellType!");
		
		observable = new TrpObservable(this);
		customTagList = new CustomTagList(this);
	}

	@Override public ITrpShapeType copy() {
		return null;
	}

	@Override public void copyFields(ITrpShapeType src) {
		if (!(src instanceof TrpTableCellType))
			throw new RuntimeException("copyFields - not a TrpTableCellType!");
		
		TrpTableCellType srcCell = (TrpTableCellType) src;
		
		this.id = TrpPageType.getUniqueId(getName());
		this.coords = BeanCopyUtils.copyCellCoordsType(srcCell.coords);
		
		if (srcCell.textLine!=null)
			textLine = new ArrayList<>(srcCell.textLine);
		
		this.row = srcCell.row;
		this.col = srcCell.col;
		this.rowSpan = srcCell.rowSpan;
		this.colSpan = srcCell.colSpan;
		
	    if (srcCell.getCustomTagList() != null)
	    	srcCell.getCustomTagList().writeToCustomTag();
	    if (srcCell.custom != null)
	    	custom = new String(srcCell.custom);
	    
	    if (srcCell.comments != null)
	    	comments = new String(srcCell.comments);
			
		// copy new fields:
		parent = srcCell.parent;
		data = srcCell.data;
		
		observable = new TrpObservable(this);
		customTagList = new CustomTagList(this);
	}

	@Override public String getName() {
		return "TableCell";
	}

	@Override public int getLevel() {
		return 0;
	}

	@Override public TrpPageType getPage() {
		return null;
	}

	@Override public ITrpShapeType getParentShape() {
		return null;
	}

	@Override public void setParent(Object parent) {
	}

	@Override public Object getParent() {
		return null;
	}

	@Override public ITrpShapeType getSiblingShape(boolean previous) {
		return null;
	}

	@Override public Object getData() {
		return data;
	}

	@Override public void setData(Object data) {
		this.data = data;
	}

	@Override public TextStyleType getTextStyle() {
		return null;
	}

	@Override public void setTextStyle(TextStyleType s, boolean recursive, Object who) {
	}

	@Override public void setTextStyle(TextStyleType s) {
	}

	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, Object who) {
	}

	@Override public List<TextStyleTag> getTextStyleTags() {
		return null;
	}

	@Override public void setReadingOrder(Integer readingOrder, Object who) {
	}

	@Override public Integer getReadingOrder() {
		return null;
	}

	@Override public void setStructure(String structureType, boolean recursive, Object who) {
	}

	@Override public String getStructure() {
		return null;
	}

	@Override public void setCoordinates(String value, Object who) {
	}

	@Override public String getCoordinates() {
		return null;
	}

	@Override public void setUnicodeText(String unicode, Object who) {
	}

	@Override public void editUnicodeText(int start, int end, String replacement, Object who) {
	}

	@Override public String getUnicodeText() {
		return null;
	}

	@Override public List<ITrpShapeType> getChildren(boolean recursive) {
		return null;
	}

	@Override public boolean hasChildren() {
		return false;
	}

	@Override public void reInsertIntoParent() {
	}

	@Override public void reInsertIntoParent(int index) {
	}

	@Override public void removeFromParent() {
	}

	@Override public void removeChildren() {
	}

	@Override public String print() {
		return null;
	}

	@Override public void rotate(double degrees) throws Exception {
	}

	@Override public void translate(int x, int y) throws Exception {
	}

	@Override public TrpObservable getObservable() {
		return null;
	}

	@Override public void addObserver(Observer o) {
	}

	@Override public void deleteObserver(Observer o) {
	}

	@Override public void deleteObservers() {
	}

	@Override public CustomTagList getCustomTagList() {
		return null;
	}

	@Override public void setTextEquiv(TextEquivType te) {
	}

	@Override public void sortChildren(boolean recursive) {
	}

	@Override public void swap(int direction) {
	}

}
