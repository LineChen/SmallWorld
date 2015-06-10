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
	/** �ϴε�View�Ƿ����°��� */
	private boolean isORP;
	private int direction;

	/** ��ǰ������ListView��position */
	private int slidePosition;
	/** ��ָ����Y������ */
	private int downY;
	/** ��ָ����X������ */
	private int downX;
	private int oldDownX;
	private int oldDownY;
	/** ɾ�������Ŀ�� */
	private int maxscrooldis = 300;
	/** ��Ļ��� */
	private int screenWidth;
	/** ListView��item */
	private View itemView;
	/** ������ */
	private Scroller scroller;

	private static final int SNAP_VELOCITY = 600;
	/** �ٶ�׷�ٶ��� */
	private VelocityTracker velocityTracker;
	/** �Ƿ���Ӧ������Ĭ��Ϊ����Ӧ */
	private boolean isSlide = false;
	/** ��Ϊ���û���������С���� */
	private int mTouchSlop;
	/** �رհ�ť�� */
	private boolean isCloseBtnOpen;
	/** ��ǰ���ڴ�״̬��item */
	private View openitemView;

	// ////////////////////����ˢ����ر���////////////////////////////////////
	/** �Ƿ���������ˢ�� */
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
	private boolean isRecored;// ���ڱ�֤startYH��ֵ��һ��������touch�¼���ֻ����¼һ��
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
	// ////////////////��д�����¼�������//////////////////////////
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
		System.out.println("��ʼ��ʼ������ˢ����ر���");
		screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		openitemView = null;
		setCacheColorHint(R.color.transparent);
		inflater = LayoutInflater.from(context);
		System.out.println("��ɳ�ʼ��0");
		if (inflater == null) {
			System.out.println("mInflaterΪ��");

		}
		headView = (LinearLayout) inflater.inflate(R.layout.chat_listview_head,
				null);
		fotterView = (LinearLayout) inflater.inflate(
				R.layout.chat_listview_footer, null);

		System.out.println("��ɳ�ʼ��1");
		arrowImageView = (ImageView) headView
				.findViewById(R.id.chat_listview_head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		System.out.println("��ɳ�ʼ��2");
		progressBar = (ProgressBar) headView
				.findViewById(R.id.chat_listview_head_progressbar);
		System.out.println("��ɳ�ʼ��3");
		tipsTextview = (TextView) headView
				.findViewById(R.id.chat_listview_head_tipsTextView);
		System.out.println("��ɳ�ʼ��4");
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.chat_listview_head_lastUpdatedTextView);
		System.out.println("��ɳ�ʼ��5");

		measureView(headView);
		measureView(fotterView);
		System.out.println("��ɳ�ʼ��6");
		headContentHeight = headView.getMeasuredHeight();
		fotterContentHeight = fotterView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		// ˢ��view
		headView.invalidate();
		fotterView.setPadding(0, -1 * fotterContentHeight, 0, 0);
		fotterView.invalidate();
		Log.v("this is", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		System.out.println("��ɳ�ʼ��7");
		// ���ͷ�ļ����� selected
		addHeaderView(headView, null, false);
		addFooterView(fotterView);
		setOnScrollListener(this);
		System.out.println("��ɳ�ʼ��8");

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);
		System.out.println("��ɳ�ʼ��9");

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		System.out.println("��ɳ�ʼ��10");

		state = ListState.DONE;
		isRefreshable = false;
		isRecored = false;
		isRecoredUD = false;
		isCurrentPosition = false;
		mLongPressRunnable = new Runnable() {

			@Override
			public void run() {
				canClick = false;
				System.out.println("---------------->���볤������");
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
				System.out.println("��ʱ������Ӧ������������ʱ");
			}
		};
		canClick = false;
		bg_nor = R.drawable.button;
		bg_press = R.drawable.button_pressed;
		setScrollbarFadingEnabled(true);
		System.out.println("������ʼ������ˢ����ر���");
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
	 * �ַ��¼�����Ҫ�������жϵ�������ĸ�item, �Լ�ͨ��postDelayed��������Ӧ���һ����¼�
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		System.out.println("����ListView��dispatchTouchEvent");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			addVelocityTracker(event);
			if (!scroller.isFinished()) {
				System.out.println("scroller������û�н���������ֱ�ӷ���");
				return super.dispatchTouchEvent(event);
			}
			System.out
					.println("����ListView��dispatchTouchEvent  MotionEvent.ACTION_DOWN:");
			oldDownX = (int) event.getX();
			oldDownY = (int) event.getY();
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);
			System.out.println("slidePosition��" + slidePosition);

			// ��Ч��position, �����κδ���
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}
			postDelayed(mLongPressRunnable, LONG_PRESS_TIME);
			postDelayed(mLongNorEvent, LONG_NOR_PRESS);
			canClick = true;
			System.out.println("��ʼ������ʱ");
			isCurrentPosition = false;
			if (event.getX() < screenWidth / 7) {
				isCurrentPosition = true;
				System.out.println("���λ��Ϊ����");
			}

			// ��ȡ���ǵ����item view
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
					.println("����ListView��dispatchTouchEvent  MotionEvent.ACTION_MOVE:");
			System.out.println("�������£�" + getScrollVelocityX() + ";"
					+ getScrollVelocityY() + ";" + SNAP_VELOCITY + ";"
					+ oldDownX + ";" + oldDownY + ";" + event.getX() + ";"
					+ event.getY());
			direction = Direction.BULL;

			removeCallbacks(mLongNorEvent);
			postDelayed(mLongNorEvent, LONG_NOR_PRESS);
			System.out.println("���¿�ʼ��ʱ������Ӧ��ʱ");

			if (slidePosition != AdapterView.INVALID_POSITION) {

				if (getScrollVelocityX() < -SNAP_VELOCITY
						|| ((event.getX() - oldDownX) < -mTouchSlop && Math
								.abs(event.getY() - oldDownY) < mTouchSlop)) {
					direction = Direction.L;
					canClick = false;
					if(itemView != null)
						itemView.setBackgroundResource(bg_nor);
					removeCallbacks(mLongPressRunnable);
					System.out.println("����������ʱDirection.L");
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
					System.out.println("����������ʱDirection.R");
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
				System.out.println("����������ʱ:" + Math.abs(getScrollVelocityY())
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
					.println("����ListView��dispatchTouchEvent  MotionEvent.ACTION_UP:"
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
					System.out.println("ɾ�����򿪣����ܽ������죬�ȹر�");
				} else {
					System.out.println("ִ�н�������");
					itemClickListener.onItemClick();
				}
			}
			if(itemView != null)
				itemView.setBackgroundResource(bg_nor);
			removeCallbacks(mLongNorEvent);
			removeCallbacks(mLongPressRunnable);
			canClick = false;
			System.out.println("����������ʱ");
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	/**
	 * ���������϶�ListView item���߼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (isSlide) {
			System.out.println("����onTouchEvent  ����ɾ��");
			addVelocityTracker(ev);
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				int deltaX = downX - x;
				int movedis = oldDownX - x;
				downX = x;
				// ��ָ�϶�itemView����, deltaX����0���������С��0���ҹ�
				System.out.println("����listview   item����");
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
					System.out.println("item��ʼ������deltaX��" + deltaX);
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
				// ��ָ�뿪��ʱ��Ͳ���Ӧ���ҹ���
				isSlide = false;
				break;
			}

			return true;// �϶���ʱ��ListView������
		}

		if (isDown) {
			System.out.println("��������ˢ�´���������");
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("firstItemIndex��" + firstItemIndex);
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startYH = (int) ev.getY();
					Log.v("this is", "��downʱ���¼��ǰλ��");
				}
				if (getLastVisiblePosition() == mTotalItemCount - 1
						&& !isRecoredUD) {
					isRecoredUD = true;
					startYF = (int) ev.getY();
					Log.v("this is", "��downʱ���¼��ǰλ��");
				}
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startYH = tempY;
					Log.v("this is", "��moveʱ���¼��λ��");
				}
				if (getLastVisiblePosition() == mTotalItemCount - 1
						&& !isRecoredUD) {
					isRecoredUD = true;
					startYF = tempY;
					Log.v("this is", "��downʱ���¼��ǰλ��");
				}
				// ��֤������padding�Ĺ����У���ǰ��λ��һֱ����head������������б�����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���
				if (state != ListState.REFRESHING && isRecored
						&& state != ListState.LOADING) {

					// ��������ȥˢ����
					if (state == ListState.RELEASE_To_REFRESH) {
						setSelection(0);
						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
						if (((tempY - startYH) / RATIO < headContentHeight)
								&& (tempY - startYH) > 0) {
							state = ListState.PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.v("this is", "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");

						}
						// һ�����Ƶ�����
						else if (tempY - startYH <= 0) {
							state = ListState.DONE;
							changeHeaderViewByState();
							Log.v("this is", "���ɿ�ˢ��״̬ת�䵽done״̬");

						}
						// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�
						else {
							// ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
						}
					}
					// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
					if (state == ListState.PULL_To_REFRESH) {
						setSelection(0);
						// ���������Խ���RELEASE_TO_REFRESH��״̬
						if ((tempY - startYH) / RATIO >= headContentHeight) {
							state = ListState.RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							Log.v("this is", "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");

						}
						// ���Ƶ�����
						else if (tempY - startYH <= 0) {
							state = ListState.DONE;
							changeHeaderViewByState();
							Log.v("this is", "��DOne��������ˢ��״̬ת�䵽done״̬");

						}
					}
					// done״̬��

					if (state == ListState.DONE) {
						if (tempY - startYH > 0) {
							state = ListState.PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// ����headView��size

					if (state == ListState.PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startYH) / RATIO, 0, 0);
					}
					// ����headView��paddingTop

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
						Log.v("this is", "������ˢ��״̬����done״̬");
					}
					if (state == ListState.RELEASE_To_REFRESH) {
						state = ListState.REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						Log.v("this is", "���ɿ�ˢ��״̬����done״̬");
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
	 * ��������ʾɾ������
	 */
	private void scrollOpen() {
		final int delta = (maxscrooldis - itemView.getScrollX());
		// System.out.println("���ջ�����ز���"+ maxscrooldis + "	+ " + screenWidth +
		// "+	" + itemView.getScrollX());
		// System.out.println("delta:"+delta);
		// ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		this.isCloseBtnOpen = true;
		this.openitemView = itemView;
		postInvalidate(); // ˢ��itemView
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
		// ����startScroll��ʱ��scroller.computeScrollOffset()����true��
		if (scroller.computeScrollOffset()) {
			// System.out.println("��������");
			// ��ListView item���ݵ�ǰ�Ĺ���ƫ�������й���
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			// itemView.setPressed(false);
			postInvalidate();

			// ��������������ʱ����ûص��ӿ�
			if (scroller.isFinished()) {
				itemView.setPressed(false);
				System.out.println("itemView.setPressed(false);"
						+ itemView.isPressed());
				itemView.postInvalidate();
			}
		}
	}

	/**
	 * ������ָ����itemView�ľ������ж��ǹ�������ʼλ�û�������������ҹ���
	 */
	private void scrollByDistanceX() {
		// �����������ľ�����ڰ�������ȵ�����֮һ��������ɾ��
		if (itemView.getScrollX() >= maxscrooldis / 3) {
			scrollOpen();
		} else {
			// ���ص�ԭʼλ��,Ϊ��͵����������ֱ�ӵ���scrollTo����
			scrollClose();
		}

	}

	/**
	 * ȡ�õ�ǰѡ��itemλ��
	 */
	public int getslidePosition() {
		System.out.println("���շ���Ҫ������slidePosition��" + slidePosition);
		return slidePosition;
	}

	/**
	 * ���ùرհ�ť״̬
	 */
	public void setCloseBtnState(boolean tf) {
		this.isCloseBtnOpen = tf;
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
			tipsTextview.setText("�ɿ�ˢ��");
			break;
		case ListState.PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������

			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText("����ˢ��");
			} else {
				tipsTextview.setText("����ˢ��");
			}
			break;

		case ListState.REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("����ˢ�� ...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		case ListState.DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow);
			tipsTextview.setText("����ˢ���Ѿ��������... ");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

	// �Զ���ӿ�(�����������ķ���)
	public interface OnRefreshListener {
		public void onRefresh();
		// Object refreshing(); //��������
		// void refreshed(Object obj); //�ⲿ����չ������ɺ�Ĳ���
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

	// ע��ӿ�
	public void onRefreshComplete() {
		state = ListState.DONE;
		lastUpdatedTextView.setText("�������: " + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	private void measureView(View child) {
		System.out.println("��ɳ�ʼ��5.1");
		ViewGroup.LayoutParams p = child.getLayoutParams();
		System.out.println("��ɳ�ʼ��5.2");
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			System.out.println("��ɳ�ʼ��5.2.1");
		}
		System.out.println("��ɳ�ʼ��5.3");
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		System.out.println("��ɳ�ʼ��5.4");
		if (lpHeight > 0) {
			// MeasureSpec.UNSPECIFIED,
			// δָ���ߴ�����������࣬һ�㶼�Ǹ��ؼ���AdapterView��ͨ��measure���������ģʽ
			// MeasureSpec.EXACTLY,��ȷ�ߴ�
			// MeasureSpec.AT_MOST���ߴ�
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
			System.out.println("��ɳ�ʼ��5.4.1");
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			System.out.println("��ɳ�ʼ��5.4.2");
		}
		if (child == null)
			System.out.println("childΪ��");
		child.measure(childWidthSpec, childHeightSpec);
		System.out.println("��ɳ�ʼ��5.5");
	}

	public class ListState {
		/** ����ˢ�� */
		public static final int RELEASE_To_REFRESH = 0;
		/** ����ˢ�� */
		public static final int PULL_To_REFRESH = 1;
		/** ����ˢ�� */
		public static final int REFRESHING = 2;
		/** ��� */
		public static final int DONE = 3;
		/** ���� */
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
