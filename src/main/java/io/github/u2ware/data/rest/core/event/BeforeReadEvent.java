package io.github.u2ware.data.rest.core.event;

import org.springframework.data.rest.core.event.LinkedEntityEvent;

public class BeforeReadEvent extends LinkedEntityEvent {

	private static final long serialVersionUID = -6090615345948638970L;

	public BeforeReadEvent(Object source, Object query) {
		super(source, query);
	}
}
