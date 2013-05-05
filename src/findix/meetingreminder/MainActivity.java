package findix.meetingreminder;

import java.io.*;

import findix.meetingreminder.backup.BackupTask;
import findix.meetingreminder.db.DatabaseHelper;
import findix.meetingreminder.segmentation.CopyDic;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.*;
import android.widget.*;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity implements OnClickListener {
	private ToggleButton toggleButton1 = null;
	private Button Button1 = null;
	private Button Button2 = null;
	private Button insertButton = null;
	private Button cursorButton = null;
	private Button backupButton = null;
	private Button restoreButton = null;

	// 建立数据库
	SQLiteDatabase db;
	DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "user.db3");

	// 全局变量
	public boolean isCover = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton1.setOnClickListener(this);
		Button1 = (Button) findViewById(R.id.button1);
		Button1.setOnClickListener(this);
		Button2 = (Button) findViewById(R.id.button2);
		Button2.setOnClickListener(this);
		insertButton = (Button) findViewById(R.id.insertbutton);
		insertButton.setOnClickListener(this);
		cursorButton = (Button) findViewById(R.id.cursorbutton);
		cursorButton.setOnClickListener(this);
		backupButton = (Button) findViewById(R.id.backupbutton);
		backupButton.setOnClickListener(this);
		restoreButton = (Button) findViewById(R.id.restorebutton);
		restoreButton.setOnClickListener(this);

		// 拷贝字典
		// CopyAndGetSQL database = new CopyAndGetSQL();
		// database.openDatabase(this);
		new CopyDic(this);

		// 控制开关
		File settings = new File("/data/data/findix.meetingreminder/Setting.db");
		if (!settings.exists()) {

			try {
				settings.createNewFile();
				FileWriter io = null;
				io = new FileWriter(
						"/data/data/findix.meetingreminder/Setting.db");
				io.write(1);
				io.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileReader io = new FileReader(
					"/data/data/findix.meetingreminder/Setting.db");
			if (io.read() == 1) {
				toggleButton1.setChecked(true);
			} else {
				toggleButton1.setChecked(false);
			}
			io.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 按钮监听器
		switch (v.getId()) {

		case R.id.toggleButton1:
			FileWriter io = null;
			try {
				io = new FileWriter(
						"/data/data/findix.meetingreminder/Setting.db");
				io.write(toggleButton1.isChecked() ? 1 : 0);
				io.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
			break;

		case R.id.button1:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, DialogActivity.class);
			// intent.setClass(this, ReplyActivity.class);
			intent.putExtra("content", "明天下午3:00在南4304开会讨论Lambda表达式。");
			intent.putExtra("sender", "18817353255");
			if (toggleButton1.isChecked())
				startActivity(intent);
			break;

		case R.id.button2: {
			final Intent intent1 = new Intent();
			final EditText et = new EditText(this);
			new AlertDialog.Builder(this)
					.setTitle("发送短信")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(et)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 数据获取
									et.getText().toString();
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent1.putExtra("content", et.getText()
											.toString());
									if (toggleButton1.isChecked())
										startActivity(intent1);
								}
							}).setNegativeButton("取消", null).show();
			intent1.setClass(this, DialogActivity.class);
			break;
		}
		case R.id.insertbutton: {
			final EditText et = new EditText(this);
			new AlertDialog.Builder(this)
					.setTitle("请输入您要添加的自定义地点")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(et)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 数据获取
									// Toast.makeText(TestTabActivity.this,
									// et.getText().toString(),
									// Toast.LENGTH_LONG).show();
									String location = et.getText().toString();
									ContentValues values = new ContentValues();

									SQLiteDatabase db = dbHelper
											.getReadableDatabase();
									values.put("location", location);
									db.insert("user", null, values);
									db.close();
								}
							}).setNegativeButton("取消", null).show();
			break;
		}
		case R.id.cursorbutton:
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from user", null);
			int num = cursor.getCount();
			final String[] location = new String[num];
			int i = 0;
			while (cursor.moveToNext()) {
				location[i] = cursor.getString(cursor
						.getColumnIndex("location"));
				i++;
			}
			db.close();

			final boolean[] defaultSelectedStatus = new boolean[num];
			for (i = 0; i < num; i++)
				defaultSelectedStatus[i] = false;
			new AlertDialog.Builder(this)
					.setTitle("自定义地点")
					// 设置对话框标题
					.setMultiChoiceItems(location, defaultSelectedStatus,
							new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// 来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
									defaultSelectedStatus[which] = isChecked;
								}
							}) // 设置对话框[肯定]按钮
					.setPositiveButton("删除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									for (int i = 0; i < defaultSelectedStatus.length; i++) {
										if (defaultSelectedStatus[i]) {
											db = dbHelper.getWritableDatabase();
											String[] whereArgs = { location[i] };
											db.delete("user", "location=?",
													whereArgs);
											db.close();
											// Toast.makeText(MainActivity.this,
											// "删除成功", 10000).show();

										}
									}
								}
							}).setNegativeButton("取消", null)// 设置对话框[否定]按钮
					.show();

			break;
		case R.id.backupbutton: {
			isCover = false;
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("此操作会覆盖之前备份，是否覆盖？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									backup(true);
								}
							}).setNegativeButton("取消", null).show();
			break;
		}
		case R.id.restorebutton: {
			isCover = false;
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("此操作会覆盖当前设置，是否覆盖？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									backup(false);
								}
							}).setNegativeButton("取消", null).show();
			break;
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		//long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		// String a =
		// "打印数组和对象数组为什么返回结果不一样为什么前者返回字符串后者返回地址而不返回地址这个实在是不科学啊不科学我现在打的这些都是为了测试你马上能不能用啊尼玛我想了这么多办法要是还不行那我就没话说了凑一百";
		// String a
		// ="民共和国技术的进步应该是真正能够带来用户体验的提升而进，而非为了让参数更加漂亮，吸引人。当然，也有的技术是越普及，体现的价值越明显。屏幕分辨率，就是一种这样的技术。一一道来";
		// String a = "人民共和国技术的进步12345应该是真正能够带来FindiX用户体验的提升";
		//String a = "明天下午3:00在南4304开会讨论Lambda表达式。";
		//NoPunctuation np = new NoPunctuation();
		//NoStopword ns = new NoStopword();
		//SegmentationByBloom seg = new SegmentationByBloom();
		// String tempString="苹果";
		// ArrayList<String> list = seg.getWordsbyArrayList(a);
		// np.getNoPunctuationWords(list);
		// ns.getNoStopwordWords(list);
		// MakeCharTogether.getCharTogether(list);
		// String[] b = (String[]) list.toArray(new String[list.size()]);
		//String[] b = seg.getWords(a);
		//for (int i = 0; i < b.length; i++) {
		//	Log.i(i + "", b[i]);
		//}
		//Log.i("Whole Time", Calendar.getInstance().getTimeInMillis() - ftime
		//		+ "");// 结束时间
		return true;
	}

	public void backup(boolean BackupOrRestore) {
		if (BackupOrRestore) {
			new BackupTask(this).execute("backupDatabase");
			Toast.makeText(MainActivity.this, "备份成功", Toast.LENGTH_LONG).show();
		} else {
			new BackupTask(this).execute("restroeDatabase");
			Toast.makeText(MainActivity.this, "还原成功", Toast.LENGTH_LONG).show();
		}
	}
}
