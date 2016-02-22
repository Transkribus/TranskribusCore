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
import eu.transkribus.core.model.beans.pagecontent_extension.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpBaselineType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpPrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpWordType;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.TextStyleTypeUtils;

final public class TrpPageUnmarshalListener extends Unmarshaller.Listener {
	TrpPageType page;
	
	Logger logger = LoggerFactory.getLogger(TrpPageUnmarshalListener.class);
	Set<String> allTagNames = new HashSet<String>(); // all tag-names are gathered here
//	Map<String, CustomTag> allTagNamesWithSample = new HashMap<>(); // all tag-names are gathered here
//	List<CustomTag> allTags = new ArrayList<>();
	
	public void beforeUnmarshal(Object target, Object parent) {
		if (target instanceof ITrpShapeType) {
			((ITrpShapeType) target).getObservable().setActive(false);
		}		
	}
	
	public void afterUnmarshal(Object target, Object parent) {
		logger.trace("unmarshalling "+target.getClass().getSimpleName()+" parent: "+parent);
				
		setParent(target, parent);
		syncTextStyleAndTags(target);
		
		if (target instanceof ITrpShapeType) {
			((ITrpShapeType) target).getObservable().setActive(true);
		}		
	}
	
	/** sync text styles for each and every shape */
	private void syncTextStyleAndTags(Object target) {
		if (PageXmlUtils.USE_TEXT_STYLE_CUSTOM_TAGS && target instanceof ITrpShapeType) {
			ITrpShapeType st = (ITrpShapeType) target;
			st.setCustom(st.getCustom()); // manually call setter method for custom tag as JAXB does not call setters!
			TextStyleTypeUtils.applyTextStyleToCustomTag(st);
			
			if (st.getCustomTagList()!=null) {
				// try registering (possibly new) tags:
				for (CustomTag t : st.getCustomTagList().getTags()) {
					try {
						CustomTagFactory.addToRegistry(t);
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
						logger.error("Could not register the tag: "+t.getCssStr()+", reason: "+e.getMessage(), e);
					}
				}

				allTagNames.addAll(st.getCustomTagList().getIndexedTagNames());
				
				
//				List<CustomTag> tags = st.getCustomTagList().getIndexedTagsAtRange(0, st.getUnicodeText().length());
//				for (CustomTag t : tags)
//					logger.debug(st.getId()+" custom tag: "+t);	
//				for (CustomTag t : st.getCustomTagList().getTags()) {
//					allTagNamesWithSample.put(t.getTagName(), t);
//				}
			}

		}
	}
	
	/** Sets the parent element of each object */
	private void setParent(Object target, Object parent) {
		if (target instanceof TrpPageType) {
			((TrpPageType) target).sortContent();
			((TrpPageType) target).setPcGtsType((PcGtsType) parent);
			((TrpPageType) target).setTagNames(allTagNames);
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
				((TrpTextLineType)target).updateTaggedWords();
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
