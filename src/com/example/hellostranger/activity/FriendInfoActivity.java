package com.example.hellostranger.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.ImageUtil;
import com.clientUtils.ClientManager;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.R;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.freind.FriendBean;

public class FriendInfoActivity extends Activity {

	private FriendBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend_info);
		Intent intent = getIntent();
		bean = (FriendBean) intent
				.getSerializableExtra("friendInfo");

		ImageView	iv_headlogo = (ImageView) findViewById(R.id.friendinfo_iv_headlogo);
		TextView tv_nickname = (TextView) findViewById(R.id.friendinfo_tv_nickname);
		TextView tv_sex = (TextView) findViewById(R.id.friendinfo_tv_sexvalue);
		TextView tv_singnatrue = (TextView) findViewById(R.id.friendinfo_tv_singnatrue);
		
		tv_nickname.setText(bean.getFriendName());
		tv_sex.setText(bean.getFriendSex());
		tv_singnatrue.setText(bean.getFriendSignature());
		iv_headlogo.setImageURI(Uri.fromFile(new File(bean.getFriendHeadPath())));
		
	}

	/**
	 * ����
	 * 
	 * @param v
	 */
	public void onChat(View v) {
		
//		Toast.makeText(this, "����" + bean.getFriendId(), 0).show();
		String friendId = bean.getFriendId();
		String friendName = bean.getFriendName();
//		Log.i("--", "�Ƿ�����������ϵ��  = " + MainActivity.mp0.isExistsInLatice(friendId));
		//���������ϵ��
		if(MainActivity.mp0.isExistsInLatice(friendId)){
			MainActivity.mp0.ResetNotReadMsg(friendId);
		} else {
			ChatInfoEntity entity = new ChatInfoEntity();
			entity.setFriendId(friendId);
			entity.setFriendName(friendName);
			entity.setChatContent("");
			entity.setChatCreatTime("");
			entity.setMsg_num(0);
			entity.setMsgtype(0);
			MainActivity.mp0.AddLaticeChatItem(entity);
			
		}
		Intent intent = new Intent(FriendInfoActivity.this, ChatActivity.class);
		intent.putExtra("friendId", friendId);
		intent.putExtra("friendName", friendName);
		startActivity(intent);
		finish();
//		MainActivity.context.LocToMp0();//��ת�������ϵ���б�
	}
	
	/**
	 * ɾ������
	 * 1.����֪ͨ��������
	 * 2.���������ϵ���У�ɾ�����������ݿ�
	 * 3.��Ϣ���ݿ��������
	 * 
	 * 4.�����б�ɾ��
	 * @param v
	 */
	public void onDeleteFriend(View v){
		String friendId = bean.getFriendId();
		MainActivity.myBinder.deleteFriend(friendId);
		MainActivity.mp1.DeleteAFriend(friendId);//�����б����
		if(MainActivity.mp0.isExistsInLatice(friendId))
			MainActivity.mp0.deleteLaticeChatItem(friendId);//�����ϵ��
		iMoMoDataBase db = new iMoMoDataBase(FriendInfoActivity.this);
		String tName = "msg"+ ClientManager.clientId + "_" + friendId;
		//ɾ�����ݿ��еĴ洢
		if(db.isTableExists(tName))
			db.clearMsg(friendId);//�����Ϣ,,Ӧ��ɾ�������
		if(db.isExistsLatice(friendId)){
			db.deleteLaticeItem(friendId);
		}
		if(MainActivity.mp0.isExistsInLatice(friendId)){
			MainActivity.mp0.deleteLaticeChatItem(friendId);
		}
			
		finish();
	}
	

	public void onBack(View v) {
		finish();
	}

}
