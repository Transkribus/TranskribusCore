package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpTextChangedEvent;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;
import eu.transkribus.core.util.SebisStopWatch;

public class TrpShapeTypeUtils {
	private final static Logger logger = LoggerFactory.getLogger(TrpShapeTypeUtils.class);
	
	public static <T> void sortShapesByReadingOrderOrCoordinates(List<T> shapes) {
		try {
			Collections.sort(shapes, new TrpElementReadingOrderComparator<T>(true));
		}
		catch (Exception e) {
			logger.warn("could not sort regions by reading order, exception = "+e.getMessage() +" - now sorting by yx coordinates(1)!");
			sortShapesByCoordinates(shapes, true);
		}
	}
	
	public static <T> void sortShapesByCoordinates(List<T> shapes, boolean forceCompareByYX) {
		try {
			Collections.sort(shapes, new TrpElementCoordinatesComparator<T>(forceCompareByYX));
		}
		catch (Exception e) {
			logger.warn("could not coordinates, exception = "+e.getMessage() +" - now sorting by yx coordinates(2)!");
			try {
				Collections.sort(shapes, new TrpElementCoordinatesComparator<T>(true));
			} catch (Exception e1) {
				logger.error("Still could not sort shapes -> should not happen here: "+e1.getMessage()+" - skipping sorting", e1);
			}
		}
	}
	
	public static void sortShapesByXY(List<ITrpShapeType> shapes) {
		try {
			Collections.sort(shapes, new TrpShapeTypeXYComparator());
		}
		catch (Exception e) {
			logger.error("Could not sort shape by XY coordinates - skipping!", e);
		}
	}
	
	public static Pair<String, String> invertCoordsCommonRegionOrientation(String coords1, String coords2, Object o1, Object o2) {
		Double orientationInRadiants = null;
		if (o1 instanceof ITrpShapeType && o2 instanceof ITrpShapeType && !(o1 instanceof RegionType) && !(o2 instanceof RegionType)) {
			TrpTextRegionType tr1 = TrpShapeTypeUtils.getTextRegion((ITrpShapeType) o1);
			TrpTextRegionType tr2 = TrpShapeTypeUtils.getTextRegion((ITrpShapeType) o2);
			
			if (tr1!=null && tr2!=null && StringUtils.equals(tr1.getId(), tr2.getId()) && tr1.getOrientation()!=null) {
				orientationInRadiants = Math.toRadians(tr1.getOrientation());
			}
		}
		
		if (orientationInRadiants!=null) {
			coords1 = PointStrUtils.rotatePoints(coords1, orientationInRadiants);
			coords2 = PointStrUtils.rotatePoints(coords2, orientationInRadiants);
			logger.trace("orientation set: "+orientationInRadiants+" rotated points: "+coords1+", "+coords2);
		}
		
		return Pair.of(coords1, coords2);
	}
	
	
	public static boolean removeShape(ITrpShapeType s) {
		if (s == null) {
			return false;
		}
		
		s.removeFromParent();
		
		// FIXME: what about the links on undo??
		// remove all links related to this shape:
		if (s.getPage() != null) {
			s.getPage().removeLinks(s);
			s.getPage().removeDeadLinks();
		}
		
		//sort children: means create new reading order without the deleted shape
		if (s.getParentShape() != null) {
			s.getParentShape().sortChildren(true);
		}
		
		return true;
	}
	

	/**
	 * Currently only implemented for TrpWordType, TrpTextLineType and TrpTextRegionType
	 * @param shape The shape for which its neighbor shape shall be found.
	 * @param previous True to get the previous sibling, false to get the next one.
	 * @param wrap True to wrap the search, i.e. to return the first shape if the end was reached and vice versa.
	 * @return 
	 */
	public static ITrpShapeType getNeighborShape(ITrpShapeType shape, boolean previous, boolean wrap) {
		List<? extends ITrpShapeType> shapes = null;
		if (shape instanceof TrpWordType) {
			shapes = shape.getPage().getWords();
		}
		else if (shape instanceof TrpTextLineType) {
			shapes = shape.getPage().getLines();
		}
		else if (shape instanceof TrpTextRegionType) {
			shapes = shape.getPage().getTextRegions(false);
		}
		
		return CoreUtils.getNeighborElement(shapes, shape, previous, wrap);
	}
	
	/**
	 * Tries to get the (parent) text region of the specified shape; returns null if not found 
	 */
	public static TrpTextRegionType getTextRegion(ITrpShapeType st) {
		if (RegionTypeUtil.isBaseline(st)) {
			TrpBaselineType bl = (TrpBaselineType) st;
			if (bl.getLine() != null) {
				return bl.getLine().getRegion();
			}
		}
		else if (RegionTypeUtil.isLine(st)) {
			return ((TrpTextLineType) st).getRegion();
		}
		else if (RegionTypeUtil.isWord(st)) {
			TrpWordType w = (TrpWordType) st;
			if (w.getLine() != null) {
				return w.getLine().getRegion();
			}
		}
		
		return null;
	}
		
	public static TrpTextLineType getLine(ITrpShapeType st) {
		if (RegionTypeUtil.isBaseline(st))
			return ((TrpBaselineType) st).getLine();
		else if (RegionTypeUtil.isLine(st)) {
			return (TrpTextLineType) st;
		}
		else if (RegionTypeUtil.isWord(st)) {
			return ((TrpWordType) st).getLine();
		}
		
		return null;
	}
	
	public static ITrpShapeType getParentShape(ITrpShapeType st) {
		if (st != null && st.getParent() instanceof ITrpShapeType) {
			return (ITrpShapeType) st.getParent();
		} else {
			return null;
		}
	}

	public static TrpBaselineType getBaseline(ITrpShapeType st) {
		if (RegionTypeUtil.isBaseline(st))
			return (TrpBaselineType) st;
		else if (RegionTypeUtil.isLine(st)) {
			return (TrpBaselineType) ((TrpTextLineType) st).getBaseline();
		}
		else if (RegionTypeUtil.isWord(st)) {
			return (TrpBaselineType) ((TrpWordType) st).getLine().getBaseline();
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
	public static void applyReadingOrderFromCoordinates(List<? extends ITrpShapeType> shapes, boolean fireEvents, boolean deleteReadingOrder, boolean recursive) {
		//sort with coordinates
//		Collections.sort(shapes, new TrpElementCoordinatesComparator());
		TrpShapeTypeUtils.sortShapesByCoordinates(shapes, false);
		
		int i=0;
		for (ITrpShapeType st : shapes) {
			
			List<? extends ITrpShapeType> children = st.getChildren(false);
			if (st instanceof TrpTextLineType) {
				TrpTextLineType tl = (TrpTextLineType) st;
				children = tl.getChildrenWithoutBaseline();
			}
			
			if (!CoreUtils.isEmpty(children)) {
				applyReadingOrderFromCoordinates(children, fireEvents, deleteReadingOrder, recursive);
			}
			
			if (!deleteReadingOrder)
				logger.trace("setting reading order "+i+" to: "+st.getId());
			else
				logger.trace("deleting reading order from: "+st.getId());
			
			boolean wasObserverActiveBefore = st.getObservable().isActive();
			if (!fireEvents)
				st.getObservable().setActive(false);
			
			if (deleteReadingOrder)
				st.setReadingOrder(null, st);
			else
				st.setReadingOrder(i++, st);
			
			if (!fireEvents) 
				st.getObservable().setActive(wasObserverActiveBefore);
		}
	}
	
	public static List<? extends ITrpShapeType> filterShapesByAreaThreshold(List<? extends ITrpShapeType> shapes, double thresholdArea) {
		return shapes.stream().filter(s -> {
			try {
				double area = PointStrUtils.getArea(s.getCoordinates());
				logger.trace("id = "+s.getId()+", polygon area = "+area+", thresholdArea = "+thresholdArea);
				return area>=thresholdArea;
			}
			catch (Exception e) {
				logger.error("Error calculating area of shape: "+e.getMessage(), e);
				return false;
			}
		}).collect(Collectors.toList());
	}
	
	public static List<? extends ITrpShapeType> filterShapesByRegionThreshold(List<? extends ITrpShapeType> shapes, double thresholdRegion, ITrpShapeType region) {
		return shapes.stream().filter(s -> {
			try {
				double lineWidth = PointStrUtils.getBoundingBox(s.getCoordinates()).getWidth();
				double regionWidth = PointStrUtils.getBoundingBox(region.getCoordinates()).getWidth();
				double treshhold = regionWidth*thresholdRegion;
				logger.trace("id = "+s.getId()+", shape width = "+lineWidth+", threshold = "+treshhold);
				return lineWidth>=treshhold;
			}
			catch (Exception e) {
				logger.error("Error calculating  width of shape: "+e.getMessage(), e);
				return false;
			}
		}).collect(Collectors.toList());
	}
	
	public static List<List<ITrpShapeType>> mergeShapesInRow(List<? extends ITrpShapeType> shapes, double thresholdRow, ITrpShapeType region) {
		
		List<List<ITrpShapeType>> result = new ArrayList();
		
		boolean alreadyProcessed = false;
		
		for (ITrpShapeType shape : shapes) {
			alreadyProcessed = false;
			
			//check if the next shape was already added to another list
			for (List<?> formerList : result) {
				//logger.debug("former list found: size is: " + formerList.size());
				if (formerList.contains(shape)) {
					alreadyProcessed = true;
					break;
				}
			}
			
			if (!alreadyProcessed) {
				logger.debug("this shape is not already processed: " + shape.getId() );
				double firstY = PointStrUtils.getBoundingBox(shape.getCoordinates()).getY();
				List<ITrpShapeType> currList = new ArrayList<ITrpShapeType>();
				currList = addShapesOfSameHeigth(shapes, firstY, thresholdRow);
				result.add(currList);
			}
//			else {
//				logger.debug("already processed: " + shape.getId() );
//			}

		}

		return result;
	
	}

	private static List<ITrpShapeType> addShapesOfSameHeigth(List<? extends ITrpShapeType> shapes, double firstY, double thresholdRow) {
		
		List<ITrpShapeType> newList = shapes.stream()
		        .filter(x -> {
		        	return Math.abs(PointStrUtils.getBoundingBox(x.getCoordinates()).getY()-firstY)<thresholdRow;
		        }).collect(Collectors.toList());
		                        
		        
		return newList;

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
