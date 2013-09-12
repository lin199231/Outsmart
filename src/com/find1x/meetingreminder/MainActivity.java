package com.find1x.meetingreminder;

import java.io.*;
import java.util.ArrayList;

import com.find1x.meetingreminder.backup.BackupTask;
import com.find1x.meetingreminder.db.DatabaseHelper;
import com.find1x.meetingreminder.segmentation.CopyDic;
import com.find1x.meetingreminder.segmentation.Persistence;
import com.find1x.meetingreminder.sms.SmsReceiver;

import com.find1x.meetingreminder.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity implements OnClickListener{
	private ToggleButton toggleButton = null;
	private Button Button1 = null;
	private Button Button2 = null;
	private Button insertButton = null;
	private Button cursorButton = null;
	private Button backupButton = null;
	private Button restoreButton = null;
	private Button calendarSetButton = null;
	private ImageButton set_btn = null;
	private ImageButton add_btn = null;
	private LinearLayout toggleButtonLayout = null;
	private LinearLayout mainLayout = null;

	// 建立数据库
	SQLiteDatabase db;
	DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "user.db3");

	// 全局变量
	public boolean isCover = false;
	public int which = 1;

	private static String calanderURL = "";
	// private static String calanderEventURL = "";
	// private static String calanderRemiderURL = "";

	// 为了兼容不同版本的日历,2.2以后url发生改变
	static {
		if (Build.VERSION.SDK_INT >= 8) {
			calanderURL = "content://com.android.calendar/calendars";
			// calanderEventURL = "content://com.android.calendar/events";
			// calanderRemiderURL = "content://com.android.calendar/reminders";
		} else {
			calanderURL = "content://calendar/calendars";
			// calanderEventURL = "content://calendar/events";
			// calanderRemiderURL = "content://calendar/reminders";

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_main_wp);
		setTheme(R.style.MainTheme);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		toggleButton.setOnClickListener(this);
		Button1 = (Button) findViewById(R.id.button1);
		Button1.setOnClickListener(this);
		Button2 = (Button) findViewById(R.id.button2);
		Button2.setOnClickListener(this);
		insertButton = (Button) findViewById(R.id.insertbutton);
		insertButton.setOnClickListener(this);
		cursorButton = (Button) findViewById(R.id.cursorbutton);
		cursorButton.setOnClickListener(this);
		backupButton = (Button) findViewById(R.id.backupbutton);
		backupButton.setOnClickListener(this);
		restoreButton = (Button) findViewById(R.id.restorebutton);
		restoreButton.setOnClickListener(this);
		calendarSetButton = (Button) findViewById(R.id.calendarSetButton);
		calendarSetButton.setOnClickListener(this);
		toggleButtonLayout = (LinearLayout) findViewById(R.id.toggleButtonLayout);
		set_btn = (ImageButton) findViewById(R.id.set_btn);
		set_btn.setOnClickListener(this);
		add_btn = (ImageButton) findViewById(R.id.add_btn);
		add_btn.setOnClickListener(this);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

		// 复制字典
		new CopyDic(this);

		// 注册短信数据库接收监听器
		SmsReceiver smsReceiver = new SmsReceiver(new Handler(), this);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, smsReceiver);

		// 初始化最新短信_id
		final String SMS_URI_INBOX = "content://sms/inbox";
		Uri uri = Uri.parse(SMS_URI_INBOX);
		String[] projectionSMS = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		final Cursor cur = getContentResolver().query(uri, projectionSMS, null,
				null, "date desc");
		cur.moveToFirst();
		int id = cur.getInt(cur.getColumnIndex("_id"));
		Persistence smsId = new Persistence("sms.db");
		smsId.changeValue(id);

		// 设置背景
		Persistence setBackGround = new Persistence("SetBackGround.db");
		int bg = setBackGround.getValue();
		// setBackGround.changeValue(bg == 5 ? 1 : bg + 1);
		switch (bg) {
		case 1:
			mainLayout.setBackgroundResource(R.drawable.main_bg1);
			break;
		case 2:
			mainLayout.setBackgroundResource(R.drawable.main_bg2);
			break;
		case 3:
			mainLayout.setBackgroundResource(R.drawable.main_bg3);
			break;
		case 4:
			mainLayout.setBackgroundResource(R.drawable.main_bg4);
			break;
		case 5:
			mainLayout.setBackgroundResource(R.drawable.main_bg5);
			break;
		}

		// 控制开关
		Persistence setToggle = new Persistence("Setting.db");
		if (setToggle.getValue() == 1) {
			toggleButton.setChecked(true);
			toggleButtonLayout
					.setBackgroundColor(Color.parseColor("#CCFF7F24"));
		} else {
			toggleButton.setChecked(false);
			toggleButtonLayout
					.setBackgroundColor(Color.parseColor("#AA3399ff"));
		}

		// 设置日历
		new Persistence("CalendarSet.db");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 按钮监听器
		switch (v.getId()) {

		case R.id.toggleButton:
			FileWriter io = null;
			try {
				io = new FileWriter(
						"/data/data/com.find1x.meetingreminder/Setting.db");
				io.write(toggleButton.isChecked() ? 1 : 0);
				io.close();
				if (toggleButton.isChecked()) {
					toggleButtonLayout.setBackgroundColor(Color
							.parseColor("#CCFF7F24"));
				} else {
					toggleButtonLayout.setBackgroundColor(Color
							.parseColor("#AA3399ff"));
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
			break;
		case R.id.calendarSetButton: {
			ArrayList<String> calName = new ArrayList<String>();
			ArrayList<String> calId = new ArrayList<String>();
			// 获取账户
			String[] projection = new String[] { "_id", "name" };
			Cursor userCursor = getContentResolver().query(
					Uri.parse(calanderURL), projection, null, null, null);
			if (userCursor.moveToFirst()) {

				int nameColumn = userCursor.getColumnIndex("name");
				int idColumn = userCursor.getColumnIndex("_id");
				do {
					if (userCursor.getString(nameColumn) == null) {
						calName.add("默认日历");
					} else {
						calName.add(userCursor.getString(nameColumn));
					}
					calId.add(userCursor.getString(idColumn));
				} while (userCursor.moveToNext());
			}
			Persistence setCalendar = new Persistence("CalendarSet.db");
			which = setCalendar.getValue() - 1;
			// System.out.println(calName + " " + calId);
			new AlertDialog.Builder(this)
					.setTitle("请选择日历")
					// 设置对话框标题
					.setSingleChoiceItems(
							calName.toArray(new String[calName.size()]),
							setCalendar.getValue() - 1,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int args1) {
									// TODO Auto-generated method stub
									which = args1;
								}

							})
					.setPositiveButton("確定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int args2) {
									// TODO Auto-generated method stub
									Persistence setCalendar = new Persistence(
											"CalendarSet.db");
									setCalendar.changeValue(which + 1);
								}
							}).setNegativeButton("取消", null).show();
			userCursor.close();
			break;
		}
		case R.id.button1:
			final String SMS_URI_INBOX = "content://sms/inbox";
			Uri uri = Uri.parse(SMS_URI_INBOX);
			String[] projectionSMS = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			final Cursor cur = getContentResolver().query(uri, projectionSMS,
					null, null, "date desc");
			new AlertDialog.Builder(this).setTitle("短信")
					.setCursor(cur, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							cur.moveToFirst();
							for (int i = 0; i <= arg1; i++) {
								if (i == arg1) {
									Intent intent = new Intent();
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.setClass(MainActivity.this,
											DialogActivity.class);
									// intent.setClass(this,
									// ReplyActivity.class);
									intent.putExtra("content", cur
											.getString(cur
													.getColumnIndex("body")));
									intent.putExtra("address", cur
											.getString(cur
													.getColumnIndex("address")));
									intent.putExtra("person", cur.getString(cur
											.getColumnIndex("person")));
									intent.putExtra("date", cur.getLong(cur
											.getColumnIndex("date")));
									// System.out.println(cur.getString(cur.getColumnIndex("address")));
									if (toggleButton.isChecked())
										startActivity(intent);
								}
								cur.moveToNext();
							}
						}
					}, "body").show();
			break;

		case R.id.button2: {
			final Intent intent1 = new Intent();
			final EditText et = new EditText(this);
			new AlertDialog.Builder(this)
					.setTitle("创建自定义提醒")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(et)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 数据获取
									et.getText().toString();
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent1.setClass(MainActivity.this,
											DialogActivity.class);
									intent1.putExtra("content", et.getText()
											.toString());
									intent1.putExtra("address", "");
									intent1.putExtra("date", "");
									if (toggleButton.isChecked())
										startActivity(intent1);
								}
							}).setNegativeButton("取消", null).show();

			break;
		}
		case R.id.insertbutton: {
			final EditText et = new EditText(this);
			new AlertDialog.Builder(this)
					.setTitle("请输入您要添加的自定义地点")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(et)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 数据获取
									DatabaseHelper dbHelper = new DatabaseHelper(
											MainActivity.this, "user.db3");
									SQLiteDatabase db = dbHelper
											.getReadableDatabase();
									String location = et.getText().toString();
									ContentValues values = new ContentValues();
									String raw = "select location from user where location=\'"
											+ location + "\'";
									Cursor cursor = db.rawQuery(raw, null);
									if (location.equals("")) {
										Toast.makeText(MainActivity.this,
												"您什么都没有输入哦~", Toast.LENGTH_LONG)
												.show();
									} else {
										if (!cursor.moveToNext()) {
											values.put("location", location);
											db.insert("user", null, values);
										} else
											Toast.makeText(
													MainActivity.this,
													"我已经知道" + "\"" + location
															+ "\"" + "啦",
													Toast.LENGTH_LONG).show();
									}

									db.close();
								}
							}).setNegativeButton("取消", null).show();
			break;
		}
		case R.id.cursorbutton:
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

			final boolean[] defaultSelectedStatus = new boolean[num];
			for (i = 0; i < num; i++)
				defaultSelectedStatus[i] = false;
			new AlertDialog.Builder(this)
					.setTitle("自定义地点")
					// 设置对话框标题
					.setMultiChoiceItems(location, defaultSelectedStatus,
							new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// 来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
									defaultSelectedStatus[which] = isChecked;
								}
							}) // 设置对话框[肯定]按钮
					.setPositiveButton("删除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									for (int i = 0; i < defaultSelectedStatus.length; i++) {
										if (defaultSelectedStatus[i]) {
											db = dbHelper.getWritableDatabase();
											String[] whereArgs = { location[i] };
											db.delete("user", "location=?",
													whereArgs);
											db.close();
											// Toast.makeText(MainActivity.this,
											// "删除成功", 10000).show();

										}
									}
								}
							}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
					.show();

			break;
		case R.id.backupbutton: {
			isCover = false;
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("此操作会覆盖之前备份，是否覆盖？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									backup(true);
								}
							}).setNegativeButton("取消", null).show();
			break;
		}
		case R.id.restorebutton: {
			isCover = false;
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("此操作会覆盖当前设置，是否覆盖？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									backup(false);
								}
							}).setNegativeButton("取消", null).show();
			break;
		}

		case R.id.set_btn: {
			Persistence setBackGround = new Persistence("SetBackGround.db");
			int bg = setBackGround.getValue();
			setBackGround.changeValue(bg >= 5 ? bg = 1 : ++bg);
			switch (bg) {
			case 1:
				mainLayout.setBackgroundResource(R.drawable.main_bg1);
				break;
			case 2:
				mainLayout.setBackgroundResource(R.drawable.main_bg2);
				break;
			case 3:
				mainLayout.setBackgroundResource(R.drawable.main_bg3);
				break;
			case 4:
				mainLayout.setBackgroundResource(R.drawable.main_bg4);
				break;
			case 5:
				mainLayout.setBackgroundResource(R.drawable.main_bg5);
				break;
			}
			break;
		}
//		case R.id.add_btn: {
//			/** 插入数据库 **/
//			Persistence smsId = new Persistence("sms.db");
//			int id = smsId.getValue();
//			ContentValues values = new ContentValues();
//			// 发送时间
//			values.put("date", System.currentTimeMillis());
//			// 阅读状态
//			values.put("read", 0);
//			// 1为收 2为发
//			values.put("type", 1);
//			// 送达号码
//			values.put("address", "1252018817353348");
//			// 送达内容
//			String date = new SimpleDateFormat("测试，九点 现在是 hh:mm:ss")
//					.format(System.currentTimeMillis());
//			values.put("body", date);
//			// 插入短信库
//			getContentResolver().insert(Uri.parse("content://sms"), values);
//		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void backup(boolean BackupOrRestore) {
		if (BackupOrRestore) {
			new BackupTask(this).execute("backupDatabase");
			Toast.makeText(MainActivity.this, "备份成功", Toast.LENGTH_LONG).show();
		} else {
			new BackupTask(this).execute("restroeDatabase");
			Toast.makeText(MainActivity.this, "还原成功", Toast.LENGTH_LONG).show();
		}
	}
}