package com.find1x.outsmart.segmentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class CopyDic {
	public CopyDic(Context context) {
		//long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		final String FILE_NAME = "dic2012.db";
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
		File file = new File(dir, FILE_NAME);
		// System.out.println("filePath:" + dir);
		// 查看文件是否存在
		if (!file.exists()) {
			try {
				// 得到输入流
				InputStream is = context.getResources().getAssets()
						.open(FILE_NAME);
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
			}
		}
		//Log.i("Time of Copy Dic", Calendar.getInstance().getTimeInMillis() - ftime + "");
	}
}
