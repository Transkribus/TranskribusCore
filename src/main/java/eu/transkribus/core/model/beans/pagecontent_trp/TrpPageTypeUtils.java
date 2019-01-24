package eu.transkribus.core.model.beans.pagecontent_trp;

import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.util.PageXmlUtils;

/**
 * Methods were moved to PageXmlUtils
 *
 */
@Deprecated
public class TrpPageTypeUtils {
	
	/**
	 * Moved to {@link PageXmlUtils#applyAffineTransformation(ITrpShapeType, double, double, double, double, double)}
	 */
	@Deprecated
	public static void applyAffineTransformation(ITrpShapeType shape, double tx, double ty, double sx, double sy, double rot) throws Exception {
		PageXmlUtils.applyAffineTransformation(shape, tx, ty, sx, sy, rot);
	}
	
	
	/**
	 * Moved to {@link PageXmlUtils#applyAffineTransformation(PageType, double, double, double, double, double)}
	 */
	@Deprecated
	public static void applyAffineTransformation(PageType page, double tx, double ty, double sx, double sy, double rot) {
		PageXmlUtils.applyAffineTransformation(page, tx, ty, sx, sy, rot);
	}
	
	/**
	 * Moved to {@link PageXmlUtils#assignUniqueIDs(PageType)}
	 */
	@Deprecated
	public static void assignUniqueIDs(PageType page) {
		PageXmlUtils.assignUniqueIDs(page);
	}

}
