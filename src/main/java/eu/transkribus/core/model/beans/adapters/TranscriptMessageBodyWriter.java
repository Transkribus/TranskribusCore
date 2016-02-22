package eu.transkribus.core.model.beans.adapters;
//package org.dea.transcript.trp.core.model.beans.adapters;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.ext.MessageBodyWriter;
//import javax.ws.rs.ext.Provider;
//import javax.ws.rs.ProcessingException;
//import javax.ws.rs.Produces;
//import javax.ws.rs.WebApplicationException;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//
//import org.dea.transcript.trp.core.model.beans.pagecontent.PcGtsType;
//
//@Provider
//@Produces("application/xml")
//public class TranscriptMessageBodyWriter implements MessageBodyWriter<PcGtsType> {
//
//	@Override
//	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
//			MediaType mediaType) {
//		return type == PcGtsType.class;
//	}
//
//	@Override
//	public long getSize(PcGtsType transcript, Class<?> type, Type genericType,
//			Annotation[] annotations, MediaType mediaType) {
//		// deprecated by JAX-RS 2.0 and ignored by Jersey runtime
//		return 0;
//	}
//
//	@Override
//	public void writeTo(PcGtsType transcript, Class<?> type, Type genericType,
//			Annotation[] annotations, MediaType mediaType,
//			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
//			throws IOException, WebApplicationException {
//
//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance(PcGtsType.class);
//
//			// serialize the entity myBean to the entity output stream
//			jaxbContext.createMarshaller().marshal(transcript, entityStream);
//		} catch (JAXBException jaxbException) {
//			throw new ProcessingException("Error serializing a " + type.getCanonicalName() + " to the output stream",
//					jaxbException);
//		}
//	}
//
//}
