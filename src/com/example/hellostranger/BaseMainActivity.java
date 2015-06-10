package com.example.hellostranger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class BaseMainActivity extends Activity {

	private boolean isTopOpen;
	private boolean isCurrentPosition;
	private boolean canSlide;
	private RelativeLayout topView;
	private RelativeLayout bottomView;
	private int bottomViewWidth;
	private VelocityTracker velocityTracker;
	private static final int SNAP_VELOCITY = 600;
	/** 手指按下Y的坐标 */
	private int downY;
	/** 手指按下X的坐标 */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** 认为是用户滑动的最小距离 */
	private int mTouchSlop;

	int screenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initScreenListener(RelativeLayout topView,
			RelativeLayout bottomView) {
		this.topView = topView;
		// topView.setFocusable(false);
		topView.setFocusableInTouchMode(false);
		this.bottomView = bottomView;
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop()+20;
		bottomViewWidth = bottomView.getWidth();
		screenWidth = ((WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		isTopOpen = false;
		isCurrentPosition = false;
		canSlide = false;
		System.out.println("initScreenListener()完成");
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

			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("点击位置为左侧边");
			}
			
			if (event.getX() > bottomViewWidth && this.isTopOpen){
				System.out.println("不处理非活动区点击事件");
				return onTouchEvent(event);
			}
			
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out.println("进入事件分配函数中的ACTION_MOVE");
			System.out.println("数据如下："+Math.abs(getScrollVelocity()) +";"+SNAP_VELOCITY+";"+mTouchSlop+";"+oldDownX+";"+oldDownY+";"+event.getX()+";"+event.getY());
			
			if (canSlide){
				System.out.println("直接进入Base的onTouchEvent");
				return onTouchEvent(event);
			}
			
			if ((isCurrentPosition || isTopOpen) && ((Math.abs(getScrollVelocity()) > SNAP_VELOCITY) || (Math.abs(event.getX() - oldDownX) > mTouchSlop && Math.abs(event.getY() - oldDownY) < mTouchSlop))) {
				System.out.println("点击的左侧边或侧栏已打开，允许滑动");
				canSlide = true;
				return onTouchEvent(event);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			System.out.println("进入事件分配函数中的ACTION_UP");
			if (canSlide) {
				System.out.println("进入事件分配函数中的ACTION_UP且分配给自身onTouchEvent()");
				onTouchEvent(event);
				return super.dispatchTouchEvent(event);
			}
			recycleVelocityTracker();
			break;
		}
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("进入触摸事件函数");
		if (canSlide) {
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE: {
				System.out.println("进入ACTION_MOVE");
				int deltaX = downX - x;
				int movedis = oldDownX - x;
				downX = x;
				System.out
						.println("topView.getScrollX():" + topView.getScrollX()
								+ "  screenWidth:" + screenWidth);
				System.out.println("movedis"+";"+bottomViewWidth+";"+deltaX);
				if (movedis >= -bottomViewWidth && movedis < 0
						&& isCurrentPosition && !isTopOpen) {
					System.out.println("item开始向右滑动，deltaX：" + deltaX);
					topView.scrollBy(deltaX, 0);
				}
				if (movedis <= bottomViewWidth && movedis > 0 && isTopOpen) {
					System.out.println("item开始向左滑动，deltaX：" + deltaX);
					topView.scrollBy(deltaX, 0);
				}
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
		topView.scrollTo(-bottomViewWidth, 0);
		topView.setEnabled(false);
		isTopOpen = true;
		topView.invalidate();
	}

	private void scrollClose() {
		topView.scrollTo(0, 0);
		topView.setEnabled(true);
		isTopOpen = false;
	}

	private void scrollByDistanceX() {
		// 如果向左滚动的距离大于按键最大宽度的三分之一，就让其打开
		System.out.println("topView.getScrollX():" + topView.getScrollX()
				+ "    " + "bottomViewWidth / 3:" + bottomViewWidth / 3);
		if (topView.getScrollX() <= -bottomViewWidth / 3) {
			System.out.println("打开2");
			scrollOpen();
		} else {
			System.out.println("关闭2");
			scrollClose();
		}

	}

}
