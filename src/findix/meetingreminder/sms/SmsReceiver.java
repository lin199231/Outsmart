package findix.meetingreminder.sms;

import java.util.Calendar;

import findix.meetingreminder.DialogActivity;
import findix.meetingreminder.analysis.GetUserTime;
import findix.meetingreminder.segmentation.Persistence;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * class name：SmsReceiver<BR>
 * class description：数据库改变监听类<BR>
 * PS：当数据改变的时候，执行里面才change方法<BR>
 * 
 * @version 1.00
 */
public class SmsReceiver extends ContentObserver {
	/**
	 * Activity对象
	 */
	private Context context;

	public SmsReceiver(Handler handler, Context context) {
		super(handler);
		this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		final String SMS_URI_INBOX = "content://sms/inbox";
		Uri uri = Uri.parse(SMS_URI_INBOX);
		String[] projectionSMS = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		final Cursor cur = context.getContentResolver().query(uri,
				projectionSMS, null, null, "date desc");
		cur.moveToFirst();
		String content = cur.getString(cur.getColumnIndex("body"));
		String sender = cur.getString(cur.getColumnIndex("address"));
		int id = cur.getInt(cur.getColumnIndex("_id"));
		// 这样启动一个Activity一定要把Intent打上FLAG_ACTIVITY_NEW_TASK的标志，不然会报错
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, DialogActivity.class);
		intent.putExtra("content", content);
		intent.putExtra("sender", sender);
		Persistence smsId = new Persistence("sms.db");
		if (smsId.getValue() - id == 1) {
			smsId.changeValue(id);
		} else {
			if (smsId.getValue() < id) {
				smsId.changeValue(id);
				if (new Persistence("Setting.db").getValue() == 1
						&& new GetUserTime(content).isMeeting()) {
					context.startActivity(intent);
				}
				Log.i("短信数据库监听结果", sender + " " + content);
			}
		}

	}
}
