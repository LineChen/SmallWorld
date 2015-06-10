package com.imomo_codecfactory;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import android.util.Log;

import com.msg_relative.iMoMoMsg;
import com.security.PBE;

public class iMoMoDecoder extends CumulativeProtocolDecoder {
	CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		iMoMoMsg moMsg = new iMoMoMsg();
		int pos = in.position();
		int remaining = in.remaining();
		try {
			// �жϳ���,��ͷsymbol ��char��
			if (remaining < 2) {
				in.position(pos);
				return false;
			}
			char symbol = in.getChar();// �൱�����ѵ��ֽ���
			moMsg.symbol = symbol;
			if (symbol == '+') {
				int msgJsonLen = 0;// msgJson����
				// �ж��Ƿ񹻽������ĳ���
				msgJsonLen = in.getInt();
				if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp = new byte[msgJsonLen];
				in.get(temp);//�õ����ܺ�byte����
				moMsg.msgJson = new String(PBE.deCiphering(temp), "gbk");
				out.write(moMsg);
//				Log.i("--","�ͻ��˽��룺"+moMsg.toString());
			} else if (symbol == '-') {
				// �����ı���Ϣ
				int msgJsonLen = 0;// msgJson����
				int msgBytesLen = 0;// msgBytes����
				msgJsonLen = in.getInt();
				if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp1 = new byte[msgJsonLen];
				in.get(temp1);//�õ����ܺ�byte����
				moMsg.msgJson = new String(PBE.deCiphering(temp1), "gbk");
				// ����ͼƬ��Ϣ
				msgBytesLen = in.getInt();
				if (remaining - 2 - 4 - 4 - msgJsonLen < msgBytesLen
						|| msgBytesLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp2 = new byte[msgBytesLen];
				in.get(temp2);//�õ����ܺ�byte����
				moMsg.msgBytes = PBE.deCiphering(temp2);
				out.write(moMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			in.position(pos);
			return false;
		}
		return true;
	}
}
