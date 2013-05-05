package findix.meetingreminder;

import java.io.FileReader;
import java.io.IOException;

import findix.meetingreminder.analysis.GetUserTime;


import android.content.*;
import android.telephony.*;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	boolean isChecked;

	@Override
	public void onReceive(Context context, Intent intent) {

		Object[] pdus = (Object[]) intent.getExtras().get("pdus");// 获取短信内容
		for (Object pdu : pdus) {
			byte[] data = (byte[]) pdu;// 获取单条短信内容，短信内容以pdu格式存在
			SmsMessage message = SmsMessage.createFromPdu(data);// 使用pdu格式的短信数据生成短信对象
			String sender = message.getOriginatingAddress();// 获取短信的发送者
			String content = message.getMessageBody();// 获取短信的内容
			SmsManager.getDefault();
			// 这样启动一个Activity一定要把Intent打上FLAG_ACTIVITY_NEW_TASK的标志，不然会报错

			try {
				FileReader io = new FileReader(
						"/data/data/findix.meetingreminder/Setting.db");
				isChecked = (io.read() == 1);
				io.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setClass(context, DialogActivity.class);
			intent.setClass(context, DialogActivity.class);
			intent.putExtra("content", content);
			intent.putExtra("sender", sender);
			if (isChecked == true && new GetUserTime(content).isMeeting()) {//&& msg.isMeeting()) {
				context.startActivity(intent);
				// 拦截短信
				// abortBroadcast();
			}
		}
	}
}