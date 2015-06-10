package com.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 * 琛ㄦ儏杞夋彌宸ュ叿
 */
public class FaceConversionUtil {

	/** 姣忎竴椤佃〃鎯呯殑涓暟 */
	private int pageSize = 21;

	private static FaceConversionUtil mFaceConversionUtil;

	/** 淇濆瓨浜庡唴瀛樹腑鐨勮〃鎯匟ashMap */
	private HashMap<String, String> emojiMap = new HashMap<String, String>();

	/** 淇濆瓨浜庡唴瀛樹腑鐨勮〃鎯呴泦鍚� */
	public List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();

	/** 琛ㄦ儏鍒嗛〉鐨勭粨鏋滈泦鍚� */
	public List<List<ChatEmoji>> emojiLists = new ArrayList<List<ChatEmoji>>();

	private FaceConversionUtil() {

	}

	public static FaceConversionUtil getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new FaceConversionUtil();
		}
		return mFaceConversionUtil;
	}

	/**
	 * 寰楀埌涓�涓猄panableString瀵硅薄锛岄�氳繃浼犲叆鐨勫瓧绗︿覆,骞惰繘琛屾鍒欏垽鏂�
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		// 姝ｅ垯琛ㄨ揪寮忔瘮閰嶅瓧绗︿覆閲屾槸鍚﹀惈鏈夎〃鎯咃紝濡傦細 鎴戝ソ[寮�蹇僝鍟�
		String zhengze = "\\[[^\\]]+\\]";
		// 閫氳繃浼犲叆鐨勬鍒欒〃杈惧紡鏉ョ敓鎴愪竴涓猵attern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 娣诲姞琛ㄦ儏
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId,
			String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				imgId);
		bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
		ImageSpan imageSpan = new ImageSpan(context, bitmap);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 瀵箂panableString杩涜姝ｅ垯鍒ゆ柇锛屽鏋滅鍚堣姹傦紝鍒欎互琛ㄦ儏鍥剧墖浠ｆ浛
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// 杩斿洖绗竴涓瓧绗︾殑绱㈠紩鐨勬枃鏈尮閰嶆暣涓鍒欒〃杈惧紡,ture 鍒欑户缁�掑綊
			if (matcher.start() < start) {
				continue;
			}
			String value = emojiMap.get(key);
			if (TextUtils.isEmpty(value)) {
				continue;
			}
			int resId = context.getResources().getIdentifier(value, "drawable",
					context.getPackageName());
			// 閫氳繃涓婇潰鍖归厤寰楀埌鐨勫瓧绗︿覆鏉ョ敓鎴愬浘鐗囪祫婧恑d
			// Field field=R.drawable.class.getDeclaredField(value);
			// int resId=Integer.parseInt(field.get(null).toString());
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);
				bitmap = Bitmap.createScaledBitmap(bitmap, 130, 130, true);
				// 閫氳繃鍥剧墖璧勬簮id鏉ュ緱鍒癰itmap锛岀敤涓�涓狪mageSpan鏉ュ寘瑁�
				ImageSpan imageSpan = new ImageSpan(bitmap);
				// 璁＄畻璇ュ浘鐗囧悕瀛楃殑闀垮害锛屼篃灏辨槸瑕佹浛鎹㈢殑瀛楃涓茬殑闀垮害
				int end = matcher.start() + key.length();
				// 灏嗚鍥剧墖鏇挎崲瀛楃涓蹭腑瑙勫畾鐨勪綅缃腑
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					// 濡傛灉鏁翠釜瀛楃涓茶繕鏈獙璇佸畬锛屽垯缁х画銆傘��
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

	public void getFileText(Context context) {
		ParseData(FileUtils.getEmojiFile(context), context);
	}

	/**
	 * 瑙ｆ瀽瀛楃
	 * 
	 * @param data
	 */
	private void ParseData(List<String> data, Context context) {
		if (data == null) {
			return;
		}
		ChatEmoji emojEentry;
		try {
			for (String str : data) {
				String[] text = str.split(",");
				String fileName = text[1]
						.substring(0, text[1].lastIndexOf("."));
				emojiMap.put(text[0], fileName);
				int resID = context.getResources().getIdentifier(fileName,
						"drawable", context.getPackageName());

				if (resID != 0) {
					emojEentry = new ChatEmoji();
					emojEentry.setId(resID);
					emojEentry.setCharacter(text[0]);
					emojEentry.setFaceName(fileName);
					emojis.add(emojEentry);
				}
			}
			int pageCount = (int) Math.ceil(emojis.size() / 20 + 0.1);

			for (int i = 0; i < pageCount; i++) {
				emojiLists.add(getOnePageFaces(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 鏍规嵁椤垫暟鑾峰彇涓�椤电殑琛ㄦ儏鏁版嵁
	 * 
	 * @param page
	 * @return
	 */
	public List<ChatEmoji> getOnePageFaces(int page) {
		int startIndex = (page - 1) * pageSize;
		int endIndex = startIndex + pageSize;
		if (endIndex > emojis.size()) {
			endIndex = emojis.size();
		}
		// 涓嶈繖涔堝啓锛屼細鍦╲iewpager鍔犺浇涓姤闆嗗悎鎿嶄綔寮傚父锛屾垜涔熶笉鐭ラ亾涓轰粈涔�
		List<ChatEmoji> list = new ArrayList<ChatEmoji>();
		list.addAll(emojis.subList(startIndex, endIndex));
		// if (list.size() < pageSize) {
		for (int i = list.size(); i < pageSize; i++) {
			ChatEmoji object = new ChatEmoji();
			list.add(object);
		}
		// }
		// if (list.size() == pageSize) {
		// ChatEmoji object = new ChatEmoji();
		// object.setId(R.drawable.delete_face);
		// list.add(object);
		// }
		return list;
	}

	/**
	 * 鏍规嵁褰撳墠椤靛拰鐐瑰嚮position杩斿洖鐐瑰嚮琛ㄦ儏
	 * @param page_index
	 * @param select_position
	 * @return
	 */
	public ChatEmoji getSelectEmoji(int page_index, int select_position) {
		int position = (page_index - 1)*pageSize + select_position;
		return emojis.get(position);
	}
	
	public HashMap<String, String> getFaceMap(){
		return emojiMap;
	}
}