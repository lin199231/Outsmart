package com.find1x.meetingreminder.segmentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class GetDicBloom {
	public static byte[] getBloom() {
		String packageNameString = "findix.meetingreminder";
		File dir = new File("data/data/" + packageNameString + "/databases");
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		File file = new File(dir, "dic2012.db");
		try {
			FileInputStream fin = new FileInputStream(file);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			fin.close();
			return buffer;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
