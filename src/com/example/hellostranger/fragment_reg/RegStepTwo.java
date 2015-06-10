package com.example.hellostranger.fragment_reg;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hellostranger.R;

public class RegStepTwo extends Fragment {

	View view;
	EditText et_birday;
	RadioGroup rg;
	private ImageView iv_hp;
	RadioButton rbm;
	RadioButton rbw;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("执行onCreateView");  
		view = inflater.inflate(R.layout.fragment_regsteptwo, container, false);
		rg = (RadioGroup) view.findViewById(R.id.regtwo_rg);
		rbm = (RadioButton) view.findViewById(R.id.regtwo_rb_boy);
		rbw = (RadioButton) view.findViewById(R.id.regtwo_rb_girl);
		iv_hp = (ImageView) view.findViewById(R.id.regtwo_imgbtn_uppho);
		if (iv_hp == null){
			System.out.println("ImageView为空");
		}
		iv_hp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PictureUtil.doPickAction(getActivity());
			}
		});
		et_birday = (EditText) view.findViewById(R.id.regtwo_et_birday);
		et_birday.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDatePickerDialog();
			}
		});
		this.getClass().toString();
		return view;
	}
	
	
	 @Override  
     public void onAttach(Activity activity) {  
         // TODO Auto-generated method stub  
		 System.out.println("执行onAttach"+this.getClass().toString()); 
		 if (iv_hp == null){
				System.out.println("ImageView为空");
			}
         super.onAttach(activity);  
     }  
       
     @Override  
     public void onCreate(Bundle savedInstanceState) {  
         // TODO Auto-generated method stub  
    	 System.out.println("执行onCreate");
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         super.onCreate(savedInstanceState);  
     }  
       
     @Override  
     public void onActivityCreated(Bundle savedInstanceState) {  
         super.onActivityCreated(savedInstanceState);  
         if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         System.out.println("执行onActivityCreated"); 
     }  

     @Override  
     public void onStart() {  
         // TODO Auto-generated method stub  
    	 System.out.println("执行onStart"); 
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         super.onStart();  
     }  
       
     @Override  
     public void onResume() {  
    	 System.out.println("执行onResume"); 
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         // TODO Auto-generated method stub  
         super.onResume();  
     }  
       
     @Override  
     public void onPause() {  
    	 System.out.println("执行onPause"); 
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
//          TODO Auto-generated method stub  
         super.onPause();  
     }  
       
     @Override  
     public void onStop() {  
    	 System.out.println("执行onStop"); 
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         // TODO Auto-generated method stub  
         super.onStop();  
     }  
       
     @Override  
     public void onDestroyView() {  
    	 System.out.println("执行onDestroyView"); 
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         // TODO Auto-generated method stub  
         super.onDestroyView();  
     }  
       
     @Override  
     public void onDestroy() {  
         // TODO Auto-generated method stub  
    	 System.out.println("执行onDestroy");  
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         super.onDestroy();  
     }  
       
     @Override  
     public void onDetach() {  
    	 System.out.println("执行onDetach");  
    	 if (iv_hp == null){
 			System.out.println("ImageView为空");
 		}
         // TODO Auto-generated method stub  
         super.onDetach();  
     }   
	
	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Log.d("OnDateSet", "select year:" + year + ";month:" + month
					+ ";day:" + day);
			et_birday.setText(Integer.valueOf(year).toString() + "."+Integer.valueOf(month + 1).toString()+ "."+Integer.valueOf(day).toString());
		}
	}

	public void showDatePickerDialog(){
		DatePickerFragment datePicker = new DatePickerFragment();
		datePicker.show(getFragmentManager(), "datePicker");
	}
	
	public String getBirday(){
		return et_birday.getText().toString();
	}
	
	public String getSex(){
		String sex = "";
		if(rbm.isChecked()) {
			sex = rbm.getText().toString();
		} else if(rbw.isChecked()) {
			sex = rbw.getText().toString();
		} else {
			sex = null;
		}
		return sex;
	}
	
	public void setImageView(Bitmap b){
		if (et_birday == null){
			System.out.println("et_birday为空");
		}
		if (iv_hp == null){
			System.out.println("ImageView为空");
		}
		if (b == null){
			System.out.println("Bitmap为空");
		}
		iv_hp.setImageBitmap(b);
	}
	
	public void setImageViewUri(Uri u){
		if (et_birday == null){
			System.out.println("et_birday为空");
		}
		if (iv_hp == null){
			System.out.println("ImageView为空");
		}
		if (u == null){
			System.out.println("u为空");
		}
		iv_hp.setImageURI(u);
	}

}
