package com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hellostranger.R;

/**
 * ����ѡ��������
 * @author Administrator
 *
 */
public class MoreSelectAdapter extends BaseAdapter {
	private static final int PHOTO_REQUEST_CAMERA = 1;// ����
	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	private static final int PHOTO_REQUEST_CUT = 3;// ���ý��
	private String PHOTO_FILE_NAME = "";
	private Context context;
	private String[] selctions = { "ͼƬ", "����", "λ��", "����", "����ʶ��", "��Ƶ" };
	private int[] selection_icons = { R.drawable.more_selction_album,
			R.drawable.more_selction_camara, R.drawable.more_selction_location,
			R.drawable.more_selction_notify, R.drawable.chatview_recding,
			R.drawable.more_selction_vidio };

	public MoreSelectAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return selctions.length;
	}

	@Override
	public Object getItem(int position) {
		return selctions[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder = null;
		final int select_position = position;
		if (convertView == null) {
			holder = new viewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.chatview_more_select_item,
					parent, false);
			holder.sele_icon = (ImageView) convertView
					.findViewById(R.id.selec_icon);
			holder.selec_name = (TextView) convertView
					.findViewById(R.id.selec_name);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}

		holder.sele_icon.setImageResource(selection_icons[position]);
		holder.selec_name.setText(selctions[position]);
//		holder.sele_icon.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switch (select_position) {
//				case 0:
//					Toast.makeText(context, "ͼƬ", 0).show();
//					break;
//				case 1:
////					Toast.makeText(context, "����", 0).show();
//					
//					
//					break;
//				case 2:
//					Toast.makeText(context, "λ��", 0).show();
//					break;
//				case 3:
//					Toast.makeText(context, "����", 0).show();
//					break;
//				case 4:
//					Toast.makeText(context, "�绰", 0).show();
//					break;
//				case 5:
//					Toast.makeText(context, "��Ƶ", 0).show();
//					break;
//				}
//			}
//		});

		return convertView;
	}

	private class viewHolder {
		public ImageView sele_icon;
		public TextView selec_name;
	}
}