package com.imomo_codecfactory;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import android.util.Log;

import com.msg_relative.iMoMoMsg;
import com.security.PBE;

public class iMoMoEncoder extends ProtocolEncoderAdapter {
	CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	
	// ���ı����͸�ʽ��ͷ���(char) + ��Ϣ����(int) +�ı�����Ϣ(����byte[])
	// �Ǵ��ı����͸�ʽ��ͷ���(char) + ������Ϣ����(int) + ������Ϣ(����byte[]) + ��ý����Ϣ����(int) + ��ý����Ϣ(����byte[])
	@Override
	public void encode(IoSession session, Object msg, ProtocolEncoderOutput out)
			throws Exception {
		iMoMoMsg moMsg = (iMoMoMsg) msg;
		Log.i("--","����ǰ��"+moMsg.toString());
		char symbol = moMsg.symbol;
		try {
			if (symbol == '+') {
				byte[] msgJsonByte = PBE.enCrypt(moMsg.msgJson.getBytes());//����
//				System.out.println("--�����ı���Ϣ   = " + moMsg.msgJson);
				int msgLen = msgJsonByte.length;
				// 2 ���ʼ��ͷ��symbol��������'2' ��4��ʾ����λ
				IoBuffer io = IoBuffer.allocate(2 + 4 + msgLen).setAutoExpand(
						true);
				io.putChar(symbol);
				io.putInt(msgLen);
				io.put(msgJsonByte);
				io.flip();
				out.write(io);
			} else if (symbol == '-') {
				byte[] msgJsonByte = PBE.enCrypt(moMsg.msgJson.getBytes());//����
				byte[] msgBytes = PBE.enCrypt(moMsg.msgBytes);
				System.out.println("--���Ͷ�ý����Ϣ = " + moMsg.msgJson
						+ " msgBytesLen = " + msgBytes.length);
				int textLen = msgJsonByte.length;// ������Ϣ����
				int msgBytesLen = msgBytes.length;// ͼƬ����
				IoBuffer io = IoBuffer.allocate(
						2 + 4 + 4 + textLen + msgBytesLen).setAutoExpand(true);
				io.putChar(symbol);
				io.putInt(textLen);
				io.put(msgJsonByte);
				io.putInt(msgBytesLen);
				io.put(msgBytes);
				io.flip();
				out.write(io);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
