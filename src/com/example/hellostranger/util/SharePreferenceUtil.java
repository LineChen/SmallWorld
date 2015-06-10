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
	 * 保存
	 */
	public void SaveValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获取值
	 * 
	 * @return
	 */
	public String GetValue(String key) {
		return sp.getString(key, "");
	}

	// 用户的id
	public void setId(String id) {
		editor.putString("id", id);
		editor.commit();
	}

	public String getId() {
		return sp.getString("id", "");
	}

	// 用户的邮箱
	public String getEmail() {
		return sp.getString(StaticValues.userEmail, "");
	}

	public void setEmail(String email) {
		editor.putString(StaticValues.userEmail, email);
		editor.commit();
	}

	// 用户的昵称
	public String getName() {
		return sp.getString("name", "Test");
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	// 用户的密码
	public void setPasswd(String passwd) {
		editor.putString(StaticValues.userPasswd, passwd);
		editor.commit();
	}

	public String getPasswd() {
		return sp.getString(StaticValues.userPasswd, "");
	}

	// 用户性别
	public void setSex(boolean sex) {
		editor.putBoolean("sex", sex);
		editor.commit();
	}

	public boolean getSex() {
		return sp.getBoolean("sex", true);
	}

	// 是否签到
	public void setIsReport(boolean isReport) {
		editor.putBoolean("isReport", isReport);
		editor.commit();
	}

	public boolean getIsReport() {
		return sp.getBoolean("isReport", false);
	}

	//签到日期，因为一天只签到一次
	public void setReportDate(){
		editor.putString("ReportDate", new Date() + "");
		editor.commit();
	}
	
	public String getReportDate(){
		return sp.getString("ReportDate", "");
	}
	
	
	// 是否在后台运行标记
	public void setSignature(String s) {
		editor.putString("Signature", s);
		editor.commit();
	}

	public String getSignature() {
		return sp.getString("Signature", "泉涸，鱼相与处于陆，相呴以湿，相濡以沫，不如相忘于江湖。");
	}

	// 是否在后台运行标记
	public void setIsBR(boolean isStart) {
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsBR() {
		return sp.getBoolean("isStart", false);
	}

	// 是否第一次运行本应用
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}

	// ////////////////////////////////////////////////////////
	// 是否有提示音
	public void setIsRing(boolean isRing) {
		editor.putBoolean("isRing", isRing);
		editor.commit();
	}

	public boolean getIsRing() {
		return sp.getBoolean("isRing", true);
	}

	// 是否有震动
	public void setIsVibration(boolean isVibration) {
		editor.putBoolean("isVibration", isVibration);
		editor.commit();
	}

	public boolean getIsVibration() {
		return sp.getBoolean("isVibration", true);
	}

	// 是否分享位置
	public void setIsShareLoc(boolean isShareLoc) {
		editor.putBoolean("isShareLoc", isShareLoc);
		editor.commit();
	}

	public boolean getIsShareLoc() {
		return sp.getBoolean("isShareLoc", true);
	}

	//////////////////////////////////////////////////////////

	
	
	// 是否有通知栏提示
	public void setIsNotice(boolean isNotice) {
		editor.putBoolean("isNotice", isNotice);
		editor.commit();
	}

	public boolean getIsNotice() {
		return sp.getBoolean("isNotice", true);
	}

}
