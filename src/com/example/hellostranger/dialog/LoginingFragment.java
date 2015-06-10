package com.example.hellostranger.dialog;

import com.example.hellostranger.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class LoginingFragment extends DialogFragment {

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
		view = inflater.inflate(R.layout.dialog_logining, container);
		
		return view;
	}

}
