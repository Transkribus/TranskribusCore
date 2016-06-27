package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple class that encodes pairs of (offset, length) values
 */
public class IntRange {
	private final static Logger logger = LoggerFactory.getLogger(IntRange.class);
	
	public int offset=0;
	public int length=0;
	
	public IntRange(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}
	
	public IntRange(IntRange src) {
		this.offset = src.offset;
		this.length = src.length;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	/** Returns the <em>non-inclusive</em> end index, i.e <code>offset + length</code>*/
	public int getEnd() { return offset+length; }
	
	public boolean isInside(int offset) {
		return offset>=this.offset && offset<(this.offset+this.length);
	}
	
	public boolean isEmpty() {
		return length<=0;
	}
	
	public OverlapType getOverlapType(IntRange r) {
		return getOverlapType(r.getOffset(), r.getLength());
	}
	
	/**
	 * Returns the {@link OverlapType} of the given range with offset o and length l
	 * with this range. The types have the following meaning:
	 * <ul>
	 * <li>{@link OverlapType#INSIDE}: The given range is fully inside this range</li>
	 * <li>{@link OverlapType#BOTH}: The given range overlaps on both sides of this range</li>
	 * <li>{@link OverlapType#NONE}: The given range does not overlap with this range</li>
	 * <li>{@link OverlapType#LEFT}: The given range overlaps on the left side with this range</li>
	 * <li>{@link OverlapType#RIGHT}: The given range overlaps on the right side with this range</li>
	 *</ul>
	 * 
	 * @param o The offset of the input range
	 * @param l The length of the input range
	 * @return {@link OverlapType}
	 */
	public OverlapType getOverlapType(int o, int l) {
//		if (l==0 && length==0) { // special case of zero length in/out ranges
//			if (o==offset)
//				return OverlapType.INSIDE;
//			else
//				return OverlapType.NONE;
//		}
		
		int e = o + l; // non-inclusive end-index
		
		// overlap when one of the ranges is zero-length: 
		if (l==0 || length==0) {
			// if this tag is empty or both tags are empty, type is inside, else both... do we need this distincation?? probably not... replace by INSIDE then...
			OverlapType type = null;
//			if (l==0 && length==0)
//				type = OverlapType.INSIDE;
//			else if (length==0 && l==0)
//				type = OverlapType.INSIDE;
//			else
//				type = OverlapType.BOTH;
			
			type = OverlapType.INSIDE;
						
			if (o < offset && e >= offset)
				return type;
			else if (o >= offset && o <= getEnd())
				return type;
			else
				return OverlapType.NONE;
		}
		
		// overlap when both ranges are non-zero-length:
		if (o < offset) {
			if (e <= offset)
				return OverlapType.NONE;
			else if (e <= getEnd())
				return OverlapType.LEFT;
			else
				return OverlapType.BOTH;
		} else {
			if (o >= getEnd())
				return OverlapType.NONE;
			else if (e <= getEnd())
				return OverlapType.INSIDE;
			else
				return OverlapType.RIGHT;
		}
	}	
	
	@Override public boolean equals(Object o) {
		if (o==null || !(o instanceof IntRange)) return false;
		IntRange r = (IntRange) o;
		return offset == r.offset && length == r.length;
	}

	@Override public String toString() {
	    final String TAB = ", ";
	    String retValue = "IntRange ( offset = " + this.offset;
		retValue += TAB + "length = " + this.length;
		retValue += " )";
	    return retValue;
	}

}
