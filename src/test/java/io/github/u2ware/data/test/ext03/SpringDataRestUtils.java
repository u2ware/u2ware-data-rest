package io.github.u2ware.data.test.ext03;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.support.SelfLinkProvider;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
public class SpringDataRestUtils implements ApplicationContextAware, BeanClassLoaderAware, InitializingBean{

	//Aware
	protected static ApplicationContext applicationContext; //ApplicationContextAware
	protected static ClassLoader classLoader; //BeanClassLoaderAware

	
	//@Autowired
	protected static RepositoryRestConfiguration config;
	protected static Associations associationLinks;
	private static PersistentEntities persistentEntities;
	private static SelfLinkProvider selfLinkProvider;
	private static Repositories repositories;
	private static EntityLinks entityLinks;
	private static ResourceMappings resourceMappings;
	private static ConversionService conversionService;
	
	
	//InitializingBean
//	private static ProjectionDefinitionConfiguration projectionConfiguration;
//	private static SpelAwareProxyProjectionFactory projectionFactory;
//	private static PersistentEntityResourceAssembler defaultPersistentEntityResourceAssembler;
//	private static DomainObjectReader domainObjectReader;
	
	@Autowired
	public SpringDataRestUtils(
			RepositoryRestConfiguration config, 
			PersistentEntities persistentEntities,
			SelfLinkProvider selfLinkProvider,
			Associations associationLinks,
			Repositories repositories,
			EntityLinks entityLinks,
			ResourceMappings resourceMappings,
			@Qualifier("defaultConversionService")ConversionService conversionService
			
	) {
		SpringDataRestUtils.config = config;		
		SpringDataRestUtils.persistentEntities = persistentEntities;		
		SpringDataRestUtils.selfLinkProvider = selfLinkProvider;		
		SpringDataRestUtils.associationLinks = associationLinks;		
		SpringDataRestUtils.repositories = repositories;		
		SpringDataRestUtils.entityLinks = entityLinks;		
		SpringDataRestUtils.resourceMappings = resourceMappings;		
		SpringDataRestUtils.conversionService = conversionService;		
	}
	
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		SpringDataRestUtils.classLoader = classLoader;		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringDataRestUtils.applicationContext = applicationContext;		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
//		projectionFactory = new SpelAwareProxyProjectionFactory();
//		projectionFactory.setBeanFactory(applicationContext);
//		projectionFactory.setBeanClassLoader(classLoader);
//		
//		
//		projectionConfiguration = config.getProjectionConfiguration();
//
//		PersistentEntityResourceAssemblerArgumentResolver peraResolver = new PersistentEntityResourceAssemblerArgumentResolver(
//				persistentEntities, selfLinkProvider, projectionConfiguration, projectionFactory, associationLinks);
//		
//		String projectionParameter = null;//webRequest.getParameter(projectionDefinitions.getParameterName());
//		PersistentEntityProjector projector = new PersistentEntityProjector(projectionConfiguration, projectionFactory, projectionParameter, associationLinks.getMappings());
//
//		PersistentEntityResourceAssembler persistentEntityResourceAssembler 
//			=  new PersistentEntityResourceAssembler(persistentEntities, projector, associationLinks, selfLinkProvider);
//		defaultPersistentEntityResourceAssembler = persistentEntityResourceAssembler(null);
	}
//	
//	public static PersistentEntityResourceAssembler persistentEntityResourceAssembler(String projectionParameter) {
//		PersistentEntityProjector projector = new PersistentEntityProjector(projectionConfiguration, projectionFactory, projectionParameter, associationLinks.getMappings());
//		PersistentEntityResourceAssembler persistentEntityResourceAssembler =  new PersistentEntityResourceAssembler(persistentEntities, projector, associationLinks, selfLinkProvider);
//		return persistentEntityResourceAssembler;
//	}
//
//	public static PersistentEntityResourceAssembler persistentEntityResourceAssembler() {
//		return defaultPersistentEntityResourceAssembler;
//	}
//	
//	public static PluginRegistry<EntityLookup<?>, Class<?>> entityLookups(){
//		return PluginRegistry.of(config.getEntityLookups(repositories));
//	};
//	
//	public static DomainObjectReader domainObjectReader(){
//		return new DomainObjectReader(persistentEntities, associationLinks);
//	}

	
	public static ConversionService conversionService() {
		return conversionService;
	}
	
	public static PersistentEntities persistentEntities() {
		return persistentEntities;
	}
	
	public static ResourceMappings resourceMappings() {
		return resourceMappings;
	}

	public static EntityLinks entityLinks() {
		return entityLinks;
	}
	
	public static SelfLinkProvider selfLinkProvider() {
		return selfLinkProvider;
	}
	
	
	public static <T> URI entityToUri(T entity){
		Link link = selfLinkProvider.createSelfLinkFor(entity);
		URI uri = link.getTemplate().expand();
		return uri;
	}
	public static <T> T uriToEntity(URI uri){
		return uriToEntity(uri, true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T uriToEntity(URI uri, boolean useRepository){
		Class<T> targetType = (Class<T>) uriToEntityClass(uri);
		return useRepository ? conversionService.convert(uri, targetType) :  newInstance(targetType, uriToEntityId(uri));
	}
	

	
	
	
	
	

	
	
	private static Class<?> uriToEntityClass(URI uri){
		for (Class<?> domainType : repositories) {
			ResourceMetadata mapping = resourceMappings.getMetadataFor(domainType);
			if (mapping.isExported() && uri.toString().contains(mapping.getPath().toString())) {
				return mapping.getDomainType();
			}
		}
		throw new ResourceNotFoundException();
	}

	private static Object uriToEntityId(URI uri){
		String[] parts = uri.getPath().split("/");
		if (parts.length < 2) {
			throw new ResourceNotFoundException("Cannot resolve URI " + uri + ". Is it local or remote? Only local URIs are resolvable.");
		}
		return parts[parts.length - 1];
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T> T newInstance(Class<T> domainType, Object id) {
		
		Optional<PersistentEntity<?, ? extends PersistentProperty<?>>> optional = persistentEntities.getPersistentEntity(domainType);
		if (!optional.isPresent()) {
			throw new ResourceNotFoundException("No PersistentEntity information available for " + domainType);
		}

		try {
			PersistentEntity<?, ? extends PersistentProperty<?>> persistentEntity = optional.get();
			Object bean = persistentEntity.getPersistenceConstructor().getConstructor().newInstance();
			PersistentPropertyAccessor<?> accessor = persistentEntity.getPropertyAccessor(bean);
//			PersistentPropertyPathAccessor<?> accessor = persistentEntity.getPropertyPathAccessor(bean);//.getPropertyAccessor(bean);
			PersistentProperty<?> idProperty = persistentEntity.getIdProperty();
			
			Object idValue = id;
			if(! ClassUtils.isAssignableValue(idProperty.getType(), idValue)) {
				idValue = conversionService.convert(idValue, idProperty.getType());
			}
			accessor.setProperty(idProperty, idValue);
			return (T)bean;
		}catch(Exception e) {
			throw new ResourceNotFoundException("", e);
		}
	}
}





