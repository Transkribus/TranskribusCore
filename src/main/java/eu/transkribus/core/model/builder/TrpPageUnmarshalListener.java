package eu.transkribus.core.model.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagFactory;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpBaselineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.SebisStopWatch;
import eu.transkribus.core.util.TextStyleTypeUtils;

final public class TrpPageUnmarshalListener extends Unmarshaller.Listener {
	TrpPageType page=null;
	
	Logger logger = LoggerFactory.getLogger(TrpPageUnmarshalListener.class);
//	Set<String> allTagNames = new HashSet<String>(); // all tag-names are gathered here
//	Map<String, CustomTag> allTagNamesWithSample = new HashMap<>(); // all tag-names are gathered here
//	List<CustomTag> allTags = new ArrayList<>();
	
	public void beforeUnmarshal(Object target, Object parent) {
		logger.trace("before unmarshalling "+target.getClass().getSimpleName()+" parent: "+parent);
		
		if (target instanceof TrpPageType) {
			page = (TrpPageType) target;
		}
		
		if (target instanceof ITrpShapeType) {
			((ITrpShapeType) target).getObservable().setActive(false);
		}		
	}
	
	public void afterUnmarshal(Object target, Object parent) {
		logger.trace("unmarshalling "+target.getClass().getSimpleName()+" parent: "+parent);
				
		setParent(target, parent);
		syncTags(target);
		
		if (target instanceof ITrpShapeType) {
			((ITrpShapeType) target).getObservable().setActive(true);
		}
		
//		if (page != null) {
//			page.registerObjectId(target);
//		}
		
//		if (target instanceof PcGtsType) {
//			page.printIdRegistry();
//		}
		
	}
	
	/** 
	 * sync tags with registry for each and every shape
	 *  */
	private void syncTags(Object target) {
		if (!(target instanceof ITrpShapeType)) {
			return;
		}
		
		ITrpShapeType st = (ITrpShapeType) target;
		if (st.getCustomTagList() != null) {
			// FIXME: empty tags do not get built (if they are not already defined)
			st.getCustomTagList().initFromCustomTagString(st.getCustom(), true);
		}
//		st.setCustom(st.getCustom()); // manually call setter method for custom tag as JAXB does not call setters!
		
//			TextStyleTypeUtils.applyTextStyleToCustomTag(st);
		
		// try registering (possibly new) tags:
		if (false) // now done in CustomTagList::initFromCustomTagString which is already called above
		if (st.getCustomTagList()!=null) {
			for (CustomTag t : st.getCustomTagList().getTags()) {
				try {
					boolean mergeAttributes = false;
					boolean canBeEmpty = t.isEmpty();
					logger.debug("t = "+t+" canBeEmpty = "+canBeEmpty);
					CustomTagFactory.addToRegistry(t, null, canBeEmpty, mergeAttributes);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
					logger.error("Could not register the tag: "+t.getCssStr()+", reason: "+e.getMessage(), e);
				}
			}
		}
	}
	
	/** Sets the parent element of each object */
	private void setParent(Object target, Object parent) {
		if (target instanceof TrpPageType) {
			((TrpPageType) target).sortContent();
			((TrpPageType) target).setPcGtsType((PcGtsType) parent);
//			((TrpPageType) target).setTagNames(allTagNames);
		}
		else if (target instanceof ITrpShapeType) {
			ITrpShapeType st = (ITrpShapeType) target;
			st.setParent(parent);
			
			if (target instanceof TrpPrintSpaceType) {
//				((TrpPrintSpaceType)target).setPage((TrpPageType)parent);
			}
			else if (target instanceof TrpTextRegionType) {
//				((TrpTextRegionType)target).setPage((TrpPageType)parent);
				((TrpTextRegionType)target).sortLines();
			}
			else if (target instanceof TrpTextLineType) {
//				((TrpTextLineType)target).setRegion((TrpTextRegionType)parent);
//				((TrpTextLineType)target).updateTaggedWords();
				((TrpTextLineType)target).sortWords();
			}
			else if (target instanceof TrpBaselineType) {
//				((TrpBaselineType)target).setLine((TrpTextLineType)parent);
			}	  
			else if (target instanceof TrpWordType) {
//				((TrpWordType)target).setLine((TrpTextLineType)parent);
			}			
		}
		
//		else if (target instanceof TrpTextStyleType) {
//			((TrpTextStyleType)target).setParent((ITrpShapeType)parent);
////			((TrpTextStyleType)target).initFromCustom();
//		}
	}
}
