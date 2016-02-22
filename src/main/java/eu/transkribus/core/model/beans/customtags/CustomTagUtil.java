package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.OrderedGroupType;
import eu.transkribus.core.model.beans.pagecontent.ReadingOrderType;
import eu.transkribus.core.model.beans.pagecontent.RegionRefIndexedType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_extension.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpWordType;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpReadingOrderChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpStructureChangedEvent;
import eu.transkribus.core.util.CoreUtils;

public class CustomTagUtil {
	private final static Logger logger = LoggerFactory.getLogger(CustomTagUtil.class);
	
	public static <T extends CustomTag> List<T> getIndexedCustomTagsForLines(TrpPageType page, String tagName) {
		List<T> tags = new ArrayList<>();
		for (TrpTextRegionType r : page.getTextRegions(true)) {
			for (CustomTag t : getIndexedCustomTagsForLines(r, tagName)) {
				tags.add((T) t);
			}
		}
		return tags;
	}
	
	public static <T extends CustomTag> List<T> getIndexedCustomTagsForLines(TrpTextRegionType region, String tagName) {
		List<T> tags = new ArrayList<>();
		CustomTag ct = null;
		
		for (TextLineType l : region.getTextLine()) {
			TrpTextLineType tl = (TrpTextLineType) l;
			
			List<T> lineTags = tl.getCustomTagList().getIndexedTags(tagName);
			
			for (T t : lineTags) {
				if (ct != null && isContinuation(tagName, t)) {
					ct.continuations.add(t);
				} else {
					tags.add(t);
					ct = t.isContinued() ? t : null;
				}
			}
		}
		
		return tags;
	}
	
//	public static <T extends CustomTag> List<T> getIndexedCustomTagsForWords(TrpTextRegionType region, String tagName) {
//		List<T> tags = new ArrayList<>();
//		CustomTag ct = null;
//		
//		for (TextLineType l : region.getTextLine()) {
//			TrpTextLineType tl = (TrpTextLineType) l;
//			
//			for (WordType w : tl.getWord()) {
//				TrpWordType tw = (TrpWordType) w;
//				
//				List<T> wordTags = tw.getCustomTagList().getIndexedTags(tagName);
//				for (T t : wordTags) {
//					if (ct != null && isContinuation(tagName, t)) {
//						ct.continuations.add(t);
//					} else {
//						tags.add(t);
//						ct = t.isContinued() ? t : null;
//					}
//				}
//				
//			}
//		}
//		
//		return tags;
//	}
	
	public static boolean isContinuation(String tagName, CustomTag t) {
		return t.getOffset()==0 && t.getTagName().equals(tagName) && t.isContinued();
	}
				
	public static CustomTag mergeEqualAttributes(CustomTag s1, CustomTag s2, boolean withIndices) {
		if (s1==null || s2==null)
			return null;
		
		if (!s1.equalsTagName(s2))
			return null;
		
		CustomTag r;
		try {
			r = CustomTagFactory.create(s1.getTagName());
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
//		CustomTag r = new CustomTag(s1.getTagName());
		
		for (CustomTagAttribute a : s1.getAttributes()) {
			String an = a.getName();
			
			if (!withIndices && CustomTag.isOffsetOrLengthProperty(an))
				continue;
			
			if (s1.hasAttribute(an) && s2.hasAttribute(an)) {
				Object v1 = s1.getAttributeValue(an);
				Object v2 = s2.getAttributeValue(an);
				if (CoreUtils.equalsObjects(v1, v2)) {
					try {
						r.setAttribute(a.getName(), v1, true);
					} catch (IOException e) {
						logger.error("Error setting attribute while merging: "+a.getName()+", message: "+e.getMessage(), e);
					}
				}
			}
		}
		
		return r;
	}
					
	public static List<CustomTag> getCustomTags(String customTag) {
		List<CssSyntaxTag> cssStyleTags = CssSyntaxTag.parseTags(customTag);
		List<CustomTag> cts = new ArrayList<>();
		
		for (CssSyntaxTag css : cssStyleTags) {
			try {
				CustomTag ct = CustomTagFactory.create(css.getTagName(), css.getAttributes());
				cts.add(ct);
			} catch (Exception e) {
				logger.warn("Could not create tag with name: "+css.getTagName()+", message: "+e.getMessage());
			}
		}
		
		return cts;
	}
	
	public static void writeReadingOrderFromPageFormatToCustomTags(TrpPageType page)  {
		logger.trace("converting reading order from page format to custom tags...");
		
		ReadingOrderType ro = page.getReadingOrder();
		if (ro == null)
			return;
		
		for (Object o : ro.getOrderedGroup().getRegionRefIndexedOrOrderedGroupIndexedOrUnorderedGroupIndexed()) {
			logger.debug("ref: "+o);
			if (o instanceof RegionRefIndexedType) {
				RegionRefIndexedType rr = (RegionRefIndexedType) o;
				logger.debug("region ref: "+rr+" ref = "+rr.getRegionRef());
				if (rr.getRegionRef() instanceof TrpTextRegionType) {
					TrpTextRegionType region = (TrpTextRegionType) rr.getRegionRef();
					logger.debug("region: "+region.getId()+" index: "+rr.getIndex());
					region.setReadingOrder(rr.getIndex(), region);
				}
			}
		}
	}
	
	public static void writeReadingOrderCustomTagsToPageFormat(TrpPageType page) {
		logger.debug("converting reading order from custom tags to page format...");
		
		ReadingOrderType ro = new ReadingOrderType();
		OrderedGroupType group = new OrderedGroupType();
		group.setCaption("Regions reading order");
		group.setId("ro_"+CoreUtils.uniqueCurrentTimeMS());		
		ro.setOrderedGroup(group);
		boolean readingOrderSet=false;
		
		for (TrpTextRegionType r : page.getTextRegions(false)) {
			if (r.getReadingOrder() != null) {
				readingOrderSet=true;
				RegionRefIndexedType rr = new RegionRefIndexedType();
				rr.setRegionRef(r);
				rr.setIndex(r.getReadingOrder());	
				group.getRegionRefIndexedOrOrderedGroupIndexedOrUnorderedGroupIndexed().add(rr);
				readingOrderSet = true;
			}
		}
		
		if (readingOrderSet)
			page.setReadingOrder(ro);
	}
	
	public static Integer getReadingOrder(ITrpShapeType shape) {
		ReadingOrderTag t = shape.getCustomTagList().getNonIndexedTag(ReadingOrderTag.TAG_NAME);
		return t == null ? null : t.getIndex();
	}
	
	public static boolean isTextregionOrLineOrWord(ITrpShapeType shape) {
		return shape!=null && (shape instanceof TrpTextRegionType || shape instanceof TrpTextLineType || shape instanceof TrpWordType);
	}
	
	public static void setStructure(ITrpShapeType shape, String structureType, boolean recursive, Object who) {
		logger.debug("setting structure: "+structureType+" id: "+shape.getId()+" type: "+shape.getClass().getSimpleName()+" recursive: "+recursive);
		
		if (!isTextregionOrLineOrWord(shape))
			return;
		
		if (shape instanceof TrpTextRegionType) { // if this is a region, also set PAGE structure field if possible
			TextTypeSimpleType s = StructureTag.parseTextType(structureType);
			((TrpTextRegionType) shape).setType(s);				
		}
		
		// set custom tag:
		if (structureType == null || structureType.equals(""))
			shape.getCustomTagList().removeTags(StructureTag.TAG_NAME);
		else {
			shape.getCustomTagList().addOrMergeTag(new StructureTag(structureType), null);
		}
		
		if (recursive) {
			for (ITrpShapeType c : shape.getChildren(recursive)) {
				c.setStructure(structureType, recursive, who);
			}
		}
		
		shape.getObservable().setChangedAndNotifyObservers(new TrpStructureChangedEvent(who));
	}
		
	public static String getStructure(ITrpShapeType shape) {
		if (!isTextregionOrLineOrWord(shape))
			return "";
		
		if (shape instanceof TrpTextRegionType) { // if this is a region, try to parse the PAGE struct element
			TextTypeSimpleType tt = ((TrpTextRegionType) shape).getType();
			if (tt != null)
				return tt.value();
		}
		
		StructureTag t = shape.getCustomTagList().getNonIndexedTag(StructureTag.TAG_NAME);
		return t==null ? "" : t.getType();
	}

	public static void setReadingOrder(ITrpShapeType shape, Integer readingOrder, Object who) {
		if (readingOrder != null) {
			shape.getCustomTagList().addOrMergeTag(new ReadingOrderTag(readingOrder), null);
		} else
			shape.getCustomTagList().removeTags(ReadingOrderTag.TAG_NAME);
		
		shape.getObservable().setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(who));
		return;
	}
	
	public static void writeCustomTagListToCustomTag(ITrpShapeType st) {
		st.getObservable().setActive(false);
				
		// write CustomTagList tags to the custom tag:
//		if (st instanceof TrpTextRegionType || st instanceof TrpTextLineType || st instanceof TrpWordType) {
		if (st instanceof RegionType || st instanceof TrpTextLineType || st instanceof TrpWordType) {
    		if (st.getCustomTagList()!=null)
    			st.getCustomTagList().writeToCustomTag();
    		else
    			logger.error("Warning: CustomTagList of "+st.getId()+" was null while marshalling file!");
		}
		
		st.getObservable().setActive(true);
	}
	
	public static void writeCustomTagsToPage(TrpPageType page) {
		CustomTagUtil.writeReadingOrderCustomTagsToPageFormat(page);
		
		for (ITrpShapeType st : page.getAllShapes(true)) {
			writeCustomTagListToCustomTag(st);
		}
	}
	
	public static void main(String[] args) {
	}
	
}
