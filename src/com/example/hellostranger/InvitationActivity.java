package com.example.hellostranger;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.adapter.InvitationAdapter;
import com.example.hellostranger.bean.InvitationInfoEntity;

public class InvitationActivity extends Activity{
	
	ListView lv;
	private static List<InvitationInfoEntity> coll;
	static InvitationAdapter invitationAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_invitation);
		coll = new ArrayList<InvitationInfoEntity>();
		lv = (ListView) findViewById(R.id.invitation_lv);
		invitationAdapter = new InvitationAdapter(this, coll);
		invitationAdapter.setListView(lv);
		lv.setAdapter(invitationAdapter);
		
		getIvitFromDb();//初始化
	}

	/**
	 * 从数据库取得未处理的系统消息
	 */
	public void getIvitFromDb(){
		iMoMoDataBase db = new iMoMoDataBase(InvitationActivity.this);
		coll.addAll(db.getInvits());
		invitationAdapter.notifyDataSetChanged();
	}
	
	
	
	public static void deleteInviteItem(String groupId){
		for(InvitationInfoEntity entity : coll){
			if(entity.getGroupId().equals(groupId)){
				coll.remove(entity);
				invitationAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
	
	public void onBack(View v){
		finish();
	}
}



