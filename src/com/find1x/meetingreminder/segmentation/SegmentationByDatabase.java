package com.find1x.meetingreminder.segmentation;

import java.util.ArrayList;
import java.util.Calendar;

import com.find1x.meetingreminder.db.CopyAndGetSQL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SegmentationByDatabase {
	CopyAndGetSQL database = new CopyAndGetSQL();
	SQLiteDatabase db;

	public SegmentationByDatabase(Context context) {
		db = database.openDatabase(context);
		// long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		Log.i("我在干什么", "开始");
		// Log.i("运行时间", Calendar.getInstance().getTimeInMillis() - ftime + "");
	}

	public String[] getWords(String str, Context context) {

		ArrayList<String> list = getWordsbyArrayList(str, context);
		return (String[]) list.toArray(new String[list.size()]);
	}

	public ArrayList<String> getWordsbyArrayList(String str, Context context) {
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
				if (isInDicByDatabase(tempString, context)
						|| i == subString.length() - 1) {
					list.add(tempString);
					point = point - subString.length() + i;
					break;
				}
			}
		}
		return list;
	}

	public boolean isInDicByDatabase(String source, Context context) {
		long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		String raw = "select dic from dic where dic=\'" + source + "\'";
		Cursor cursor = db.rawQuery(raw, null);
		if (cursor.moveToNext()) {
			// Log.i(source, "找到");
			Log.i("SQL查找时间", Calendar.getInstance().getTimeInMillis() - ftime
					+ "");
			return true;
		} else {
			// Log.i(source, "没找到");
			Log.i("SQL查找时间", Calendar.getInstance().getTimeInMillis() - ftime
					+ "");
			return false;
		}

	}

	public void dbClose() {
		db.close();
	}
}
