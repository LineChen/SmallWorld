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
	/** ��ָ����Y������ */
	private int downY;
	/** ��ָ����X������ */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** ��Ϊ���û���������С���� */
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
		System.out.println("initScreenListener()���");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// �¼��ַܷ�����main����ʱ���¼��ַ��Ա�ȡ�������¼���������ͼ��ִ�ж���
		System.out.println("�����¼����亯��");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			System.out.println("�����¼����亯���е�ACTION_DOWN��event.getX()��"
					+ event.getX() + "  bottomViewWidth:" + bottomViewWidth);

			addVelocityTracker(event);
			bottomViewWidth = bottomView.getWidth();
			System.out.println("bottomViewWidth��" + bottomViewWidth);
			oldDownX = (int) event.getX();
			oldDownY = (int) event.getY();
			downX = (int) event.getX();
			downY = (int) event.getY();

			isCurrentPosition = false;

			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("���λ��Ϊ����");
			}
			
			if (event.getX() > bottomViewWidth && this.isTopOpen){
				System.out.println("������ǻ������¼�");
				return onTouchEvent(event);
			}
			
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out.println("�����¼����亯���е�ACTION_MOVE");
			System.out.println("�������£�"+Math.abs(getScrollVelocity()) +";"+SNAP_VELOCITY+";"+mTouchSlop+";"+oldDownX+";"+oldDownY+";"+event.getX()+";"+event.getY());
			
			if (canSlide){
				System.out.println("ֱ�ӽ���Base��onTouchEvent");
				return onTouchEvent(event);
			}
			
			if ((isCurrentPosition || isTopOpen) && ((Math.abs(getScrollVelocity()) > SNAP_VELOCITY) || (Math.abs(event.getX() - oldDownX) > mTouchSlop && Math.abs(event.getY() - oldDownY) < mTouchSlop))) {
				System.out.println("��������߻�����Ѵ򿪣�������");
				canSlide = true;
				return onTouchEvent(event);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			System.out.println("�����¼����亯���е�ACTION_UP");
			if (canSlide) {
				System.out.println("�����¼����亯���е�ACTION_UP�ҷ��������onTouchEvent()");
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
		System.out.println("���봥���¼�����");
		if (canSlide) {
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE: {
				System.out.println("����ACTION_MOVE");
				int deltaX = downX - x;
				int movedis = oldDownX - x;
				downX = x;
				System.out
						.println("topView.getScrollX():" + topView.getScrollX()
								+ "  screenWidth:" + screenWidth);
				System.out.println("movedis"+";"+bottomViewWidth+";"+deltaX);
				if (movedis >= -bottomViewWidth && movedis < 0
						&& isCurrentPosition && !isTopOpen) {
					System.out.println("item��ʼ���һ�����deltaX��" + deltaX);
					topView.scrollBy(deltaX, 0);
				}
				if (movedis <= bottomViewWidth && movedis > 0 && isTopOpen) {
					System.out.println("item��ʼ���󻬶���deltaX��" + deltaX);
					topView.scrollBy(deltaX, 0);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				System.out.println("����ACTION_UP");
				int velocityX = getScrollVelocity();
				canSlide = false;
				System.out.println("velocityX:" + velocityX);
				if (velocityX < -SNAP_VELOCITY) {
					System.out.println("�ر�1");
					scrollClose();
				} else if (velocityX > SNAP_VELOCITY) {
					System.out.println("��1");
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
		System.out.println("����򿪺���");
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
		// �����������ľ�����ڰ�������ȵ�����֮һ���������
		System.out.println("topView.getScrollX():" + topView.getScrollX()
				+ "    " + "bottomViewWidth / 3:" + bottomViewWidth / 3);
		if (topView.getScrollX() <= -bottomViewWidth / 3) {
			System.out.println("��2");
			scrollOpen();
		} else {
			System.out.println("�ر�2");
			scrollClose();
		}

	}

}
