package com.find1x.outsmart.analysis;

import java.util.ArrayList;

import com.find1x.outsmart.db.DatabaseHelper;
import com.find1x.outsmart.segmentation.NoPunctuation;
import com.find1x.outsmart.segmentation.NoStopword;
import com.find1x.outsmart.segmentation.SegmentationByBloom;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class GetUserLocation {
	String[] str;
	String text;

	public GetUserLocation(String text) {
		this.text=text;
		SegmentationByBloom seg = new SegmentationByBloom();
		ArrayList<String> location = seg.getWordsbyArrayList(text);
		NoPunctuation np = new NoPunctuation();
		NoStopword ns = new NoStopword();
		np.getNoPunctuationWords(location);
		ns.getNoStopwordWords(location);
		String[] str = location.toArray(new String[location.size()]);
		this.str = str;
	}

	public String[] getLocation() {
		return str;
	}

	public String getUserLocation(Context context) {
		// 建立数据库
		SQLiteDatabase db;
		DatabaseHelper dbHelper = new DatabaseHelper(context, "user.db3");
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user", null);
		int num = cursor.getCount();
		final String[] location = new String[num];
		int i = 0;
		while (cursor.moveToNext()) {
			location[i] = cursor.getString(cursor
					.getColumnIndex("location"));
			i++;
		}
		db.close();
		for (i=0;i<location.length;i++)
			if (text.contains(location[i]))
				return location[i];
		return "请选择地点";
	}
}
