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
import eu.transkribus.core.util.SebisStopWatch;

@Provider
public class PcGtsTypeMessageBodyReader implements MessageBodyReader<PcGtsType> {
	private final static Logger logger = LoggerFactory.getLogger(PcGtsTypeMessageBodyReader.class);

	static SebisStopWatch sw = new SebisStopWatch();
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		/*
		 * FIXME type check should actually be done here!
		 * This was "return true;" since the beginning and in order to not break any third party clients
		 * I leave it like that for now. P
		 */
//		final boolean res = type == PcGtsType.class;
//		logger.debug("isReadable() in PcGtsTypeMessageBodyReader: " + res);
//		logger.debug("type = " + type + " | genericType = " + genericType + " | mediaType = " + mediaType);
//		return res;
		return true;
	}

	@Override
	public PcGtsType readFrom(Class<PcGtsType> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {	
        try {
    		logger.debug("unmarshalling PcGtsType from input stream, type = "+type+" genericType = "+genericType+" mediaType = "+mediaType);

    		sw.start();
    		PcGtsType pc =  PageXmlUtils.unmarshal(entityStream);
    		sw.stop(true, "time to unmarshal: ", logger);
    		
    		return pc;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WebApplicationException(e);
        }
	}

}
