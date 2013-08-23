package findix.meetingreminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.adsmogo.adapters.AdsMogoCustomEventPlatformEnum;
import com.adsmogo.adview.AdsMogoLayout;
import com.adsmogo.controller.listener.AdsMogoListener;
import com.adsmogo.util.AdsMogoUtil;

import findix.meetingreminder.analysis.GetUserLocation;
import findix.meetingreminder.analysis.GetUserTime;
import findix.meetingreminder.db.DatabaseHelper;
import findix.meetingreminder.segmentation.Persistence;
import findix.meetingreminder.sms.Contact;
import findix.meetingreminder.sms.SendSMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.*;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnClickListener,
		AdsMogoListener {

	private TextView sendertextView = null;
	private TextView timeSMStextView = null;
	private TextView smstextView = null;
	private TextView timetextView = null;
	private TextView datetextView = null;
	private EditText editText_location = null;
	private EditText editText_event = null;
	private EditText editText = null;
	private Button btn_ok = null;
	private Button btn_cancel = null;
	private Button btn_reply = null;
	private Button btn_changeLocation = null;
	private Button btn_changeEvent = null;
	private Button btn_changeTime = null;
	private Button btn_changeDate = null;
	private ImageView contact_imageView = null;
	private String[] location;
	private Calendar time = Calendar.getInstance();
	private String address = new String();
	private String replyText = new String();

	private boolean isClear_Event = false;
	private boolean isClear_Location = false;

	AdsMogoLayout adsMogoLayout;

	/** 发送与接收的广播 **/
	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

	// private static String calanderURL = "";
	private static String calanderEventURL = "";
	private static String calanderRemiderURL = "";
	// 为了兼容不同版本的日历,2.2以后url发生改变
	static {
		if (Build.VERSION.SDK_INT >= 8) {
			// calanderURL = "content://com.android.calendar/calendars";
			calanderEventURL = "content://com.android.calendar/events";
			calanderRemiderURL = "content://com.android.calendar/reminders";
		} else {
			// calanderURL = "content://calendar/calendars";
			calanderEventURL = "content://calendar/events";
			calanderRemiderURL = "content://calendar/reminders";

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog);
		setTheme(R.style.DialogTheme);
		sendertextView = (TextView) findViewById(R.id.sendertextView);
		timeSMStextView = (TextView) findViewById(R.id.timeSMStextView);
		datetextView = (TextView) findViewById(R.id.datetextView);
		timetextView = (TextView) findViewById(R.id.timetextView);
		smstextView = (TextView) findViewById(R.id.smstextView);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		btn_reply = (Button) findViewById(R.id.btn_reply);
		btn_reply.setOnClickListener(this);
		btn_changeDate = (Button) findViewById(R.id.changeDateButton);
		btn_changeDate.setOnClickListener(new BtnOnClickListener(
				R.id.changeDateButton));
		btn_changeTime = (Button) findViewById(R.id.changeTimeButton);
		btn_changeTime.setOnClickListener(new BtnOnClickListener(
				R.id.changeTimeButton));
		btn_changeLocation = (Button) findViewById(R.id.changeLocationButton);
		btn_changeLocation.setOnClickListener(this);
		btn_changeEvent = (Button) findViewById(R.id.changeEventButton);
		btn_changeEvent.setOnClickListener(this);
		editText_location = (EditText) findViewById(R.id.locationEditText);
		editText_location.setOnClickListener(this);
		editText_event = (EditText) findViewById(R.id.eventEditText);
		editText_event.setOnClickListener(this);
		contact_imageView = (ImageView) findViewById(R.id.contact_imageView);

		// 芒果广告
		// adsMogoLayout = ((AdsMogoLayout)
		// this.findViewById(R.id.adsMogoView));
		// adsMogoLayout.setAdsMogoListener(this);
		// adsMogoLayout.downloadIsShowDialog = true;

		// 接受intent
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		String address = intent.getStringExtra("address");
		String person = intent.getStringExtra("person");
		Long date = intent.getLongExtra("date", 0);
		String id = Contact.getContactId(this, address);
		GetUserTime getUserTime = new GetUserTime(content);
		time = getUserTime.getTime();
		GetUserLocation getUserLocation = new GetUserLocation(
				getUserTime.getNoDateMsg());
		location = getUserLocation.getLocation();
		this.address = address;
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-M-d");
		SimpleDateFormat formatTime = new SimpleDateFormat("H:mm");
		SimpleDateFormat formatSMSTime = new SimpleDateFormat("M月d日 H:mm");
		if (id != null) {
			sendertextView.setText(Contact.getDisplayName(this, id));
			if (Contact.getContactsPhoto(this, id) != null)
				contact_imageView.setImageBitmap(Contact.getContactsPhoto(this,
						id));
		} else
			sendertextView.setText(address);
		if (date != 0)
			timeSMStextView.setText(formatSMSTime.format(date));
		datetextView.setText(formatDate.format(time.getTime()));
		timetextView.setText(formatTime.format(time.getTime()));

		editText_location.setText(getUserLocation.getUserLocation(this));
		editText_location.clearFocus();
		smstextView.setText(content);

		// 注册广播
		// registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
		// registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));

		// TipHelper.PlaySound(this);// 响铃
		// long ring[]={1000,500,1000};
		// TipHelper.Vibrate(this, ring, false);//震动
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 解除注册广播
		// unregisterReceiver(sendMessage);
		// unregisterReceiver(receiver);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.changeLocationButton: {
			final boolean[] defaultSelectedStatus = new boolean[location.length];
			for (int i = 0; i < location.length; i++)
				defaultSelectedStatus[i] = false;
			new AlertDialog.Builder(this)
					.setTitle("设置地点")
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
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									StringBuffer locationSet = new StringBuffer();
									for (int i = 0; i < defaultSelectedStatus.length; i++) {
										if (defaultSelectedStatus[i]) {
											locationSet.append(location[i]);
										}
									}
									editText_location.setText(locationSet);
								}
							}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
					.show();
			break;
		}
		case R.id.changeEventButton: {
			final boolean[] defaultSelectedStatus = new boolean[location.length];
			for (int i = 0; i < location.length; i++)
				defaultSelectedStatus[i] = false;
			new AlertDialog.Builder(this)
					.setTitle("设置事件")
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
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									StringBuffer locationSet = new StringBuffer();
									for (int i = 0; i < defaultSelectedStatus.length; i++) {
										if (defaultSelectedStatus[i]) {
											locationSet.append(location[i]);
										}
									}
									editText_event.setText(locationSet);
								}
							}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
					.show();
			break;
		}
		case R.id.btn_ok: {

			// 插入事件
			String calId = "";
			Persistence setCalendar = new Persistence("CalendarSet.db");
			calId = (setCalendar.getValue()) + "";
			ContentValues event = new ContentValues();
			event.put("title", editText_event.getText().toString());
			event.put("description", editText_event.getText().toString());
			// 插入账户
			if (!editText_location.equals("")
					&& !editText_location.equals("请选择地点")) {
				event.put("eventLocation", editText_location.getText()
						.toString());
			}
			event.put("calendar_id", calId);
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(Calendar.HOUR_OF_DAY, 10);
			long start = time.getTimeInMillis();
			mCalendar.set(Calendar.HOUR_OF_DAY, 11);
			long end = time.getTimeInMillis() + 3600000;
			event.put("dtstart", start);
			event.put("dtend", end);
			TimeZone tz = TimeZone.getDefault();
			event.put("eventTimezone", tz.getID());
			event.put("hasAlarm", 1);
			Uri newEvent = getContentResolver().insert(
					Uri.parse(calanderEventURL), event);
			long id = Long.parseLong(newEvent.getLastPathSegment());
			ContentValues values = new ContentValues();
			values.put("event_id", id);
			values.put("minutes", 10);
			// 插入日历
			getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
			Toast.makeText(DialogActivity.this, "添加提醒成功!!!", Toast.LENGTH_SHORT)
					.show();

			// finish();

			// 添加地点到数据库
			// 建立数据库
			DatabaseHelper dbHelper = new DatabaseHelper(this, "user.db3");
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String location_Temp = editText_location.getText().toString();
			String raw = "select location from user where location=\'"
					+ location_Temp + "\'";
			Cursor cursor = db.rawQuery(raw, null);
			if (!cursor.moveToNext() && !location_Temp.equals("")
					&& !location_Temp.equals("请选择地点")) {
				String sql = "insert or ignore into user(location) values('"
						+ location_Temp + "');";
				// System.out.println(sql);
				db.execSQL(sql);
				Toast.makeText(this,
						"我现在知道" + "\"" + location_Temp + "\"" + "这个地方啦",
						Toast.LENGTH_LONG).show();
			}
			db.close();
			break;
		}
		case R.id.btn_reply: {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			final View replyView = layoutInflater.inflate(
					R.layout.activity_reply, null);
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setView(replyView)
					//.setTitle("确认回复")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									editText = (EditText) replyView
											.findViewById(R.id.EditText);
									String reply = editText.getText()
											.toString();
									replyText = reply;
									if (PhoneNumberUtils
											.isGlobalPhoneNumber(address)
											&& address.length() > 0
											&& reply.length() > 0) {
										new SendSMS(DialogActivity.this,
												address, reply);
										/** 将发送的短信插入数据库 **/
										ContentValues values = new ContentValues();
										// 发送时间
										values.put("date",
												System.currentTimeMillis());
										// 阅读状态
										values.put("read", 0);
										// 1为收 2为发
										values.put("type", 2);
										// 送达号码
										values.put("address", address);
										// 送达内容
										values.put("body", replyText);
										// 插入短信库
										getContentResolver().insert(
												Uri.parse("content://sms"),
												values);
										// finish();
									} else {
										if (address.length() == 0) {
											Toast.makeText(DialogActivity.this,
													"这条信息没有发件人，所以是没法回复的哦~",
													Toast.LENGTH_LONG).show();
										} else if (reply.length() == 0) {
											Toast.makeText(DialogActivity.this,
													"这条信息什么都没写哦，我应该回复什么呢？",
													Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(
													DialogActivity.this,
													"向号码 \"" + address
															+ "\" 发送短信 \""
															+ reply
															+ "\" 失败，请重新尝试",
													Toast.LENGTH_LONG).show();
										}
									}
									// finish();
								}
							}).setNegativeButton("取消", null).create();
			alertDialog.show();
			break;
		}

		case R.id.btn_cancel: {
			finish();
			break;
		}
		case R.id.eventEditText: {
			if (isClear_Event == false) {
				editText_event.setText("");
				isClear_Event = true;
			}
			break;
		}
		case R.id.locationEditText: {
			if (isClear_Location == false) {
				editText_location.setText("");
				isClear_Location = true;
			}
			break;
		}
		}
	}

	protected Dialog onCreateDialog(int id) {
		// 用来获取日期和时间的
		// Calendar calendar = Calendar.getInstance();
		Calendar calendar = time;
		Dialog dialog = null;
		switch (id) {
		case R.id.changeDateButton:
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					// Calendar月份是从0开始,所以month要加1
					time.set(year, month, dayOfMonth);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					datetextView.setText(format.format(time.getTime()));
				}
			};
			dialog = new DatePickerDialog(this, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			break;
		case R.id.changeTimeButton:
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timerPicker, int hourOfDay,
						int minute) {
					time.set(Calendar.HOUR_OF_DAY, hourOfDay);
					time.set(Calendar.MINUTE, minute);
					SimpleDateFormat format = new SimpleDateFormat("H:mm");
					timetextView.setText(format.format(time.getTime()));
				}
			};
			dialog = new TimePickerDialog(this, timeListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), false); // 是否为二十四制
			break;
		default:
			break;
		}
		return dialog;
	}

	/*
	 * 成员内部类,此处为提高可重用性，也可以换成匿名内部类
	 */
	private class BtnOnClickListener implements View.OnClickListener {

		private int dialogId = 0; // 默认为0则不显示对话框

		public BtnOnClickListener(int dialogId) {
			this.dialogId = dialogId;
		}

		@Override
		public void onClick(View view) {
			showDialog(dialogId);
		}

	}

	/**
	 * 参数说明 destinationAddress:收信人的手机号码 scAddress:发信人的手机号码 text:发送信息的内容
	 * sentIntent:发送是否成功的回执，用于监听短信是否发送成功。
	 * DeliveryIntent:接收是否成功的回执，用于监听短信对方是否接收成功。
	 */
	public BroadcastReceiver sendMessage = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 判断短信是否发送成功
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
				Log.i("短信发送", "成功！");
				break;
			default:
				Toast.makeText(DialogActivity.this, "发送失败", Toast.LENGTH_LONG)
						.show();
				Log.i("短信发送", "失败！");
				break;
			}
		}
	};

	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 表示对方成功收到短信
			Toast.makeText(DialogActivity.this, "对方接收成功", Toast.LENGTH_LONG)
					.show();
			Log.i("短信接收", "成功！");
		}
	};

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
		return false;
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
	public Class<?> getCustomEvemtPlatformAdapterClass(
			AdsMogoCustomEventPlatformEnum arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}