package com.example.hellostranger.fragment_reg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.bool;
import android.widget.Toast;

public class ForCheck {

	public static boolean UserFormat(String username) {
		Pattern pattern = Pattern
				.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$");
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}
	public static boolean PasswdFormat(String password) {
		Pattern pattern = Pattern
				.compile("^(?=.{6,16}$)(?![0-9]+$)(?!.*(.).*\1)[0-9a-zA-Z#@$]+$");
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	public static boolean PhonenumberFormat(String phonenumber) {
		Pattern pattern1 = Pattern
				.compile("^1[3578][01379]\\d{8}$");
		Pattern pattern2 = Pattern
				.compile("^1[34578][01256]\\d{8}$");
		Pattern pattern3 = Pattern
				.compile("^(134[012345678]\\d{7}|1[34578][012356789]\\d{8})$");
		if(pattern1.matcher(phonenumber).matches()||
				pattern2.matcher(phonenumber).matches()||
				pattern3.matcher(phonenumber).matches()){
			return true;
		} else {
			return false;
		}
	}
	public static boolean EmailFormat(String Email) {
		Pattern pattern = Pattern
				.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = pattern.matcher(Email);
		return matcher.matches();
	}
}
