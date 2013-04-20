package findix.meetingreminder.segmentation;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class NoPunctuation {
	final String[] punctuation = { "~", "·", "！", "@", "#", "￥", "%", "…", "&",
			"*", "^", "&", "*", "（", "）", "——", "+", "-", "*", "/", "=", "$",
			"《", "》", "，", "。", "/", "\\", "？", "“", "”", "‘", "’", "；", "：",
			"【", "】", "『", "』", "|", "、", "!", "$", "^", "(", ")", "_", "\"",
			"'", "[", "]", "{", "}", ":", ";", "?", ".", ",", "<", ">" };

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
