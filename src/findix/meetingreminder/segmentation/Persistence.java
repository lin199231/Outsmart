package findix.meetingreminder.segmentation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Persistence {
	String fileName=new String();

	public Persistence(String fileName) {
		this.fileName=fileName;
		File settings = new File("/data/data/findix.meetingreminder/"
				+ fileName);
		if (!settings.exists()) {
			try {
				settings.createNewFile();
				FileWriter io = null;
				io = new FileWriter("/data/data/findix.meetingreminder/"
						+ fileName);
				io.write(1);
				io.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int  getValue(){
		int value = 0;
		try {
			FileReader io = new FileReader(
					"/data/data/findix.meetingreminder/"
							+ fileName);
			value=io.read();
			io.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return value;
	}
	public void changeValue(int value){
		FileWriter io = null;
		try {
			io = new FileWriter(
					"/data/data/findix.meetingreminder/"+ fileName);
			io.write(value);
			io.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
	}
}
