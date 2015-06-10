package com.example.hellostranger.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.hellostranger.R;

public class SlideCutListView extends ListView implements OnScrollListener {

	Context context;
	/** 上次的View是否被重新按下 */
	private boolean isORP;
	private int direction;

	/** 当前滑动的ListView　position */
	private int slidePosition;
	/** 手指按下Y的坐标 */
	private int downY;
	/** 手指按下X的坐标 */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** 删除按键的宽度 */
	private int maxscrooldis = 300;
	/** 屏幕宽度 */
	private int screenWidth;
	/** ListView的item */
	private View itemView;
	/** 滑动类 */
	private Scroller scroller;

	private static final int SNAP_VELOCITY = 600;
	/** 速度追踪对象 */
	private VelocityTracker velocityTracker;
	/** 是否响应滑动，默认为不响应 */
	private boolean isSlide = false;
	/** 认为是用户滑动的最小距离 */
	private int mTouchSlop;
	/** 关闭按钮打开 */
	private boolean isCloseBtnOpen;
	/** 当前处于打开状态的item */
	private View openitemView;

	// ////////////////////下拉刷新相关变量////////////////////////////////////
	/** 是否允许下拉刷新 */
	private boolean isDown;
	private boolean isRefreshable;
	// private boolean canRefreshListen;
	private int firstItemIndex;
	private int state;
	private final static int RATIO = 2;
	private LayoutInflater inflater;
	private LinearLayout headView;
	private LinearLayout fotterView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private boolean isRecored;// 用于保证startYH的值在一个完整的touch事件中只被记录一次
	private boolean isRecoredUD;
	private int headContentWidth;
	private int headContentHeight;
	private int fotterContentHeight;
	private int startYH;
	private int startYF;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private OnItemLongClickListener itemLongClickListener = null;
	private OnItemClickListener itemClickListener = null;
	private boolean canClick;
	private boolean isCurrentPosition;
	private int mTotalItemCount;
	// //////////////////////////////////////////////////////////////////////
	// ////////////////重写长按事件监听器//////////////////////////
	private Runnable mLongPressRunnable;
	private Runnable mLongNorEvent;
	private int LONG_PRESS_TIME = 800;
	private int LONG_NOR_PRESS = 200;

	// ///////////////////////////////////////////////////////////////
	private int bg_nor;
	private int bg_press;

	public SlideCutListView(Context context) {
		super(context, null);
		this.context = context;
		init();
	}

	public SlideCutListView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		this.context = context;
		init();
	}

	public SlideCutListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init() {
		System.out.println("开始初始化滑动刷新相关变量");
		screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		openitemView = null;
		setCacheColorHint(R.color.transparent);
		inflater = LayoutInflater.from(context);
		System.out.println("完成初始化0");
		if (inflater == null) {
			System.out.println("mInflater为空");

		}
		headView = (LinearLayout) inflater.inflate(R.layout.chat_listview_head,
				null);
		fotterView = (LinearLayout) inflater.inflate(
				R.layout.chat_listview_footer, null);

		System.out.println("完成初始化1");
		arrowImageView = (ImageView) headView
				.findViewById(R.id.chat_listview_head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		System.out.println("完成初始化2");
		progressBar = (ProgressBar) headView
				.findViewById(R.id.chat_listview_head_progressbar);
		System.out.println("完成初始化3");
		tipsTextview = (TextView) headView
				.findViewById(R.id.chat_listview_head_tipsTextView);
		System.out.println("完成初始化4");
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.chat_listview_head_lastUpdatedTextView);
		System.out.println("完成初始化5");

		measureView(headView);
		measureView(fotterView);
		System.out.println("完成初始化6");
		headContentHeight = headView.getMeasuredHeight();
		fotterContentHeight = fotterView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		// 刷新view
		headView.invalidate();
		fotterView.setPadding(0, -1 * fotterContentHeight, 0, 0);
		fotterView.invalidate();
		Log.v("this is", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		System.out.println("完成初始化7");
		// 添加头文件不被 selected
		addHeaderView(headView, null, false);
		addFooterView(fotterView);
		setOnScrollListener(this);
		System.out.println("完成初始化8");

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);
		System.out.println("完成初始化9");

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		System.out.println("完成初始化10");

		state = ListState.DONE;
		isRefreshable = false;
		isRecored = false;
		isRecoredUD = false;
		isCurrentPosition = false;
		mLongPressRunnable = new Runnable() {

			@Override
			public void run() {
				canClick = false;
				System.out.println("---------------->进入长按操作");
				if(itemView != null)
					itemView.setBackgroundResource(bg_nor);
				if (!isCloseBtnOpen)
					if (itemLongClickListener != null)
						itemLongClickListener.onItemLongClick();
			}
		};
		mLongNorEvent = new Runnable() {

			@Override
			public void run() {

				if (isCurrentPosition) {
					if (itemView != null) {
						itemView.setBackgroundResource(bg_nor);
						removeCallbacks(mLongPressRunnable);
					}
				}
				System.out.println("长时间无响应，结束长按计时");
			}
		};
		canClick = false;
		bg_nor = R.drawable.button;
		bg_press = R.drawable.button_pressed;
		setScrollbarFadingEnabled(true);
		System.out.println("结束初始化滑动刷新相关变量");
	}

	@Override
	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisiableItem;
		mTotalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	/**
	 * 分发事件，主要做的是判断点击的是哪个item, 以及通过postDelayed来设置响应左右滑动事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		System.out.println("进入ListView的dispatchTouchEvent");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			addVelocityTracker(event);
			if (!scroller.isFinished()) {
				System.out.println("scroller滚动还没有结束，我们直接返回");
				return super.dispatchTouchEvent(event);
			}
			System.out
					.println("进入ListView的dispatchTouchEvent  MotionEvent.ACTION_DOWN:");
			oldDownX = (int) event.getX();
			oldDownY = (int) event.getY();
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);
			System.out.println("slidePosition：" + slidePosition);

			// 无效的position, 不做任何处理
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			postDelayed(mLongPressRunnable, LONG_PRESS_TIME);
			postDelayed(mLongNorEvent, LONG_NOR_PRESS);
			canClick = true;
			System.out.println("开始长按计时");
			isCurrentPosition = false;
			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("点击位置为左侧边");
			}

			// 获取我们点击的item view
			if (slidePosition != AdapterView.INVALID_POSITION ) {
				itemView = getChildAt(slidePosition - getFirstVisiblePosition());
				maxscrooldis = ((ImageButton) itemView
						.findViewById(R.id.chat_listview_item_btn_del)).getWidth();
				if (!itemView.equals(this.openitemView) && this.isCloseBtnOpen) {
					oldScrollClose();
				}
				if (itemView.equals(this.openitemView)
						&& event.getX() < screenWidth / 6)
					oldScrollClose();
				isORP = false;
				if (itemView.equals(this.openitemView) && this.isCloseBtnOpen)
					isORP = true;
				if (!isORP)
					itemView.setBackgroundResource(bg_press);
			}
			
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out
					.println("进入ListView的dispatchTouchEvent  MotionEvent.ACTION_MOVE:");
			System.out.println("数据如下：" + getScrollVelocityX() + ";"
					+ getScrollVelocityY() + ";" + SNAP_VELOCITY + ";"
					+ oldDownX + ";" + oldDownY + ";" + event.getX() + ";"
					+ event.getY());
			direction = Direction.BULL;

			removeCallbacks(mLongNorEvent);
			postDelayed(mLongNorEvent, LONG_NOR_PRESS);
			System.out.println("重新开始长时间无响应计时");

			if (slidePosition != AdapterView.INVALID_POSITION) {

				if (getScrollVelocityX() < -SNAP_VELOCITY
						|| ((event.getX() - oldDownX) < -mTouchSlop && Math
								.abs(event.getY() - oldDownY) < mTouchSlop)) {
					direction = Direction.L;
					canClick = false;
					if(itemView != null)
						itemView.setBackgroundResource(bg_nor);
					removeCallbacks(mLongPressRunnable);
					System.out.println("结束长按计时Direction.L");
				}

				if (getScrollVelocityX() > SNAP_VELOCITY
						|| ((event.getX() - oldDownX) > mTouchSlop && Math
								.abs(event.getY() - oldDownY) < mTouchSlop)) {
					direction = Direction.R;
					canClick = false;
					if (itemView != null) {
						itemView.setBackgroundResource(bg_nor);
						removeCallbacks(mLongPressRunnable);
					}
					System.out.println("结束长按计时Direction.R");
				}
			}

			if (Math.abs(getScrollVelocityY()) > SNAP_VELOCITY
					|| (Math.abs(event.getY() - oldDownY) > mTouchSlop && Math
							.abs(event.getX() - oldDownX) < mTouchSlop)) {
				direction = Direction.UP;
				canClick = false;
				if (slidePosition != AdapterView.INVALID_POSITION && itemView != null) {
					itemView.setBackgroundResource(bg_nor);
				}
				removeCallbacks(mLongPressRunnable);
				System.out.println("结束长按计时:" + Math.abs(getScrollVelocityY())
						+ ";" + Math.abs(event.getY() - oldDownY) + ";"
						+ Math.abs(event.getX() - oldDownX));
			}

			if (state != ListState.REFRESHING) {
				if ((direction == Direction.L || direction == Direction.R)
						&& isDown == false
						&& slidePosition != AdapterView.INVALID_POSITION && itemView != null) {
					itemView.setPressed(false);
					isSlide = true;
				}
				if (direction == Direction.UP && isSlide == false
						&& isRefreshable && !isORP) {
					isDown = true;
				}
				if (direction == Direction.UP && isORP) {
					oldScrollClose();
				}
			}

			break;
		}
		case MotionEvent.ACTION_UP:
			System.out
					.println("进入ListView的dispatchTouchEvent  MotionEvent.ACTION_UP:"
							+ canClick);
			recycleVelocityTracker();
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			if (isORP && oldDownX > screenWidth - maxscrooldis)
				canClick = false;
			if (canClick) {
				System.out.println("canClick");
				if (isORP) {
					oldScrollClose();
					System.out.println("删除键打开，不能进入聊天，先关闭");
				} else {
					System.out.println("执行进入聊天");
					itemClickListener.onItemClick();
				}
			}
			if(itemView != null)
				itemView.setBackgroundResource(bg_nor);
			removeCallbacks(mLongNorEvent);
			removeCallbacks(mLongPressRunnable);
			canClick = false;
			System.out.println("结束长按计时");
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	/**
	 * 处理我们拖动ListView item的逻辑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (isSlide) {
			System.out.println("进入onTouchEvent  滑动删除");
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				int deltaX = downX - x;
				int movedis = oldDownX - x;
				downX = x;
				// 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
				System.out.println("触发listview   item滑动");
				int loc;
				loc = itemView.getScrollX();
				System.out.println("topView.getScrollX():"
						+ itemView.getScrollX() + "  maxscrooldis:"
						+ maxscrooldis);
				System.out.println("movedis" + ";" + maxscrooldis + ";"
						+ deltaX);
				if ((loc + deltaX) <= 0) {
					deltaX = -loc;
				}
				if ((loc + deltaX) >= maxscrooldis) {
					deltaX = maxscrooldis - loc;
				}
				if (loc >= 0 && loc <= maxscrooldis) {
					System.out.println("item开始滑动，deltaX：" + deltaX);
					// itemView.setPressed(false);
					itemView.scrollBy(deltaX, 0);
				}

				break;
			case MotionEvent.ACTION_UP:
				int velocityX = getScrollVelocityX();
				// itemView.setPressed(false);
				// System.out.println("itemView.setPressed(false);");
				if (velocityX < -SNAP_VELOCITY) {
					scrollOpen();
				} else if (velocityX > SNAP_VELOCITY) {
					scrollClose();
				} else {
					scrollByDistanceX();
				}

				recycleVelocityTracker();
				// 手指离开的时候就不响应左右滚动
				isSlide = false;
				break;
			}

			return true;// 拖动的时候ListView不滚动
		}

		if (isDown) {
			System.out.println("进入下拉刷新触摸监听器");
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("firstItemIndex：" + firstItemIndex);
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startYH = (int) ev.getY();
					Log.v("this is", "在down时候记录当前位置");
				}
				if (getLastVisiblePosition() == mTotalItemCount - 1
						&& !isRecoredUD) {
					isRecoredUD = true;
					startYF = (int) ev.getY();
					Log.v("this is", "在down时候记录当前位置");
				}
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startYH = tempY;
					Log.v("this is", "在move时候记录下位置");
				}
				if (getLastVisiblePosition() == mTotalItemCount - 1
						&& !isRecoredUD) {
					isRecoredUD = true;
					startYF = tempY;
					Log.v("this is", "在down时候记录当前位置");
				}
				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				if (state != ListState.REFRESHING && isRecored
						&& state != ListState.LOADING) {

					// 可以松手去刷新了
					if (state == ListState.RELEASE_To_REFRESH) {
						setSelection(0);
						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startYH) / RATIO < headContentHeight)
								&& (tempY - startYH) > 0) {
							state = ListState.PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.v("this is", "由松开刷新状态转变到下拉刷新状态");

						}
						// 一下子推到顶了
						else if (tempY - startYH <= 0) {
							state = ListState.DONE;
							changeHeaderViewByState();
							Log.v("this is", "由松开刷新状态转变到done状态");

						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == ListState.PULL_To_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startYH) / RATIO >= headContentHeight) {
							state = ListState.RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							Log.v("this is", "由done或者下拉刷新状态转变到松开刷新");

						}
						// 上推到顶了
						else if (tempY - startYH <= 0) {
							state = ListState.DONE;
							changeHeaderViewByState();
							Log.v("this is", "由DOne或者下拉刷新状态转变到done状态");

						}
					}
					// done状态下

					if (state == ListState.DONE) {
						if (tempY - startYH > 0) {
							state = ListState.PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的size

					if (state == ListState.PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startYH) / RATIO, 0, 0);
					}
					// 更新headView的paddingTop

					if (state == ListState.RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startYH) / RATIO
								- headContentHeight, 0, 0);
					}
				}
				if (isRecoredUD) {
					fotterView.setPadding(0, (-tempY + startYF) / RATIO
							- headContentHeight, 0, 0);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != ListState.REFRESHING && state != ListState.LOADING) {
					if (state == ListState.DONE) {
					}
					if (state == ListState.PULL_To_REFRESH) {
						state = ListState.DONE;
						changeHeaderViewByState();
						Log.v("this is", "由下拉刷新状态，到done状态");
					}
					if (state == ListState.RELEASE_To_REFRESH) {
						state = ListState.REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						Log.v("this is", "由松开刷新状态，到done状态");
					}
				}
				fotterView.setPadding(0, -headContentHeight, 0, 0);
				isDown = false;
				isRecored = false;
				isRecoredUD = false;
				isBack = false;
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 滑动，显示删除按键
	 */
	private void scrollOpen() {
		final int delta = (maxscrooldis - itemView.getScrollX());
		// System.out.println("最终滑动相关参数"+ maxscrooldis + "	+ " + screenWidth +
		// "+	" + itemView.getScrollX());
		// System.out.println("delta:"+delta);
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		this.isCloseBtnOpen = true;
		this.openitemView = itemView;
		postInvalidate(); // 刷新itemView
	}

	private void scrollClose() {
		this.isCloseBtnOpen = false;
		if (itemView != null)
			itemView.scrollTo(0, 0);
	}

	public void oldScrollClose() {
		this.isCloseBtnOpen = false;
		if (openitemView != null)
			this.openitemView.scrollTo(0, 0);
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (scroller.computeScrollOffset()) {
			// System.out.println("滑动结束");
			// 让ListView item根据当前的滚动偏移量进行滚动
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			// itemView.setPressed(false);
			postInvalidate();

			// 滚动动画结束的时候调用回调接口
			if (scroller.isFinished()) {
				itemView.setPressed(false);
				System.out.println("itemView.setPressed(false);"
						+ itemView.isPressed());
				itemView.postInvalidate();
			}
		}
	}

	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX() {
		// 如果向左滚动的距离大于按键最大宽度的三分之一，就让其删除
		if (itemView.getScrollX() >= maxscrooldis / 3) {
			scrollOpen();
		} else {
			// 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
			scrollClose();
		}

	}

	/**
	 * 取得当前选中item位置
	 */
	public int getslidePosition() {
		System.out.println("最终返回要操作的slidePosition：" + slidePosition);
		return slidePosition;
	}

	/**
	 * 设置关闭按钮状态
	 */
	public void setCloseBtnState(boolean tf) {
		this.isCloseBtnOpen = tf;
	}

	/**
	 * 添加用户的速度跟踪器
	 * 
	 * @param event
	 */
	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}

		velocityTracker.addMovement(event);
	}

	/**
	 * 移除用户速度跟踪器
	 */
	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	/**
	 * 获取X方向的滑动速度,大于0向右滑动，反之向左
	 * 
	 * @return
	 */
	private int getScrollVelocityX() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

	private int getScrollVelocityY() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getYVelocity();
		return velocity;
	}

	private void changeHeaderViewByState() {
		switch (state) {
		case ListState.RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText("松开刷新");
			break;
		case ListState.PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的

			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			break;

		case ListState.REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新 ...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		case ListState.DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow);
			tipsTextview.setText("下拉刷新已经加载完毕... ");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

	// 自定义接口(方便调用里面的方法)
	public interface OnRefreshListener {
		public void onRefresh();
		// Object refreshing(); //加载数据
		// void refreshed(Object obj); //外部可扩展加载完成后的操作
	}

	public interface OnItemLongClickListener {
		public void onItemLongClick();
	}

	public interface OnItemClickListener {
		public void onItemClick();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			if (isCloseBtnOpen)
				scrollClose();
			refreshListener.onRefresh();
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener itemLongClickListener) {
		this.itemLongClickListener = itemLongClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	// 注入接口
	public void onRefreshComplete() {
		state = ListState.DONE;
		lastUpdatedTextView.setText("最近更新: " + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	private void measureView(View child) {
		System.out.println("完成初始化5.1");
		ViewGroup.LayoutParams p = child.getLayoutParams();
		System.out.println("完成初始化5.2");
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			System.out.println("完成初始化5.2.1");
		}
		System.out.println("完成初始化5.3");
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		System.out.println("完成初始化5.4");
		if (lpHeight > 0) {
			// MeasureSpec.UNSPECIFIED,
			// 未指定尺寸这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式
			// MeasureSpec.EXACTLY,精确尺寸
			// MeasureSpec.AT_MOST最大尺寸
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
			System.out.println("完成初始化5.4.1");
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			System.out.println("完成初始化5.4.2");
		}
		if (child == null)
			System.out.println("child为空");
		child.measure(childWidthSpec, childHeightSpec);
		System.out.println("完成初始化5.5");
	}

	public class ListState {
		/** 松手刷新 */
		public static final int RELEASE_To_REFRESH = 0;
		/** 下拉刷新 */
		public static final int PULL_To_REFRESH = 1;
		/** 正在刷新 */
		public static final int REFRESHING = 2;
		/** 完成 */
		public static final int DONE = 3;
		/** 加载 */
		public static final int LOADING = 4;
	}

	public class Direction {
		public static final int UP = 0;
		public static final int L = 1;
		public static final int R = 2;
		public static final int RL = 3;
		public static final int BULL = 4;
	}

}
