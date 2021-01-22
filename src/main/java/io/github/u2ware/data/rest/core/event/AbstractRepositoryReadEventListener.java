package io.github.u2ware.data.rest.core.event;

import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;

import org.springframework.context.ApplicationListener;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.AfterLinkDeleteEvent;
import org.springframework.data.rest.core.event.AfterLinkSaveEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.event.BeforeLinkDeleteEvent;
import org.springframework.data.rest.core.event.BeforeLinkSaveEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.core.event.RepositoryEvent;

//AbstractRepositoryEventListener
public class AbstractRepositoryReadEventListener<T> implements ApplicationListener<RepositoryEvent> {

	private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), AbstractRepositoryReadEventListener.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public final void onApplicationEvent(RepositoryEvent event) {

		
		Class<?> srcType = event.getSource().getClass();

		if (null != INTERESTED_TYPE && !INTERESTED_TYPE.isAssignableFrom(srcType)) {
			return;
		}

		if (event instanceof BeforeSaveEvent) {
			onBeforeSave((T) event.getSource());
		} else if (event instanceof BeforeCreateEvent) {
			onBeforeCreate((T) event.getSource());
		} else if (event instanceof AfterCreateEvent) {
			onAfterCreate((T) event.getSource());
		} else if (event instanceof AfterSaveEvent) {
			onAfterSave((T) event.getSource());
		} else if (event instanceof BeforeLinkSaveEvent) {
			onBeforeLinkSave((T) event.getSource(), ((BeforeLinkSaveEvent) event).getLinked());
		} else if (event instanceof AfterLinkSaveEvent) {
			onAfterLinkSave((T) event.getSource(), ((AfterLinkSaveEvent) event).getLinked());
		} else if (event instanceof BeforeLinkDeleteEvent) {
			onBeforeLinkDelete((T) event.getSource(), ((BeforeLinkDeleteEvent) event).getLinked());
		} else if (event instanceof AfterLinkDeleteEvent) {
			onAfterLinkDelete((T) event.getSource(), ((AfterLinkDeleteEvent) event).getLinked());
		} else if (event instanceof BeforeDeleteEvent) {
			onBeforeDelete((T) event.getSource());
		} else if (event instanceof AfterDeleteEvent) {
			onAfterDelete((T) event.getSource());
			
		} else if (event instanceof AfterReadEvent) {
			onAfterRead((T) event.getSource());
		} else if (event instanceof BeforeReadEvent) {
			onBeforeRead((T) event.getSource(), ((BeforeReadEvent) event).getLinked());
		}		
	}
	
	protected void onAfterRead(T entity) {}

	protected <X> void onBeforeRead(T entity, X query) {}

	protected void onBeforeCreate(T entity) {}

	protected void onAfterCreate(T entity) {}

	protected void onBeforeSave(T entity) {}

	protected void onAfterSave(T entity) {}

	protected void onBeforeLinkSave(T parent, Object linked) {}

	protected void onAfterLinkSave(T parent, Object linked) {}

	protected void onBeforeLinkDelete(T parent, Object linked) {}

	protected void onAfterLinkDelete(T parent, Object linked) {}

	protected void onBeforeDelete(T entity) {}

	protected void onAfterDelete(T entity) {}
	
}
