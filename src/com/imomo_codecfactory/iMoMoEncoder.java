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
	
	// 纯文本发送格式：头标记(char) + 信息长度(int) +文本信信息(加密byte[])
	// 非纯文本发送格式：头标记(char) + 文字信息长度(int) + 文字信息(加密byte[]) + 多媒体信息长度(int) + 多媒体信息(加密byte[])
	@Override
	public void encode(IoSession session, Object msg, ProtocolEncoderOutput out)
			throws Exception {
		iMoMoMsg moMsg = (iMoMoMsg) msg;
		Log.i("--","编码前："+moMsg.toString());
		char symbol = moMsg.symbol;
		try {
			if (symbol == '+') {
				byte[] msgJsonByte = PBE.enCrypt(moMsg.msgJson.getBytes());//加密
//				System.out.println("--发送文本信息   = " + moMsg.msgJson);
				int msgLen = msgJsonByte.length;
				// 2 ：最开始的头部symbol，这里是'2' ，4表示长度位
				IoBuffer io = IoBuffer.allocate(2 + 4 + msgLen).setAutoExpand(
						true);
				io.putChar(symbol);
				io.putInt(msgLen);
				io.put(msgJsonByte);
				io.flip();
				out.write(io);
			} else if (symbol == '-') {
				byte[] msgJsonByte = PBE.enCrypt(moMsg.msgJson.getBytes());//加密
				byte[] msgBytes = PBE.enCrypt(moMsg.msgBytes);
				System.out.println("--发送多媒体信息 = " + moMsg.msgJson
						+ " msgBytesLen = " + msgBytes.length);
				int textLen = msgJsonByte.length;// 文字信息长度
				int msgBytesLen = msgBytes.length;// 图片长度
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
