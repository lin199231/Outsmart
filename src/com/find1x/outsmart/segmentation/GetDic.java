package findix.meetingreminder.segmentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.http.util.EncodingUtils;


public class GetDic {
	public static String getString() {
		String packageNameString = "findix.meetingreminder";
		String res = null;
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
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}

	public static HashSet<String> getHash() {
		
		String packageNameString = "findix.meetingreminder";
		HashSet<String> hash = new HashSet<String>();
		File dir = new File("data/data/" + packageNameString + "/databases");
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		String s=new String(); 
		File file = new File(dir, "dic2012.db");
		try {
			BufferedReader in = new BufferedReader(
				      new FileReader(file)); 
			while((s=in.readLine())!=null)
			  {
			           hash.add(s);
			           //Log.i("hashset",s);
			  }
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hash;

	}
}
