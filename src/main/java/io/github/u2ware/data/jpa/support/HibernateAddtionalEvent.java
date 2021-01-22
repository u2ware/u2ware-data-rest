package io.github.u2ware.data.jpa.support;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public interface HibernateAddtionalEvent {

	public class HibernatePreLoadEvent extends ApplicationEvent{
		public HibernatePreLoadEvent(Object source) {
			super(source);
		}
	}
	public class HibernatePostLoadEvent extends ApplicationEvent{
		public HibernatePostLoadEvent(Object source) {
			super(source);
		}
	}
	public class HibernatePreInsertEvent extends ApplicationEvent{
		public HibernatePreInsertEvent(Object source) {
			super(source);
		}
	}
	public class HibernatePostInsertEvent extends ApplicationEvent{
		public HibernatePostInsertEvent(Object source) {
			super(source);
		}
	}
	public class HibernatePreUpdateEvent extends ApplicationEvent{
		public HibernatePreUpdateEvent(Object source) {
			super(source);
		}
	}
	public class HibernatePostUpdateEvent extends ApplicationEvent{
		public HibernatePostUpdateEvent(Object source) {
			super(source);
		}
	}
	
	
}
