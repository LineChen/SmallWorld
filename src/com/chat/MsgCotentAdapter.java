package com.chat;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.clientUtils.ClientManager;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.ChatActivity;
import com.example.hellostranger.activity.FriendInfoActivity;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.activity.PersonalInfoActivity;
import com.example.hellostranger.view.GifTextView;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsgClient;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class MsgCotentAdapter extends BaseAdapter {

	public static final int MESSAGE_TYPE_INVALID = -1;
	public static final int MESSAGE_TYPE_MINE_TETX = 0x00;
	public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
	public static final int MESSAGE_TYPE_MINE_AUDIO = 0x02;
	public static final int MESSAGE_TYPE_OTHER_TEXT = 0x03;
	public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
	public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x05;
	public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
	private static final int VIEW_TYPE_COUNT = 9;

	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
	private Context context;
	private List<iMoMoMsgClient> msgList;
	LayoutInflater inflater;

	// private Handler handler;//聊天界面传来的handler
	public MsgCotentAdapter(Context context, List<iMoMoMsgClient> msgList) {
		this.context = context;
		this.msgList = msgList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position >= msgList.size()) {
			return MESSAGE_TYPE_INVALID;
		}
		iMoMoMsgClient item = msgList.get(position);
		if (item != null) {
			boolean isGetted = item.isGetted();
			JSONObject msgJson = JSON.parseObject(item.getMsgJson());
			int msgType = msgJson.getInteger(MsgKeys.msgType);
			if (isGetted) {
				switch (msgType) {
				case iMoMoMsgTypes.CHATING_IMAGE_MSG:
					return MESSAGE_TYPE_OTHER_IMAGE;
				case iMoMoMsgTypes.CHATING_TEXT_MSG:
					return MESSAGE_TYPE_OTHER_TEXT;
				case iMoMoMsgTypes.CHATING_VOICE_MSG:
					return MESSAGE_TYPE_OTHER_AUDIO;
				default:
					break;
				}
			} else {
				switch (msgType) {
				case iMoMoMsgTypes.CHATING_IMAGE_MSG:
					return MESSAGE_TYPE_MINE_IMAGE;
				case iMoMoMsgTypes.CHATING_TEXT_MSG:
					return MESSAGE_TYPE_MINE_TETX;
				case iMoMoMsgTypes.CHATING_VOICE_MSG:
					return MESSAGE_TYPE_MINE_AUDIO;
				default:
					break;
				}
			}
		}
		return MESSAGE_TYPE_INVALID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		System.out.print("位置：" + position + "   ");
		iMoMoMsgClient moMoMsg = msgList.get(position);
		if (moMoMsg == null) {
			// System.out.println("数据 为空，返回空布局");
			return null;
		} else {
			// System.out.println("数据 不为空");
		}
		int type = getItemViewType(position);
		ViewHolderBase holder;
		if (null == convertView) {
			holder = new ViewHolderBase();
			System.out.println("布局为空");
			switch (type) {
			case MESSAGE_TYPE_MINE_TETX: {
				System.out.println("布局为文字");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_text_right, parent, false);
				holder = new ViewHolderText();
				convertView.setTag(holder);
				fillTextMessageHolder((ViewHolderText) holder, convertView);
				break;
			}
			case MESSAGE_TYPE_MINE_IMAGE: {
				System.out.println("布局为图片");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_image_right, parent, false);
				holder = new ViewHolderImg();
				convertView.setTag(holder);
				fillImageMessageHolder((ViewHolderImg) holder, convertView);
				break;
			}
			case MESSAGE_TYPE_MINE_AUDIO: {
				System.out.println("布局为录音");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_text_right, parent, false);
				holder = new ViewHolderText();
				convertView.setTag(holder);
				fillTextMessageHolder((ViewHolderText) holder, convertView);
				break;
			}
			case MESSAGE_TYPE_OTHER_TEXT: {
				System.out.println("布局为文字");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_text_left, parent, false);
				holder = new ViewHolderText();
				convertView.setTag(holder);
				fillTextMessageHolder((ViewHolderText) holder, convertView);
				break;
			}
			case MESSAGE_TYPE_OTHER_IMAGE: {
				System.out.println("布局为图片");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_image_left, parent, false);
				holder = new ViewHolderImg();
				convertView.setTag(holder);
				fillImageMessageHolder((ViewHolderImg) holder, convertView);
				break;
			}
			case MESSAGE_TYPE_OTHER_AUDIO: {
				System.out.println("布局为录音");
				convertView = inflater.inflate(
						R.layout.chatting_item_msg_text_left, parent, false);
				holder = new ViewHolderText();
				convertView.setTag(holder);
				fillTextMessageHolder((ViewHolderText) holder, convertView);
				break;
			}
			default:
				break;
			}
		} else {
			System.out.println("布局不为空");
			// return convertView;
			holder = (ViewHolderBase) convertView.getTag();
		}
		final JSONObject msgJson = JSON.parseObject(moMoMsg.getMsgJson());
		final boolean isGetted = moMoMsg.isGetted();
		if (msgJson != null) {
			int msgType = msgJson.getInteger(MsgKeys.msgType);
			if (msgType == iMoMoMsgTypes.CHATING_TEXT_MSG) {
				System.out.println("消息为文字");
				handleTextMessage((ViewHolderText) holder, msgJson, parent,
						isGetted);

			} else if (msgType == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				System.out.println("消息为图片");
				handleImageMessage((ViewHolderImg) holder, msgJson, parent,
						isGetted);

			} else if (msgType == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				System.out.println("消息为录音");
				handleAudioMessage((ViewHolderText) holder, msgJson, parent,
						isGetted);
			}
		}

		return convertView;
	}

	private void fillBaseMessageholder(ViewHolderBase holder, View convertView) {
		holder.head = (ImageView) convertView.findViewById(R.id.iv_userhead);
		holder.tv_sendtime = (TextView) convertView
				.findViewById(R.id.tv_sendtime);
	}

	private void fillTextMessageHolder(ViewHolderText holder, View convertView) {
		fillBaseMessageholder(holder, convertView);
		holder.tv_chatcontent = (GifTextView) convertView
				.findViewById(R.id.tv_chatcontent);
	}

	private void fillImageMessageHolder(ViewHolderImg holder, View convertView) {
		fillBaseMessageholder(holder, convertView);
		holder.Iv_chatimage = (ImageView) convertView
				.findViewById(R.id.Iv_chatimage);
	}

	private void handleBaseMessage(ViewHolderBase holder,
			final JSONObject mItem, final boolean isGetted) {
		final String friendId = mItem.getString(MsgKeys.friendId);
		if (isGetted) {
			if (ChatActivity.isgroupMsg) {
				holder.head.setImageResource(R.drawable.imomo2);
//				int ran = (int) (Math.random() * 5);
//				switch (ran) {
//				case 1:
//					holder.head.setImageResource(R.drawable.stranger1);// 群组聊天不能看个人头像
//					break;
//				case 2:
//					holder.head.setImageResource(R.drawable.stranger2);// 群组聊天不能看个人头像
//					break;
//				case 3:
//					holder.head.setImageResource(R.drawable.stranger3);// 群组聊天不能看个人头像
//					break;
//				case 4:
//					holder.head.setImageResource(R.drawable.stranger4);// 群组聊天不能看个人头像
//					break;
//				}
			} else {
				// Log.i("--", "friendId = " + friendId);
				String headPath = StaticValues.USER_HEADPATH + friendId
						+ ".png";
				// Log.i("--", "好友头像headPath = " + headPath + "存在吗？ " +
				// FileUtils.isFileExists(headPath)) ;
				holder.head.setImageURI(Uri.fromFile(new File(
						StaticValues.USER_HEADPATH + friendId + ".png")));
			}

		} else {
			String headPath = StaticValues.USER_HEADPATH
					+ ClientManager.clientEmail + ".png";
			holder.head.setImageURI(Uri.fromFile(new File(headPath)));
		}
		holder.head.setOnClickListener(new OnClickListener() {
			Intent intent = new Intent();

			@Override
			public void onClick(View v) {
				if (isGetted) {
					if (ChatActivity.isgroupMsg) {
						Toast.makeText(context, "陌生人必须解密才能看哦", 0).show();
					} else {
						if (ClientManager.isOnline) {
							intent.setClass(context, FriendInfoActivity.class);
							intent.putExtra("friendInfo",
									MainActivity.mp1.getFriendItem(friendId));
							context.startActivity(intent);
						} else {
							Toast.makeText(context, "当前不在线，无法查看", 0).show();
						}
					}
				} else {
					intent.setClass(context, PersonalInfoActivity.class);
					context.startActivity(intent);
				}
			}
		});

		holder.tv_sendtime.setText(mItem.getString(MsgKeys.sendTime));

	}

	private void handleTextMessage(final ViewHolderText holder,
			final JSONObject mItem, final View parent, boolean isGetted) {
		handleBaseMessage(holder, mItem, isGetted);
		String msgCotent = mItem.getString(MsgKeys.msgCotent);
		holder.tv_chatcontent
				.insertGif(convertNormalStringToSpannableString(msgCotent + " "));
		holder.tv_chatcontent
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

					}
				});
	}

	private void handleImageMessage(final ViewHolderImg holder,
			final JSONObject mItem, final View parent, boolean isGetted) {
		handleBaseMessage(holder, mItem, isGetted);
		final String imagePath = mItem.getString(MsgKeys.imagePath);
		if (FileUtils.isFileExists(imagePath))
			holder.Iv_chatimage.setImageURI(Uri.fromFile(new File(imagePath)));
		else {
			Toast.makeText(context, "文件不存在或被破坏", 0).show();
		}
		holder.Iv_chatimage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog lookDialog = new Dialog(context,
						R.style.Dialog_Fullscreen);
				lookDialog.setCanceledOnTouchOutside(true); // 点击其他部位，消失
				Window window = lookDialog.getWindow();
				// 设置显示动画
				window.setWindowAnimations(R.style.dialog_animstyle);
				WindowManager.LayoutParams wl = window.getAttributes();
				lookDialog.onWindowAttributesChanged(wl);// 设置对框框动画属性
				lookDialog.setContentView(R.layout.look_image_dialog);
				ImageView Iv_lookPic = (ImageView) lookDialog
						.findViewById(R.id.Iv_lookPic);
				Iv_lookPic.setImageURI(Uri.fromFile(new File(imagePath)));
				lookDialog.show();
				Iv_lookPic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lookDialog.dismiss();
					}
				});
			}
		});
		holder.Iv_chatimage
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
					}
				});
	}

	private void handleAudioMessage(final ViewHolderText holder,
			final JSONObject mItem, final View parent, boolean isGetted) {
		handleBaseMessage(holder, mItem, isGetted);

		Log.i("--", "语音消息mItem = " + mItem);
		int voiceTime = mItem.getInteger(MsgKeys.voiceTime);
		String msgCotent = "";
		if (isGetted) {
			msgCotent = "[语音左] " + voiceTime + "″";
		} else {
			msgCotent = voiceTime + "″ [语音右]";
		}

		holder.tv_chatcontent
				.insertGif(convertNormalStringToSpannableString(msgCotent + " "));
		holder.tv_chatcontent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String voicePath = mItem.getString(MsgKeys.voicePath);
				if (voicePath != null) {
					SoundUtil.getInstance().playRecorder(context, voicePath);
				}
			}
		});
		holder.tv_chatcontent
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

					}
				});
	}

	private static class ViewHolderBase {
		ImageView head;
		TextView tv_sendtime;
	}

	/** 文本信息和语音信息 */
	private class ViewHolderText extends ViewHolderBase {
		GifTextView tv_chatcontent;// 文本消息显示区
	}

	/** 图片信息 */
	private class ViewHolderImg extends ViewHolderBase {
		ImageView Iv_chatimage;// 图片显示区
	}

	private String convertNormalStringToSpannableString(String message) {
		System.out.println("匹配前" + message);
		String hackTxt;
		if (message.startsWith("[") && message.endsWith("]")) {
			hackTxt = message + " ";
		} else {
			hackTxt = message;
		}

		Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
			String value = FaceConversionUtil.getInstace().getFaceMap()
					.get(str2);
			if (FaceConversionUtil.getInstace().getFaceMap().containsKey(str2)) {
				String faceName = context.getResources().getString(
						context.getResources().getIdentifier(value, "drawable",
								context.getPackageName()));
				System.out.println("faceName:" + faceName);
				CharSequence name = options(faceName);
				message = message.replace(str2, name);
			}

		}
		System.out.println("匹配后" + message);
		return message;
	}

	private CharSequence options(String faceName) {
		int start = faceName.lastIndexOf("/");
		CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
		return c;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

}
