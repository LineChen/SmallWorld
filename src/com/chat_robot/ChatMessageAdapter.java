package com.chat_robot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat_robot.ChatMessage.Type;
import com.clientUtils.ClientManager;
import com.example.hellostranger.R;
import com.static_configs.StaticValues;

public class ChatMessageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ChatMessage> mDatas;

	private final int INCOMING = 0;// 接收
	private final int MYSEND = 1;// 发送

	public ChatMessageAdapter(Context context, List<ChatMessage> mDatas) {
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage chatMessage = mDatas.get(position);
		if (chatMessage.getType() == Type.INCOMING) {
			return INCOMING;
		}
		return MYSEND;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage chatMessage = mDatas.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			// 通过ItemType设置不同的布局
			if (getItemViewType(position) == INCOMING) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mHead = (ImageView) convertView
						.findViewById(R.id.iv_userhead);
				viewHolder.mDate = (TextView) convertView
						.findViewById(R.id.tv_sendtime);
				viewHolder.mMsg = (TextView) convertView
						.findViewById(R.id.tv_chatcontent);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mHead = (ImageView) convertView
						.findViewById(R.id.iv_userhead);
				viewHolder.mDate = (TextView) convertView
						.findViewById(R.id.tv_sendtime);
				viewHolder.mMsg = (TextView) convertView
						.findViewById(R.id.tv_chatcontent);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置数据
		if(getItemViewType(position) == INCOMING){
			viewHolder.mHead.setBackgroundResource(R.drawable.robot2);
		} else {
			String headPath = StaticValues.USER_HEADPATH
					+ ClientManager.clientEmail + ".png";
			viewHolder.mHead.setImageURI(Uri.fromFile(new File(headPath)));
		}
		
		viewHolder.mDate.setText(chatMessage.getDate());
		viewHolder.mMsg.setText(chatMessage.getMsg());
		return convertView;
	}

	private final class ViewHolder {
		ImageView mHead;
		TextView mDate;
		TextView mMsg;
	}

}
