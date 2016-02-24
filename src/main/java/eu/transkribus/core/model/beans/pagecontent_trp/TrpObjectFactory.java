package eu.transkribus.core.model.beans.pagecontent_trp;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.BaselineType;
import eu.transkribus.core.model.beans.pagecontent.ImageRegionType;
import eu.transkribus.core.model.beans.pagecontent.ObjectFactory;
import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;


/**
 * Custom ObjectFactory that creates the customized JAXB objects also contained in this package.
 * Can be used for marshalling and unmarshalling. <br> 
 * Example:
 * <code>
 * 	Unmarshaller u = context.createUnmarshaller();
 * 	u.setProperty("com.sun.xml.bind.ObjectFactory",new ObjectFactoryEx());
 * </code>
 */
@XmlRegistry
public class TrpObjectFactory extends ObjectFactory {
	Logger logger = LoggerFactory.getLogger(TrpObjectFactory.class);
	
	public TrpObjectFactory() {
		super();
	}
	
	@Override
	public PageType createPageType() {
		logger.trace("creating page");
		return new TrpPageType();
	}
	
	@Override
    public PrintSpaceType createPrintSpaceType() {
		logger.trace("creating printspace...");
        return new TrpPrintSpaceType();
    }	
	
	@Override
    public TextRegionType createTextRegionType() {
		logger.trace("creating textregion...");
        return new TrpTextRegionType();
    }
	
	@Override
    public TextLineType createTextLineType() {
		logger.trace("creating textline...");
        return new TrpTextLineType();
    }
	
	@Override
    public WordType createWordType() {
		logger.trace("creating word...");
        return new TrpWordType();
    }
	
	@Override
    public BaselineType createBaselineType() {
		logger.trace("creating baseline...");
        return new TrpBaselineType();
    }	
		
//	@Override
//	public TextStyleType createTextStyleType() {
//		logger.trace("creating textstyle...");
//		return new TrpTextStyleType();
//	}

}
