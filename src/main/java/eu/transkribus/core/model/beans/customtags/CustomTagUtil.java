package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.OrderedGroupIndexedType;
import eu.transkribus.core.model.beans.pagecontent.OrderedGroupType;
import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.ReadingOrderType;
import eu.transkribus.core.model.beans.pagecontent.RegionRefIndexedType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReadingOrderChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpStructureChangedEvent;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PAGETypeFactory;

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
	
	public static CustomTag parseSingleCustomTag(String tag) throws ParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IOException {
		CssSyntaxTag cssTag = CssSyntaxTag.parseSingleCssTag(tag);
		CustomTag ct = CustomTagFactory.create(cssTag.getTagName(), cssTag.getAttributes());
		return ct;
	}
	
	public static CustomTag parseSingleCustomTag2(String tag) {
		try {
			return parseSingleCustomTag(tag);
		} catch (Exception e) {
			return null;
		}
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
			logger.trace("ref: "+o);
			if (o instanceof RegionRefIndexedType) {
				RegionRefIndexedType rr = (RegionRefIndexedType) o;
				logger.trace("region ref: "+rr+" ref = "+rr.getRegionRef());
				if (rr.getRegionRef() instanceof TrpTextRegionType) {
					TrpTextRegionType region = (TrpTextRegionType) rr.getRegionRef();
					logger.trace("region: "+region.getId()+" index: "+rr.getIndex());
					region.setReadingOrder(rr.getIndex(), region);
				}
			}
		}
	}
	
	public static OrderedGroupType createReadingOrderOrderedGroup(List<? extends ITrpShapeType> shapes, String caption) {
		OrderedGroupType group = new OrderedGroupType();
		if (caption != null)
			group.setCaption(caption);
		
		group.setId("ro_"+CoreUtils.uniqueCurrentTimeMS());
		
		for (ITrpShapeType s : shapes) {
			if (s.getReadingOrder() != null) {
				RegionRefIndexedType rr = new RegionRefIndexedType();
				rr.setRegionRef(s);
				rr.setIndex(s.getReadingOrder());
				group.getRegionRefIndexedOrOrderedGroupIndexedOrUnorderedGroupIndexed().add(rr);
			}
		}
		
		return group;
	}
	
//	public static void createReadingOrderOrderedGroupIndexed(TrpRegionType r, OrderedGroupIndexedType g) {
//		
//		
//		
//		
//		if (s instanceof TrpRegionType) {
//			s.getChildren(recursive)
//			
//			
//			TrpRegionType r = (TrpRegionType) s;
//			for (int i=0; i<r.getTextRegionOrImageRegionOrLineDrawingRegion().size(); ++i) {
//				TrpRegionType cr = r.getTextRegionOrImageRegionOrLineDrawingRegion().get(i);
//				
//				if (cr.hasChildren()) {
//					OrderedGroupIndexedType cg = PAGETypeFactory.createOrderedGroupIndexed(i, "r_"+CoreUtils.uniqueCurrentTimeMS(), null);
//					
//					
//					
//					RegionRefIndexedType rr = PAGETypeFactory.createRegionRefIndexed(index, refObject)
//					
//				}
//				
//				
//			}
//			
//		}
//		
//		
//		
//	}
//	
//	public static void writeReadingOrderCustomTagsToPageFormat(TrpPageType page) {
//		logger.trace("converting reading order from custom tags to page format... NEW");
//		
//		ReadingOrderType ro = new ReadingOrderType();
//		
//		// 1st: create parent group for all reading order elements
//		OrderedGroupType group = PAGETypeFactory.createOrderedGroup("ro_"+CoreUtils.uniqueCurrentTimeMS(), "Reading order");
//		
//		// 2nd: create either a region ref
//		for (TrpRegionType r : page.getTextRegionOrImageRegionOrLineDrawingRegion()) {
//			xxx
//			
//			
//			
//			
//		}
//		
//		OrderedGroupType group = createReadingOrderOrderedGroup(page.getTextRegionOrImageRegionOrLineDrawingRegion(), "Regions reading order");
//		
//		
//		
//		
//		
//		
//		OrderedGroupType group = new OrderedGroupType();
//		group.setCaption("Regions reading order");
//		group.setId("ro_"+CoreUtils.uniqueCurrentTimeMS());		
//		ro.setOrderedGroup(group);
//		boolean readingOrderSet=false;
//		
//		for (TrpTextRegionType r : page.getTextRegions(false)) {
//			if (r.getReadingOrder() != null) {
//				readingOrderSet=true;
//				RegionRefIndexedType rr = new RegionRefIndexedType();
//				rr.setRegionRef(r);
//				rr.setIndex(r.getReadingOrder());	
//				group.getRegionRefIndexedOrOrderedGroupIndexedOrUnorderedGroupIndexed().add(rr);
//				readingOrderSet = true;
//			}
//		}
//		
//		if (readingOrderSet)
//			page.setReadingOrder(ro);
//	}
	
	public static void writeReadingOrderCustomTagsToPageFormat(TrpPageType page) {
		logger.trace("converting reading order from custom tags to page format...");
		
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
		if (shape == null)
			return;
		
		logger.trace("setting structure: "+structureType+" id: "+shape.getId()+" type: "+shape.getClass().getSimpleName()+" recursive: "+recursive);
		
		if (!isTextregionOrLineOrWord(shape))
			return;
		
		if (shape instanceof TrpTextRegionType) { // if this is a text region, also set PAGE structure field if possible
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
	
	public static boolean hasParagraphStructure(ITrpShapeType shape) {
		return getStructure(shape).equalsIgnoreCase(TextTypeSimpleType.PARAGRAPH.value());
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
	
//	public static List<CustomTag> extractCustomTags(PageType p) {
//		if (p instanceof TrpPageType)
//			return extractCustomTagsTrp((TrpPageType) p);
//		
//		List<CustomTag> tags = new ArrayList<>();
//				
//		for (RegionType r : p.getTextRegionOrImageRegionOrLineDrawingRegion()) {
//			tags.addAll(CustomTagUtil.getCustomTags(r.getCustom()));
//			if (r instanceof TextRegionType) {
//				for (TextLineType l : ((TextRegionType) r).getTextLine()) {
//					tags.addAll(CustomTagUtil.getCustomTags(l.getCustom()));
//					for (WordType w : l.getWord()) {
//						tags.addAll(CustomTagUtil.getCustomTags(w.getCustom()));
//					}	
//				}
//			}			
//		}
//		
//		return tags;
//	}
	
	public static List<CustomTag> extractCustomTags(TrpPageType p) {
		List<CustomTag> tags = new ArrayList<>();
		
		for (ITrpShapeType s : p.getAllShapes(true)) {
			CustomTagList cl = s.getCustomTagList();
			if (cl==null)
				continue;
			
			tags.addAll(cl.getTags());
		}
			
		return tags;
	}
	
	public static void main(String[] args) {
	}
	
}
