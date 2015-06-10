package com.example.hellostranger.util;

import java.util.Date;

import com.static_configs.StaticValues;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context) {
		sp = context.getSharedPreferences(StaticValues.sharePreName,
				context.MODE_PRIVATE);
		editor = sp.edit();
	}

	/**
	 * ����
	 */
	public void SaveValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * ��ȡֵ
	 * 
	 * @return
	 */
	public String GetValue(String key) {
		return sp.getString(key, "");
	}

	// �û���id
	public void setId(String id) {
		editor.putString("id", id);
		editor.commit();
	}

	public String getId() {
		return sp.getString("id", "");
	}

	// �û�������
	public String getEmail() {
		return sp.getString(StaticValues.userEmail, "");
	}

	public void setEmail(String email) {
		editor.putString(StaticValues.userEmail, email);
		editor.commit();
	}

	// �û����ǳ�
	public String getName() {
		return sp.getString("name", "Test");
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	// �û�������
	public void setPasswd(String passwd) {
		editor.putString(StaticValues.userPasswd, passwd);
		editor.commit();
	}

	public String getPasswd() {
		return sp.getString(StaticValues.userPasswd, "");
	}

	// �û��Ա�
	public void setSex(boolean sex) {
		editor.putBoolean("sex", sex);
		editor.commit();
	}

	public boolean getSex() {
		return sp.getBoolean("sex", true);
	}

	// �Ƿ�ǩ��
	public void setIsReport(boolean isReport) {
		editor.putBoolean("isReport", isReport);
		editor.commit();
	}

	public boolean getIsReport() {
		return sp.getBoolean("isReport", false);
	}

	//ǩ�����ڣ���Ϊһ��ֻǩ��һ��
	public void setReportDate(){
		editor.putString("ReportDate", new Date() + "");
		editor.commit();
	}
	
	public String getReportDate(){
		return sp.getString("ReportDate", "");
	}
	
	
	// �Ƿ��ں�̨���б��
	public void setSignature(String s) {
		editor.putString("Signature", s);
		editor.commit();
	}

	public String getSignature() {
		return sp.getString("Signature", "Ȫ�ԣ������봦��½��������ʪ�������ĭ�����������ڽ�����");
	}

	// �Ƿ��ں�̨���б��
	public void setIsBR(boolean isStart) {
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsBR() {
		return sp.getBoolean("isStart", false);
	}

	// �Ƿ��һ�����б�Ӧ��
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}

	// ////////////////////////////////////////////////////////
	// �Ƿ�����ʾ��
	public void setIsRing(boolean isRing) {
		editor.putBoolean("isRing", isRing);
		editor.commit();
	}

	public boolean getIsRing() {
		return sp.getBoolean("isRing", true);
	}

	// �Ƿ�����
	public void setIsVibration(boolean isVibration) {
		editor.putBoolean("isVibration", isVibration);
		editor.commit();
	}

	public boolean getIsVibration() {
		return sp.getBoolean("isVibration", true);
	}

	// �Ƿ����λ��
	public void setIsShareLoc(boolean isShareLoc) {
		editor.putBoolean("isShareLoc", isShareLoc);
		editor.commit();
	}

	public boolean getIsShareLoc() {
		return sp.getBoolean("isShareLoc", true);
	}

	//////////////////////////////////////////////////////////

	
	
	// �Ƿ���֪ͨ����ʾ
	public void setIsNotice(boolean isNotice) {
		editor.putBoolean("isNotice", isNotice);
		editor.commit();
	}

	public boolean getIsNotice() {
		return sp.getBoolean("isNotice", true);
	}

}
