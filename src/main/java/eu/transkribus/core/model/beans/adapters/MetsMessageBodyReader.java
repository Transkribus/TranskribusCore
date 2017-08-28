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

import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.SebisStopWatch;

@Provider
public class MetsMessageBodyReader implements MessageBodyReader<Mets> {
	private final static Logger logger = LoggerFactory.getLogger(MetsMessageBodyReader.class);

	static SebisStopWatch sw = new SebisStopWatch();

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		final boolean res = type == Mets.class;
		logger.debug("isReadable() in MetsMessageBodyReader: " + res);
		logger.debug("type = " + type + " | genericType = " + genericType + " | mediaType = " + mediaType);
		return res;
	}

	@Override
	public Mets readFrom(Class<Mets> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try {
			logger.debug("unmarshalling Mets from input stream, type = " + type + " genericType = " + genericType
					+ " mediaType = " + mediaType);

			sw.start();
			Mets mets = JaxbUtils.unmarshal(entityStream, Mets.class, TrpDocMetadata.class);
			sw.stop(true, "time to unmarshal: ", logger);
			return mets;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e);
		}
	}

}
