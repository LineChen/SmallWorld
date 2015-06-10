package com.freind;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.hellostranger.R;

public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
	final static class ViewHolder {
		ImageView friend_headpic;
		TextView friend_name;
		TextView tvLetter;
	}

	private List<FriendBean> list = null;
	private Context mContext;

	public FriendListAdapter(Context mContext, List<FriendBean> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final FriendBean aFriend = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.friend_item, null);
			viewHolder.friend_headpic = (ImageView) convertView
					.findViewById(R.id.friend_headpic);
			viewHolder.friend_name = (TextView) convertView
					.findViewById(R.id.friend_name);
			viewHolder.tvLetter = (TextView) convertView
					.findViewById(R.id.friend_tag);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
//		Log.i("--", "viewHolder = "+(viewHolder== null));
		// �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(aFriend.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.friend_name.setText(aFriend.getFriendName());
		viewHolder.friend_headpic.setImageURI(Uri.fromFile(new File(aFriend.getFriendHeadPath())));
		return convertView;
	}

	/**
	 * ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<FriendBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
	 */
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * ��ȡӢ�ĵ�����ĸ����Ӣ����ĸ��#���档
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
