package eu.transkribus.core.catti;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class CattiRequestEncoder implements Encoder.Text<CattiRequest> {

	@Override public void init(EndpointConfig config) {
	}

	@Override public void destroy() {
	}

	@Override public String encode(CattiRequest object) throws EncodeException {
		return CattiRequest.toJsonStr(object);
	}

}
