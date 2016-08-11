package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.RegionTypeTag;
import eu.transkribus.core.model.beans.pagecontent.AdvertRegionType;
import eu.transkribus.core.model.beans.pagecontent.ChartRegionType;
import eu.transkribus.core.model.beans.pagecontent.ChemRegionType;
import eu.transkribus.core.model.beans.pagecontent.GraphicRegionType;
import eu.transkribus.core.model.beans.pagecontent.ImageRegionType;
import eu.transkribus.core.model.beans.pagecontent.LineDrawingRegionType;
import eu.transkribus.core.model.beans.pagecontent.MathsRegionType;
import eu.transkribus.core.model.beans.pagecontent.MusicRegionType;
import eu.transkribus.core.model.beans.pagecontent.NoiseRegionType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.SeparatorRegionType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent.UnknownRegionType;
import eu.transkribus.core.util.PointStrUtils;

public class RegionTypeUtil {

	
	public static final String TEXT_REGION = "TextRegion";
	public static final String LINE = "Line";
	public static final String BASELINE = "Baseline";
	public static final String WORD = "Word";
	public static final String TABLE = "Table";
	public static final String TABLE_CELL = "TableCell";
	public static final String PRINTSPACE = "Printspace";
	
	public static final String ADVERT_REGION = "Advert";
	public static final String CHART_REGION = "Chart";
	public static final String CHEM_REGION = "Chem";
	public static final String GRAPHIC_REGION = "Graphic";
	public static final String IMAGE_REGION = "Image";
	public static final String LINE_DRAWING_REGION = "LineDrawing";
	public static final String MATHS_REGION = "Maths";
	public static final String MUSIC_REGION = "Music";
	public static final String NOISE_REGION = "Noise";
	public static final String SEPARATOR_REGION = "Separator";
	public static final String UNKNOWN_REGION = "UnknownRegion";
	public static final String BLACKENING_REGION = "Blackening";
	
	public final static List<String> SPECIAL_REGIONS = new ArrayList<String>() {{
		add(ADVERT_REGION);
		add(CHART_REGION);
		add(CHEM_REGION);
		add(GRAPHIC_REGION);
		add(IMAGE_REGION);
		add(LINE_DRAWING_REGION);
		add(MATHS_REGION);
		add(MUSIC_REGION);
		add(NOISE_REGION);
		add(SEPARATOR_REGION);
		add(UNKNOWN_REGION);
		add(BLACKENING_REGION);
	}};	
	
	public final static List<String> ALL_REGIONS = new ArrayList<String>() {{
			add(TEXT_REGION);
			add(LINE);
			add(BASELINE);
			add(WORD);
			add(PRINTSPACE);
			add(TABLE);
			add(TABLE_CELL);

			addAll(SPECIAL_REGIONS);
	}};

//	public static String[] ALL_REGIONS = {
//			TEXT_REGION_TYPE ,
//			LINE_TYPE ,
//			WORD_TYPE ,
//			PRINTSPACE_TYPE ,
//			
//			ADVERT_REGION ,
//			CHART_REGION ,
//			CHEM_REGION ,
//			GRAPHIC_REGION ,
//			IMAGE_REGION ,
//			LINE_DRAWING_REGION ,
//			MATHS_REGION ,
//			MUSIC_REGION ,
//			NOISE_REGION ,
//			SEPARATOR_REGION ,
//			TABLE_REGION ,
//			UNKNOWN_REGION ,
//			BLACKENING_REGION ,
//	};
	
	static Map<String, Class<? extends ITrpShapeType>> REGIONS = new HashMap<>();
	static {
		REGIONS.put(TEXT_REGION, TrpTextRegionType.class);
		REGIONS.put(LINE, TrpTextLineType.class);
		REGIONS.put(BASELINE, TrpBaselineType.class);
		REGIONS.put(WORD, TrpWordType.class);
		REGIONS.put(PRINTSPACE, TrpPrintSpaceType.class);
		REGIONS.put(TABLE, TrpTableRegionType.class);
		REGIONS.put(TABLE_CELL, TrpTableCellType.class);
		
		REGIONS.put(ADVERT_REGION, AdvertRegionType.class);
		REGIONS.put(CHART_REGION, ChartRegionType.class);
		REGIONS.put(CHEM_REGION, ChemRegionType.class);
		REGIONS.put(GRAPHIC_REGION, GraphicRegionType.class);
		REGIONS.put(IMAGE_REGION, ImageRegionType.class);
		REGIONS.put(LINE_DRAWING_REGION, LineDrawingRegionType.class);
		REGIONS.put(MATHS_REGION, MathsRegionType.class);
		REGIONS.put(MUSIC_REGION, MusicRegionType.class);
		REGIONS.put(NOISE_REGION, NoiseRegionType.class);
		REGIONS.put(SEPARATOR_REGION, SeparatorRegionType.class);
		
		REGIONS.put(UNKNOWN_REGION, UnknownRegionType.class);
		REGIONS.put(BLACKENING_REGION, UnknownRegionType.class);
	}
	
	public static boolean hasBlackenings(TrpPageType page) {
		return !getBlackeningRegions(page).isEmpty();
	}
	
	public static List<TrpRegionType> getBlackeningRegions(TrpPageType page) {
		List<TrpRegionType> blackenings = new ArrayList<>();
		for (TrpRegionType r : page.getTextRegionOrImageRegionOrLineDrawingRegion()) {
			if (isBlackening(r))
				blackenings.add(r);
		}
		
		return blackenings;
	}
	
	public static List<List<Point>> getBlackeningRegionsPoints(TrpPageType page) {
		List<List<Point>> ptsList = new ArrayList<>();
		for (TrpRegionType b : getBlackeningRegions(page)) {
			List<Point> pts = PointStrUtils.parsePoints2(b.getCoordinates());
			if (!pts.isEmpty())
				ptsList.add(PointStrUtils.parsePoints2(b.getCoordinates()));
		}
		
		return ptsList;
	}
	
	
	
//	public List<List<Point>> getBlackeningPoints
	
	public static boolean isBlackening(ITrpShapeType s) {
		return getRegionType(s).equals(BLACKENING_REGION);
	}
	
	public static Class<? extends ITrpShapeType> getRegionClass(String name) {
		return REGIONS.get(name);
	}
	
	public static boolean isSpecialRegion(String name) {
		return SPECIAL_REGIONS.contains(name);
	}
	
	private static String getName(Class<? extends ITrpShapeType> clazz) {
		for (String name : REGIONS.keySet()) {
			Class c = REGIONS.get(name);
			if (c.equals(clazz))
				return name;
		}
		return "";
	}
	
	public static String getRegionType(ITrpShapeType s) {
		if (s == null)
			return "";
//		if (!(s instanceof RegionType)) {
//			return "";
//		}
		
//		RegionType r = (RegionType) s;
		if (!(s instanceof UnknownRegionType)) {
			return getName(s.getClass());
		}

		RegionTypeTag t = s.getCustomTagList().getNonIndexedTag(RegionTypeTag.TAG_NAME);
		return t==null ? UNKNOWN_REGION : t.getType();
	}
	
	public static void setRegionTypeTag(ITrpShapeType shape, String regionType, Object who) {
		if (!(shape instanceof UnknownRegionType)) // setting region types only possible for "unknown" region types!!
			return;
		
		// set custom tag:
		if (StringUtils.isEmpty(regionType))
			shape.getCustomTagList().removeTags(RegionTypeTag.TAG_NAME);
		else {
			shape.getCustomTagList().addOrMergeTag(new RegionTypeTag(regionType), null);
		}
		
//		shape.getObservable().setChangedAndNotifyObservers(new TrpStructureChangedEvent(who)); // TODO - send event for region type change??
	}	
}
