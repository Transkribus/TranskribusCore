//package eu.transkribus.core.model.beans.adapters;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.ext.MessageBodyReader;
//import javax.ws.rs.ext.Provider;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import eu.transkribus.core.model.beans.TrpDocMetadata;
//import eu.transkribus.core.model.beans.TrpDocStructure;
//import eu.transkribus.core.util.JaxbUtils;
//import eu.transkribus.core.util.SebisStopWatch;
//
//@Provider
//public class TrpDocStructureMessageBodyReader implements MessageBodyReader<TrpDocStructure> {
//	private final static Logger logger = LoggerFactory.getLogger(TrpDocStructureMessageBodyReader.class);
//
//	static SebisStopWatch sw = new SebisStopWatch();
//	
//	@Override
//	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//		final boolean res = type == TrpDocStructure.class;
//		logger.debug("isReadable() in TrpDocStructureMessageBodyReader: " + res);
//		logger.debug("type = " + type + " | genericType = " + genericType + " | mediaType = " + mediaType);
//		return res;
//	}
//
//	@Override
//	public TrpDocStructure readFrom(Class<TrpDocStructure> type, Type genericType, Annotation[] annotations, MediaType mediaType,
//			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
//			throws IOException, WebApplicationException {	
//        try {
//    		logger.debug("unmarshalling TrpDocStructure from input stream, type = "+type+" genericType = "+genericType+" mediaType = "+mediaType);
//    		
//    		sw.start();
//    		TrpDocStructure docStruct = JaxbUtils.unmarshal(entityStream, TrpDocStructure.class, TrpDocMetadata.class);
//    		sw.stop(true, "time to unmarshal: ", logger);
//    		return docStruct;
//    		
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            throw new WebApplicationException(e);
//        }
//	}
//
//}
