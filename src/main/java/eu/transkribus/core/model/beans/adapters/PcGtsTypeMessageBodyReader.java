package eu.transkribus.core.model.beans.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

@Provider
public class PcGtsTypeMessageBodyReader implements MessageBodyReader<PcGtsType> {
	private final static Logger logger = LoggerFactory.getLogger(PcGtsTypeMessageBodyReader.class);

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public PcGtsType readFrom(Class<PcGtsType> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {	
        try {
    		logger.debug("unmarshalling PcGtsType string!");
    		
    		return PageXmlUtils.unmarshal(entityStream);        	
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WebApplicationException(e);
        }
	}

}
