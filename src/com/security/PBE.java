package com.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Hex;

public class PBE {
	// ��
	private final static byte[] salt = { 70, -106, 92, -82, 108, 58, 6, -75 };
	// ��������Կ
	private final static String password = "iMoMo_LineChen";
	public static void main(String[] args) {
		String ss = "I am good.";
		byte[] encresults = enCrypt(ss.getBytes());
		deCiphering(encresults);
	}
	public static byte[] enCrypt(byte[] encptBytes) {
		byte[] results = null;// ���ܽ��
		long time1 = System.currentTimeMillis();
		try {
			PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBEWITHMD5andDES");
			Key key = factory.generateSecret(pbeKeySpec);

			// ����
			PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(salt, 100);// ��������
			Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
			cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpac);
			results = cipher.doFinal(encptBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time2 = System.currentTimeMillis();
		System.out.println("���μ�����ʱ �� " + (time2 - time1));
		return results;
	}

	public static byte[] deCiphering(byte[] deciBytes) {
		byte[] results = null;// ���ܽ��
		long time1 = System.currentTimeMillis();
		try {
			PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBEWITHMD5andDES");
			Key key = factory.generateSecret(pbeKeySpec);
			// ����
			PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(salt, 100);// ��������
			Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
			cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpac);
			results = cipher.doFinal(deciBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long time2 = System.currentTimeMillis();
		System.out.println("���ν�����ʱ �� " + (time2 - time1));
		return results;
	}

}
