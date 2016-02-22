package eu.transkribus.core.model.beans.pagecontent_extension.observable;

import eu.transkribus.core.model.beans.customtags.CustomTagList;

public class TrpObserveEvent {
	private static final String COORDS_CHANGED = "COORDS_CHANGED";
	private static final String TEXT_CHANGED = "TEXT_CHANGED";
	private static final String TAGS_CHANGED = "TAGS_CHANGED";
	private static final String REMOVED = "REMOVED";
	private static final String REINSERT_INTO_PARENT = "REINSERT_INTO_PARENT";
	private static final String CONSTRUCTED_WITH_PARENT = "CONSTRUCTED_WITH_PARENT";
	private static final String CHILDREN_CLEARED = "CHILDREN_CLEARED";
	private static final String TEXTSTYLE_CHANGED = "TEXTSTYLE_CHANGED";
	private static final String STRUCTURE_CHANGED = "STRUCTURE_CHANGED";
	private static final String READING_ORDER_CHANGED = "READING_ORDER_CHANGED";
	
	public String description;
	public Object who;
	public Object data;
	
	public TrpObserveEvent(String description, Object who) {
		this.description = description;
		this.who = who;
	}
	
	public TrpObserveEvent(String description, Object who, Object data) {
		this.description = description;
		this.who = who;
		this.data = data;
	}
	
	public static class TrpTextChangedEvent extends TrpObserveEvent {
		public int start, end;
		public String text;
		
		public TrpTextChangedEvent(Object who, int start, int end, String text) {
			super(TEXT_CHANGED, who);
			this.start = start; this.end = end; this.text = text;
		}
	}
	
	public static class TrpReadingOrderChangedEvent extends TrpObserveEvent {
		public TrpReadingOrderChangedEvent(Object who) {
			super(READING_ORDER_CHANGED, who);
		}
	}
	
	public static class TrpStructureChangedEvent extends TrpObserveEvent {
		public TrpStructureChangedEvent(Object who) {
			super(STRUCTURE_CHANGED, who);
		}
	}
	
	public static class TrpTextStyleChangedEvent extends TrpObserveEvent {
		public TrpTextStyleChangedEvent(Object who) {
			super(TEXTSTYLE_CHANGED, who);
		}
	}
	
	public static class TrpChildrenClearedEvent extends TrpObserveEvent {
		public TrpChildrenClearedEvent(Object who) {
			super(CHILDREN_CLEARED, who);
		}
	}
	
	public static class TrpConstructedWithParentEvent extends TrpObserveEvent {
		public TrpConstructedWithParentEvent(Object who) {
			super(CONSTRUCTED_WITH_PARENT, who);
		}
	}
	
	public static class TrpReinsertIntoParentEvent extends TrpObserveEvent {
		public TrpReinsertIntoParentEvent(Object who) {
			super(REINSERT_INTO_PARENT, who);
		}
	}
	
	public static class TrpRemovedEvent extends TrpObserveEvent {
		public TrpRemovedEvent(Object who) {
			super(REMOVED, who);
		}
	}
	
	public static class TrpTagsChangedEvent extends TrpObserveEvent {
		public enum Type { // FIXME??
			REMOVE, ADD, CHANGED;
		}
		
		public Type type;
		public CustomTagList ctl;

		public TrpTagsChangedEvent(Object who, CustomTagList ctl, Type type) {
			super(TAGS_CHANGED, who);
			this.type = type;
			this.ctl = ctl;
		}
	}
	
	public static class TrpCoordsChangedEvent extends TrpObserveEvent {
		public TrpCoordsChangedEvent(Object who) {
			super(COORDS_CHANGED, who);
		}
	}
	
}


