package eu.transkribus.core.model.beans.customtags;

import java.util.Set;


public class ReadingOrderTag extends CustomTag {
	public static final String TAG_NAME = "readingOrder";
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("index", true, "Index", "The reading order index - lower index means reading order before larger index")
	};
	
	int index=0;
	
	public ReadingOrderTag() {
		super(TAG_NAME);
	}
	
	public ReadingOrderTag(int index) {
		this();
//		Assert.assertTrue("reading order index must be >= 0", index>=0);
		this.index = index;
	}
	
	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return false; }
	
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public ReadingOrderTag copy() {
		return new ReadingOrderTag(index);
	}

	@Override
	public boolean isIndexed() {
		return false;
	}
	
	@Override public void setOffset(int offset) { }
	@Override public void setLength(int length) { }	
	
//    @Override public void setAttributes(CustomTag toMerge) {
//    	super.setAttributes(toMerge);
//    	if (toMerge instanceof ReadingOrderTag) {
//    		this.index = ((ReadingOrderTag) toMerge).index;
//    	}
//    }
	
//	@Override
//	public String getCssStr() {
//		return getTagName() + " {"+"index:"+index+";}";
//	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
//	@Override public String getDisplayName() {
//		return "Reading order";
//	}
//
//	@Override public void setDisplayName(String displayName) {
//	}
	
	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
	
	
}
