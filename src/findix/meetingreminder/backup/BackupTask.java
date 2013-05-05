package findix.meetingreminder.backup;

import java.io.*;
import java.nio.channels.FileChannel;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class BackupTask extends AsyncTask<String, Void, Integer> {
	private static final String COMMAND_BACKUP = "backupDatabase";
	public static final String COMMAND_RESTORE = "restroeDatabase";
	private Context mContext;

	public BackupTask(Context context) {
		this.mContext = context;
	}
	
	public BackupTask() {
	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		// 获得正在使用的数据库路径
		// 获取SD卡下的用
		// Environment.getExternalStorageDirectory().getAbsolutePath()+"*.db."
		// 默认路径是 /data/data/(包名)/databases/*.db3
		File dbFile = mContext.getDatabasePath("/data/data/findix.meetingreminder/databases/user.db3");
		File exportDir = new File(Environment.getExternalStorageDirectory(),
				"/findix/Backup");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File backup = new File(exportDir, dbFile.getName());
		String command = params[0];
		System.out.println(backup.getAbsolutePath());
		if (command.equals(COMMAND_BACKUP)) {
			try {
				backup.createNewFile();
				fileCopy(dbFile, backup);
				return Log.d("backup", "ok");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Log.d("backup", "fail");
			}
		} else if (command.equals(COMMAND_RESTORE)) {
			try {
				fileCopy(backup, dbFile);
				return Log.d("restore", "success");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Log.d("restore", "fail");
			}
		} else {
			return null;
		}
	}

	private void fileCopy(File dbFile, File backup) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fis=new FileInputStream(dbFile);
		FileOutputStream fos=new FileOutputStream(backup);
		FileChannel inChannel = fis.getChannel();
		FileChannel outChannel = fos.getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
			fis.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
}
