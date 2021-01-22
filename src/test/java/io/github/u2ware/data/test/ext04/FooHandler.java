package io.github.u2ware.data.test.ext04;

import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePostInsertEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePostUpdateEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreInsertEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreUpdateEvent;

@RepositoryEventHandler(Foo.class)
@Component
public class FooHandler  {

	@EventListener
	public void HibernatePreInsertEvent(HibernatePreInsertEvent e) {
		System.err.println("x HibernatePreInsertEvent "+e);
	}
	
	@EventListener
	public void HibernatePostInsertEvent(HibernatePostInsertEvent e) {
		System.err.println("x HibernatePostInsertEvent "+e);
	}

	@EventListener
	public void HibernatePreUpdateEvent(HibernatePreUpdateEvent e) {
		System.err.println("x HibernatePreUpdateEvent "+e);
	}
	@EventListener
	public void HibernatePostUpdateEvent(HibernatePostUpdateEvent e) {
		System.err.println("x HibernatePostUpdateEvent "+e);
	}
	
	
	@HandleBeforeCreate
	public void handleBeforeCreate(Foo e){
		System.err.println("handleBeforeCreate "+e);
	}
	
	@HandleAfterCreate
	public void HandleAfterCreate(Foo e){
		System.err.println("HandleAfterCreate "+e);
	}

	@HandleBeforeSave
	public void HandleBeforeSave(Foo e){
		System.err.println("HandleBeforeSave "+e);
	}

	@HandleAfterSave
	public void HandleAfterSave(Foo e){
		System.err.println("HandleAfterSave "+e);
	}
	
	
	
	
	
}
