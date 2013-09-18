package com.find1x.outsmart;

import java.util.ArrayList;

import org.jraf.android.backport.switchwidget.Switch;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.find1x.outsmart.backup.BackupTask;
import com.find1x.outsmart.db.DatabaseHelper;
import com.find1x.outsmart.segmentation.CopyDic;
import com.find1x.outsmart.segmentation.Persistence;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends SherlockPreferenceActivity implements
		OnPreferenceClickListener {
	private Switch actionSwitch = null;

	private Preference showlastSMS;
	private Preference showSMS;
	private Preference editSMS;
	private Preference chooseCalendar;
	private Preference addLocation;
	private Preference deleteLocation;
	private Preference backup;
	private Preference restore;
	// 建立数据库
	SQLiteDatabase db;
	DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "user.db3");

	// 全局变量
	public boolean isCover = false;
	public int which = 1;

	private static String calanderURL;
	// private static String calanderEventURL = "";
	// private static String calanderRemiderURL = "";

	// 为了兼容不同版本的日历,2.2以后url发生改变
	static {
		if (Build.VERSION.SDK_INT >= 8) {
			setCalanderURL("content://com.android.calendar/calendars");
			// calanderEventURL = "content://com.android.calendar/events";
			// calanderRemiderURL = "content://com.android.calendar/reminders";
		} else {
			setCalanderURL("content://calendar/calendars");
			// calanderEventURL = "content://calendar/events";
			// calanderRemiderURL = "content://calendar/reminders";

		}

	}

	public static String getCalanderURL() {
		return calanderURL;
	}

	public static void setCalanderURL(String calanderURL) {
		MainActivity.calanderURL = calanderURL;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化设置界面
		addPreferencesFromResource(R.xml.preference);
		// 复制字典
		new CopyDic(this);

		// 注册短信数据库接收监听器
		//SmsReceiver smsReceiver = new SmsReceiver(new Handler(), this);
		//this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, smsReceiver);
		//启动短信数据库监听器服务
		startService(new Intent(this, SmsReceiverService.class));  

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

		// 设置日历
		new Persistence("CalendarSet.db");

		// 注册preference组件
		showlastSMS = (Preference) findPreference(getString(R.string.show_last_SMS));
		showSMS = (Preference) findPreference(getString(R.string.show_SMS));
		editSMS = (Preference) findPreference(getString(R.string.edit_SMS));
		chooseCalendar = (Preference) findPreference(getString(R.string.choose_calendar));
		addLocation = (Preference) findPreference(getString(R.string.add_location));
		deleteLocation = (Preference) findPreference(getString(R.string.delete_location));
		backup = (Preference) findPreference(getString(R.string.backup));
		restore = (Preference) findPreference(getString(R.string.restore));
		// 注册Preference监听
		showlastSMS.setOnPreferenceClickListener(this);
		showSMS.setOnPreferenceClickListener(this);
		editSMS.setOnPreferenceClickListener(this);
		chooseCalendar.setOnPreferenceClickListener(this);
		addLocation.setOnPreferenceClickListener(this);
		deleteLocation.setOnPreferenceClickListener(this);
		backup.setOnPreferenceClickListener(this);
		restore.setOnPreferenceClickListener(this);
	}

	/*
	 * ==========================================================================
	 * ============== Preference 监听
	 * ======================================================
	 * ==================================
	 */
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(getString(R.string.show_last_SMS))) {
			showlastSMSDo();
		} else if (preference.getKey().equals(getString(R.string.show_SMS))) {
			ShowSMSDo();
		} else if (preference.getKey().equals(getString(R.string.edit_SMS))) {
			editSMSDo();
		} else if (preference.getKey().equals(
				getString(R.string.choose_calendar))) {
			chooseCalendarDo();
		} else if (preference.getKey().equals(getString(R.string.add_location))) {
			addLocationDo();
		} else if (preference.getKey().equals(
				getString(R.string.delete_location))) {
			deleteLocationDo();
		} else if (preference.getKey().equals(getString(R.string.backup))) {
			backupDo();
		} else if (preference.getKey().equals(getString(R.string.restore))) {
			restoreDo();
		}
		// 返回true表示允许改变
		return true;
	}

	// 监听响应
	private void showlastSMSDo() {
		final String SMS_URI_INBOX = "content://sms/inbox";
		Uri uri = Uri.parse(SMS_URI_INBOX);
		String[] projectionSMS = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		final Cursor cur = getContentResolver().query(uri, projectionSMS, null,
				null, "date desc");
		cur.moveToFirst();
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(MainActivity.this, DialogActivity.class);
		// intent.setClass(this,
		// ReplyActivity.class);
		intent.putExtra("content", cur.getString(cur.getColumnIndex("body")));
		intent.putExtra("address", cur.getString(cur.getColumnIndex("address")));
		intent.putExtra("person", cur.getString(cur.getColumnIndex("person")));
		intent.putExtra("date", cur.getLong(cur.getColumnIndex("date")));
		// System.out.println(cur.getString(cur.getColumnIndex("address")));
		if (actionSwitch.isChecked())
			startActivity(intent);
	}

	private void ShowSMSDo() {
		final String SMS_URI_INBOX = "content://sms/inbox";
		Uri uri = Uri.parse(SMS_URI_INBOX);
		String[] projectionSMS = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		final Cursor cur = getContentResolver().query(uri, projectionSMS, null,
				null, "date desc");
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
								intent.putExtra("content", cur.getString(cur
										.getColumnIndex("body")));
								intent.putExtra("address", cur.getString(cur
										.getColumnIndex("address")));
								intent.putExtra("person", cur.getString(cur
										.getColumnIndex("person")));
								intent.putExtra("date",
										cur.getLong(cur.getColumnIndex("date")));
								// System.out.println(cur.getString(cur.getColumnIndex("address")));
								if (actionSwitch.isChecked())
									startActivity(intent);
							}
							cur.moveToNext();
						}
					}
				}, "body").show();
	}

	private void editSMSDo() {
		final Intent intent1 = new Intent();
		final EditText et = new EditText(this);
		new AlertDialog.Builder(this).setTitle("创建自定义提醒")
				.setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 数据获取
						et.getText().toString();
						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent1.setClass(MainActivity.this,
								DialogActivity.class);
						intent1.putExtra("content", et.getText().toString());
						intent1.putExtra("address", "");
						intent1.putExtra("date", "");
						if (actionSwitch.isChecked())
							startActivity(intent1);
					}
				}).setNegativeButton("取消", null).show();
	}

	private void chooseCalendarDo() {
		ArrayList<String> calName = new ArrayList<String>();
		ArrayList<String> calId = new ArrayList<String>();
		// 获取账户
		String[] projection = new String[] { "_id", "name" };
		Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL),
				projection, null, null, null);
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
							public void onClick(DialogInterface arg0, int args1) {
								// TODO Auto-generated method stub
								which = args1;
							}

						})
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int args2) {
						// TODO Auto-generated method stub
						Persistence setCalendar = new Persistence(
								"CalendarSet.db");
						setCalendar.changeValue(which + 1);
					}
				}).setNegativeButton("取消", null).show();
		userCursor.close();
	}

	private void addLocationDo() {
		final EditText et = new EditText(this);
		new AlertDialog.Builder(this).setTitle("请输入您要添加的自定义地点")
				.setIcon(android.R.drawable.ic_dialog_info).setView(et)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 数据获取
						DatabaseHelper dbHelper = new DatabaseHelper(
								MainActivity.this, "user.db3");
						SQLiteDatabase db = dbHelper.getReadableDatabase();
						String location = et.getText().toString();
						ContentValues values = new ContentValues();
						String raw = "select location from user where location=\'"
								+ location + "\'";
						Cursor cursor = db.rawQuery(raw, null);
						if (location.equals("")) {
							Toast.makeText(MainActivity.this, "您什么都没有输入哦~",
									Toast.LENGTH_LONG).show();
						} else {
							if (!cursor.moveToNext()) {
								values.put("location", location);
								db.insert("user", null, values);
							} else
								Toast.makeText(MainActivity.this,
										"我已经知道" + "\"" + location + "\"" + "啦",
										Toast.LENGTH_LONG).show();
						}

						db.close();
					}
				}).setNegativeButton("取消", null).show();
	}

	private void deleteLocationDo() {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user", null);
		int num = cursor.getCount();
		final String[] location = new String[num];
		int i = 0;
		while (cursor.moveToNext()) {
			location[i] = cursor.getString(cursor.getColumnIndex("location"));
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
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						for (int i = 0; i < defaultSelectedStatus.length; i++) {
							if (defaultSelectedStatus[i]) {
								db = dbHelper.getWritableDatabase();
								String[] whereArgs = { location[i] };
								db.delete("user", "location=?", whereArgs);
								db.close();
								// Toast.makeText(MainActivity.this,
								// "删除成功", 10000).show();

							}
						}
					}
				}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
				.show();
	}

	private void backupDo() {
		isCover = false;
		new AlertDialog.Builder(this).setTitle("警告")
				.setMessage("此操作会覆盖之前备份，是否覆盖？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						backup(true);
					}
				}).setNegativeButton("取消", null).show();
	}

	private void restoreDo() {
		isCover = false;
		new AlertDialog.Builder(this).setTitle("警告")
				.setMessage("此操作会覆盖当前设置，是否覆盖？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						backup(false);
					}
				}).setNegativeButton("取消", null).show();
	}
	/*
	 * ==========================================================================
	 * ============== Menu
	 * ======================================================
	 * ==================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		MenuItem switchItem = menu.findItem(R.id.action_switch);
		actionSwitch = (Switch) switchItem.getActionView();
		// 控制开关
		Persistence setSwitch = new Persistence("Setting.db");
		if (setSwitch.getValue() == 1) {
			actionSwitch.setChecked(true);
		} else {
			actionSwitch.setChecked(false);
		}
		actionSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
		return super.onCreateOptionsMenu(menu);
	}

	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Persistence setSwitch = new Persistence("Setting.db");
			setSwitch.changeValue(actionSwitch.isChecked() ? 1 : 0);
		}
	};

	/*
	 * ==========================================================================
	 * ============== 备份还原
	 * ======================================================
	 * ==================================
	 */
	public void backup(boolean BackupOrRestore) {
		if (BackupOrRestore) {
			new BackupTask(this).execute("backupDatabase");
			Toast.makeText(this, "备份成功", Toast.LENGTH_LONG).show();
		} else {
			new BackupTask(this).execute("restroeDatabase");
			Toast.makeText(this, "还原成功", Toast.LENGTH_LONG).show();
		}
	}

}