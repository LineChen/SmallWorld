package com.example.hellostranger.fragment_main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clientUtils.ClientManager;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.InvitationActivity;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.ChatActivity;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.adapter.ChatListViewAdapter;
import com.example.hellostranger.adapter.ChatListViewAdapter.RemoveListener;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.view.SlideCutListView;
import com.example.hellostranger.view.SlideCutListView.OnItemClickListener;
import com.example.hellostranger.view.SlideCutListView.OnItemLongClickListener;
import com.example.hellostranger.view.SlideCutListView.OnRefreshListener;
import com.msg_relative.MsgKeys;

public class MainPart0 extends Fragment implements RemoveListener,
		OnItemLongClickListener, OnItemClickListener {

	View view;
	View connect_down_header;// 和服务器断开时，显示提示header
	SlideCutListView slideCutListView;
	ChatListViewAdapter adapter;
	private List<ChatInfoEntity> laticeChatList;

	static String chatingId = "";// 当前正在聊天的好友Id

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_part0, container, false);
		connect_down_header = inflater.inflate(R.layout.connect_down_header,
				null);
		init();
		initLaticeChatList();// 初始化最近联系人
		// updateHeader();//判断是否连接服务器了，
		return view;
	}

	/**
	 * 设置正在聊天的好友Id
	 * 
	 * @param chatId
	 */
	public void setChatingId(String chatId) {
		chatingId = chatId;
	}

	/**
	 * 得到正在聊天的好友Id
	 * 
	 * @return
	 */
	public String getChatingId() {
		return chatingId;
	}

	/**
	 * 初始化聊天好友
	 */
	public void initLaticeChatList() {
		iMoMoDataBase dataBase = new iMoMoDataBase(getActivity());
		List<ChatInfoEntity> list = dataBase.getLaticeChatList();
		Log.i("--", "最近联系人个数=================list.size = " + list.size());
		laticeChatList.addAll(list);
		adapter.notifyDataSetChanged();
		for (ChatInfoEntity entity : laticeChatList) {
			Log.i("--", "从数据库得到 = " + entity.toString());
		}
	}

	/**
	 * 判断一个好友是否在最近联系人中
	 */
	public boolean isExistsInLatice(String friendId) {
		boolean isExists = false;
		for (ChatInfoEntity entity : laticeChatList) {
			if (friendId.equals(entity.getFriendId())) {
				isExists = true;
				break;
			}
		}
		return isExists;
	}

	// /**
	// * 添加一个好友到最近 联系人,之前已经聊过,只用从数据库取出来显示即可
	// *
	// * @param friendId
	// */
	// public void AddLaticeChatItem(String friendId) {
	// iMoMoDataBase db = new iMoMoDataBase(getActivity());
	//
	// ChatInfoEntity entity = (ChatInfoEntity) db.getLaticeChatList();//wrong
	// laticeChatList.add(entity);
	// adapter.notifyDataSetChanged();
	// }

	/**
	 * 删除一个最近好友
	 * 
	 * @param friendId
	 */
	public void deleteLaticeChatItem(String friendId) {
		for (ChatInfoEntity entity : laticeChatList) {
			if (friendId.equals(entity.getFriendId())) {
				laticeChatList.remove(entity);
				adapter.notifyDataSetChanged();
				iMoMoDataBase dataBase = new iMoMoDataBase(getActivity());
				if (dataBase.isExistsLatice(friendId)) {
					dataBase.deleteLaticeItem(friendId);
				}
				break;
			}
		}
	}

	/**
	 * 清空最近联系人
	 */
	public void ClearLatice() {
		if (laticeChatList.size() > 0) {
			iMoMoDataBase dataBase = new iMoMoDataBase(getActivity());
			dataBase.clearLatice();
			laticeChatList.clear();
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 添加一个好友到最近 联系人,之前未聊过，更新数据库
	 * 
	 * @param friendId
	 */
	public void AddLaticeChatItem(ChatInfoEntity entity) {
		iMoMoDataBase db = new iMoMoDataBase(getActivity());
		db.insertLaticeChat(entity);// 更新数据库
		List<ChatInfoEntity> temp = new ArrayList<ChatInfoEntity>();
		temp.add(entity);
		laticeChatList.addAll(0, temp);// 先来的消息插入到最上面
		adapter.notifyDataSetChanged();
		Log.i("--", "add entity = " + entity.toString());
	}

	/**
	 * 把一个好友的未读消息置为0
	 * 
	 * @param friendId
	 */
	public void ResetNotReadMsg(String friendId) {
		for (ChatInfoEntity entity : laticeChatList) {
			if (friendId.equals(entity.getFriendId())) {
				if (entity.getMsg_num() > 0) {
					iMoMoDataBase db = new iMoMoDataBase(getActivity());
					entity.setMsg_num(0);
					adapter.notifyDataSetChanged();
					if (db.isExistsLatice(friendId)) {
						db.updateLaticeChat(entity);
					}
					// 把该好友数据库中的聊天记录标识置为已读
					if (db.isTableExists("msg" + ClientManager.clientId + "_"
							+ friendId)) {
						db.updateRead(friendId);
					}
					break;
				}
			}
		}
	}

	/**
	 * 更新一个最近聊天Item,,,已经存在于最近联系人列表中
	 * 
	 * @param friendId
	 * @param time
	 *            发送时间
	 * @param content
	 *            内容
	 * @param isZero
	 *            当不是和该好友正在聊天时，未读信息条数要自加1， 如果是刚从chatactivity跳出来，未读消息是0
	 */
	public void updateLaticeItem(String friendId, String time, String content,
			boolean isZero) {
		for (ChatInfoEntity entity : laticeChatList) {
			if (friendId.equals(entity.getFriendId())) {
				entity.setChatCreatTime(time);
				if (isZero) {
					entity.setMsg_num(0);
				} else {
					entity.setMsg_num(entity.getMsg_num() + 1);
				}
				entity.setChatContent(content);
				List<ChatInfoEntity> temp = new ArrayList<ChatInfoEntity>();
				temp.add(entity);
				laticeChatList.remove(entity);
				laticeChatList.addAll(0, temp);// 先来的消息插入到最上面
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	// /**
	// * 添加断线提示header , 会和下拉刷新的header冲突
	// */
	// public void updateHeader(){
	// if(ClientManager.isOnline){
	// slideCutListView.removeHeaderView(connect_down_header);
	// } else {
	// Log.i("--","Mp0--添加header提示");
	// slideCutListView.addHeaderView(connect_down_header);//setAdapter之前添加，否则之后添加不上
	// }
	// }

	private void init() {
		System.out.println("开始初始化slideCutListView");
		slideCutListView = (SlideCutListView) view
				.findViewById(R.id.fra_p0_listview);
		System.out.println(" 初始化slideCutListView");
		laticeChatList = new ArrayList<ChatInfoEntity>();
		// initData();
		System.out.println("开始初始化Adapter");
		adapter = new ChatListViewAdapter(getActivity(), laticeChatList);
		adapter.setListView(slideCutListView);
		adapter.setRemoveListener(this);
		System.out.println("为listview设置Adapter");

		slideCutListView.addHeaderView(connect_down_header);// setAdapter之前添加，否则之后添加不上
		slideCutListView.setAdapter(adapter);
		slideCutListView.removeHeaderView(connect_down_header);
		slideCutListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					// 运行在UI线程中，在调用doInBackground()之前执行
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						slideCutListView.onRefreshComplete();
						// updateHeader();
					}

					// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
							// 重新登录
							if (!ClientManager.isOnline) {
								MainActivity.myBinder.ConnectServer();
								// MainActivity.myBinder.SuperLogin();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						return null;
					}

				}.execute();
			}
		});

		slideCutListView.setOnItemLongClickListener(this);
		slideCutListView.setOnItemClickListener(this);
		System.out.println("完成MainPart0的初始化");
	}

	@Override
	public void removeItem() {
		// System.out.println("开始删除laticeChatList中数据：" + laticeChatList.size());
		int position = slideCutListView.getslidePosition() - 1;
		deleteLaticeChatItem(laticeChatList.get(position).getFriendId());// 删除一项
		// laticeChatList.remove(position);
		// System.out.println("删除laticeChatList中数据");
		// adapter.notifyDataSetChanged();
		slideCutListView.oldScrollClose();
	}

	@Override
	public void onItemLongClick() {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("删除最近联系人").setMessage("确认删除用户？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (laticeChatList.size() > 0) {
					laticeChatList.remove(slideCutListView.getslidePosition() - 1);
					System.out.println("删除laticeChatList中数据");
					adapter.notifyDataSetChanged();
					slideCutListView.oldScrollClose();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onItemClick() {
		if (laticeChatList.size() > 0) {
			ChatInfoEntity entity = laticeChatList.get(slideCutListView
					.getslidePosition() - 1);

			Intent intent = new Intent();
			if (entity.getMsgtype() == 0) {
				intent.setClass(getActivity(), ChatActivity.class);
				intent.putExtra("friendName", entity.getFriendName());
				intent.putExtra("friendId", entity.getFriendId());

			} else if (entity.getMsgtype() == 1) {
				// 群组邀请
				intent.setClass(getActivity(), InvitationActivity.class);
			} else if (entity.getMsgtype() == 2) {
				intent.setClass(getActivity(), ChatActivity.class);
				intent.putExtra("friendName", entity.getFriendName());
				intent.putExtra("friendId", entity.getFriendId());
				intent.putExtra("isgroupMsg", true);// 群组聊天 标识
			}
			startActivity(intent);

			/** 把所有未读消息置为已读 */
			ResetNotReadMsg(entity.getFriendId());
		}
	}

}
