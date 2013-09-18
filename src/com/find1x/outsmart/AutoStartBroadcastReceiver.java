package com.find1x.outsmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//			// ---------声明一个Intent,打开一个Activity;
//			Intent intent_Activity = new Intent(arg0, My_android_Activity.class);
//			// 设置启动的Action,不是强制的；
//			intent_Activity.setAction("android.intent.action.MAIN");
//			// 添加category，,不是强制的；
//			intent_Activity.addCategory("android.intent.category.LAUNCHER");
//			/*
//			 * 如果活动是在不活动的环境下展开，这个标志是强制性的设置，必须加；
//			 * 为刚要启动的Activity设置启动参数，此参数申明启动时为Activity开辟新的栈。
//			 */
//			intent_Activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//			// 启动activity
//			arg0.startActivity(intent_Activity);

			// --------声明一个Intent用以启动一个Service;

			Intent intent_service = new Intent(arg0, SmsReceiverService.class);
			// 可以在服务里面进行一些用户不需要知道的操作，比如更新。
			arg0.startService(intent_service);

		}
	}

}
