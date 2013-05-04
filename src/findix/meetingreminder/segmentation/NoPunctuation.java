package findix.meetingreminder.segmentation;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class NoPunctuation {
	final String[] punctuation = { "~", "·", "！", "@", "#", "￥", "%", "…", "&",
			"*", "^", "&", "*", "（", "）", "——", "+", "-", "*", "/", "=", "$",
			"《", "》", "，", "。", "/", "\\", "？", "“", "”", "‘", "’", "；", "：",
			"【", "】", "『", "』", "!", "\"", "#", "$", "%", "&", "'", "(", ")",
			"*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C",
			"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]",
			"^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "{", "|", "}", "~" };

	public ArrayList<String> getNoPunctuationWords(ArrayList<String> list) {
		long ftime = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).length() == 1) {
				for (int j = 0; j < punctuation.length; j++) {
					if (punctuation[j].equals(list.get(i))) {
						list.remove(i);
					}
				}
			}
		}
		Log.i("去标点时间", Calendar.getInstance().getTimeInMillis() - ftime + "");// 结束时间
		return list;
	}
}
