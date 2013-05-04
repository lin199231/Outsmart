package findix.meetingreminder.analysis;

import findix.meetingreminder.segmentation.SegmentationByBloom;


public class GetUserLocation {
	public String[] getLocation(String text){
		SegmentationByBloom seg =new SegmentationByBloom();
		return seg.getWords(text);	
	}
}
