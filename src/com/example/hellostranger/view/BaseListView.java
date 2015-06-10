package com.example.hellostranger.view;

import com.example.hellostranger.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class BaseListView extends ListView {

	OnItemLongClickListener itemLongClickListener;
	OnItemClickListener itemClickListener;
	private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	private static final int SNAP_VELOCITY = 600;
	private int LONG_PRESS_TIME = 800;
	private int LONG_NOR_PRESS = 200;
	private int bg_nor = R.drawable.button;
	private int bg_press = R.drawable.button_pressed;
	private VelocityTracker velocityTracker;
	private int screenWidth;
	
	int downX;
	int downY;
	int slidePosition;
	int oldDownX;
	int oldDownY;
	
	private boolean isCurrentPosition;
	
	private View itemView;

	private Runnable mLongPressRunnable = new Runnable() {

		@Override
		public void run() {
			itemView.setBackgroundResource(bg_nor);
			postInvalidate();
			if (itemLongClickListener != null)
				itemLongClickListener.onItemLongClick();
		}
	};

	private Runnable mLongNorEvent = new Runnable() {

		@Override
		public void run() {
			if(itemView != null){
				itemView.setBackgroundResource(bg_nor);
				postInvalidate();
				itemView.cancelLongPress();
				removeCallbacks(mLongPressRunnable);  
				System.out.println("��ʱ������Ӧ������������ʱ");
			}
		}
	};

	public BaseListView(Context context) {
		super(context, null);
	}

	public BaseListView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		
	}

	public BaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		System.out.println("����BaseListView�ַ��¼�");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			System.out.println("����BaseListView�ַ��¼�ACTION_DOWN");
			downX = (int) event.getX();
			downY = (int) event.getY();
			isCurrentPosition = false;
			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("���λ��Ϊ����");
			}
			slidePosition = pointToPosition(downX, downY);
			System.out.println("����BaseListView�ַ��¼�slidePosition:"+slidePosition);
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			System.out.println("����BaseListView�ַ��¼�itemViewPosition:"+(slidePosition - getFirstVisiblePosition()));
			itemView = getChildAt(slidePosition - getFirstVisiblePosition());
			if (itemView!=null)
				System.out.println("�õ�itemView");
			addVelocityTracker(event);
			postDelayed(mLongPressRunnable, LONG_PRESS_TIME); 
			postDelayed(mLongNorEvent,LONG_NOR_PRESS); 
			itemView.setBackgroundResource(bg_press);
			postInvalidate();
			oldDownX = (int) event.getX();
			oldDownY = (int) event.getY();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			System.out.println("����BaseListView�ַ��¼�ACTION_MOVE");
			removeCallbacks(mLongNorEvent);  
			postDelayed(mLongNorEvent,LONG_NOR_PRESS); 
			System.out.println("���¿�ʼ��ʱ������Ӧ��ʱ");
			if (Math.abs(getScrollVelocityX()) < -SNAP_VELOCITY
					|| ( Math.abs(event.getX() - oldDownX) > mTouchSlop && Math.abs(event.getY() - oldDownY) < mTouchSlop)) {
				removeCallbacks(mLongPressRunnable);  
				itemView.setBackgroundResource(bg_nor);
				postInvalidate();
				System.out.println("����������ʱDirection.L");
			}
			
			if (Math.abs(getScrollVelocityY()) > SNAP_VELOCITY
					|| (Math.abs(event.getY() - oldDownY) > mTouchSlop && Math
							.abs(event.getX() - oldDownX) < mTouchSlop)) {
				removeCallbacks(mLongPressRunnable);
				if(itemView != null){
					itemView.setBackgroundResource(bg_nor);
					postInvalidate();
				}
				System.out.println("����������ʱDirection.UD");
			}	
			break;
		}
		case MotionEvent.ACTION_UP: {
			System.out.println("����BaseListView�ַ��¼�ACTION_UP");
			recycleVelocityTracker();
			if(itemView != null)
				itemView.setBackgroundResource(bg_nor);
			postInvalidate();
			removeCallbacks(mLongPressRunnable);
			break;
		}
		}
		return super.dispatchTouchEvent(event);

	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener itemLongClickListener) {
		this.itemLongClickListener = itemLongClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public interface OnItemLongClickListener {
		public void onItemLongClick();
	}

	public interface OnItemClickListener {
		public void onItemClick();
	}
	
	/**
	 * ����û����ٶȸ�����
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
	 * �Ƴ��û��ٶȸ�����
	 */
	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	/**
	 * ��ȡX����Ļ����ٶ�,����0���һ�������֮����
	 * 
	 * @return
	 */
	private int getScrollVelocityX() {
		int velocity = 0; 
		if(velocityTracker != null){
			velocityTracker.computeCurrentVelocity(1000);
			velocity = (int) velocityTracker.getXVelocity();
		}
		return velocity;
	}
	
	private int getScrollVelocityY() {
		int velocity = 0; 
		if(velocityTracker != null){
			velocityTracker.computeCurrentVelocity(1000);
			velocity = (int) velocityTracker.getYVelocity();
		}
		return velocity;
	}

}
