package com.example.hellostranger.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.clientUtils.ClientManager;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.InvitationActivity;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.bean.InvitationInfoEntity;
import com.example.hellostranger.view.SlideCutListView;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class InvitationAdapter extends BaseAdapter{
	
	private List<InvitationInfoEntity> coll;
	private LayoutInflater mInflater;
	private Context context;
	ListView listView;
	
	public InvitationAdapter(Context context, List<InvitationInfoEntity> coll) {
		this.coll = coll;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final InvitationInfoEntity entity = coll.get(position);
		if (mInflater == null) {
			System.out.println("mInflater为空");
			return null;
		}
		ViewHolderInvition viewHolder = new ViewHolderInvition();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.invitation_listview_item, null);			
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_lv_it_icon);
			viewHolder.ivusername = (TextView) convertView.findViewById(R.id.iv_lv_it_ivusername);
			viewHolder.groupname = (TextView) convertView.findViewById(R.id.iv_lv_it_groupname);
			viewHolder.topic = (TextView) convertView.findViewById(R.id.iv_lv_it_topic);
			viewHolder.drop = (Button) convertView.findViewById(R.id.iv_lv_it_drop);
			viewHolder.agree = (Button) convertView.findViewById(R.id.iv_lv_it_agree);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolderInvition) convertView.getTag();
		}
		viewHolder.icon.setImageURI(Uri.fromFile(new File(entity.getGroupIconPath())));
		viewHolder.ivusername.setText(entity.getInvitorName());
		viewHolder.groupname.setText(entity.getGroupName());
		viewHolder.topic.setText(entity.getTopic());
		
		viewHolder.agree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iMoMoMsg agreeMsg = new iMoMoMsg();
				agreeMsg.symbol = '+';
				JSONObject json = new JSONObject();
				json.put(MsgKeys.msgType, iMoMoMsgTypes.AGREEE_TO_GROUP);
				json.put(MsgKeys.userId, ClientManager.clientId);
				json.put(MsgKeys.groupId, entity.getGroupId());
				agreeMsg.msgJson = json.toJSONString();
				if (MainActivity.ClientSession != null
						&& !MainActivity.ClientSession.isClosing()) {
					MainActivity.ClientSession.write(agreeMsg);
				}
				/////在最近联系人列表中添加一个群组聊天
				ChatInfoEntity laticeItem = new ChatInfoEntity();
				laticeItem.setFriendId(entity.getGroupId());
				laticeItem.setFriendName(entity.getGroupName());
				laticeItem.setChatContent("");
				laticeItem.setChatCreatTime("");
				laticeItem.setMsg_num(0);
				laticeItem.setMsgtype(2);
				MainActivity.mp0.AddLaticeChatItem(laticeItem);
				
				InvitationActivity.deleteInviteItem(entity.getGroupId());
				
				iMoMoDataBase db = new iMoMoDataBase(context);
				db.deleteInvite(entity.getGroupId());
			}
		});
		
		viewHolder.drop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iMoMoDataBase db = new iMoMoDataBase(context);
				db.deleteInvite(entity.getGroupId());
			}
		});
		
		return convertView;
	}
	
	public void setListView(ListView listView) {
		this.listView = listView;
	}
	
	public class ViewHolderInvition {
		ImageView icon;
		TextView ivusername;
		TextView groupname;
		TextView topic;
		Button drop;
		Button agree;	
	}
}
