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

	private boolean once = false;// onMeasure�����Ƿ��ǵ�һ��

	private ViewGroup topView;
	private ViewGroup bottomView;
	private boolean isTopOpen;
	private boolean isCurrentPosition;
	private boolean canSlide;
	private int bottomViewWidth;
	private VelocityTracker velocityTracker;
	private static final int SNAP_VELOCITY = 1000;
	/** ��ָ����Y������ */
	private int downY;
	/** ��ָ����X������ */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** ��Ϊ���û���������С���� */
	private int mTouchSlop;
	/** ������ */
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
		System.out.println("initScreenListener()���");
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
			if (isTopOpen)
				mDis = -bottomViewWidth;
			else
				mDis = -0;

			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("���λ��Ϊ����");
			}

			if (event.getX() > bottomViewWidth && isTopOpen) {
				System.out.println("������ǻ������¼�");
				canSlide = true;
				return onTouchEvent(event);
			}

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out.println("�����¼����亯���е�ACTION_MOVE");
			System.out.println("�������£�" + Math.abs(getScrollVelocity()) + ";"
					+ SNAP_VELOCITY + ";" + mTouchSlop + ";" + oldDownX + ";"
					+ oldDownY + ";" + event.getX() + ";" + event.getY());

			if (canSlide) {
				System.out.println("ֱ�ӽ���Base��onTouchEvent");
				return onTouchEvent(event);
			}

			if ((isCurrentPosition || isTopOpen)
					&& ((Math.abs(getScrollVelocity()) > SNAP_VELOCITY) || (Math
							.abs(event.getX() - oldDownX) > mTouchSlop && Math
							.abs(event.getY() - oldDownY) < mTouchSlop))) {
				System.out.println("��������߻�����Ѵ򿪣�������");
				canSlide = true;
				downX = (int) event.getX();
				return onTouchEvent(event);
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			System.out.println("�����¼����亯���е�ACTION_UP");
			if (canSlide) {
				System.out.println("�����¼����亯���е�ACTION_UP�ҷ��������onTouchEvent()");
				return onTouchEvent(event);
				// return super.dispatchTouchEvent(event);
			}
			recycleVelocityTracker();
			break;
		}
		}
		boolean i = super.dispatchTouchEvent(event);
		System.out.println("�¼����亯������ֵ��"+i);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("���봥���¼�����");
		if (canSlide) {
			System.out.println("canSlideΪtrue");
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();

			switch (action) {
			case MotionEvent.ACTION_MOVE: {
				System.out.println("����ACTION_MOVE");
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
		final int delta = (bottomViewWidth + mDis);
		System.out.println("��ǰλ�ã�" + mDis+"          "+-delta);
		scroller.startScroll(mDis, 0, -delta, 0, Math.abs(delta));
		invalidate();
		// topView.scrollTo(-bottomViewWidth, 0);
//		topView.setEnabled(false);
		isTopOpen = true;
//		topView.invalidate();
	}

	private void scrollClose() {
		System.out.println("��ǰλ�ã�" + topView.getScrollX());
		final int delta = mDis;
		scroller.startScroll(mDis, 0, -delta, 0, Math.abs(delta));
		invalidate();
		isTopOpen = false;
	}

	@Override
	public void computeScroll() {
		// ����startScroll��ʱ��scroller.computeScrollOffset()����true��
		if (scroller.computeScrollOffset()) {
			// System.out.println("��������");
			// ��ListView item���ݵ�ǰ�Ĺ���ƫ�������й���
//			topView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			System.out.println("������"+scroller.getCurrX()+"     "+ scroller.getCurrY());
			onScrollChanged(scroller.getCurrX(),0);
			postInvalidate();

//			// ��������������ʱ����ûص��ӿ�
//			if (scroller.isFinished()) {
//				topView.scrollTo(-bottomViewWidth, 0);
//			}
		}
	}

	private void scrollByDistanceX() {
		// �����������ľ�����ڰ�������ȵ�����֮һ���������
		System.out.println("topView.getScrollX():" + topView.getScrollX()
				+ "    " + "bottomViewWidth / 3:" + bottomViewWidth / 3);
		if (mDis <= -bottomViewWidth / 3) {
			System.out.println("��2");
			scrollOpen();
		} else {
			System.out.println("�ر�2");
			scrollClose();
		}

	}

	protected void onScrollChanged(int l, int t) {
		// �������Զ���,����TranslationX
		// ViewHelper.setTranslationX(mMenu, mMenuWidth - l);//����2.0
		// l: mMenuWidth ~ 0 (�����ص���ʾ)
		// l: 0 ~ mMenuWidth (����ʾ������)
		l = Math.abs(l);
		float scale = l * 1.0f / bottomViewWidth; // (1 ~ 0)
		/**
		 * ����1����������1.0~0.8 ���ŵ�Ч�� scale : 1.0~0.0 0.8 + 0.2 * scale
		 * 
		 * ����2���˵���ƫ������Ҫ�޸�
		 * 
		 * ����3���˵�����ʾʱ�������Լ�͸���ȱ仯 ���ţ�0.7 ~1.0 1.0 - scale * 0.3 ͸���� 0.4 ~ 1.0 1.0-
		 * scale * 0.6 ;
		 */
		float rightScale = 0.7f + 0.3f * scale;// �ײ㲼�ִ�С�仯
		float leftScale = 1.0f - scale * 0.2f;// ���㲼�ִ�С��С
		float leftAlpha = scale;// ͸����

		// ����
		// ����content�����ŵ����ĵ�
//		float d = bottomViewWidth * scale-(topView.getWidth()-topView.getWidth()*leftScale)/2;
		float d = bottomViewWidth * scale;
		System.out.println("topViewƫ�ƾ��룺"+d);
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
