package com.freind;

import java.util.Comparator;

public class PinyinComparator implements Comparator<FriendBean> {

	public int compare(FriendBean o1, FriendBean o2) {
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
