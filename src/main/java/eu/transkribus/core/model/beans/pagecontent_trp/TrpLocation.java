package eu.transkribus.core.model.beans.pagecontent_trp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;

public class TrpLocation {
	private final static Logger logger = LoggerFactory.getLogger(TrpLocation.class);
	
	public int docId = -1;
	public int collectionId = -1;
	public File localFolder = null;
	public int pageNr = 0;
	public String shapeId = null;
	
	public TrpTextRegionType r = null;
	public TrpTextLineType l = null;
	public TrpWordType w = null;
	public TrpPageType p = null;
	public TrpTranscriptMetadata md = null;
	public CustomTag t;
	
	public TrpLocation() {
	}
	
	public TrpLocation(CustomTag t) {
		this.t = t;
		initFromCustomTag();
	}
	
	public boolean hasDoc() { return docId != -1 || localFolder != null; }
	public boolean hasPage() { return pageNr > 0; }
	public boolean hasTranscriptMetadata() { return md != null; }
	
	public boolean hasShapeType() { return r != null || l != null || w != null; }
	public ITrpShapeType getLowestShapeType() {
		if (w == null && l == null)
			return r;
		else if (w == null)
			return l;
		else
			return w;
	}
	
	private void initFromCustomTag() {
		if (t==null)
			return;
		
		CustomTagList ctl = t.getCustomTagList();
		if (ctl != null) {
			ITrpShapeType s = ctl.getShape();
			p = s.getPage();
			md = p.getMd();
//			logger.debug("md = "+md);
			if (md != null) {
				docId = md.getDocId();
				
				localFolder = md.getLocalFolder();
				pageNr = md.getPageNr();
			}
			
			if (s instanceof TrpTextRegionType) {
				r = (TrpTextRegionType) s;
			}
			else if (s instanceof TrpTextLineType) {
				r = (TrpTextRegionType) s.getParent();
				l = (TrpTextLineType) s;
			}
			else if (s instanceof TrpWordType) {
				l = (TrpTextLineType) s.getParent();
				r = l.getRegion();
				w = (TrpWordType) s;
			}
			
			shapeId = hasShapeType() ? getLowestShapeType().getId() : null;
		}
	}

	public String toString() {
	    final String TAB = ", ";
	    String retValue = "TrpLocation ( "+super.toString();
		retValue += TAB + "docId = " + this.docId;
		retValue += TAB + "localFolder = " + this.localFolder;
		retValue += TAB + "pageNr = " + this.pageNr;
		retValue += TAB + "r = " + this.r;
		retValue += TAB + "l = " + this.l;
		retValue += TAB + "w = " + this.w;
		retValue += TAB + "p = " + this.p;
		retValue += TAB + "md = " + this.md;
		retValue += TAB + "t = " + this.t;
		retValue += " )";
	    return retValue;
	}
	
	
}
