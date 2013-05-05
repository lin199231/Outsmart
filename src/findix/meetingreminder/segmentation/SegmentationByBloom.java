package findix.meetingreminder.segmentation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class SegmentationByBloom {
	static byte[] dic = null;
	static GetHash GH = new GetHash();

	public SegmentationByBloom() {
		long ftime = Calendar.getInstance().getTimeInMillis();// 开始时间
		dic = GetDicBloom.getBloom();
		Log.i("Input Dic Time", Calendar.getInstance().getTimeInMillis()
				- ftime + "");// 结束时间
	}

	public static void getDic() {
		for (int i = 0; i <= dic.length; i++) {
			System.out.println(i + " " + (dic[i] - 48));
		}
	}

	public String[] getWords(String str) {
		ArrayList<String> list = getWordsbyArrayList(str);
		return (String[]) list.toArray(new String[list.size()]);
	}

	public ArrayList<String> getWordsbyArrayList(String str) {
		ArrayList<String> list = new ArrayList<String>();
		String subString = null;
		int max = 4;
		int i;
		int point = str.length();
		while (point != 0) {
			if (point - max < 0)
				subString = str.substring(0, point);
			else
				subString = str.substring(point - max, point);
			for (i = 0; i < subString.length(); i++) {
				String tempString = subString.substring(i, subString.length());
				int[] backhash = GH.getHashCode(tempString);
				if (isInDicByBloom(tempString, backhash)
						|| i == subString.length() - 1) {
					list.add(tempString);
					point = point - subString.length() + i;
					break;
				}
			}
		}
		// 把分词结果倒序使其正序
		Stack<String> stack=new Stack<String>();
		for(i=0;i<list.size();i++)
			stack.push(list.get(i));
		list.clear();
		while(!stack.empty())
			list.add(stack.pop()+"");
		MakeCharTogether.getCharTogether(list);
		return list;
	}

	public static boolean isInDicByBloom(String source, int[] backhash) {

		if (dic[backhash[0]] == 49 && dic[backhash[1]] == 49
				&& dic[backhash[2]] == 49)
			return true;
		else
			return false;

	}

}
