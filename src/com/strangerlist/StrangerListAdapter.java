package com.strangerlist;

import java.util.List;

import com.example.hellostranger.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StrangerListAdapter extends BaseAdapter {

	private Context context;
	private List<StrangerBean> list;

	public StrangerListAdapter(Context context, List<StrangerBean> list) {
		this.context = context;
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
		viewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.stranger_item, parent, false);
			holder = new viewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.friend_name);
			holder.tvLocation = (TextView) convertView.findViewById(R.id.friend_loc);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		holder.tvName.setText(list.get(position).strangerName);
		holder.tvLocation.setText(list.get(position).strangerLoc);
		return convertView;
	}

	private class viewHolder {
		TextView tvName;
		TextView tvLocation;
	}

}
