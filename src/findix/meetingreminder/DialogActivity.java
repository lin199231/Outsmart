package findix.meetingreminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import findix.meetingreminder.analysis.GetUserLocation;
import findix.meetingreminder.analysis.MSG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnClickListener {

	private TextView smstextView = null;
	private TextView timetextView = null;
	private TextView locationtextView = null;
	private EditText editText = null;
	private Button btn_ok = null;
	private Button btn_cancel = null;
	private Button btn_reply = null;
	private String[] location;
	private Date time;
	private String sender = new String();
	private AutoCompleteTextView autoCompletetextView = null;

	private boolean isClear = false;

	private static String calanderURL = "";
	private static String calanderEventURL = "";
	private static String calanderRemiderURL = "";
	// 为了兼容不同版本的日历,2.2以后url发生改变
	static {
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			calanderURL = "content://com.android.calendar/calendars";
			calanderEventURL = "content://com.android.calendar/events";
			calanderRemiderURL = "content://com.android.calendar/reminders";
		} else {
			calanderURL = "content://calendar/calendars";
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
		setTheme(R.style.translucent);
		timetextView = (TextView) findViewById(R.id.timetextView);
		locationtextView = (TextView) findViewById(R.id.locationtextView);
		smstextView = (TextView) findViewById(R.id.smstextView);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		btn_reply = (Button) findViewById(R.id.btn_reply);
		btn_reply.setOnClickListener(this);
		autoCompletetextView = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView);
		autoCompletetextView.setOnClickListener(this);

		// 接受intent
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		String sender = intent.getStringExtra("sender");
		Log.i("content", content);
		Log.i("sender", sender);

		MSG msg = new MSG(content);
		GetUserLocation getUserLocation=new GetUserLocation();
		location = getUserLocation.getLocation(content);
		time = msg.getTime();
		System.out.println(time.getTime());
		this.sender = sender;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timetextView.setText("时间：" + format.format(time));
		locationtextView.setText("地点：" + location);
		smstextView.setText(content);

		// TipHelper.PlaySound(this);// 响铃
		// long ring[]={1000,500,1000};
		// TipHelper.Vibrate(this, ring, false);//震动
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok: {

			// 获取账户
			Cursor userCursor = getContentResolver().query(
					Uri.parse(calanderURL), null, null, null, null);
			if (userCursor.getCount() > 0) {
				userCursor.moveToFirst();
				String userName = userCursor.getString(userCursor
						.getColumnIndex("name"));
				Toast.makeText(DialogActivity.this, userName,
						Toast.LENGTH_SHORT).show();
			}

			// 插入事件
			String calId = "";
			Cursor userCursor1 = getContentResolver().query(
					Uri.parse(calanderURL), null, null, null, null);
			if (userCursor1.getCount() > 0) {
				userCursor1.moveToFirst();
				System.out.println(userCursor1.getCount());
				calId = userCursor1
						.getString(userCursor1.getColumnIndex("_id"));
			}
			ContentValues event = new ContentValues();
			event.put("title", autoCompletetextView.getText().toString());
			event.put("description", "");
			// 插入账户
			event.put("calendar_id", calId);
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(Calendar.HOUR_OF_DAY, 10);
			long start = time.getTime();
			mCalendar.set(Calendar.HOUR_OF_DAY, 11);
			long end = time.getTime() + 3600000;
			event.put("dtstart", start);
			event.put("dtend", end);
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
			break;
		}
		case R.id.btn_reply: {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			final View myLoginView = layoutInflater.inflate(
					R.layout.activity_reply, null);
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setView(myLoginView)
					.setTitle("确认回复")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									editText = (EditText) myLoginView
											.findViewById(R.id.EditText);
									String reply = editText.getText()
											.toString();
									System.out.println(reply);
									PendingIntent sentIntent = PendingIntent
											.getBroadcast(DialogActivity.this,
													0, new Intent(),

													0);
									if (PhoneNumberUtils
											.isGlobalPhoneNumber(sender)
											&& sender.length() > 0) {
										SmsManager sms = SmsManager
												.getDefault();
										sms.sendTextMessage(sender, null,
												reply, sentIntent, null);
										Toast.makeText(DialogActivity.this,
												"短信发送成功",

												Toast.LENGTH_LONG).show();
										// finish();
									} else
										Toast.makeText(DialogActivity.this,
												"短信发送失败，请重新尝试",
												Toast.LENGTH_LONG).show();
									finish();
								}
							}).setNegativeButton("取消", null).create();
			alertDialog.show();
			break;
		}

		case R.id.btn_cancel: {
			finish();
			break;
		}
		case R.id.AutoCompleteTextView: {
			if (isClear == false) {
				autoCompletetextView.setText("");
				isClear = true;
			}
		}
		}
	}
}