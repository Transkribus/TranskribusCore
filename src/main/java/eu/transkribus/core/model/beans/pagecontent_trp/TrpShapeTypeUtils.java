package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpTextChangedEvent;
import eu.transkribus.core.util.SebisStopWatch;

public class TrpShapeTypeUtils {
	private final static Logger logger = LoggerFactory.getLogger(TrpShapeTypeUtils.class);
	
	public static TrpTextLineType getLine(ITrpShapeType st) {
		if (RegionTypeUtil.isBaseline(st))
			return ((TrpBaselineType) st).getLine();
		else if (RegionTypeUtil.isLine(st)) {
			return (TrpTextLineType) st;
		}
		
		return null;
	}

	public static TrpBaselineType getBaseline(ITrpShapeType st) {
		if (RegionTypeUtil.isBaseline(st))
			return (TrpBaselineType) st;
		else if (RegionTypeUtil.isLine(st)) {
			return (TrpBaselineType) ((TrpTextLineType) st).getBaseline();
		}
		
		return null;
	}
	
	public static void setUnicodeText(ITrpShapeType shape, String unicode, Object who) {
		logger.trace("setting unicode text in "+shape.getName()+", id: "+shape.getId()+", text: "+unicode);
		int lBefore = shape.getUnicodeText().length();
		TextEquivType te = new TextEquivType();
		te.setUnicode(unicode);
		shape.setTextEquiv(te);
	    
	    shape.getCustomTagList().onTextEdited(0, lBefore, unicode);
		shape.getObservable().setChangedAndNotifyObservers(new TrpTextChangedEvent(who, 0, lBefore, unicode));
	}
	
	public static void editUnicodeText(ITrpShapeType shape, int start, int end, String replacement, Object who) {
		logger.trace("editing unicode text in "+shape.getName()+", id: "+shape.getId()+", start/end: "+start+"/"+end+", text: "+replacement);
		StringBuilder sb = new StringBuilder(shape.getUnicodeText());
		sb.replace(start, end, replacement);
		String unicode = sb.toString();
		if (shape instanceof TrpWordType && unicode.equals(TrpWordType.EMPTY_WORD_FILL)) {
			unicode = "";
			replacement = "";
		}
		
		TextEquivType te = new TextEquivType();
		te.setUnicode(unicode);
		shape.setTextEquiv(te);
	    
		SebisStopWatch.SW.start();
		shape.getCustomTagList().onTextEdited(start, end, replacement);
		SebisStopWatch.SW.stop(true, "onTextEdited: ", logger);
		
		shape.getObservable().setChangedAndNotifyObservers(new TrpTextChangedEvent(who, start, end, replacement));
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void applyReadingOrderFromCoordinates(List content, boolean fireEvents, boolean deleteReadingOrder) {
		//sort with coordinates
		Collections.sort(content, new TrpElementCoordinatesComparator());
		
		int i=0;
		for (Object o : content) {
			if (o instanceof ITrpShapeType) {
				ITrpShapeType st = (ITrpShapeType) o;
				if (!deleteReadingOrder)
					logger.trace("setting reading order "+i+" to: "+st.getId());
				else
					logger.trace("deleting reading order from: "+st.getId());
				
				if (!fireEvents)
					st.getObservable().setActive(false);
				
				if (deleteReadingOrder)
					st.setReadingOrder(null, st);
				else
					st.setReadingOrder(i++, st);
				
				if (!fireEvents) 
					st.getObservable().setActive(true);
			}
		}
	}
	
//	public static void reinsertIntoParent(ITrpShapeType shape) {
//		reinsertIntoParent(shape, null);
//	}
//	
//	/**
//	 * Reinserts shape into its parent shape. If index is null, it will be appended to the end of the list.
//	 */
//	public static void reinsertIntoParent(ITrpShapeType shape, Integer index) {
//		ITrpShapeType parent = shape.getParentShape();
//		if (parent==null)
//			return;
//		
//		List<ITrpShapeType> children = parent.getChildren(false);
//		int nChildren = children.size();
//		
//		if (!children.contains(shape)) {
//			if (index==null)
//				children.add(shape);
//			else {
//				if (index<0)
//					index=0;
//				if (index>=nChildren)
//					index=nChildren-1;
//
//				children.add(index, shape);
//			}
//			
//			parent.sortChildren(false);
//			if (parent instanceof TrpTextRegionType) {
//				((TrpTextRegionType) parent).applyTextFromLines(); 
//			}
//			parent.getObservable().setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(shape));
//		}		
//	}
	
	
}
