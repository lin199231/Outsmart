package findix.meetingreminder.segmentation;

import java.util.ArrayList;

public class MakeCharTogether {

	/**
	 * @param args
	 */

	public static ArrayList<String> getCharTogether(ArrayList<String> list) {
		boolean flag = false;
		for (int i = 0; i < list.size() - 1; i++) {
			if (isW(list.get(i)))
				flag = true;
			if (flag && isW(list.get(i + 1))) {
				list.set(i, ((list.get(i) + list.get(i + 1))) + "");
				list.remove(i + 1);
				i--;
			} else {
				flag = false;
			}

		}
		return list;
	}

	private static boolean isW(String str) {
		if (str.length() == 1
				&& ((str.charAt(0)==':')||(str.charAt(0)=='£º')||(str.charAt(0) >= '0' && str.charAt(0) <= '9')
						|| (str.charAt(0) >= 'a' && str.charAt(0) <= 'z') || (str
						.charAt(0) >= 'A' && str.charAt(0) <= 'Z')))
			return true;
		return false;
	}
}
