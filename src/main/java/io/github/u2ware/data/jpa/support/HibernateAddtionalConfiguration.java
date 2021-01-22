package io.github.u2ware.data.jpa.support;

import java.io.Serializable;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
//import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
//import org.hibernate.EntityMode;
//import org.hibernate.Transaction;
import org.hibernate.boot.internal.SessionFactoryOptionsBuilder;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePostInsertEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePostLoadEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePostUpdateEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreInsertEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreLoadEvent;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreUpdateEvent;

public class HibernateAddtionalConfiguration extends EmptyInterceptor implements InitializingBean, ApplicationEventPublisherAware, EmbeddedValueResolverAware, 
PostLoadEventListener, PreLoadEventListener , 
PreInsertEventListener, PostInsertEventListener,
PreUpdateEventListener, PostUpdateEventListener
{

	private static final long serialVersionUID = 2787103521260283735L;

	protected Log logger = LogFactory.getLog(getClass());

	private EntityManagerFactory emf;
	private StringValueResolver resolver;
	private ApplicationEventPublisher publisher;
	private boolean enableHandleLoad = true;
	
	public HibernateAddtionalConfiguration(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.resolver = resolver;
	}
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}


	public boolean isEnableHandleLoad() {
		return enableHandleLoad;
	}

	public void setEnableHandleLoad(boolean enableHandleLoad) {
		this.enableHandleLoad = enableHandleLoad;
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
		SessionFactoryOptionsBuilder options = (SessionFactoryOptionsBuilder) sessionFactory.getSessionFactoryOptions();
		options.applyInterceptor(this);
		
		EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.PRE_LOAD).appendListener(this);
		registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
		registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(this);
		registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
		registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(this);
		registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
	}
	
	
	@Override
	public void onPreLoad(PreLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePreLoadEvent(event.getEntity()));
	}
	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostLoadEvent(event.getEntity()));
	}
	
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		if (!isEnableHandleLoad()) return false;
		publisher.publishEvent(new HibernatePreInsertEvent(event.getEntity()));
		return false;
	}
	@Override
	public void onPostInsert(PostInsertEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostInsertEvent(event.getEntity()));
	}
	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		if (!isEnableHandleLoad()) return true;
		publisher.publishEvent(new HibernatePreUpdateEvent(event.getEntity())); 
		return true;
	}
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		if (!isEnableHandleLoad()) return;
		publisher.publishEvent(new HibernatePostUpdateEvent(event.getEntity()));
	}

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		return false;
	}

	
	
	@Override
	public String onPrepareStatement(String sql) {
		return resolver.resolveStringValue(sql);
	}




	
	

	//////////////////////////////////////
	//
	//////////////////////////////////////
//	@Override
//	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onLoad");
//		return false;
//	}
//
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
		logger.info("onFlushDirty");
		System.err.println("onFlushDirty : "+entity);
		return false;
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		logger.info("onSave");
		System.err.println("onSave : "+entity);
//		System.err.println(entity);
		return false;
	}
//
//	@Override
//	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
//		logger.info("onDelete");
//	}
//
//	@Override
//	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionRecreate");
//	}
//
//	@Override
//	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionRemove");
//	}
//
//	@Override
//	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
//		logger.info("onCollectionUpdate");
//	}
//
//	@Override
//	public void preFlush(Iterator entities) throws CallbackException {
//		logger.info("preFlush");
//	}
//
//	@Override
//	public void postFlush(Iterator entities) throws CallbackException {
//		logger.info("postFlush");
//	}
//
//	@Override
//	public Boolean isTransient(Object entity) {
//		logger.info("isTransient");
//		return null;
//	}
//
//	@Override
//	public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
//		logger.info("findDirty");
//		return null;
//	}
//
//	@Override
//	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
//		logger.info("instantiate: "+entityName);
//		return null;
//	}
//
//	@Override
//	public String getEntityName(Object object) throws CallbackException {
//		logger.info("getEntityName: "+object);
//		return null;
//	}
//
//	@Override
//	public Object getEntity(String entityName, Serializable id) throws CallbackException {
//		logger.info("getEntity: "+entityName);
//		return null;
//	}
//
//	@Override
//	public void afterTransactionBegin(Transaction tx) {
//		logger.info("afterTransactionBegin");
//		
//	}
//
//	@Override
//	public void beforeTransactionCompletion(Transaction tx) {
//		logger.info("beforeTransactionCompletion");
//		
//	}
//
//	@Override
//	public void afterTransactionCompletion(Transaction tx) {
//		logger.info("afterTransactionCompletion");
//		
//	}


}