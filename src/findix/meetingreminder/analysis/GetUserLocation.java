package findix.meetingreminder.analysis;

import java.util.ArrayList;
import java.util.Collections;

import findix.meetingreminder.segmentation.NoPunctuation;
import findix.meetingreminder.segmentation.NoStopword;
import findix.meetingreminder.segmentation.SegmentationByBloom;

public class GetUserLocation {
	public String[] getLocation(String text) {
		SegmentationByBloom seg = new SegmentationByBloom();
		ArrayList<String> location = seg.getWordsbyArrayList(text);
		NoPunctuation np = new NoPunctuation();
		NoStopword ns = new NoStopword();
		np.getNoPunctuationWords(location);
		ns.getNoStopwordWords(location);
		Collections.reverse(location);
		return (String[]) location.toArray(new String[location.size()]);
	}
}
