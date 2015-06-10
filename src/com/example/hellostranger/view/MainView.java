package com.example.hellostranger.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class MainView extends RelativeLayout {

	private boolean once = false;// onMeasure调用是否是第一次

	private ViewGroup topView;
	private ViewGroup bottomView;
	private boolean isTopOpen;
	private boolean isCurrentPosition;
	private boolean canSlide;
	private int bottomViewWidth;
	private VelocityTracker velocityTracker;
	private static final int SNAP_VELOCITY = 1000;
	/** 手指按下Y的坐标 */
	private int downY;
	/** 手指按下X的坐标 */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** 认为是用户滑动的最小距离 */
	private int mTouchSlop;
	/** 滑动类 */
	private Scroller scroller;

	int screenWidth;

	int loc;

	private int mDis;

	public MainView(Context context) {
		this(context, null);
	}

	public MainView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initScreenListener();
	}

	public void initScreenListener() {
		scroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() + 20;
		screenWidth = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		isTopOpen = false;
		isCurrentPosition = false;
		canSlide = false;
		loc = 0;
		System.out.println("initScreenListener()完成");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			topView = (ViewGroup) getChildAt(1);
			bottomView = (ViewGroup) getChildAt(0);
			bottomViewWidth = bottomView.getWidth();
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// 事件能分发，当main滑动时，事件分发以便取消长按事件但是子视图不执行动作
		System.out.println("进入事件分配函数");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			System.out.println("进入事件分配函数中的ACTION_DOWN，event.getX()："
					+ event.getX() + "  bottomViewWidth:" + bottomViewWidth);

			addVelocityTracker(event);
			bottomViewWidth = bottomView.getWidth();
			System.out.println("bottomViewWidth：" + bottomViewWidth);
			oldDownX = (int) event.getX();
			oldDownY = (int) event.getY();
			downX = (int) event.getX();
			downY = (int) event.getY();
			isCurrentPosition = false;
			if (isTopOpen)
				mDis = -bottomViewWidth;
			else
				mDis = -0;

			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("点击位置为左侧边");
			}

			if (event.getX() > bottomViewWidth && isTopOpen) {
				System.out.println("不处理非活动区点击事件");
				canSlide = true;
				return onTouchEvent(event);
			}

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out.println("进入事件分配函数中的ACTION_MOVE");
			System.out.println("数据如下：" + Math.abs(getScrollVelocity()) + ";"
					+ SNAP_VELOCITY + ";" + mTouchSlop + ";" + oldDownX + ";"
					+ oldDownY + ";" + event.getX() + ";" + event.getY());

			if (canSlide) {
				System.out.println("直接进入Base的onTouchEvent");
				return onTouchEvent(event);
			}

			if ((isCurrentPosition || isTopOpen)
					&& ((Math.abs(getScrollVelocity()) > SNAP_VELOCITY) || (Math
							.abs(event.getX() - oldDownX) > mTouchSlop && Math
							.abs(event.getY() - oldDownY) < mTouchSlop))) {
				System.out.println("点击的左侧边或侧栏已打开，允许滑动");
				canSlide = true;
				downX = (int) event.getX();
				return onTouchEvent(event);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			System.out.println("进入事件分配函数中的ACTION_UP");
			if (canSlide) {
				System.out.println("进入事件分配函数中的ACTION_UP且分配给自身onTouchEvent()");
				return onTouchEvent(event);
				// return super.dispatchTouchEvent(event);
			}
			recycleVelocityTracker();
			break;
		}
		}
		boolean i = super.dispatchTouchEvent(event);
		System.out.println("事件分配函数返回值："+i);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("进入触摸事件函数");
		if (canSlide) {
			System.out.println("canSlide为true");
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();

			switch (action) {
			case MotionEvent.ACTION_MOVE: {
				System.out.println("进入ACTION_MOVE");
				int deltaX = downX - x;
				downX = x;
				mDis = mDis + deltaX;
				if (mDis>0)
					mDis = 0;
				if(mDis<-bottomViewWidth)
					mDis = -bottomViewWidth;
				System.out.println("mDis:" + mDis);
				onScrollChanged(mDis,0);
				break;
			}
			case MotionEvent.ACTION_UP: {
				System.out.println("进入ACTION_UP");
				int velocityX = getScrollVelocity();
				canSlide = false;
				System.out.println("velocityX:" + velocityX);
				if (velocityX < -SNAP_VELOCITY) {
					System.out.println("关闭1");
					scrollClose();
				} else if (velocityX > SNAP_VELOCITY) {
					System.out.println("打开1");
					scrollOpen();
				} else {
					scrollByDistanceX();
				}
				recycleVelocityTracker();
				break;
			}
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

	private void scrollOpen() {
		System.out.println("进入打开函数");
		final int delta = (bottomViewWidth + mDis);
		System.out.println("当前位置：" + mDis+"          "+-delta);
		scroller.startScroll(mDis, 0, -delta, 0, Math.abs(delta));
		invalidate();
		// topView.scrollTo(-bottomViewWidth, 0);
//		topView.setEnabled(false);
		isTopOpen = true;
//		topView.invalidate();
	}

	private void scrollClose() {
		System.out.println("当前位置：" + topView.getScrollX());
		final int delta = mDis;
		scroller.startScroll(mDis, 0, -delta, 0, Math.abs(delta));
		invalidate();
		isTopOpen = false;
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (scroller.computeScrollOffset()) {
			// System.out.println("滑动结束");
			// 让ListView item根据当前的滚动偏移量进行滚动
//			topView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			System.out.println("滑动中"+scroller.getCurrX()+"     "+ scroller.getCurrY());
			onScrollChanged(scroller.getCurrX(),0);
			postInvalidate();

//			// 滚动动画结束的时候调用回调接口
//			if (scroller.isFinished()) {
//				topView.scrollTo(-bottomViewWidth, 0);
//			}
		}
	}

	private void scrollByDistanceX() {
		// 如果向左滚动的距离大于按键最大宽度的三分之一，就让其打开
		System.out.println("topView.getScrollX():" + topView.getScrollX()
				+ "    " + "bottomViewWidth / 3:" + bottomViewWidth / 3);
		if (mDis <= -bottomViewWidth / 3) {
			System.out.println("打开2");
			scrollOpen();
		} else {
			System.out.println("关闭2");
			scrollClose();
		}

	}

	protected void onScrollChanged(int l, int t) {
		// 调用属性动画,设置TranslationX
		// ViewHelper.setTranslationX(mMenu, mMenuWidth - l);//兼容2.0
		// l: mMenuWidth ~ 0 (从隐藏到显示)
		// l: 0 ~ mMenuWidth (从显示到隐藏)
		l = Math.abs(l);
		float scale = l * 1.0f / bottomViewWidth; // (1 ~ 0)
		/**
		 * 区别1：内容区域1.0~0.8 缩放的效果 scale : 1.0~0.0 0.8 + 0.2 * scale
		 * 
		 * 区别2：菜单的偏移量需要修改
		 * 
		 * 区别3：菜单的显示时有缩放以及透明度变化 缩放：0.7 ~1.0 1.0 - scale * 0.3 透明度 0.4 ~ 1.0 1.0-
		 * scale * 0.6 ;
		 */
		float rightScale = 0.7f + 0.3f * scale;// 底层布局大小变化
		float leftScale = 1.0f - scale * 0.2f;// 顶层布局大小大小
		float leftAlpha = scale;// 透明度

		// 动画
		// 设置content的缩放的中心点
//		float d = bottomViewWidth * scale-(topView.getWidth()-topView.getWidth()*leftScale)/2;
		float d = bottomViewWidth * scale;
		System.out.println("topView偏移距离："+d);
		System.out.println("topView.getWidth():"+topView.getWidth());
		
		topView.setScaleX(leftScale);
		topView.setScaleY(leftScale);
		topView.setPivotX(topView.getWidth() >> 1);
		topView.setPivotY(topView.getHeight() >> 1);
//		topView.setTranslationX(l*scale*0.3f);
		topView.setTranslationX(d);
		
		
		bottomView.setScaleX(rightScale);
		bottomView.setScaleY(rightScale);
		bottomView.setPivotX(bottomView.getWidth() >> 1);
		bottomView.setPivotY(bottomView.getHeight() >> 1);
		bottomView.setAlpha(leftAlpha);

	}

}
