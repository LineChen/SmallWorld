package com.example.hellostranger.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hellostranger.R;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.view.SlideCutListView;
import com.static_configs.StaticValues;

public class ChatListViewAdapter extends BaseAdapter {

	private List<ChatInfoEntity> coll;
	private LayoutInflater mInflater;
	private Context context;
	private RemoveListener mRemoveListener;
	SlideCutListView slideCutListView;

	public ChatListViewAdapter(Context context, List<ChatInfoEntity> coll) {
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
		ChatInfoEntity entity = coll.get(position);

		ViewHolderChatListView viewHolder = null;
		if (mInflater == null) {
			System.out.println("mInflater为空");
			return null;
		}
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_listview_item, null);

			viewHolder = new ViewHolderChatListView();
			viewHolder.iv_hp = (ImageView) convertView
					.findViewById(R.id.chat_listview_item_iv_hp);
			viewHolder.tv_username = (TextView) convertView
					.findViewById(R.id.chat_listview_item_tv_username);
			viewHolder.tv_chatcretime = (TextView) convertView
					.findViewById(R.id.chat_listview_item_tv_time);
			viewHolder.tv_chatcontent = (TextView) convertView
					.findViewById(R.id.chat_listview_item_tv_content);
			viewHolder.btn_del = (ImageButton) convertView
					.findViewById(R.id.chat_listview_item_btn_del);
			viewHolder.tv_msg_num = (TextView) convertView
					.findViewById(R.id.chat_lsitview_item_tv_num);
//			viewHolder.iv_isSystem = (ImageView) convertView
//					.findViewById(R.id.chat_listview_item_iv_v);
			
			viewHolder.num_bg = (ImageView) convertView.findViewById(R.id.chat_lsitview_item_iv_numbg);
			viewHolder.btn_del.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mRemoveListener.removeItem();
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderChatListView) convertView.getTag();
		}
		viewHolder.position = position;
		if(entity.getMsgtype() == 0){
			viewHolder.iv_hp.setImageURI(Uri.fromFile(new File(StaticValues.USER_HEADPATH + entity.getFriendId() + ".png")));
		} else {
			viewHolder.iv_hp.setImageResource(R.drawable.imomo2);//系统消息图标
//			viewHolder.iv_isSystem.setImageResource(R.drawable.icon_v);//小太阳
		}
		viewHolder.tv_username.setText(entity.getFriendName());
		viewHolder.tv_chatcretime.setText(entity.getChatCreatTime());
		viewHolder.tv_chatcontent.setText(entity.getChatContent());
		if (entity.getMsg_num() == 0) {
			viewHolder.num_bg.setBackgroundDrawable(null);
			viewHolder.tv_msg_num.setText(null);
		} else {
			viewHolder.num_bg.setBackgroundResource(R.drawable.red_point);
			viewHolder.tv_msg_num.setText(Integer.valueOf(entity.getMsg_num())
					.toString());
		}
		// System.out.println("viewHolder.extra的值为："+viewHolder.extra);
		return convertView;
	}

	public void setListView(SlideCutListView slideCutListView) {
		this.slideCutListView = slideCutListView;
		this.slideCutListView.requestDisallowInterceptTouchEvent(true);
	}

	public void setRemoveListener(RemoveListener removeListener) {
		this.mRemoveListener = removeListener;
	}

	public interface RemoveListener {
		public void removeItem();
	}

	
	public class ViewHolderChatListView {
		public ImageView iv_hp;
		public TextView tv_username;
		public TextView tv_chatcretime;
		public TextView tv_chatcontent;
		public TextView tv_msg_num;
		public ImageButton btn_del;
		public ImageView num_bg;
//		public ImageView iv_isSystem;//如果是系统消息，则显示一个太阳
		public int extra;
		public int position;
		
	}
	
}
