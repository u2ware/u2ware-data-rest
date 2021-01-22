package io.github.u2ware.data.rest.core.event;

import org.springframework.data.rest.core.event.RepositoryEvent;

public class AfterReadEvent extends RepositoryEvent {

	private static final long serialVersionUID = -6090615345948638970L;
	
	public AfterReadEvent(Object source) {
		super(source);
	}
}
