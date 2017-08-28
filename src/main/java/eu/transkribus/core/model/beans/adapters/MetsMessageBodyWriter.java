package eu.transkribus.core.model.beans.adapters;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.util.JaxbUtils;

@Provider
@Produces("application/xml")
public class MetsMessageBodyWriter implements MessageBodyWriter<Mets> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return type == Mets.class;
	}
	
	@Override
	public long getSize(Mets t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// return -1 with respect to Javadoc of this method
		return -1;
	}

	@Override
	public void writeTo(Mets mets, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		try {
	        JaxbUtils.marshalToStream(mets, entityStream, TrpDocMetadata.class);
		} catch (JAXBException jaxbException) {
			throw new ProcessingException("Error serializing a " + type.getCanonicalName() + " to the output stream",
					jaxbException);
		}
	}

}
