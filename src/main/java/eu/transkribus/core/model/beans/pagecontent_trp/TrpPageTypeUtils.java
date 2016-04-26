package eu.transkribus.core.model.beans.pagecontent_trp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.awt.geom.AffineTransform;

import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;

public class TrpPageTypeUtils {
	private final static Logger logger = LoggerFactory.getLogger(TrpPageTypeUtils.class);
	
	public static void applyAffineTransformation(ITrpShapeType shape, double tx, double ty, double sx, double sy, double rot) throws Exception {
		String coords = shape.getCoordinates();
		logger.debug("old coords = "+coords);
		String newCoords = PointStrUtils.affineTransPoints(coords, tx, ty, sx, sy, rot);
		logger.debug("new coords = "+newCoords);
		
		shape.setCoordinates(newCoords, null);
	}
	
	/**
	 * Applies an affine transformation, i.e. a translation, scaling and rotation (in radiants!) to all the coordinates of the page
	 */
	public static void applyAffineTransformation(PageType page, double tx, double ty, double sx, double sy, double rot) {
		AffineTransform at = new AffineTransform();
		at.setToScale(sx, sy);
		at.setToTranslation(tx, ty);
		at.setToRotation(rot);
		
		page.setImageWidth((int) (page.getImageWidth()*sx));
		page.setImageHeight((int) (page.getImageHeight()*sy));
		
		for (ITrpShapeType shape : ((TrpPageType) page).getAllShapes(true)) {
			try {
				applyAffineTransformation(shape, tx, ty, sx, sy, rot);
			} catch (Exception e) {
				logger.error("Error transforming coordinates of shape "+shape.getId()+": "+e.getMessage(), e);
			}
		}
	}
	
	/** Assigns unique IDs to the elements in the page using the current order of the elements. */
	public static void assignUniqueIDs(PageType page) {
		int i = 1;
		for (RegionType r : page.getTextRegionOrImageRegionOrLineDrawingRegion()) {
			if (r instanceof TextRegionType) {
				TextRegionType region = (TextRegionType) r;
				String rid = "r" + i;

				region.setId(rid);
				int j = 1;
				for (TextLineType l : region.getTextLine()) {
					String lid = rid + "l" + j;
					l.setId(lid);

					int k = 1;
					for (WordType word : l.getWord()) {
						String wid = lid + "w" + k;
						word.setId(wid);

						k++;
					}
					++j;
				}
				++i;
			}
		}
	}

}
