package com.clientUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.bean.InvitationInfoEntity;
import com.msg_relative.iMoMoMsgDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class iMoMoDataBase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "imomo_client_db";
	private static final int DATABASE_VERSION = 1;

	// private static final String TABLE_NAME = "msg_table";

	public iMoMoDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	/**
	 * 判断一个表是否存在
	 * 
	 * @param tName
	 * @return 
	 *         "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = ? "
	 *         ;
	 */
	public boolean isTableExists(String tName) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? ";
		String paras[] = { tName };
		try {
			Cursor cursor = db.rawQuery(sql, paras);
			cursor.moveToNext();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			// Log.i("--", "tname = " + name);
			if (name.equals(tName)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除一个表
	 * 
	 * @param friendId
	 */
	public void dropMsgTable(String friendId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		String sql = "drop table " + tName;
		db.execSQL(sql);
	}

	/**
	 * 创建消息表(is ok)
	 */
	public void createMsgTable(String friendId) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;// 表名格式包含用户名,因为不同用户可能有相同的好友
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "create table "
				+ tName
				+ "( msgType integer, msgJson text, sendTime text, isGetted integer, isLooked integer)";
		db.execSQL(sql);
		Log.i("--", "createMsgTable");
	}

	/**
	 * SELECT * from imomo_clients limit 0,1;(类似MySQL)
	 * 获得消息记录,在没有未读消息时，默认值显示15条历史消息(is ok)
	 * 
	 * @param userId
	 * @return Cursor c = db.rawQuery("SELECT * FROM person WHERE age >= ?", new
	 *         String[]{"33"}); select * from table order by id desc limit 0,5
	 */
	public List<iMoMoMsgDb> getMsgLatice(String friendId, int offset,
			int maxResult) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getReadableDatabase();
		List<iMoMoMsgDb> list = new ArrayList<iMoMoMsgDb>();
		String sql = "select * from " + tName
				+ " order by sendTime desc limit ?, ?";// order by sendTime
														// desclimit 0, 5
		int count = getMsgCount(friendId);// 记录总数
		if (count < 1) {
			return list;
		} else {
			if (count < maxResult) {
				maxResult = count;
			}
			Cursor cursor = db.rawQuery(
					sql,
					new String[] { String.valueOf(offset),
							String.valueOf(maxResult) });// new String[]{"1"}
			while (cursor.moveToNext()) {
				iMoMoMsgDb msgDb = new iMoMoMsgDb();
				msgDb.msgType = cursor.getInt(cursor.getColumnIndex("msgType"));
				msgDb.msgJson = cursor.getString(cursor
						.getColumnIndex("msgJson"));
				msgDb.sendTime = cursor.getString(cursor
						.getColumnIndex("sendTime"));
				msgDb.isGetted = cursor.getInt(cursor
						.getColumnIndex("isGetted"));
				msgDb.isLooked = cursor.getInt(cursor
						.getColumnIndex("isLooked"));
				list.add(msgDb);
			}
			cursor.close();
			return list;
		}
	}

	/**
	 * 获取未读消息(is ok)
	 * 
	 * @param userId
	 * @return Cursor c = db.rawQuery("SELECT * FROM person WHERE age >= ?", new
	 *         String[]{"33"});
	 */
	public List<iMoMoMsgDb> getMsgNotLooked(String friendId) {// select top 65
																// *// by id
																// desc
		String tName = "msg" + ClientManager.clientId + "_" + friendId; // from
																		// table
																		// order
		SQLiteDatabase db = this.getReadableDatabase();
		List<iMoMoMsgDb> list = new ArrayList<iMoMoMsgDb>();
		String where = "isLooked = ?";
		Cursor cursor = db.query(tName, null, where, new String[] { "0" },
				null, null, "sendTime desc");
		while (cursor.moveToNext()) {
			iMoMoMsgDb msgDb = new iMoMoMsgDb();
			msgDb.msgType = cursor.getInt(cursor.getColumnIndex("msgType"));
			msgDb.msgJson = cursor.getString(cursor.getColumnIndex("msgJson"));
			msgDb.sendTime = cursor
					.getString(cursor.getColumnIndex("sendTime"));
			msgDb.isGetted = cursor.getInt(cursor.getColumnIndex("isGetted"));
			msgDb.isLooked = cursor.getInt(cursor.getColumnIndex("isLooked"));
			list.add(msgDb);
		}
		cursor.close();
		return list;
	}

	/**
	 * 保存消息(is ok)
	 * 
	 * @param msgDb
	 * @return
	 */
	public boolean insertMsg(iMoMoMsgDb msgDb, String friendId) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			// 注意这种插入，要表明插入的列名tName +"(msgType, msgJson, sendTime, isGetted,
			// isLooked) values(?, ?, ?, ?, ?)
			String sql = "insert into "
					+ tName
					+ "(msgType, msgJson, sendTime, isGetted, isLooked) values(?, ?, ?, ?, ?)";
			String[] args = { msgDb.msgType + "", msgDb.msgJson,
					msgDb.sendTime, msgDb.isGetted + "", msgDb.isLooked + "" };
			db.execSQL(sql, args);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 把未查看的消息状态置为已查看(is ok)
	 * 
	 * @param tName
	 */
	public void updateRead(String friendId) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update " + tName + " set isLooked = ? where isLooked = ?";
		String where = " isLooked = ?";
		db.execSQL(sql, new String[] { "1", "0" });
	}

	/**
	 * 清空一个好友的记录(is ok)
	 * 
	 * @param userId
	 * @param courseName
	 */
	public void clearMsg(String friendId) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tName, null, null);
		db.close();
	}

	/**
	 * 删除一条纪录(发送时间的唯一性)
	 */
	public void delete(String friendId, String sendTime) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + tName + " where sendTime = ?";
		db.execSQL(sql, new String[] { sendTime });
		db.close();
	}

	/**
	 * 取得数据记录数
	 * 
	 * @param tName
	 * @return
	 */
	public int getMsgCount(String friendId) {
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select count(*) from " + tName;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/********************************* 以上是消息处理 *****************************************************/

	/*********************************** 以下是处理最近联系人 *************************************************/
	/**
	 * 创建最近聊天好友 String userId用户Id ; friendId 好友Id //好友名 String userName;//用户名
	 * String chatCreatTime; String chatContent; int msg_num;
	 * userEmail作为用户标识，确认登录的用户是哪个 (ok)
	 **/
	public void createLaticeChatTable() {
		String tName = "latice_chat_table";
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "create table "
				+ tName
				+ "( userEmail text, friendId text primary key, friendName text, chatCreatTime text, chatContent text, msg_num integer, Msgtype integer)";
		db.execSQL(sql);
		Log.i("--", "createLaticeChatTable");
	}

	/**
	 * 判断该好友是否聊过（即是否之前存在于最近聊天界面）
	 * 
	 * @param friendId
	 * @return
	 */
	public boolean isExistsLatice(String friendId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select friendId from latice_chat_table where userEmail = ? and friendId = ? ";
		try {
			Cursor cursor = db.rawQuery(sql, new String[] {
					ClientManager.clientEmail, friendId });
			cursor.moveToNext();
			String Id = cursor.getString(cursor.getColumnIndex("friendId"));
			if (friendId.equals(Id)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 插入最近聊天好友记录(ok)
	 * 
	 * @param entity
	 * @return
	 */
	public boolean insertLaticeChat(ChatInfoEntity entity) {
		String tName = "latice_chat_table";
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			String sql = "insert into "
					+ tName
					+ "(userEmail, friendId, friendName, chatCreatTime, chatContent, msg_num, Msgtype) values(?, ?, ?, ?, ? ,?, ?)";
			String[] args = { ClientManager.clientEmail, entity.getFriendId(),
					entity.getFriendName(), entity.getChatCreatTime(),
					entity.getChatContent(), entity.getMsg_num() + "",
					entity.getMsgtype() + "" };
			db.execSQL(sql, args);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新
	 * 
	 * @param entity
	 */
	public void updateLaticeChat(ChatInfoEntity entity) {
		String tName = "latice_chat_table";
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "update "
				+ tName
				+ " set chatCreatTime = ? ,chatContent = ?, msg_num = ? , Msgtype = ? where friendId = ?";
		db.execSQL(
				sql,
				new String[] { entity.getChatCreatTime(),
						entity.getChatContent(), entity.getMsg_num() + "",
						entity.getMsgtype() + "", entity.getFriendId() });
		Log.i("--", "===============最近联系人  插入成功");
	}

	/**
	 * 删除一项最近聊天item
	 * 
	 * @param userId
	 */
	public void deleteLaticeItem(String friendId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String tName = "latice_chat_table";
		String sql = "delete from " + tName
				+ " where userEmail = ? and friendId = ?";
		db.execSQL(sql, new String[] { ClientManager.clientEmail, friendId });
		db.close();
	}
	
	/**
	 * 清空一个用户的最近联系人
	 */
	public void clearLatice(){
		SQLiteDatabase db = this.getWritableDatabase();
		String tName = "latice_chat_table";
		String sql = "delete from " + tName
				+ " where userEmail = ?";
		db.execSQL(sql, new String[] { ClientManager.clientEmail});
		db.close();
	}

	/**
	 * 获得最近聊天列表
	 * 
	 * @return
	 */
	public List<ChatInfoEntity> getLaticeChatList() {
		// String tName = "latice_chat_table";
		List<ChatInfoEntity> list = new ArrayList<ChatInfoEntity>();
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select friendId, friendName, chatContent, chatCreatTime, msg_num, Msgtype from latice_chat_table where userEmail = ?";
		Cursor cursor = db.rawQuery(sql,
				new String[] { ClientManager.clientEmail });
		while (cursor.moveToNext()) {
			ChatInfoEntity entity = new ChatInfoEntity();
			entity.setFriendId(cursor.getString(cursor
					.getColumnIndex("friendId")));
			entity.setFriendName(cursor.getString(cursor
					.getColumnIndex("friendName")));
			entity.setChatContent(cursor.getString(cursor
					.getColumnIndex("chatContent")));
			entity.setChatCreatTime(cursor.getString(cursor
					.getColumnIndex("chatCreatTime")));
			entity.setMsg_num(cursor.getInt(cursor.getColumnIndex("msg_num")));
			entity.setMsgtype(cursor.getInt(cursor.getColumnIndex("Msgtype")));
			list.add(entity);
		}
		cursor.close();
		return list;
	}

	/**
	 * 得到最近聊天的一个好友
	 * 
	 * @param friendId
	 * @return
	 */
	public ChatInfoEntity getLaticeChatItem(String friendId) {
		ChatInfoEntity entity = new ChatInfoEntity();
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select friendId, friendName, chatContent, chatCreatTime, msg_num, Msgtype from latice_chat_table where userEmail = ? and friendId = ?";
		Cursor cursor = db.rawQuery(sql, new String[] {
				ClientManager.clientEmail, friendId });
		cursor.moveToNext();
		entity.setFriendId(cursor.getString(cursor.getColumnIndex("friendId")));
		entity.setFriendName(cursor.getString(cursor
				.getColumnIndex("friendName")));
		entity.setChatContent(cursor.getString(cursor
				.getColumnIndex("chatContent")));
		entity.setChatCreatTime(cursor.getString(cursor
				.getColumnIndex("chatCreatTime")));
		entity.setMsg_num(cursor.getInt(cursor.getColumnIndex("msg_num")));
		entity.setMsgtype(cursor.getInt(cursor.getColumnIndex("Msgtype")));
		return entity;
	}

	/************************************群组聊天邀请消息************************************************/
	public void createInviteTable() {
		String tName = "invitations";
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "create table "
				+ tName
				+ "( groupId text primary key, invitorName text , groupName text, topic text, groupIconPath text)";
		db.execSQL(sql);
	}

	public boolean insertInvite(InvitationInfoEntity entity) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			String sql = "insert into invitations (groupId, invitorName, groupName, topic, groupIconPath) values(?, ?, ?, ? ,?)";
			String[] args = { entity.getGroupId(), entity.getInvitorName(),
					entity.getGroupName(), entity.getTopic(),
					entity.getGroupIconPath() };
			db.execSQL(sql, args);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<InvitationInfoEntity> getInvits() {
		List<InvitationInfoEntity> list = new ArrayList<InvitationInfoEntity>();
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from invitations";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			InvitationInfoEntity entity = new InvitationInfoEntity();
			entity.setGroupId(cursor.getString(cursor.getColumnIndex("groupId")));
			entity.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
			entity.setInvitorName(cursor.getString(cursor.getColumnIndex("invitorName")));
			entity.setTopic(cursor.getString(cursor.getColumnIndex("topic")));
			entity.setGroupIconPath(cursor.getString(cursor.getColumnIndex("groupIconPath")));
			list.add(entity);
		}
		cursor.close();
		return list;
	}

	public void deleteInvite(String groupId) {
		String sql = "delete from invitations where groupId = ?";
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL(sql, new String[] { groupId });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/************************************群组聊天邀请消息************************************************/
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
