package eu.transkribus.core.catti;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CattiRequestDecoder implements Decoder.Text<CattiRequest>{

	@Override public void init(EndpointConfig config) {
	}

	@Override public void destroy() {
	}

	@Override public CattiRequest decode(String s) throws DecodeException {
		return CattiRequest.fromJsonStr(s);
	}

	@Override public boolean willDecode(String s) {
		return CattiRequest.fromJsonStr(s) != null;
	}

}
