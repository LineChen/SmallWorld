package com.example.hellostranger.fragment_reg;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hellostranger.R;
import com.example.hellostranger.util.PasswordUtil;

public class RegStepThree extends Fragment{
	
	View view;
	EditText password;
	EditText passwordagain;
	TextView tv_hint;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_regstepthree, container, false);
		password = (EditText) view.findViewById(R.id.regthree_et_password);
		passwordagain = (EditText) view.findViewById(R.id.regthree_et_passwordagain); 
		tv_hint = (TextView) view.findViewById(R.id.regthree_tv_hint);
		
		return view;
	}
	
	public String getPassword (){
		String p = password.getText().toString();
		return p;
	}
	public String getPasswordAgain() {
		String pa = passwordagain.getText().toString();
		return pa;
	}
	
	public void resetPassword() {
		password.setText("");
		passwordagain.setText("");
	}

}
