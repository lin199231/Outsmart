package com.find1x.meetingreminder.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

//这个类用于导入assets中的字典到手机中
public class CopyAndGetSQL {

	SQLiteDatabase database;

	public SQLiteDatabase openDatabase(Context context) {
		File dir = new File("data/data/" + context.getPackageName()
				+ "/databases");
		// 不存在先创建文件夹
		if (!dir.exists() || !dir.isDirectory()) {
			if (dir.mkdir()) {
				System.out.println("创建成功");
			} else {
				System.out.println("创建失败");
			}
		}
		File file = new File(dir, "dic2012.db3");
		//System.out.println("filePath:" + dir);
		// 查看数据库文件是否存在
		if (file.exists()) {
			// 存在则直接返回打开的数据库
			return SQLiteDatabase.openOrCreateDatabase(file, null);
		} else {
			try {
				// 得到数据库的输入流
				InputStream is = context.getResources().getAssets()
						.open("dic2012.db3");
				// 用输出流写到手机上
				FileOutputStream fos = new FileOutputStream(file);
				// 创建byte数组 用于1KB写一次
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				// 最后关闭
				fos.flush();
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			// 如果没有这个数据库 我们已经把他写到手机上了，然后在执行一次这个方法 就可以返回数据库了
			return openDatabase(context);
		}
	}
}