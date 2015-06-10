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
	View connect_down_header;// �ͷ������Ͽ�ʱ����ʾ��ʾheader
	SlideCutListView slideCutListView;
	ChatListViewAdapter adapter;
	private List<ChatInfoEntity> laticeChatList;

	static String chatingId = "";// ��ǰ��������ĺ���Id

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_part0, container, false);
		connect_down_header = inflater.inflate(R.layout.connect_down_header,
				null);
		init();
		initLaticeChatList();// ��ʼ�������ϵ��
		// updateHeader();//�ж��Ƿ����ӷ������ˣ�
		return view;
	}

	/**
	 * ������������ĺ���Id
	 * 
	 * @param chatId
	 */
	public void setChatingId(String chatId) {
		chatingId = chatId;
	}

	/**
	 * �õ���������ĺ���Id
	 * 
	 * @return
	 */
	public String getChatingId() {
		return chatingId;
	}

	/**
	 * ��ʼ���������
	 */
	public void initLaticeChatList() {
		iMoMoDataBase dataBase = new iMoMoDataBase(getActivity());
		List<ChatInfoEntity> list = dataBase.getLaticeChatList();
		Log.i("--", "�����ϵ�˸���=================list.size = " + list.size());
		laticeChatList.addAll(list);
		adapter.notifyDataSetChanged();
		for (ChatInfoEntity entity : laticeChatList) {
			Log.i("--", "�����ݿ�õ� = " + entity.toString());
		}
	}

	/**
	 * �ж�һ�������Ƿ��������ϵ����
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
	// * ���һ�����ѵ���� ��ϵ��,֮ǰ�Ѿ��Ĺ�,ֻ�ô����ݿ�ȡ������ʾ����
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
	 * ɾ��һ���������
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
	 * ��������ϵ��
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
	 * ���һ�����ѵ���� ��ϵ��,֮ǰδ�Ĺ����������ݿ�
	 * 
	 * @param friendId
	 */
	public void AddLaticeChatItem(ChatInfoEntity entity) {
		iMoMoDataBase db = new iMoMoDataBase(getActivity());
		db.insertLaticeChat(entity);// �������ݿ�
		List<ChatInfoEntity> temp = new ArrayList<ChatInfoEntity>();
		temp.add(entity);
		laticeChatList.addAll(0, temp);// ��������Ϣ���뵽������
		adapter.notifyDataSetChanged();
		Log.i("--", "add entity = " + entity.toString());
	}

	/**
	 * ��һ�����ѵ�δ����Ϣ��Ϊ0
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
					// �Ѹú������ݿ��е������¼��ʶ��Ϊ�Ѷ�
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
	 * ����һ���������Item,,,�Ѿ������������ϵ���б���
	 * 
	 * @param friendId
	 * @param time
	 *            ����ʱ��
	 * @param content
	 *            ����
	 * @param isZero
	 *            �����Ǻ͸ú�����������ʱ��δ����Ϣ����Ҫ�Լ�1�� ����Ǹմ�chatactivity��������δ����Ϣ��0
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
				laticeChatList.addAll(0, temp);// ��������Ϣ���뵽������
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	// /**
	// * ��Ӷ�����ʾheader , �������ˢ�µ�header��ͻ
	// */
	// public void updateHeader(){
	// if(ClientManager.isOnline){
	// slideCutListView.removeHeaderView(connect_down_header);
	// } else {
	// Log.i("--","Mp0--���header��ʾ");
	// slideCutListView.addHeaderView(connect_down_header);//setAdapter֮ǰ��ӣ�����֮����Ӳ���
	// }
	// }

	private void init() {
		System.out.println("��ʼ��ʼ��slideCutListView");
		slideCutListView = (SlideCutListView) view
				.findViewById(R.id.fra_p0_listview);
		System.out.println(" ��ʼ��slideCutListView");
		laticeChatList = new ArrayList<ChatInfoEntity>();
		// initData();
		System.out.println("��ʼ��ʼ��Adapter");
		adapter = new ChatListViewAdapter(getActivity(), laticeChatList);
		adapter.setListView(slideCutListView);
		adapter.setRemoveListener(this);
		System.out.println("Ϊlistview����Adapter");

		slideCutListView.addHeaderView(connect_down_header);// setAdapter֮ǰ��ӣ�����֮����Ӳ���
		slideCutListView.setAdapter(adapter);
		slideCutListView.removeHeaderView(connect_down_header);
		slideCutListView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					// ������UI�߳��У��ڵ���doInBackground()֮ǰִ��
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						slideCutListView.onRefreshComplete();
						// updateHeader();
					}

					// ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ���
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
							// ���µ�¼
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
		System.out.println("���MainPart0�ĳ�ʼ��");
	}

	@Override
	public void removeItem() {
		// System.out.println("��ʼɾ��laticeChatList�����ݣ�" + laticeChatList.size());
		int position = slideCutListView.getslidePosition() - 1;
		deleteLaticeChatItem(laticeChatList.get(position).getFriendId());// ɾ��һ��
		// laticeChatList.remove(position);
		// System.out.println("ɾ��laticeChatList������");
		// adapter.notifyDataSetChanged();
		slideCutListView.oldScrollClose();
	}

	@Override
	public void onItemLongClick() {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("ɾ�������ϵ��").setMessage("ȷ��ɾ���û���");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (laticeChatList.size() > 0) {
					laticeChatList.remove(slideCutListView.getslidePosition() - 1);
					System.out.println("ɾ��laticeChatList������");
					adapter.notifyDataSetChanged();
					slideCutListView.oldScrollClose();
				}
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
				// Ⱥ������
				intent.setClass(getActivity(), InvitationActivity.class);
			} else if (entity.getMsgtype() == 2) {
				intent.setClass(getActivity(), ChatActivity.class);
				intent.putExtra("friendName", entity.getFriendName());
				intent.putExtra("friendId", entity.getFriendId());
				intent.putExtra("isgroupMsg", true);// Ⱥ������ ��ʶ
			}
			startActivity(intent);

			/** ������δ����Ϣ��Ϊ�Ѷ� */
			ResetNotReadMsg(entity.getFriendId());
		}
	}

}
