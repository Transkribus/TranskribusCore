package eu.transkribus.core.util;

import java.util.EventObject;

public class Event extends EventObject {
	private static final long serialVersionUID = 1L;
	public String description;

	public Event(Object source, String description) {
		super(source);
		this.description = description;
	}
	
	public Event(Object source) {
		super(source);
		this.description = "";
	}
}
