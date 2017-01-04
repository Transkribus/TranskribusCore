package eu.transkribus.core.model.beans.customtags;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.search.CustomTagSearchFacets;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpTagsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpTagsChangedEvent.Type;
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.OverlapType;

/**
 * Utility class that manages multiple CustomTag objects for a given
 * ITrpShapeType object It merges indexed tags according to their start and end
 * indices and removes empty valued tags. Non indexed tags are not added again
 * if already present.<br>
 * <em>Warning:</em> the code in this class is pretty complicated and was the
 * effort of <a
 * href="http://en.wikipedia.org/wiki/Blood,_toil,_tears,_and_sweat"
 * >"Blood, toil, tears and sweat"</a><br>
 * I hereby suggest not to make any blindfold changes to it
 * */
public class CustomTagList {
	private final static Logger logger = LoggerFactory.getLogger(CustomTagList.class);

	ITrpShapeType shape;
	List<CustomTag> tags = new ArrayList<CustomTag>();

	public CustomTagList(ITrpShapeType shape) {
		Assert.assertNotNull(shape);
		this.shape = shape;

		List<CustomTag> cts = CustomTagUtil.getCustomTags(shape.getCustom());
		logger.trace("nr of custom tags: " + cts.size() + " id: " + shape.getId());

		for (CustomTag ct : CustomTagUtil.getCustomTags(shape.getCustom())) {
			logger.trace("adding custom tag: " + ct);
			try {
				addOrMergeTag(ct, null);
			} catch (IndexOutOfBoundsException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	// public CustomTagList(CustomTagList src) {
	// this.tags = new ArrayList<>(src.tags.size());
	// Collections.copy(this.tags, src.tags);
	// setShape(src.shape);
	// }

	// void setShape(ITrpShapeType shape) {
	// Assert.assertNotNull(shape);
	// this.shape = shape;
	// }

	// public void update(Observable obj, Object arg) {
	// TrpObserveEvent e = (TrpObserveEvent) arg;
	// if (e instanceof TrpTextChangedEvent) {
	// logger.trace("text edited, who = "+e.who);
	// onTextEdited((TrpTextChangedEvent) e);
	// }
	// }

	public ITrpShapeType getShape() {
		return shape;
	}

	public int getTextLength() {
		return shape.getUnicodeText() != null ? shape.getUnicodeText().length() : 0;
	}

	public Pair<CustomTagList, CustomTag> getNextContinuedCustomTag(CustomTag ct) {
		if (!hasTag(ct) || !ct.isContinued()) // tag is not in the list
			return null;
		if (ct.getEnd() != getTextLength()) // tag does not reach the end
			return null;

		// get next shape:
		ITrpShapeType nextShape = shape.getSiblingShape(false);
		if (nextShape == null)
			return null;

		CustomTag nextTag = nextShape.getCustomTagList().getOverlappingTag(ct.getTagName(), 0);
		if (nextTag == null || !nextTag.isContinued())
			return null;

		return Pair.of(nextShape.getCustomTagList(), nextTag);
	}

	public Pair<CustomTagList, CustomTag> getPreviousContinuedCustomTag(CustomTag ct) {
		if (!hasTag(ct) || !ct.isContinued()) // tag is not in the list
			return null;
		if (ct.getOffset() != 0) // tag does not start at beginning
			return null;

		// get previous shape:
		ITrpShapeType prevShape = shape.getSiblingShape(true);
		if (prevShape == null)
			return null;

		int lastIndex = Math.max(0, prevShape.getCustomTagList().getTextLength() - 1);
		CustomTag prevTag = prevShape.getCustomTagList().getOverlappingTag(ct.getTagName(), lastIndex);
		logger.trace("prevTag = " + prevTag);
		if (prevTag == null || !prevTag.isContinued())
			return null;

		return Pair.of(prevShape.getCustomTagList(), prevTag);
	}

	public List<Pair<CustomTagList, CustomTag>> getCustomTagAndContinuations(CustomTag tag) {
		LinkedList<Pair<CustomTagList, CustomTag>> allTags = new LinkedList<>();
		if (!hasTag(tag))
			return allTags;

		allTags.add(Pair.of(this, tag));

		if (!tag.isContinued())
			return allTags;

		// previous tags:
		Pair<CustomTagList, CustomTag> c = getPreviousContinuedCustomTag(tag);
		while (c != null) {
			allTags.addFirst(c);
			c = c.getLeft().getPreviousContinuedCustomTag(c.getRight());
		}
		// next tags:
		c = getNextContinuedCustomTag(tag);
		while (c != null) {
			allTags.addLast(c);
			c = c.getLeft().getNextContinuedCustomTag(c.getRight());
		}

		return allTags;
	}

	public void deleteTagAndContinuations(CustomTag tag) {
		List<Pair<CustomTagList, CustomTag>> tags = getCustomTagAndContinuations(tag);
		logger.debug(tag + " tags and continuations: ");
		for (Pair<CustomTagList, CustomTag> t : tags) {
			logger.debug("1shape: " + t.getLeft().getShape().getId() + " tag: " + t.getRight());

			t.getLeft().removeTag(t.getRight());
		}
	}

	// public List<CustomTag> getCustomTagAndContinuations(String tagName, int
	// offset) { // FIXME??
	// CustomTag startTag = getOverlappingTag(tagName, offset);
	// if (startTag == null)
	// return null;
	//
	// CustomTag ct = startTag;
	// ITrpShapeType cs = shape;
	//
	// LinkedList<CustomTag> allTags = new LinkedList<>();
	// allTags.add(ct);
	//
	// // look at previous shapes:
	// while (ct!=null && ct.isContinued() && ct.getOffset()==0) {
	// ct = null;
	// cs = cs.getSiblingShape(true);
	// if (cs != null) {
	// CustomTagList prevCtl = cs.getCustomTagList();
	//
	// ct = prevCtl.getOverlappingTag(tagName, prevCtl.getLength()-1 >= 0 ?
	// prevCtl.getLength()-1 : 0);
	// if (ct!=null)
	// allTags.addFirst(ct);
	// }
	// }
	// // look at next shapes:
	// ct = startTag;
	// cs = shape;
	// while (ct!=null && ct.getEnd()==cs.getCustomTagList().getLength()) {
	// ct = null;
	// cs = cs.getSiblingShape(false);
	// if (cs != null) {
	// CustomTagList nextCtl = cs.getCustomTagList();
	//
	// ct = nextCtl.getOverlappingTag(tagName, 0);
	// if (ct!=null && ct.isContinued())
	// allTags.addLast(ct);
	// }
	// }
	//
	// return allTags;
	// }

	public boolean hasTag(CustomTag ct) {
		return tags.contains(ct);
	}

	// public CustomTagList(String customTag) {
	// for (CustomTag ct : CustomTagUtil.getCustomTags(customTag)) {
	// addOrMergeTag(ct, null);
	// }
	// }

	void checkRange(CustomTag tag) throws IndexOutOfBoundsException {
		if (!tag.isIndexed())
			return;

		int tl = getTextLength();
		IntRange shapeRange = new IntRange(0, tl);
		OverlapType ot = shapeRange.getOverlapType(tag.getOffset(), tag.getLength());
		if (ot != OverlapType.INSIDE ) {
			if (!tag.isEmpty() || tag.getOffset()!=tl) {
				throw new IndexOutOfBoundsException("Tag does not fit into shape text of size " + tl + " (offset/length of tag: " + tag.getOffset() + "/"
						+ tag.getLength() + ") tag: " + tag + " shape: " + shape.getId());
			}
		}
	}

	void checkAllTagRanges() throws IndexOutOfBoundsException {
		for (CustomTag t : tags) {
			if (t.isIndexed())
				checkRange(t);
		}
	}

	private void tryRegisterTag(CustomTag givenTag) {
		try {
			CustomTagFactory.addToRegistry(givenTag, null);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	public void addOrMergeTag(CustomTag givenTag, String addOnlyThisProperty) throws IndexOutOfBoundsException {
		addOrMergeTag(givenTag, addOnlyThisProperty, true);
	}

	/**
	 * Adds or merge the givenTag into this CustomTagList. If
	 * addOnlyThisProperty is not null, only this property of the givenTag is
	 * merged into the current list! This is useful e.g. when a user wants to
	 * add a certain style (bold, italic, ...) from the UI and does not want to
	 * overwrite other styles at this position.
	 * 
	 * @param givenTag
	 *            The tag to add or merge
	 * @param addOnlyThisProperty
	 *            If not null, only this property is added
	 * @param sendSingal
	 *            If true, a signal is sent that tags have changed
	 */
	public void addOrMergeTag(CustomTag givenTag, String addOnlyThisProperty, boolean sendSignal) throws IndexOutOfBoundsException {
		if (givenTag == null)
			return;

		logger.trace("adding/merging tag: " + givenTag + " addOnlyThisPropery: " + addOnlyThisProperty);

//		tryRegisterTag(givenTag);

		// non-indexed tag:
		if (!givenTag.isIndexed()) {
			CustomTag existing = getNonIndexedTag(givenTag.getTagName());
			if (existing != null)
				removeCustomTagFromList(existing);

			addCustomTagToList(givenTag);
			sortTags();
			notifyTagsChanged();
			return;
		}

		// indexed-tag:

		// check if tag fits into bounds of shape:
		checkRange(givenTag);

		if (tags.isEmpty()) {
			addCustomTagToList(givenTag);
			notifyTagsChanged();
			return;
		}

		// determine overlapping tags and compute splittings:
		List<CustomTag> overlapping = getOverlappingTagsOfType(givenTag);
		boolean hasOverlaps = overlapping.size() > 0;

		if (!hasOverlaps) {
			addCustomTagToList(givenTag);
		}

		int currentPosition = 0;
		final int N = overlapping.size();
		List<CustomTag> newTags = new ArrayList<>();
		for (int i = 0; i < overlapping.size(); ++i) {
			CustomTag overlapTag = overlapping.get(i);

			CustomTag left = null, middle = null, right = null; // the
																// overlapping
																// parts
			OverlapType overlapType = givenTag.getOverlapType(overlapTag);
			logger.trace(overlapType.toString() + " overlap!");
			switch (overlapType) {
			case NONE:
				break;
			case LEFT:
				Assert.assertEquals("For the LEFT overlap type, this must be the first element!", i, 0);
				left = overlapTag.copy();
				left.setOffset(overlapTag.getOffset());
				left.setLength(givenTag.getOffset() - overlapTag.getOffset());

				middle = overlapTag.copy();
				middle.setOffset(givenTag.getOffset());
				middle.setLength(overlapTag.getEnd() - givenTag.getOffset());

				currentPosition = middle.getEnd();

				// if this is the last overlapping tag: add right side carry
				// over:
				if ((i + 1) == N) {
					logger.trace("last overlap tag!");
					right = givenTag.copy();
					right.setOffset(currentPosition);
					right.setLength(givenTag.getEnd() - currentPosition);
				}

				removeCustomTagFromList(overlapTag);
				break;
			case INSIDE:
				int o = Math.max(currentPosition, givenTag.getOffset());
				int l = overlapTag.getOffset() - o;

				if (l > 0) {
					left = givenTag.copy();
					left.setOffset(o);
					left.setLength(l);
				}

				middle = overlapTag.copy();
				middle.setOffset(overlapTag.getOffset());
				middle.setLength(overlapTag.getLength());

				currentPosition = middle.getEnd();

				// if this is the last overlapping tag: add right side carry
				// over:
				if ((i + 1) == N) {
					logger.trace("last overlap tag!");
					right = givenTag.copy();
					right.setOffset(currentPosition);
					right.setLength(givenTag.getEnd() - currentPosition);
				}

				removeCustomTagFromList(overlapTag);
				break;
			case RIGHT:
				Assert.assertEquals("For the RIGHT overlap type, this must be the last element!", i + 1, N);
				int o1 = Math.max(currentPosition, givenTag.getOffset());
				int l1 = overlapTag.getOffset() - o1;

				if (l1 > 0) {
					left = givenTag.copy();
					left.setOffset(Math.max(currentPosition, givenTag.getOffset()));
					left.setLength(l1);
				}

				middle = overlapTag.copy();
				middle.setOffset(overlapTag.getOffset());
				middle.setLength(givenTag.getEnd() - overlapTag.getOffset());

				right = overlapTag.copy();
				right.setOffset(middle.getEnd());
				right.setLength(overlapTag.getLength() - middle.getLength());

				currentPosition = right.getEnd(); // not really here necessary i
													// guess..

				removeCustomTagFromList(overlapTag);
				break;
			case BOTH:
				logger.trace("N=" + N);
				Assert.assertEquals("For the BOTH overlap type, nr of overlapping elements must be 1!", N, 1);

				if (givenTag.getOffset() - overlapTag.getOffset() > 0) {
					left = overlapTag.copy();
					left.setOffset(overlapTag.getOffset());
					left.setLength(givenTag.getOffset() - overlapTag.getOffset());
				}

				middle = overlapTag.copy();
				middle.setOffset(givenTag.getOffset());
				middle.setLength(givenTag.getLength());

				if (overlapTag.getEnd() - givenTag.getEnd() > 0) {
					right = overlapTag.copy();
					right.setOffset(middle.getEnd());
					right.setLength(overlapTag.getEnd() - givenTag.getEnd());
				}

				if (right != null)
					currentPosition = right.getEnd(); // not really necessary
														// here i guess..
				else
					currentPosition = middle.getEnd(); // not really necessary
														// here i guess..

				removeCustomTagFromList(overlapTag);
				break;
			}

			// middle is the overlapping part whose fields shall be merged
			if (middle != null) {
				if (addOnlyThisProperty != null) { // set only this property if
													// not null!
					String propValue;
					try {
						propValue = BeanUtils.getProperty(givenTag, addOnlyThisProperty);
						logger.trace("setting prop / value = " + addOnlyThisProperty + "/" + propValue);
						BeanUtils.setProperty(middle, addOnlyThisProperty, propValue);
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						logger.warn("Could not retrieve or set property '" + addOnlyThisProperty + "' from bean, message: " + e.getMessage());
					}
				} else { // or if no property string given --> merge fields from
							// given tag into middle tag
					middle.setAttributes(givenTag, false);
				}
			}

			// now add the newly created tags:
			if (left != null) {
				newTags.add(left);
				addCustomTagToList(left);
			}
			if (middle != null) {
				newTags.add(middle);
				addCustomTagToList(middle);
			}
			if (right != null) {
				newTags.add(right);
				addCustomTagToList(right);
			}

			logger.trace("overlapping tag, i=" + i);
		} // end for all overlapping tags i

		// now sort tags, merge neighours that are equals and remove empty
		// valued tags:
		sortTags();
		mergeNeighboringEqualValuedIndexedTags(newTags, false); // <--- TEST
		removeEmptyAndEmptyValuedTags();

		if (sendSignal)
			notifyTagsChanged();

		logger.trace("ctl: " + this);
	}

	void notifyTagsChanged() {
		// if (true) return;
		shape.getObservable().setChangedAndNotifyObservers(new TrpTagsChangedEvent(shape, this, Type.CHANGED));
	}
	
	public void removeNonIndexedTags() {
		for (CustomTag t : getNonIndexedTags()) {
			removeTag(t);
		}
	}
	
	public void removeIndexedTags() {
		for (CustomTag t : getIndexedTags()) {
			removeTag(t);
		}
	}

	public void removeTag(CustomTag t) {
		List<CustomTag> tagsCopy = new ArrayList<>(tags);
		for (CustomTag ct : tagsCopy) {
			if (ct.equals(t)) {
				removeCustomTagFromList(t);
			}
		}
		sortTags();
		notifyTagsChanged();
	}

	/**
	 * Remove all tags with the given tagName
	 */
	public void removeTags(String tagName) {
		List<CustomTag> tagsCopy = new ArrayList<>(tags);
		for (CustomTag t : tagsCopy) {
			if (t.getTagName().equals(tagName)) {
				removeCustomTagFromList(t);
			}
		}
		sortTags();
		notifyTagsChanged();
	}

	/**
	 * Return the custom tag as a list of css-styled tags
	 */
	public String getCustomTag() {
		if (getTags().isEmpty()) {
			return null;
		}

		String custom = "";
		for (CustomTag t : getTags()) {
			custom += t.getCssStr() + " ";
		}
		return custom.trim();
	}

	public List<CustomTag> getTags() {
		return tags;
	}

	public <T extends CustomTag> T getNonIndexedTag(String tagName) {
		for (CustomTag t : tags) {
			if (!t.isIndexed() && t.getTagName().equals(tagName))
				return (T) t;
		}
		return null;
	}

	public <T extends CustomTag> List<T> getIndexedTags(String tagName) {
		List<T> it = new ArrayList<>();
		for (CustomTag t : tags) {
			if (t.isIndexed() && t.getTagName().equals(tagName))
				it.add((T) t);
		}
		return it;
	}

	public Set<CustomTag> getIndexedTags() {
		Set<CustomTag> indexedTags = new HashSet<>();
		for (CustomTag t : tags) {
			if (t.isIndexed())
				indexedTags.add(t);
		}
		return indexedTags;
	}

	public Set<String> getIndexedTagNames() {
		Set<String> indexedTagNames = new HashSet<>();
		for (CustomTag t : tags) {
			if (t.isIndexed())
				indexedTagNames.add(t.getTagName());
		}
		return indexedTagNames;
	}

	public Set<CustomTag> getNonIndexedTags() {
		Set<CustomTag> indexedTags = new HashSet<>();
		for (CustomTag t : tags) {
			if (!t.isIndexed())
				indexedTags.add(t);
		}
		return indexedTags;
	}

	public Set<String> getNonIndexedTagNames() {
		Set<String> indexedTagNames = new HashSet<>();
		for (CustomTag t : tags) {
			if (!t.isIndexed())
				indexedTagNames.add(t.getTagName());
		}
		return indexedTagNames;
	}

	/**
	 * Returns a CustomTag of type T that covers the given range between
	 * (offset, offset+length] and with attributes that are equal along this
	 * range. If attributes do not match over the whole range, its default value
	 * is set. Null is returned, if there are any gaps in the given range where
	 * no tag is defined.<br>
	 * Constructed especially for {@link TextStyleTag} objects where a user
	 * wants to determine the common style for a certain range in the text.
	 */
	public <T extends CustomTag> T getCommonIndexedCustomTag(String tagName, int offset, int length) {
		List<T> cts = getIndexedTags(tagName);
		logger.trace("getCommonIndexedCustomTag: " + offset + "/" + length + " n-tags = " + cts.size());
		// no tags --> return null
		if (cts.isEmpty())
			return null;

		boolean sneakToLeft = cts.get(0).sneakToLeft();

		List<CustomTag> overlapping;
		if (sneakToLeft && offset > 1) {
			overlapping = getOverlappingTags(tagName, offset - 1, length + 1);
		} else {
			overlapping = getOverlappingTags(tagName, offset, length);
		}

		if (overlapping.isEmpty())
			return null;
		if (overlapping.get(0).getOffset() > offset) // gap between first tag
														// and given offset!
			return null;
		if (overlapping.get(overlapping.size() - 1).getEnd() < (offset + length)) // gap
																					// between
																					// last
																					// tag
																					// and
																					// end
																					// of
																					// given
																					// range!
			return null;

		logger.trace("nr of overlapping = " + overlapping.size());

		// check if all overlapping tags are consecutive and determine their
		// common attributes:
		CustomTag commonTag = overlapping.get(0).copy();
		logger.trace("commonTag, start = " + commonTag);
		for (int i = 1; i < overlapping.size(); ++i) {
			CustomTag next = overlapping.get(i);
			if (next.getOffset() != commonTag.getEnd()) // non-consecutive tags!
				return null;
			else { // consecutive --> adjust offset and merge equals fields
				commonTag.setLength(next.getEnd() - commonTag.getOffset());
				commonTag.mergeEqualAttributes(next, false);
			}
			logger.trace("commonTag, " + i + " = " + commonTag);
		}

		// now set offset and length according to the given range and return:
		commonTag.setOffset(offset);
		commonTag.setLength(length);

		logger.trace("commonTag final = " + commonTag);
		return (T) commonTag;
	}

	// public void clearAllTags() {
	// tags.clear();
	// notifyTagsChanged();
	// }

	public boolean isSingleIndexedTagOverShapeRange(String tagName) {
		return isSingleIndexedTagOverRange(tagName, 0, getTextLength());
	}

	public boolean isSingleIndexedTagOverRange(String tagName, int offset, int length) {
		List<CustomTag> tags = getIndexedTags(tagName);
		return (tags.size() == 1) ? (tags.get(0).hasRange(offset, length)) : false;
	}

	private void mergeNeighboringEqualValuedIndexedTags(List<CustomTag> alwaysMergeTheseTags, boolean forceMerge) {
		logger.trace("merging tags, currently: " + tags.size());
		logger.trace("always merge tags: " + alwaysMergeTheseTags.size());

		List<CustomTag> mergedTags = new ArrayList<>();
		List<Integer> skip = new ArrayList<Integer>();

		for (int i = 0; i < tags.size(); ++i) {
			if (skip.contains(i))
				continue;
			
			CustomTag t = tags.get(i);
			if (t.isIndexed()) {
				boolean isAlwaysMergeTag1 = alwaysMergeTheseTags.contains(t);
				for (int j = i + 1; j < tags.size(); ++j) {
					if (skip.contains(j))
						continue;					
					
					CustomTag n = tags.get(j);
					if (!n.getTagName().equals(t.getTagName()))
						continue;
					
					boolean isNeighborAndEqualValues = n.getOffset() == t.getEnd() && t.equalsEffectiveValues(n, false);
					// ...
					if (!isNeighborAndEqualValues) {
						break;
					} else {
						boolean isAlwaysMergeTag2 = alwaysMergeTheseTags.contains(n);
						logger.trace("i1 = " + isAlwaysMergeTag1 + " i2 = " + isAlwaysMergeTag2);
						if ( (isAlwaysMergeTag1 && isAlwaysMergeTag2) || (t.mergeWithEqualValuedNeighbor() || forceMerge) ) {
							t.setLength(t.getLength() + n.getLength());
//							i = j;
							skip.add(j);
						} else
							break;
					}
				}
			}
			
			mergedTags.add(t);
		}

		tags = mergedTags;
		logger.trace("merged tags, now: " + tags.size());
	}

	private void removeEmptyAndEmptyValuedTags() {
		List<CustomTag> copyOfTags = new ArrayList<>(tags);
		for (CustomTag t : copyOfTags) {
			if (t.canBeEmpty())
				continue;
			
			if (t.isEmptyValued() || t.isEmpty()) {
				removeCustomTagFromList(t);
			}
		}
	}

	/**
	 * Returns all <em>common</em> indexed tags for the given range
	 */
	public List<CustomTag> getCommonIndexedTags(int start, int length) {
		List<CustomTag> indexedTags = new ArrayList<>();
		for (String tagName : getIndexedTagNames()) {
			CustomTag ct = getCommonIndexedCustomTag(tagName, start, length);
			if (ct != null)
				indexedTags.add(ct);
		}
		return indexedTags;
	}

	// FIXME: Test, Test, Test...
	public void onTextEdited(int start, int end, String replacement) {
		if (false)
			return;
		
		// int start = e.start;
		// int end = e.end;
		// String replacement = e.text;
		logger.trace("on text edited: start, end, replacement: " + start + ", " + end + ", " + replacement + " id = " + shape.getId());

		// get tags from start index
		List<CustomTag> startIndexTags = null;
		final boolean isEmptyText = replacement.isEmpty();
		if (!isEmptyText) { // only needed later when replacement not empty!
			startIndexTags = getCommonIndexedTags(start, 0); // the new shit ->
																// get *common*
																// tags at index
			// startIndexTags = getOverlappingTags(null, start, 0); // the old
			// shit -> get *all* overlapping tags
			for (CustomTag t : startIndexTags) {
				logger.trace("start-index tag: " + t);
			}
		}

		// delete tags in edit-range:
		deleteTagsInRange(start, end - start, false);

		// adjust indices according to edit position:
		final int adjust = -(end - start) + replacement.length();
		for (CustomTag t : tags) {
			if (!t.isIndexed())
				continue;

			if (t.getOffset() < start && t.getEnd() > start) { // edit on right side of tag start
				t.setLength(t.getLength() + adjust);
			}
			else if (t.getOffset() == start && !t.isEmpty()) { // edit on tag start -> exclude empty tags to prevent copying of them!
				t.setOffset(t.getOffset() + adjust);
			}
			else if (t.getOffset() > start) { // edit on left side of tag start
				t.setOffset(t.getOffset() + adjust);
			}
		}

		// add new tags if new text was inserted:
		if (!isEmptyText) {
			for (CustomTag t : startIndexTags) {
				CustomTag newT = t.copy();
				newT.setOffset(start);
				newT.setLength(replacement.length());
				addOrMergeTag(newT, null, false);
				// tags.add(newT);
			}
		}

		sortTags();
		checkAllTagRanges();
		mergeNeighboringEqualValuedIndexedTags(new ArrayList<CustomTag>(), true); // DELETED HERE, ADDED IN deleteTagsInRange
//		removeEmptyAndEmptyValuedTags(); // not necessary here I guess...

		logger.trace("ctl: " + this);

		if (true) // TODO: check above, if tags have really changed (maybe some
					// time...)
			notifyTagsChanged();
	}

	// FIXME: Test, Test, Test...
	public void deleteTagsInRange(int offset, int length, boolean sendSignal) {
		// if (offset < 0 || (offset+length) > shape.getUnicodeText().length())
		// throw new
		// IndexOutOfBoundsException("Cannot delete range: "+offset+"/"+(offset+length)+", text size is: "+shape.getUnicodeText().length());

		if (length == 0)
			return;

		List<CustomTag> overlapping = getOverlappingTags(null, offset, length);
		
		logger.debug("overlapping tags are: "+overlapping.size());
		for (CustomTag t : overlapping) {
			logger.debug("t = "+t);
		}

		IntRange range = new IntRange(offset, length);

		List<CustomTag> newTags = new ArrayList<CustomTag>();
		for (CustomTag overlapTag : overlapping) {
			OverlapType type = range.getOverlapType(overlapTag.getOffset(), overlapTag.getLength());
			CustomTag left = null, right = null;
			// compute left and right overlaps that must be retained after
			// deleting:
			switch (type) {
			case BOTH:
				left = overlapTag.copy();
				left.setOffset(overlapTag.getOffset());
				left.setLength(range.getOffset() - overlapTag.getOffset());

				right = overlapTag.copy();
				right.setOffset(range.getEnd());
				right.setLength(overlapTag.getEnd() - range.getEnd());
				break;
			case INSIDE:
				// a tag that was inside the range will only be removed!
				break;
			case LEFT:
				left = overlapTag.copy();
				left.setOffset(overlapTag.getOffset());
				left.setLength(range.getOffset() - overlapTag.getOffset());
				break;
			case RIGHT:
				right = overlapTag.copy();
				right.setOffset(range.getEnd());
				right.setLength(overlapTag.getEnd() - range.getEnd());
				break;
			case NONE:
				throw new RuntimeException("Fatal error: tag overlap cannot be == NONE here, overlapTag = "+overlapTag);
			}

			// remove the overlapTag and add the left and right overlaps if
			// present:
			removeCustomTagFromList(overlapTag);
			if (left != null) {
				addCustomTagToList(left);
				newTags.add(left);
			}
			if (right != null) {
				addCustomTagToList(right);
				newTags.add(right);
			}
		}

		// now sort tags, merge neighours that are equals and remove empty and
		// empty valued tags:
		sortTags();
//		mergeNeighboringEqualValuedIndexedTags(newTags); // not necessary here I
		// guess...
		removeEmptyAndEmptyValuedTags(); // also not necessary but cannot hurt
											// (I guess)
		if (sendSignal)
			notifyTagsChanged();
	}

	private List<CustomTag> getOverlappingTagsOfType(CustomTag givenSt) {
		return getOverlappingTags(givenSt.getTagName(), givenSt.getOffset(), givenSt.getLength());
	}

	public CustomTag getOverlappingTag(String tagName, int offset) {
		List<CustomTag> tagsAtOffset = getOverlappingTags(tagName, offset, 0);
		if (tagsAtOffset.isEmpty())
			return null;

		// should never happen!
		Assert.assertTrue("Nr of tags at position " + offset + " is greater 1: " + tagsAtOffset.size(), tagsAtOffset.size() == 1);

		CustomTag firstTag = tagsAtOffset.get(0);
		return firstTag;
	}

	/**
	 * Returns all overlapping tags for the given range and the specified
	 * tagName. If tagName is null, all tags are considered.
	 */
	public List<CustomTag> getOverlappingTags(String tagName, int offset, int length) {
		List<CustomTag> overlapping = new ArrayList<>();
		
		for (CustomTag st : tags) {
			if (!st.isIndexed())
				continue;
			if (st.getOverlapType(offset, length) == OverlapType.NONE)
				continue;

			if (tagName == null || st.getTagName().equals(tagName))
				overlapping.add(st);
		}

		return overlapping;
	}

	private void sortTags() {
		Collections.sort(tags);
	}

	@Override public String toString() {
		String str = "CustomTagList: nr of tags: " + tags.size() + "; ";
		for (CustomTag t : tags) {
			str += t + "; ";
		}
		return str;
	}

	public void writeToCustomTag() {
		logger.trace("writeToCustomTag, shape = " + shape);
		shape.setCustom(this.getCustomTag());
	}

	private void addCustomTagToList(CustomTag tag) {
		tags.add(tag);
		tag.customTagList = this;
	}

	private void removeCustomTagFromList(CustomTag tag) {
		tags.remove(tag);
		tag.customTagList = null;
	}
	
	public void printTags() {
		logger.info("Custom tags for shape " + getShape().getId() + ":");
		for (CustomTag t : tags) {
			logger.info(t.toString());
		}
		logger.info("----------------");
	}
	
//	public List<CustomTag> findText(TextSearchFacets facets, boolean stopOnFirst, int startOffset, boolean previous) {
//		List<CustomTag> ft = new ArrayList<>();
//		
////		 sortTags(); // should be sorted, just to be sure...
////		 printTags();
//		
//		String textRegex = facets.getText(true);
////		if (facets.isWholeWord()) {
////			textRegex = "\\b"+textRegex+"\\b";
////		}
//		
//		String txt = getShape().getUnicodeText();
//		logger.debug("searching for text: "+textRegex+" in line: "+txt);
//		
////		Pattern p = Pattern.compile(textRegex, facets.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);
//		Pattern p = Pattern.compile(textRegex);
//		
//		Matcher m = p.matcher(txt);
//		while (m.find()) {
//			if (!previous && startOffset!=-1 && m.start() < startOffset)
//				continue;
//			else if (previous && startOffset!=-1 && m.end() >= startOffset)
//				continue;
//						
//		    String s = m.group();
//		    logger.debug("found matching text: "+s);
//		    
//		    CustomTag t = new CustomTag("textSearch");
//		    t.setOffset(m.start());
//		    t.setLength(s.length());
//		    t.customTagList = this;
//		    
//		    ft.add(t);
//			if (stopOnFirst)
//				return ft;
//		    
//		    logger.debug("textSearch tag: "+t);
//		}
//
//		return ft;
//	}	

	/**
	 * Finds custom tags in this list for given the facets - every facet can
	 * include the wildcards '*' for multiple unknown characters and '?' for one
	 * single unknown character
	 * 
	 * @param tagNameRegex
	 *            The wildcarded tag name to search for
	 * @param props
	 *            A map containing wildcarded property names and (optional)
	 *            wildcarded values for those properties that must be satisfied
	 *            by the returned tags
	 * @param stopOnFirst
	 *            Stop on the first tag matching
	 * @param startOffset
	 *            The offset in the text to start searching from
	 * @param previous
	 *            True to search for tags before the given offset
	 * @return The list of tags fulfilling the search criteria
	 */
	public List<CustomTag> findTags(CustomTagSearchFacets facets, boolean stopOnFirst, int startOffset, boolean previous) {
		List<CustomTag> ft = new ArrayList<>();

//		 sortTags(); // should be sorted, just to be sure...
//		 printTags();
		
		int inc = previous ? -1 : 1;
		logger.debug("searching for tags in shape="+shape.getId()+", startOffset = "+startOffset);
		
		// FIXME ?
		for (int i=previous?tags.size()-1:0; previous && i>=0 || !previous && i<tags.size(); i+=inc) {
			CustomTag t = tags.get(i);
			if (!previous && startOffset!=-1 && t.getOffset() < startOffset)
				continue;
			else if (previous && startOffset!=-1 && t.getEnd() >= startOffset)
				continue;
			
			if (!t.showInTagWidget()) {
				continue;
			}

			String tn = facets.getTagName(true);
			String tv = facets.getTagValue(true);

			logger.debug("tagNameRegex = " + tn + " tag: " + t.getTagName());
			if (!tn.isEmpty() && !t.getTagName().matches(tn)) {
				logger.debug("tag name '" + t.getTagName() + "' does not match!");
				continue;
			}

			logger.debug("tagValue = " + tv + " tag value: " + t.getContainedText());
			if (!tv.isEmpty() && !t.getContainedText().matches(tv)) {
				logger.debug(t.getContainedText() + " does not match!");
				continue;
			}

			Set<String> propertiesToSearch = facets.getProperties(true);
			boolean hasAttributeMatch = propertiesToSearch.isEmpty();

			for (String a : facets.getProperties(true)) {
				Object v = facets.getPropValue(a, true);

				logger.debug("searching for prop: " + a + " v: " + v);
				for (String an : t.getAttributeNames()) {
					if (!an.matches(a)) // attribute does not match name
						continue;

					// if attribute value is not null, check for a match:
					if (v != null) { // TODO: check for attribute value!
						String vStr = v.toString();

						Object av = t.getAttributeValue(an);
						String aVStr = (av == null) ? "" : av.toString();
						logger.debug("vStr = " + vStr + " aVStr = " + aVStr);

						if (aVStr.matches(vStr)) {
							hasAttributeMatch = true;
							break;
						}
					} // end if v != null
					else {
						hasAttributeMatch = true;
						break;
					}
				}
				if (hasAttributeMatch)
					break;
			} // end check of all attributes

			// if all 'tests' passed -> add to list of found tags!
			if (hasAttributeMatch) {
				ft.add(t);
				if (stopOnFirst)
					break;
			}
		}

		return ft;
	}
}
