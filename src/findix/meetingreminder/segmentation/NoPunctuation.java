package findix.meetingreminder.segmentation;

import java.util.ArrayList;;

public class NoPunctuation {
	final String[] punctuation = { "~", "¡¤", "£¡", "@", "#", "£¤", "%", "¡­", "&",
			"*", "^", "&", "*", "£¨", "£©", "¡ª¡ª", "+", "-", "*", "/", "=", "$",
			"¡¶", "¡·", "£¬", "¡£", "/", "\\", "£¿", "¡°", "¡±", "¡®", "¡¯", "£»", "£º",
			"¡¾", "¡¿", "¡º", "¡»","¡¢", "!", "\"", "#", "$", "%", "&", "'", "(", ")",
			"*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "@",
			"[", "\\", "]", "^", "_", "`", "{", "|", "}", "~" };

	public ArrayList<String> getNoPunctuationWords(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).length() == 1) {
				for (int j = 0; j < punctuation.length; j++) {
					if (punctuation[j].equals(list.get(i))) {
						list.remove(i);
						if(i==0)
							break;
						i--;
						//System.out.println(list.size());
					}
				}
			}
		}
		return list;
	}
}
