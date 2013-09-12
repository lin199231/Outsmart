package com.find1x.meetingreminder.segmentation;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class SegmentationByString {
	String str;

	public SegmentationByString() {
		long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		//str = GetDic.getString();
		str = GetDic.getString();
		Log.i("导入String时间", Calendar.getInstance().getTimeInMillis() - ftime
				+ "");// 结束时间
	}

	public String[] getWords(String str) {
		long ftime = Calendar.getInstance().getTimeInMillis();
		ArrayList<String> list = getWordsbyArrayList(str);
		Log.i("分词时间", Calendar.getInstance().getTimeInMillis() - ftime + "");// 结束时间
		return (String[]) list.toArray(new String[list.size()]);
	}

	public ArrayList<String> getWordsbyArrayList(String str) {
		ArrayList<String> list = new ArrayList<String>();
		String subString = null;
		int max = 4;
		int i;
		int point = str.length();
		while (point != 0) {
			if (point - max < 0)
				subString = str.substring(0, point);
			else
				subString = str.substring(point - max, point);
			for (i = 0; i < subString.length(); i++) {
				String tempString = subString.substring(i, subString.length());
				if (isInDicByString(tempString) || i == subString.length() - 1) {
					list.add(tempString);
					point = point - subString.length() + i;
					break;
				}
			}
		}
		return list;
	}

	public boolean isInDicByString(String source) {
		int index;
		index = str.indexOf("\n"+source + "\n");//快速分词
		// index = str.indexOf("\r\n"+source + "\r\n");//高精确分词，但是效率很低
		if (index != -1) {
			// Log.i(source, str.substring(index - 1, index));
			// Log.i(source, "找到");
			return true;
		} else {
			// Log.i(source, "没找到");
			return false;
		}
	}
}
