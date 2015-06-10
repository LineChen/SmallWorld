package com.chat;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.hellostranger.R;
import com.static_configs.StaticValues;

public class FacePageFragment extends Fragment {
	private int page_index;// 当前页 第一页按1算
	private Handler handler;//聊听界面传来的handler
	public FacePageFragment(int page_index, Handler handler) {
		this.page_index = page_index;
		this.handler = handler;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle savedInstanceState) {
		FrameLayout fl = (FrameLayout) inflater.inflate(
				R.layout.chatview_face_page_fragment, root, false);
		GridView face_pagegridView = (GridView) fl
				.findViewById(R.id.gridview_face_page);
//		face_pagegridView.setSelector(Color.TRANSPARENT);
		FaceConversionUtil.getInstace().getFileText(getActivity());
		Face_PageAdapter2 adapter = new Face_PageAdapter2(page_index,
				getActivity());
		face_pagegridView.setAdapter(adapter);
		return fl;
	}

	/**
	 * 表情页适配器
	 * 
	 * @author LineChen
	 */
	class Face_PageAdapter2 extends BaseAdapter {
		int page_index;// 是第几页表情
		Context context;
		List<ChatEmoji> chatEmojis;// 该页的所有表情

		public Face_PageAdapter2(int page_index, Context context) {
			this.page_index = page_index;
			this.context = context;
			this.chatEmojis = FaceConversionUtil.getInstace().getOnePageFaces(
					page_index);
		}

		@Override
		public int getCount() {
			return chatEmojis.size();
		}

		@Override
		public Object getItem(int position) {
			return chatEmojis.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Viewholder holder = null;
			final int select_position = position;
			if (convertView == null) {
				holder = new Viewholder();
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.chatview_face_item,
						parent, false);
				holder.face_item = (ImageView) convertView
						.findViewById(R.id.face_item);
				convertView.setTag(holder);
			} else {
				holder = (Viewholder) convertView.getTag();
			}
			holder.face_item.setBackgroundResource(chatEmojis.get(position)
					.getId());
			holder.face_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ChatEmoji emoji = FaceConversionUtil.getInstace()
							.getSelectEmoji(page_index, select_position);
					Log.i("--", "点击的表情信息:" + emoji.toString());
					if (select_position == 20){
						Message msg = new Message();
						msg.what = StaticValues.DEL_EDIT_TEXT;
						handler.sendMessage(msg);
						return;
					}
					
					SpannableString spannableString = FaceConversionUtil
							.getInstace().addFace(context, emoji.getId(),
									emoji.getCharacter());
					Log.i("--","点击:"+spannableString);
					Message msg = new Message();
					msg.what = StaticValues.FACE_GETTED;
					msg.obj = spannableString;
					handler.sendMessage(msg);
				}
			});
			return convertView;
		}
	}

	private class Viewholder {
		public ImageView face_item;
	}

}
