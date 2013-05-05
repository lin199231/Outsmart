package findix.meetingreminder.analysis;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import findix.meetingreminder.MainActivity;
import findix.meetingreminder.db.DatabaseHelper;
import findix.meetingreminder.segmentation.NoPunctuation;
import findix.meetingreminder.segmentation.NoStopword;
import findix.meetingreminder.segmentation.SegmentationByBloom;

public class GetUserLocation {
	String[] str;

	public GetUserLocation(String text) {
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
		return "请选择地点";
	}
}
