package com.example.hellostranger.bean;

import com.example.hellostranger.fragment_reg.RegStepOne;
import com.example.hellostranger.fragment_reg.RegStepThree;
import com.example.hellostranger.fragment_reg.RegStepTwo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.widget.Button;

public class RegActivityData {
	
	public Fragment[] fragmentarray;
	public RegStepOne regStepOne;
	public RegStepTwo regStepTwo;
	public RegStepThree regStepThree;
	public Button btn_left;
	public Button btn_right;
	public int step;
	public String username;
	public String birday;
	public String password;
	public String sex;
	public Bitmap myBitmap;
	public FragmentManager manager;
}
