package com.example.hellostranger;

import com.alibaba.fastjson.JSONObject;
import com.clientUtils.ClientManager;
import com.example.hellostranger.activity.FriendAddActivity;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.activity.SignInActivity;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

	ImageView[][] images;
	Drawable[] drawable;
	int[][] unitInfo;
	int actionTime;
	MyOnClickListener myOnClickListener;
	MyAnimationListener myAnimationListener;
	int SHOWUSERMSG = 200;
	String strangerId;

	int step;
	TextView tv_step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game);
		Intent intent = getIntent();
		strangerId = intent.getStringExtra("strangerId");
		actionTime = 200;
		myOnClickListener = new MyOnClickListener();
		myAnimationListener = new MyAnimationListener();
		initImageView();
		initDrawable();
		initUnitInfo();
		initUI();
	}

	private void initImageView() {
		images = new ImageView[3][3];
		images[0][0] = (ImageView) findViewById(R.id.game_iv_00);
		images[0][0].setTag("00");
		images[0][0].setOnClickListener(myOnClickListener);

		images[0][1] = (ImageView) findViewById(R.id.game_iv_01);
		images[0][1].setTag("01");
		images[0][1].setOnClickListener(myOnClickListener);

		images[0][2] = (ImageView) findViewById(R.id.game_iv_02);
		images[0][2].setTag("02");
		images[0][2].setOnClickListener(myOnClickListener);

		images[1][0] = (ImageView) findViewById(R.id.game_iv_10);
		images[1][0].setTag("10");
		images[1][0].setOnClickListener(myOnClickListener);

		images[1][1] = (ImageView) findViewById(R.id.game_iv_11);
		images[1][1].setTag("11");
		images[1][1].setOnClickListener(myOnClickListener);

		images[1][2] = (ImageView) findViewById(R.id.game_iv_12);
		images[1][2].setTag("12");
		images[1][2].setOnClickListener(myOnClickListener);

		images[2][0] = (ImageView) findViewById(R.id.game_iv_20);
		images[2][0].setTag("20");
		images[2][0].setOnClickListener(myOnClickListener);

		images[2][1] = (ImageView) findViewById(R.id.game_iv_21);
		images[2][1].setTag("21");
		images[2][1].setOnClickListener(myOnClickListener);

		images[2][2] = (ImageView) findViewById(R.id.game_iv_22);
		images[2][2].setTag("22");
		images[2][2].setOnClickListener(myOnClickListener);
	}

	private void initDrawable() {
		drawable = new Drawable[9];
		drawable[0] = null;
		drawable[1] = getResources().getDrawable(R.drawable.image_num_1);
		drawable[2] = getResources().getDrawable(R.drawable.image_num_2);
		drawable[3] = getResources().getDrawable(R.drawable.image_num_3);
		drawable[4] = getResources().getDrawable(R.drawable.image_num_4);
		drawable[5] = getResources().getDrawable(R.drawable.image_num_5);
		drawable[6] = getResources().getDrawable(R.drawable.image_num_6);
		drawable[7] = getResources().getDrawable(R.drawable.image_num_7);
		drawable[8] = getResources().getDrawable(R.drawable.image_num_8);
		step = 0;
		tv_step = (TextView) findViewById(R.id.game_tv_step);
		tv_step.setText(Integer.valueOf(step).toString());
	}

	private void initUnitInfo() {
		step = 0;
		int x = 2, y = 2;
		unitInfo = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++)
				unitInfo[i][j] = i * 3 + j + 1;
		}
		unitInfo[2][2] = 0;

		for (int i = 0; i < 1000; i++) {
			int dir = (int) (Math.random() * 4);

			if (dir == 0 && (y - 1 >= 0)) {
				unitInfo[x][y] = unitInfo[x][y - 1];
				unitInfo[x][y - 1] = 0;
				y = y - 1;
			} else if (dir == 1 && (y + 1 < 3)) {
				unitInfo[x][y] = unitInfo[x][y + 1];
				unitInfo[x][y + 1] = 0;
				y = y + 1;
			} else if (dir == 2 && (x - 1 >= 0)) {
				unitInfo[x][y] = unitInfo[x - 1][y];
				unitInfo[x - 1][y] = 0;
				x = x - 1;
			} else if (dir == 3 && (x + 1 < 3)) {
				unitInfo[x][y] = unitInfo[x + 1][y];
				unitInfo[x + 1][y] = 0;
				x = x + 1;
			} else {
			}
		}

	}

	private void initUI() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				images[i][j].setImageDrawable(drawable[unitInfo[i][j]]);
			}
		}

	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = (String) v.getTag();
			if (tag.equals("00")) {
				itemClick(0, 0);
			} else if (tag.equals("01")) {
				itemClick(0, 1);
			} else if (tag.equals("02")) {
				itemClick(0, 2);
			} else if (tag.equals("10")) {
				itemClick(1, 0);
			} else if (tag.equals("11")) {
				itemClick(1, 1);
			} else if (tag.equals("12")) {
				itemClick(1, 2);
			} else if (tag.equals("20")) {
				itemClick(2, 0);
			} else if (tag.equals("21")) {
				itemClick(2, 1);
			} else if (tag.equals("22")) {
				itemClick(2, 2);
			} else {
				System.out.println("未识别事件？");
			}
		}

	}

	private void itemClick(int i, int j) {

		if (j - 1 >= 0) {
			if (unitInfo[i][j - 1] == 0) {
				animation(i, j, i, j - 1);
				step++;
				tv_step.setText(Integer.valueOf(step).toString());

			}
		}
		if (j + 1 < 3) {
			if (unitInfo[i][j + 1] == 0) {
				animation(i, j, i, j + 1);
				step++;
				tv_step.setText(Integer.valueOf(step).toString());
			}
		}
		if (i - 1 >= 0) {
			if (unitInfo[i - 1][j] == 0) {
				animation(i, j, i - 1, j);
				step++;
				tv_step.setText(Integer.valueOf(step).toString());
			}
		}
		if (i + 1 < 3) {
			if (unitInfo[i + 1][j] == 0) {
				animation(i, j, i + 1, j);
				step++;
				tv_step.setText(Integer.valueOf(step).toString());
			}
		}
	}

	private void animation(int i, int j, int x, int y) {

		int lrnum = y - j;
		int udnum = x - i;
		final int m = unitInfo[i][j];
		unitInfo[i][j] = 0;
		unitInfo[x][y] = m;
		myAnimationListener.setImageView(images[i][j], images[x][y], m);
		TranslateAnimation translateAnimation1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				lrnum, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, udnum);
		translateAnimation1.setDuration(actionTime);
		translateAnimation1.setFillBefore(true);
		translateAnimation1.setAnimationListener(myAnimationListener);
		images[i][j].bringToFront();
		images[i][j].startAnimation(translateAnimation1);
	}

	private class MyAnimationListener implements AnimationListener {

		ImageView image;
		ImageView emptyImage;
		int m;

		public void setImageView(ImageView image, ImageView emptyImage, int m) {
			this.image = image;
			this.emptyImage = emptyImage;
			this.m = m;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			image.clearAnimation();
			image.setImageDrawable(drawable[0]);
			emptyImage.setImageDrawable(drawable[m]);
			judgResult();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	}

	public void judgResult() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (unitInfo[i][j] != (i * 3 + j + 1) % 9) {
					System.out.println("结果不满足" + i + "    " + j + "   "
							+ unitInfo[i][j] + "   " + (i * 3 + j + 1) % 9);
					return;
				}
			}
		}
		new AlertDialog.Builder(this).setTitle("消息").setMessage("完成游戏")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(GameActivity.this,
								FriendAddActivity.class);
						intent.putExtra("strangerId", strangerId);
						startActivity(intent);
						finish();
					}
				}).show();
	}

	// 后门
	public void onFinish(View v) {
		Intent intent = new Intent(GameActivity.this, FriendAddActivity.class);
		intent.putExtra("strangerId", strangerId);
		startActivity(intent);
		finish();
	}

	// 不玩了wo
	public void onBack(View v) {
		finish();
	}

	/**
	 * 用活力值通关
	 * 
	 * @param v
	 */
	public void onPassGame(View v) {
		
		if(ClientManager.vitalityValue < 10){
			Toast.makeText(GameActivity.this, "您的活力值不够", 0).show();
		} else {
			Dialog dialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			builder.setTitle("用活力值通关").setMessage(
					"您当前的活力值是" + ClientManager.vitalityValue + ",确定将减去10");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					iMoMoMsg passGame = new iMoMoMsg();
					passGame.symbol = '+';
					JSONObject json = new JSONObject();
					json.put(MsgKeys.userId, ClientManager.clientId);
					json.put(MsgKeys.msgType, iMoMoMsgTypes.PASS_GAME);
					passGame.msgJson = json.toJSONString();
					if (MainActivity.ClientSession != null
							&& !MainActivity.ClientSession.isClosing()) {
						MainActivity.ClientSession.write(passGame);
					}
					
					Intent intent = new Intent(GameActivity.this, FriendAddActivity.class);
					intent.putExtra("strangerId", strangerId);
					startActivity(intent);
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			dialog.show();
		}
		
		
	}
}
