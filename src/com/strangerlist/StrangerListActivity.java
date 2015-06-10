package com.strangerlist;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.clientUtils.ClientManager;
import com.example.hellostranger.GameActivity;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.MainActivity;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 陌生人列表，分批加载
 * 
 * @author Administrator
 * 
 */
public class StrangerListActivity extends Activity {
	static ListView StrangerLv;
	private static List<StrangerBean> data = new ArrayList<StrangerBean>();
	static StrangerListAdapter adapter;
	static View footer;
	private static boolean loadfinish = true;
	Context context;

	ImageButton btn_back;//返回键
	
	static int disRange = 2;
	
	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == iMoMoMsgTypes.STRANGERS_LIST_MORE){
				data.addAll((List<StrangerBean>) msg.obj);
				adapter.notifyDataSetChanged();// 告诉ListView数据已经发生改变，要求ListView更新界面显示
				if (StrangerLv.getFooterViewsCount() > 0){
					StrangerLv.removeFooterView(footer);
					disRange++;
				}
				loadfinish = true;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_strangerlist);
		context = this;
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		StrangerLv = (ListView) findViewById(R.id.lv_strList);
		StrangerLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String strangerId = data.get(position).strangerId;
				Intent intent = new Intent(context,
						GameActivity.class);
				intent.putExtra("strangerId", strangerId);
				startActivity(intent);
			}
			
		});
		
		
		StrangerLv.setOnScrollListener(new ScrollListener());
		footer = getLayoutInflater().inflate(R.layout.strangerlist_footer, null);
		
		MainActivity.myBinder.getStrangerListMore(disRange);
		
		adapter = new StrangerListAdapter(context, data);
		StrangerLv.addFooterView(footer);// 添加页脚(放在ListView最后)
		StrangerLv.setAdapter(adapter);
		StrangerLv.removeFooterView(footer);
	}

	private final class ScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Log.i("MainActivity", "onScrollStateChanged(scrollState="
					+ scrollState + ")");
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			int lastItemid = StrangerLv.getLastVisiblePosition();// 获取当前屏幕最后Item的ID
			if ((lastItemid + 1) == totalItemCount) {// 达到数据的最后一条记录
				if (totalItemCount > 0) {
					if (loadfinish) {
						loadfinish = false;
						StrangerLv.addFooterView(footer);
						MainActivity.myBinder.getStrangerListMore(disRange);
					}
				}
			}
		}
	}
}




