package com.example.hellostranger.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessengerService extends Service{

	MyHandler myHandler = new MyHandler();
	private Messenger mMessenger = new Messenger(myHandler);
	private Messenger cMessenger = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("��MessageServer");
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("��MessageServer");
		return mMessenger.getBinder();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("����MessageServer");
		
	}
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("���յ���Ϣ");
			cMessenger = msg.replyTo;
			String s = (String)msg.obj;
			Message message = Message.obtain(null, 1,s);
			try {
				cMessenger.send(message);
				System.out.println("������Ϣ");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
