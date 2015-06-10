package com.example.hellostranger.fragment_main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.clientUtils.ClientManager;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.FriendInfoActivity;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.view.BaseListView;
import com.freind.FriendBean;
import com.freind.FriendListAdapter;
import com.freind.PinyinComparator;
import com.sortlistview.CharacterParser;
import com.sortlistview.ClearEditText;
import com.sortlistview.SideBar;
import com.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.static_configs.StaticValues;

public class MainPart1 extends Fragment implements SectionIndexer {

	View view;
	private Context context;
	// private ListView sortListView;
	private BaseListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private FriendListAdapter adapter;
	private ClearEditText mClearEditText;
	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<FriendBean> FriendDataList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private TextView friendsNum;// 好友人数

	// 可能会多次调用
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_part1, container, false);
		context = getActivity();
		initViews();
		return view;
	}
	
	/**
	 * 返回一个好友的昵称
	 * @param friendId
	 * @return
	 */
	public String getFriendName(String friendId){
		String friendName = "";
		for(FriendBean bean : FriendDataList){
			if(friendId.equals(bean.getFriendId())){
				friendName = bean.getFriendName();
				break;
			}
		}
		return friendName;
	}
	
	/**
	 * 返回所有好友的Id
	 * @return
	 */
	public List<String> getFriendsIds(){
		List<String> list = new ArrayList<String>();
		if(FriendDataList.size() > 0){
			for(FriendBean bean : FriendDataList){
				list.add(bean.getFriendId());
			}
		}
		return list;
	}
	
	
	/**
	 * 返回一个好友bean
	 * @param friendId
	 * @return
	 */
	public FriendBean getFriendItem(String friendId){
		for(FriendBean bean : FriendDataList){
			if(friendId.equals(bean.getFriendId())){
				return bean;
			}
		}
		return null;
	}
	
	/**
	 * 删除一个好友
	 * @param bean
	 */
	public void DeleteAFriend(String friendId) {
		for(FriendBean bean : FriendDataList){
			if(bean.getFriendId().equals(friendId)){
				FriendDataList.remove(bean);
				FriendDataList = filledData(FriendDataList);
				Collections.sort(FriendDataList, pinyinComparator);
				adapter = new FriendListAdapter(context, FriendDataList);
				sortListView.setAdapter(adapter);
				friendsNum.setText("共有" + FriendDataList.size() + "位好友");
				break;
			}
		}
	}

	/**
	 * 判断好友是否已经在好友列表中
	 * @param id
	 * @return
	 */
	public boolean isFriendExists(String id){
		boolean isExists = false;
		if(FriendDataList.size() > 0){
			for(FriendBean afriend : FriendDataList){
				if(id.equals(afriend.getFriendId())){
					isExists = true;
					break;
				}
			}
		}
		return isExists;
	}
	
	
	/**
	 * 新增一个好友
	 * @param bean
	 */
	public void AddAFriend(FriendBean bean) {
		if(!isFriendExists(bean.getFriendId()) ){
			FriendDataList.add(bean);
			FriendDataList = filledData(FriendDataList);
			Collections.sort(FriendDataList, pinyinComparator);
			adapter = new FriendListAdapter(context, FriendDataList);
			sortListView.setAdapter(adapter);
			friendsNum.setText("共有" + FriendDataList.size() + "位好友");
		}
	}
	
	

	private void initViews() {
//		titleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
//		title = (TextView) this.view.findViewById(R.id.title_layout_catalog);
		
		tvNofriends = (TextView) this
				.view.findViewById(R.id.title_layout_no_friends);
		// 实例化汉字转拼音类s
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) view.findViewById(R.id.sidrbar);
		dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (BaseListView) view.findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendBean bean = ((FriendBean) adapter.getItem(position));
				Intent intent = new Intent(getActivity(),FriendInfoActivity.class);
				intent.putExtra("friendInfo", bean);
				startActivity(intent);
			}
		});
		
		
		FriendDataList = new ArrayList<FriendBean>();
		adapter = new FriendListAdapter(context, FriendDataList);
		
		View footer = LayoutInflater.from(context).inflate(R.layout.friendlist_footer,
				null);
		footer.setEnabled(false);
		friendsNum = (TextView) footer.findViewById(R.id.FriendsNum);
		friendsNum.setText("共有" + FriendDataList.size() + "位好友");
		sortListView.addFooterView(footer);
		sortListView.setAdapter(adapter);
		
		
//		sortListView.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				lastFirstVisibleItem = firstVisibleItem;
//			}
//		});

		mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 这个时候不需要挤压效果 就把他隐藏掉
//				titleLayout.setVisibility(View.GONE);
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	
//	// 上下文菜单监听
//		@Override
//		public boolean onContextItemSelected(MenuItem item) {
//			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
//					.getMenuInfo();
//			int position = menuInfo.position;// 需要减一 ， menuInfo.position从1开始计数
//			Toast.makeText(context, "第几个:" + position, 0).show();
//			switch (item.getItemId()) {
//			case 0:
//				FriendDataList.remove(position);
//				adapter.notifyDataSetChanged();
//				friendsNum.setText("共有" + FriendDataList.size() + "位好友");
//				break;
//			case 1:
//				break;
//			default:
//				break;
//			}
//
//			return super.onContextItemSelected(item);
//		}
	/**
	 * 根据listView Item 中好友名来重新设置item拼音标识
	 * 
	 * @param date
	 * @return
	 */
	private List<FriendBean> filledData(List<FriendBean> list) {
		List<FriendBean> mSortList = new ArrayList<FriendBean>();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			FriendBean sortModel = (FriendBean) iterator.next();

			String pinyin = characterParser.getSelling(sortModel
					.getFriendName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<FriendBean> filterDataList = new ArrayList<FriendBean>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDataList = FriendDataList;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDataList.clear();
			for (FriendBean sortModel : FriendDataList) {
				String name = sortModel.getFriendName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDataList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDataList, pinyinComparator);
		adapter.updateListView(filterDataList);
		if (filterDataList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if(FriendDataList != null && FriendDataList.size() > 0)
			return FriendDataList.get(position).getSortLetters().charAt(0);
		return 0;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < FriendDataList.size(); i++) {
			String sortStr = FriendDataList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	
}
