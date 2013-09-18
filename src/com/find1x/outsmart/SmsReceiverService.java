package com.find1x.outsmart;

import com.find1x.outsmart.sms.SmsReceiver;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class SmsReceiverService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override  
    public void onCreate() {  
    	System.out.println("服务已创建");
		SmsReceiver smsReceiver = new SmsReceiver(new Handler(), this);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, smsReceiver);
    }  

}
