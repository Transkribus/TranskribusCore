package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.pagecontent.LayerType;
import eu.transkribus.core.model.beans.pagecontent.OrderedGroupIndexedType;
import eu.transkribus.core.model.beans.pagecontent.OrderedGroupType;
import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionRefType;
import eu.transkribus.core.model.beans.pagecontent.RelationType;
import eu.transkribus.core.model.beans.pagecontent.RelationsType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.UnorderedGroupIndexedType;
import eu.transkribus.core.model.beans.pagecontent.UnorderedGroupType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.util.SebisStopWatch;

public class TrpPageType extends PageType {
	private final static Logger logger = LoggerFactory.getLogger(TrpPageType.class);
	
	PcGtsType pcGtsType;
	
	boolean edited=false;
	
	Set<String> tagNames=new HashSet<>();	
	TrpTranscriptMetadata md;
	
	Map<String, Object> idRegistry = new HashMap<>(); // TEST

	public TrpPageType() {
	}
		
	public TrpPageType(TrpPageType src) {
		super();
		
		// new elements
		this.edited = src.edited;
		this.pcGtsType = src.pcGtsType;
	}
	
	public boolean registerObjectId(Object o) {
		if (o instanceof ITrpShapeType) {
			idRegistry.put(((ITrpShapeType)o).getId(), o);
			return true;
		}		
		if (o instanceof PcGtsType) {
			idRegistry.put(((PcGtsType)o).getPcGtsId(), o);
			return true;
		}
		if (o instanceof OrderedGroupIndexedType) {
			idRegistry.put(((OrderedGroupIndexedType)o).getId(), o);
			return true;
		}
		if (o instanceof UnorderedGroupIndexedType) {
			idRegistry.put(((UnorderedGroupIndexedType)o).getId(), o);
			return true;
		}
		if (o instanceof OrderedGroupType) {
			idRegistry.put(((OrderedGroupType)o).getId(), o);
			return true;
		}
		if (o instanceof UnorderedGroupType) {
			idRegistry.put(((UnorderedGroupType)o).getId(), o);
			return true;
		}
		if (o instanceof LayerType) {
			idRegistry.put(((LayerType)o).getId(), o);
			return true;
		}
		
		return false;
	}
	
	public void printIdRegistry() {
		logger.trace("nr of elements with id: "+idRegistry.size());
		for (String id : idRegistry.keySet()) {
			logger.trace("id: "+id+" element: "+idRegistry.get(id));
		}
		
//		SebisStopWatch.SW.start();
		for (int i=0; i<10000; ++i) {
			idRegistry.containsKey("tc_"+i);
		}
//		SebisStopWatch.SW.stop(true);
	}
	
//	public void replaceLinkId(String idOld, String idNew) {
//		List<RelationType> links = getLinks(idOld);
//		
//		for (RelationType r : links) {
//			ITrpShapeType s1 = (ITrpShapeType) r.getRegionRef().get(0).getRegionRef();
//			ITrpShapeType s2 = (ITrpShapeType) r.getRegionRef().get(1).getRegionRef();
//			if (s1 == null || s2 == null)
//				continue;
//			
//			ITrpShapeType s = s1;
//			if (s2.getId().equals(idOld))
//				s = s2;
//			
//			s.setId(id);
//				
//			
//			
//		}
//		
//		
//	}
	
	public TrpTranscriptMetadata getMd() {
		return md;
	}

	public void setMd(TrpTranscriptMetadata md) {
		this.md = md;
	}

	public Set<String> getTagNames() {
		return tagNames;
	}

	public void setTagNames(Set<String> tagNames) {
		this.tagNames = tagNames;
	}
	
	public int removeDeadLinks() {
		if (relations==null) {
			return 0;
		}
		
		int c=0;
		for (RelationType r : new ArrayList<>(relations.getRelation())) {
			ITrpShapeType s1 = (ITrpShapeType) r.getRegionRef().get(0).getRegionRef();
			ITrpShapeType s2 = (ITrpShapeType) r.getRegionRef().get(1).getRegionRef();
			
			if (s1 == null || s2 == null) {
				logger.debug("removing dead link "+r);
				getRelations().getRelation().remove(r);
				++c;
			}
		}
		if (relations.getRelation().isEmpty())
			relations = null;
		
		logger.debug("removed "+c+" dead links!");
		
		return c;
	}
	
	public boolean hasLink(ITrpShapeType s1, ITrpShapeType s2) {
		if (s1 == null || s2 == null) return false;
		
		return hasLink(s1.getId(), s2.getId());
	}
	
	public boolean hasLink(String id1, String id2) {
		if (relations==null) return false;
		
		for (RelationType r : relations.getRelation()) {			
			ITrpShapeType s1 =  (ITrpShapeType) r.getRegionRef().get(0).getRegionRef();
			ITrpShapeType s2 =  (ITrpShapeType) r.getRegionRef().get(1).getRegionRef();
			
//			logger.debug(s1.getId()+" - "+s2.getId());
			
			if (s1.getId().equals(id1) && s2.getId().equals(id2)) {
				return true;
			}
		}
		return false;
	}
	
	public List<RelationType> getLinks(ITrpShapeType s1) {
		List<RelationType> links = new ArrayList<>();
		if (s1 == null) return links;
		
		return getLinks(s1.getId());
	}
	
	public void removeLinks(ITrpShapeType s1) {
		// remove all links related to this shape:
		for (RelationType r : getLinks(s1)) {
			removeLink(r);
		}
	}
	
	public boolean removeLink(RelationType link) {
		if (relations == null) return false;
		
		boolean removed = relations.getRelation().remove(link);
		
		if (relations.getRelation().isEmpty())
			relations = null;
		
		return removed;
	}
	
	/** Returns all links for a given shape. */
	public List<RelationType> getLinks(String id1) {
		List<RelationType> links = new ArrayList<>();
		if (id1 == null) return links;
		
		for (ITrpShapeType s : getAllShapes(true)) {
			if (s==null || s.getId() == null || s.getId().equals(id1))
				continue;
			
			RelationType l = getLink(id1, s.getId());
			if (l != null)
				links.add(l);
		}
		return links;
	}
	
	/** Returns the link between the two shapes or null if none exists. */
	public RelationType getLink(ITrpShapeType s1, ITrpShapeType s2) {
		if (s1 == null || s2 == null) return null;
	
		return getLink(s1.getId(), s2.getId());
	}
	
	public RelationType getLink(String id1, String id2) {
		if (relations==null) return null;
		
		for (RelationType r : relations.getRelation()) {
			ITrpShapeType s1 =  (ITrpShapeType) r.getRegionRef().get(0).getRegionRef();
			ITrpShapeType s2 =  (ITrpShapeType) r.getRegionRef().get(1).getRegionRef();
			
			if (s1.getId().equals(id1) && s2.getId().equals(id2)) {
				return r;
			}		
		}
		return null;
	}
	
	public boolean removeLink(ITrpShapeType s1, ITrpShapeType s2) {
		if (s1 == null || s2 == null) return false;
		
		return removeLink(s1.getId(), s2.getId());
	}
	
	public boolean removeLink(String id1, String id2) {
		if (relations==null) return false;
		
		for (RelationType r : relations.getRelation()) {
			ITrpShapeType s1 =  (ITrpShapeType) r.getRegionRef().get(0).getRegionRef();
			ITrpShapeType s2 =  (ITrpShapeType) r.getRegionRef().get(1).getRegionRef();
			
			if (s1.getId().equals(id1) && s2.getId().equals(id2)) {
				getRelations().getRelation().remove(r);
				if (relations.getRelation().isEmpty())
					relations = null;				
				return true;
			}
		}
		return false;
	}
	
	public boolean addLink(ITrpShapeType s1, ITrpShapeType s2) {
		if (s1 == null || s2 == null || s1==s2) return false;
		if (s1 instanceof TrpPrintSpaceType || s1 instanceof TrpBaselineType) return false;
		if (s2 instanceof TrpPrintSpaceType || s2 instanceof TrpBaselineType) return false;
		
		if (hasLink(s1, s2))
			return false;
		
		RelationType rt = new RelationType();
		RegionRefType regRef1 = new RegionRefType();
		regRef1.setRegionRef(s1);
		RegionRefType regRef2 = new RegionRefType();
		regRef2.setRegionRef(s2);
		rt.setType("link");
		
		rt.getRegionRef().add(regRef1);
		rt.getRegionRef().add(regRef2);
		
		if (relations==null) {
			relations = new RelationsType();
		}
		
		relations.getRelation().add(rt);
		return true;
	}
	
	public int countCharactersInWords() {
		int c=0;
		for (ITrpShapeType s : getAllShapes(true)) {
			if (!(s instanceof TrpWordType))
				continue;
			
			TrpWordType w = (TrpWordType) s;
			c += w.getUnicodeText().length();
		}
		return c;
	}
	
	public int countWords() {
		int c=0;
		for (ITrpShapeType s : getAllShapes(true)) {
			if (!(s instanceof TrpWordType))
				continue;
			
			++c;
		}
		return c;
	}
	
	public int countLines() {
		int c=0;
		for (ITrpShapeType s : getAllShapes(true)) {
			if (!(s instanceof TrpTextLineType))
				continue;
			
			++c;
		}
		return c;
	}
	
	public int countTextRegions() {
		int c=0;
		for (ITrpShapeType s : getAllShapes(true)) {
			if (!(s instanceof TrpTextRegionType))
				continue;
			
			++c;
		}
		return c;
	}
	
	public String getUnicodeText() {
		String text = "";
		for (TrpTextRegionType tr : getTextRegions(true)) {
			text += tr.getUnicodeTextFromLines() + "\n";
		}
		return text;
	}
	
	public void updateIDsAccordingToCurrentSorting() {
//		sortContent();
		TrpPageTypeUtils.assignUniqueIDs(this);
		setEdited(true);
	}
	
    private static int counter = 0;
    private static synchronized java.lang.String getUniqueSuffix() {
        if (counter > 99999) {
            counter = 0;
        }
        ++counter;
        return java.lang.Long.toString(java.lang.System.currentTimeMillis()) + "_" + counter;
    }

	public static String getUniqueId(String suffix) {
//		return suffix+"_"+UUID.randomUUID().toString();
		return suffix+"_"+getUniqueSuffix();
		
//		int i=1;
//		
//		String id;
//		do {
//			id=suffix+"_"+i;	
//		} while (hasId(id));
//		return id;
	}
	
	public boolean hasId(String id) {		
		for (ITrpShapeType st : getAllShapes(true)) {
			if (id.equals(st.getId()))
				return true;
		}
		return false;
	}
	
//	public List<ITrpShapeType> getAllFirstLevelShapes() {
//		final boolean USE_ALL_REGIONS = true;
//		List<ITrpShapeType> shapes = new ArrayList<>();
//		
//		// ps:
//		if (getPrintSpace() != null) {
//			shapes.add((TrpPrintSpaceType) getPrintSpace());
//		}
//		// text regions:
//		if (!USE_ALL_REGIONS)
//			shapes.addAll(getTextRegions());
//		else
//			shapes.addAll(getTextRegionOrImageRegionOrLineDrawingRegion());
//
//		return shapes;		
//	}
	
	public List<ITrpShapeType> getAllShapes(boolean recursive) {
		final boolean USE_ALL_REGIONS = true;
		
		List<ITrpShapeType> shapes = new ArrayList<>();
		
		// ps:
		if (getPrintSpace() != null) {
			shapes.add((TrpPrintSpaceType) getPrintSpace());
		}

		// regions:		
		List<ITrpShapeType> regions = new ArrayList<>();
		
		if (USE_ALL_REGIONS)
			regions.addAll(getTextRegionOrImageRegionOrLineDrawingRegion());
		else
			regions.addAll(getTextRegions(false));
				
//		for (TrpTextRegionType r : getTextRegions()) {
		for (ITrpShapeType r : regions) {
			shapes.add(r);
			if (recursive) {
				// add children of regions:
				shapes.addAll(r.getChildren(true));
			}
		}
		
		return shapes;
	}
	
	public void writeCustomTagsToPage() {
		CustomTagUtil.writeCustomTagsToPage(this);
	}
	
	public List<TrpTextRegionType> getTextRegions(boolean recursive) {
		return TrpRegionType.getTextRegions(textRegionOrImageRegionOrLineDrawingRegion, recursive);
		
//		List<TrpTextRegionType> res = new ArrayList<TrpTextRegionType>();
//				
//		for (RegionType region : getTextRegionOrImageRegionOrLineDrawingRegion()) {
//			if (region instanceof TextRegionType) {
//				res.add((TrpTextRegionType) region);
//				if (recursive) {
//					
//				}
//			}
//		}
//		
//		return res;
	}
	
	public TrpTextRegionType getTextRegion(String id) {
		for (TrpTextRegionType r : getTextRegions(true)) {
			if (id.equals(r.getId())) {
				return r;
			}
		}
		return null;
	}
	
	/** Returns all lines of all regions in the current order. */
	public List<TrpTextLineType> getLines() {
		List<TrpTextLineType> lines = new ArrayList<>();
		for (TrpTextRegionType tr : getTextRegions(true)) {
			for (TextLineType tl : tr.getTextLine()) {
				lines.add((TrpTextLineType) tl);
			}
		}
		
		return lines;
	}
	
	/** Returns all words of all lines in the current order. */
	public List<TrpWordType> getWords() {
		List<TrpWordType> words = new ArrayList<>();
		for (TrpTextLineType l : getLines()) {
			for (WordType w : l.getWord()) {
				words.add((TrpWordType)w);
			}
		}
		return words;
	}
	
	public TrpWordType getWordWithId(String id) {
		for (TrpWordType w : getWords()) {
			if (w.getId().equals(id))
				return w;
		}
		return null;
	}
	
	public void sortContent() {
		sortRegions();
		
		for (TrpTextRegionType r : getTextRegions(true)) {
			r.sortLines();
			for (TextLineType l : getLines()) {
				((TrpTextLineType) l).sortWords();
			}
		}
	}
		
	public void sortRegions() {
		
		boolean doSort = false;
		List<TrpRegionType> regions = getTextRegionOrImageRegionOrLineDrawingRegion();
		
		//sort only if reading order is not set actually
		for (ITrpShapeType o : regions) {
			if (o.getReadingOrder() == null){
				doSort = true;
				break;
			}
		}
		logger.trace("sortRegions, doSort = "+doSort);
//		doSort = false;
		
		if (doSort){
			TrpRegionType.sortRegions(regions);
		}
		
//		int i=0;
//		for (TrpRegionType r : getTextRegionOrImageRegionOrLineDrawingRegion()) {
//			r.setReadingOrder(i++, this);
//		}
		for (int i=0; i<regions.size(); i++) {
			TrpRegionType r = (TrpRegionType) regions.get(i);
			if (r.getReadingOrder() == null || !r.getReadingOrder().equals(i)){
				r.setReadingOrder(i, this);
			}
		}
		
		
//		Collections.sort(this.getTextRegionOrImageRegionOrLineDrawingRegion(), new TrpElementCoordinatesComparator<RegionType>());
//		Collections.sort(this.getTextRegionOrImageRegionOrLineDrawingRegion(), new TrpElementReadingOrderComparator<RegionType>(true));
//		setEdited(true);
	}
	
	public int getTextRegionIndex(TrpTextRegionType region) {
		int i=0;
		for (TrpTextRegionType tr : getTextRegions(true)) {
			if (tr == region)
				return i;
			++i;
		}
		return -1;
	}
	
	public List<TrpRegionType> getRegions() {
		return getTextRegionOrImageRegionOrLineDrawingRegion();
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}
	public boolean isEdited() { return edited; }

	public PcGtsType getPcGtsType() {
		return pcGtsType;
	}

	public void setPcGtsType(PcGtsType pcGtsType) {
		this.pcGtsType = pcGtsType;
	}

//	public List<TrpTextRegionType> getTextRegionsOrTableRegions(boolean b) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public int getIndexAccordingToCoordinates(ITrpShapeType o1) {
		// TODO Auto-generated method stub
		for (int i=0; i<textRegionOrImageRegionOrLineDrawingRegion.size(); i++) {
			ITrpShapeType o2 = (ITrpShapeType) textRegionOrImageRegionOrLineDrawingRegion.get(i);
			
//			logger.debug("o1 coords " + o1.getCoordinates());
//			logger.debug("o2 coords " + o2.getCoordinates());
			
			int ordering = new TrpElementCoordinatesComparator<ITrpShapeType>().compare(o1, o2);
			
			//o1 is smaller the second shape according to their coordinates
			if (ordering < 0){
				//logger.debug(" o1 smaller o2 ");
				//i is than the index in the parent shape to insert the child shape
				return i;
			}
						
		}
		//logger.debug(" no smaller shape found: return last index " + (textLine.size()-1));
		//int idx = (textLine.size()>0? (textLine.size()):0);

		return textRegionOrImageRegionOrLineDrawingRegion.size();
	}
	
	
	
	
	
	

}
