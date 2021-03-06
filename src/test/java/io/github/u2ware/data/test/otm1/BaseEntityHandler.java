package io.github.u2ware.data.test.otm1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;


@RepositoryEventHandler(BaseEntity.class)
@Component
public class BaseEntityHandler  {

//	@EventListener
//	public void HibernatePreInsertEvent(HibernatePreInsertEvent e) {
//		System.err.println("x HibernatePreInsertEvent "+e);
//	}
//	
//	@EventListener
//	public void HibernatePostInsertEvent(HibernatePostInsertEvent e) {
//		System.err.println("x HibernatePostInsertEvent "+e);
//	}
//
//	@EventListener
//	public void HibernatePreUpdateEvent(HibernatePreUpdateEvent e) {
//		System.err.println("x HibernatePreUpdateEvent "+e);
//	}
//	@EventListener
//	public void HibernatePostUpdateEvent(HibernatePostUpdateEvent e) {
//		System.err.println("x HibernatePostUpdateEvent "+e);
//	}
//	
//	private @Autowired ManyToOneEntityRepository ManyToOneEntityRepository;
//	private @Autowired ResourceMappings resourceMappings;

	@HandleBeforeCreate
	public void handleBeforeCreate(BaseEntity e){
		System.err.println("handleBeforeCreate "+e);
//		e.getOneToManyEntities().add(OneToManyEntity.builder().name(""+System.currentTimeMillis()).build());
	}
//	
//	@HandleAfterCreate
//	public void HandleAfterCreate(BaseEntity e){
//		System.err.println("HandleAfterCreate "+e);
//	}
//
//	@HandleBeforeSave
//	public void HandleBeforeSave(BaseEntity e){
//		System.err.println("HandleBeforeSave "+e);
//	}
//
//	@HandleAfterSave
//	public void HandleAfterSave(BaseEntity e){
//		System.err.println("HandleAfterSave "+e);
//	}
//	
//	
//	@HandleBeforeLinkSave
//	public void HandleBeforeLinkSave(BaseEntity e, Object link){
//		System.err.println("@HandleBeforeLinkSave "+e);
//		System.err.println("@HandleBeforeLinkSave "+link);
//	}
//
//	@HandleAfterLinkSave
//	public void HandleAfterLinkSave(BaseEntity e, Object link){
//		System.err.println("@HandleAfterLinkSave "+e);
//		System.err.println("@HandleAfterLinkSave "+link);
//	}
	
	protected Log logger = LogFactory.getLog(getClass());
	

	
}
