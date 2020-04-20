package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;
import static org.springframework.http.HttpMethod.PUT;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.core.projection.ProjectionDefinitions;
import org.springframework.data.rest.core.support.SelfLinkProvider;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.PersistentEntityProjector;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.AnnotationAttribute;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.core.MethodParameters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class AbstractSpringDataRestController implements ApplicationContextAware, BeanClassLoaderAware, ApplicationEventPublisherAware, InitializingBean {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected ClassLoader beanClassLoader; 
	protected ApplicationContext applicationContext;
	protected ApplicationEventPublisher publisher;
	
	protected @Autowired PagedResourcesAssembler<Object> pagedResourcesAssembler;
	protected @Autowired RepositoryRestConfiguration config;
	protected @Autowired RepositoryEntityLinks entityLinks;
	protected @Autowired HttpHeadersPreparer headersPreparer;
	protected @Autowired Repositories repositories;
	protected @Autowired ResourceMappings mappings;
	protected @Autowired PersistentEntities entities;
	protected @Autowired RepositoryInvokerFactory invokerFactory;
	protected @Autowired SelfLinkProvider linkProvider;
	protected @Autowired Associations links;
	
	protected ResourceStatus resourceStatus;
	protected ProjectionFactory projectionFactory;
	protected ProjectionDefinitions projectionDefinitions;

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.resourceStatus = ResourceStatus.of(headersPreparer);
		
		SpelAwareProxyProjectionFactory f = new SpelAwareProxyProjectionFactory();
		f.setBeanFactory(applicationContext);
		f.setBeanClassLoader(beanClassLoader);
		this.projectionFactory = f;
		
		this.projectionDefinitions = config.getProjectionConfiguration();
	}
	
	//////////////////////////////////////////////////////////////////
	// AbstractRepositoryRestController 
	//////////////////////////////////////////////////////////////////
	private static final EmbeddedWrappers WRAPPERS = new EmbeddedWrappers(false);

	
	@SuppressWarnings("rawtypes")
	protected Link resourceLink(RootResourceInformation resourceLink, Resource resource) {

		ResourceMetadata repoMapping = resourceLink.getResourceMetadata();

		Link selfLink = resource.getLink("self");
		String rel = repoMapping.getItemResourceRel();

		return new Link(selfLink.getHref(), rel);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Resources<?> toResources(Iterable<?> source, PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink) {

		if (source instanceof Page) {
			Page<Object> page = (Page<Object>) source;
			return entitiesToResources(page, assembler, domainType, baseLink);
		} else if (source instanceof Iterable) {
			return entitiesToResources((Iterable<Object>) source, assembler, domainType);
		} else {
			return new Resources(EMPTY_RESOURCE_LIST);
		}
	}

	protected Resources<?> entitiesToResources(Page<Object> page, PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink) {

		if (page.getContent().isEmpty()) {
			return baseLink.<PagedResources<?>> map(it -> pagedResourcesAssembler.toEmptyResource(page, domainType, it))//
					.orElseGet(() -> pagedResourcesAssembler.toEmptyResource(page, domainType));
		}

		return baseLink.map(it -> pagedResourcesAssembler.toResource(page, assembler, it))//
				.orElseGet(() -> pagedResourcesAssembler.toResource(page, assembler));
	}

	protected Resources<?> entitiesToResources(Iterable<Object> entities, PersistentEntityResourceAssembler assembler,
			Class<?> domainType) {

		if (!entities.iterator().hasNext()) {

			List<Object> content = Arrays.<Object> asList(WRAPPERS.emptyCollectionOf(domainType));
			return new Resources<Object>(content, getDefaultSelfLink());
		}

		List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

		for (Object obj : entities) {
			resources.add(obj == null ? null : assembler.toResource(obj));
		}

		return new Resources<Resource<Object>>(resources, getDefaultSelfLink());
	}

	protected Link getDefaultSelfLink() {
		return new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
	}

	
	//////////////////////////////////////////////////////////////////
	// RepositoryEntityController
	//////////////////////////////////////////////////////////////////

	protected List<Link> getCollectionResourceLinks(RootResourceInformation resourceInformation,
			DefaultedPageable pageable) {

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();

		List<Link> links = new ArrayList<Link>();
		links.add(new Link(ProfileController.getPath(this.config, metadata), ProfileResourceProcessor.PROFILE_REL));

		if (searchMappings.isExported()) {
			links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath())
					.withRel(searchMappings.getRel()));
		}

		return links;
	}
	
	protected ResponseEntity<ResourceSupport> saveAndReturn(Object domainObject, RepositoryInvoker invoker,
			HttpMethod httpMethod, PersistentEntityResourceAssembler assembler, boolean returnBody) {

		publisher.publishEvent(new BeforeSaveEvent(domainObject));
		Object obj = invoker.invokeSave(domainObject);
		publisher.publishEvent(new AfterSaveEvent(obj));

		PersistentEntityResource resource = assembler.toFullResource(obj);
		HttpHeaders headers = headersPreparer.prepareHeaders(Optional.of(resource));

		if (PUT.equals(httpMethod)) {
			addLocationHeader(headers, assembler, obj);
		}

		if (returnBody) {
			return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, resource);
		} else {
			return ControllerUtils.toEmptyResponse(HttpStatus.NO_CONTENT, headers);
		}
	}


	protected ResponseEntity<ResourceSupport> createAndReturn(Object domainObject, RepositoryInvoker invoker,
			PersistentEntityResourceAssembler assembler, boolean returnBody) {

		publisher.publishEvent(new BeforeCreateEvent(domainObject));
		Object savedObject = invoker.invokeSave(domainObject);
		publisher.publishEvent(new AfterCreateEvent(savedObject));

		Optional<PersistentEntityResource> resource = Optional
				.ofNullable(returnBody ? assembler.toFullResource(savedObject) : null);

		HttpHeaders headers = headersPreparer.prepareHeaders(resource);
		addLocationHeader(headers, assembler, savedObject);

		return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, resource);
	}


	protected void addLocationHeader(HttpHeaders headers, PersistentEntityResourceAssembler assembler, Object source) {

		String selfLink = assembler.getSelfLinkFor(source).getHref();
		headers.setLocation(new UriTemplate(selfLink).expand());
	}

	protected Optional<Object> getItemResource(RootResourceInformation resourceInformation, Serializable id)
			throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {

		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.ITEM);

		return resourceInformation.getInvoker().invokeFindById(id);
	}
	
	//////////////////////////////////////////////////////////////////
	// RepositorySearchController
	//////////////////////////////////////////////////////////////////
	protected ResponseEntity<?> toResource(Optional<Object> source, final PersistentEntityResourceAssembler assembler,
			Class<?> domainType, Optional<Link> baseLink, HttpHeaders headers, RootResourceInformation information) {

		return source.map(it -> {

			if (it instanceof Iterable) {
				return ResponseEntity.ok(toResources((Iterable<?>) it, assembler, domainType, baseLink));
			} else if (ClassUtils.isPrimitiveOrWrapper(it.getClass())) {
				return ResponseEntity.ok(it);
			}

			PersistentEntity<?, ?> entity = information.getPersistentEntity();

			// Returned value is not of the aggregates type - probably some projection
			if (!entity.getType().isInstance(it)) {
				return ResponseEntity.ok(it);
			}

			return resourceStatus.getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
	
	protected Method checkExecutability(RootResourceInformation resourceInformation, String searchName) {

		SearchResourceMappings searchMapping = verifySearchesExposed(resourceInformation);

		Method method = searchMapping.getMappedMethod(searchName);

		if (method == null) {
			throw new ResourceNotFoundException();
		}

		return method;
	}


	protected Optional<Object> executeQueryMethod(final RepositoryInvoker invoker,
			@RequestParam MultiValueMap<String, Object> parameters, Method method, DefaultedPageable pageable, Sort sort,
			PersistentEntityResourceAssembler assembler) {

		MultiValueMap<String, Object> result = new LinkedMultiValueMap<String, Object>(parameters);
		MethodParameters methodParameters = new MethodParameters(method, new AnnotationAttribute(Param.class));
		List<MethodParameter> parameterList = methodParameters.getParameters();
		List<TypeInformation<?>> parameterTypeInformations = ClassTypeInformation.from(method.getDeclaringClass())
				.getParameterTypes(method);

		for (Entry<String, List<Object>> entry : parameters.entrySet()) {

			MethodParameter parameter = methodParameters.getParameter(entry.getKey());

			if (parameter == null) {
				continue;
			}

			int parameterIndex = parameterList.indexOf(parameter);
			TypeInformation<?> domainType = parameterTypeInformations.get(parameterIndex).getActualType();

			ResourceMetadata metadata = mappings.getMetadataFor(domainType.getType());

			if (metadata != null && metadata.isExported()) {
				result.put(parameter.getParameterName(), prepareUris(entry.getValue()));
			}
		}

		return invoker.invokeQueryMethod(method, result, pageable.getPageable(), sort);
	}

	protected static SearchResourceMappings verifySearchesExposed(RootResourceInformation resourceInformation) {

		SearchResourceMappings resourceMappings = resourceInformation.getSearchMappings();

		if (!resourceMappings.isExported()) {
			throw new ResourceNotFoundException();
		}

		return resourceMappings;
	}


	protected static List<Object> prepareUris(List<Object> source) {

		if (source == null || source.isEmpty()) {
			return Collections.emptyList();
		}

		List<Object> result = new ArrayList<Object>(source.size());

		for (Object element : source) {

			try {
				result.add(new URI(element.toString()));
			} catch (URISyntaxException o_O) {
				result.add(element);
			}
		}

		return result;
	}
	
	
	//////////////////////////////////////////////////////////////////
	// Addon
	//////////////////////////////////////////////////////////////////
	protected ResponseEntity<Resource<?>> toResponseEntity(Object source, PersistentEntityResourceAssembler assembler, HttpHeaders headers, RootResourceInformation information){
		PersistentEntity<?, ?> entity = information.getPersistentEntity();
		return resourceStatus.getStatusAndHeaders(headers, source, entity).toResponseEntity(() -> assembler.toFullResource(source));
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ResponseEntity<?> toResponseEntity(Object source, PersistentEntityResourceAssembler assembler, HttpHeaders headers, RootResourceInformation information, String search, Optional<Link> baseLink){
		if (source instanceof Iterable) {
			SearchResourceMappings searchMappings = information.getSearchMappings();
			MethodResourceMapping methodMapping = searchMappings.getExportedMethodMappingForPath(search);
			Class<?> returnType = methodMapping.getReturnedDomainType();
			
			//modified...
			if(ClassUtils.isAssignable(returnType, information.getDomainType())) {
				return ResponseEntity.ok(toResources((Iterable<?>) source, assembler, information.getDomainType(), baseLink));
			}else {
				return ResponseEntity.ok(new Resources((Iterable<?>) source, getDefaultSelfLink()));
			}
			//modified...
		} else if (ClassUtils.isPrimitiveOrWrapper(source.getClass())) {
			return ResponseEntity.ok(source);
		}
		
		PersistentEntity<?, ?> entity = information.getPersistentEntity();
		// Returned value is not of the aggregates type - probably some projection
		if (! entity.getType().isInstance(source)) {
			return ResponseEntity.ok(source);
		}
		return toResponseEntity(source, assembler, headers, information);
	}
	
	//////////////////////////////////////////////////////////////////
	// Addon
	//////////////////////////////////////////////////////////////////
	protected Optional<Object> getRepositoryFor(Class<?> domainType){
		return repositories.getRepositoryFor(domainType);
	}
	protected ResourceMetadata getMetadataFor(Class<?> domainType) {
		return mappings.getMetadataFor(domainType);
	}
	protected PersistentEntity<?,?> getPersistentEntity(Class<?> domainType) {
		return entities.getPersistentEntity(domainType).get();
	}
	protected RepositoryInvoker getInvokerFor(Class<?> domainType) {
		return invokerFactory.getInvokerFor(domainType);
	}
	
	protected RootResourceInformation information(Class<?> domainType) {
		return new RootResourceInformation(getMetadataFor(domainType), getPersistentEntity(domainType), getInvokerFor(domainType));
	}
	
	protected PersistentEntityResourceAssembler assembler() {
		return assembler(null);
	}
	//PersistentEntityResourceAssemblerArgumentResolver
	protected PersistentEntityResourceAssembler assembler(String projection) {
		PersistentEntityProjector projector = new PersistentEntityProjector(projectionDefinitions, projectionFactory, projection, links.getMappings());
		return new PersistentEntityResourceAssembler(entities, projector, links, linkProvider);
	}
	
	
}