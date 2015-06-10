package com.imomo_codecfactory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class iMoMoCodecFactory implements ProtocolCodecFactory {

	private final iMoMoEncoder encoder; // ±àÂë
	private final iMoMoDecoder decoder; // ½âÂë

	public iMoMoCodecFactory() {
		encoder = new iMoMoEncoder();
		decoder = new iMoMoDecoder();
	}

	public iMoMoEncoder getEncoder(IoSession session) {
		return encoder;
	}

	public iMoMoDecoder getDecoder(IoSession session) {
		return decoder;
	}

}
