package findix.meetingreminder;

import java.io.*;
import java.util.ArrayList;

import com.adsmogo.adapters.AdsMogoAdapter;
import com.adsmogo.adapters.AdsMogoCustomEventPlatformEnum;
import com.adsmogo.adview.AdsMogoLayout;
import com.adsmogo.controller.listener.AdsMogoListener;
import com.adsmogo.util.AdsMogoUtil;

import findix.meetingreminder.backup.BackupTask;
import findix.meetingreminder.db.DatabaseHelper;
import findix.meetingreminder.segmentation.CopyDic;
import findix.meetingreminder.segmentation.Persistence;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity implements OnClickListener,
		AdsMogoListener {
	private ToggleButton toggleButton = null;
	private Button Button1 = null;
	private Button Button2 = null;
	private Button insertButton = null;
	private Button cursorButton = null;
	private Button backupButton = null;
	private Button restoreButton = null;
	private Button calendarSetButton = null;
	private ImageButton set_btn = null;
	private LinearLayout toggleButtonLayout = null;
	private LinearLayout mainLayout = null;

	AdsMogoLayout adsMogoLayout;

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
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
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
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

		new CopyDic(this);
		// 设置背景
		Persistence setBackGround = new Persistence("SetBackGround.db");
		int bg = setBackGround.getValue();
		// setBackGround.changeValue(bg == 5 ? 1 : bg + 1);
		switch (bg) {
		case 1:
			mainLayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_bg1));
			break;
		case 2:
			mainLayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_bg2));
			break;
		case 3:
			mainLayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_bg3));
			break;
		case 4:
			mainLayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_bg4));
			break;
		case 5:
			mainLayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.main_bg5));
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

		// 芒果广告
		adsMogoLayout = ((AdsMogoLayout) this
				.findViewById(R.id.adsMogoView_main));
		adsMogoLayout.setAdsMogoListener(this);
		adsMogoLayout.downloadIsShowDialog = true;
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
						"/data/data/findix.meetingreminder/Setting.db");
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
									intent.putExtra("sender", cur.getString(cur
											.getColumnIndex("address")));
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
									intent1.putExtra("sender", "");
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
				mainLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.main_bg1));
				break;
			case 2:
				mainLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.main_bg2));
				break;
			case 3:
				mainLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.main_bg3));
				break;
			case 4:
				mainLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.main_bg4));
				break;
			case 5:
				mainLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.main_bg5));
				break;
			}

		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		// String a =
		// "打印数组和对象数组为什么返回结果不一样为什么前者返回字符串后者返回地址而不返回地址这个实在是不科学啊不科学我现在打的这些都是为了测试你马上能不能用啊尼玛我想了这么多办法要是还不行那我就没话说了凑一百";
		// String a
		// ="民共和国技术的进步应该是真正能够带来用户体验的提升而进，而非为了让参数更加漂亮，吸引人。当然，也有的技术是越普及，体现的价值越明显。屏幕分辨率，就是一种这样的技术。一一道来";
		// String a = "人民共和国技术的进步12345应该是真正能够带来FindiX用户体验的提升";
		// String a = "明天下午3:00在南4304开会讨论Lambda表达式。";
		// NoPunctuation np = new NoPunctuation();
		// NoStopword ns = new NoStopword();
		// SegmentationByBloom seg = new SegmentationByBloom();
		// String tempString="苹果";
		// ArrayList<String> list = seg.getWordsbyArrayList(a);
		// np.getNoPunctuationWords(list);
		// ns.getNoStopwordWords(list);
		// MakeCharTogether.getCharTogether(list);
		// String[] b = (String[]) list.toArray(new String[list.size()]);
		// String[] b = seg.getWords(a);
		// for (int i = 0; i < b.length; i++) {
		// Log.i(i + "", b[i]);
		// }
		// Log.i("Whole Time", Calendar.getInstance().getTimeInMillis() - ftime
		// + "");// 结束时间
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

	/**
	 * 当用户点击广告*(Mogo服务根据次记录点击数，次点击是过滤过的点击，一条广告一次展示只能对应一次点击)
	 */
	@Override
	public void onClickAd(String arg0) {
		Log.d(AdsMogoUtil.ADMOGO, "-=onClickAd=-");

	}

	// 当用户点击了广告关闭按钮时回调(关闭广告按钮功能可以在Mogo的App管理中设置)
	// return false 则广告关闭 return true 广告将不会关闭

	@Override
	public boolean onCloseAd() {
		Log.d(AdsMogoUtil.ADMOGO, "-=onCloseAd=-");
		AlertDialog dialog = new AlertDialog.Builder(this).create();

		dialog.setMessage("是否关闭广告？");

		dialog.setButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// return true;

				dialog.dismiss();

				if (adsMogoLayout != null) {
					// 关闭当前广告
					adsMogoLayout.setADEnable(false);
				}

			}
		});

		dialog.setButton2("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

		dialog.show();

		return true;
	}

	/**
	 * 当用户关闭了下载类型广告的详细界面时回调(广告物料类型为下载广告并且是弹出简介下载的才会有此Dialog)
	 */
	@Override
	public void onCloseMogoDialog() {
		Log.d(AdsMogoUtil.ADMOGO, "-=onCloseMogoDialog=-");
	}

	/**
	 * 所有广告平台请求失败时回调
	 */
	@Override
	public void onFailedReceiveAd() {
		Log.d(AdsMogoUtil.ADMOGO, "-=onFailedReceiveAd=-");

	}

	/**
	 * 当用户点击广告*(真实点击 Mogo不会根据此回调时记录点击数，次点击是无过滤过的点击)
	 */
	@Override
	public void onRealClickAd() {
		Log.d(AdsMogoUtil.ADMOGO, "-=onRealClickAd=-");

	}

	/**
	 * 请求广告成功时回调 arg0为单一平台的广告视图 arg1为请求平台名称
	 */

	@Override
	public void onReceiveAd(ViewGroup arg0, String arg1) {
		Log.d(AdsMogoUtil.ADMOGO, "-=onReceiveAd=-");

	}

	/**
	 * 开始请求广告时回调 arg0为请求平台名称
	 */
	@Override
	public void onRequestAd(String arg0) {
		Log.d(AdsMogoUtil.ADMOGO, "-=onRequestAd=-");

	}

	@Override
	protected void onDestroy() {
		// 清除 adsMogoLayout 实例 所产生用于多线程缓冲机制的线程池
		// 此方法请不要轻易调用，如果调用时间不当，会造成无法统计计数
		if (adsMogoLayout != null) {
			adsMogoLayout.clearThread();
		}
		super.onDestroy();
	}

	// 自定义平台功能：关联自定义Adapter
	// 如不需要自定义平台功能， 返回 null
	// AdsMogoCustomEventPlatform_1对应平台一
	// AdsMogoCustomEventPlatform_2对应平台二，如果开发者修改平台名称的话，需备注一下以免弄混
	// 如不需要自定义平台功能， 返回 null

	@Override
	public Class<? extends AdsMogoAdapter> getCustomEvemtPlatformAdapterClass(
			AdsMogoCustomEventPlatformEnum enumIndex) {
		Class<? extends AdsMogoAdapter> clazz = null;
		if (AdsMogoCustomEventPlatformEnum.AdsMogoCustomEventPlatform_1
				.equals(enumIndex)) {
			// clazz = DianDongAdapter.class;
		}
		return clazz;
	}
}
