package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.event.AnnotatedRepositoryReadEventHandlerInvoker;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.u2ware.data.jpa.repository.query.PartTreeSpecification;
import io.github.u2ware.data.rest.core.event.AfterReadEvent;
import io.github.u2ware.data.rest.core.event.BeforeReadEvent;


@RepositoryRestController
@Configuration
public class RepositoryAddtionalController extends AbstractRepositoryRestController implements ApplicationEventPublisherAware {

	@Bean 
	public AnnotatedRepositoryReadEventHandlerInvoker annotatedReadEventHandlerInvoker() {
		return new AnnotatedRepositoryReadEventHandlerInvoker();
	}
		
	
//	RepositoryEntityController RepositoryEntityController;
//	RepositorySearchController RepositorySearchController;
	
//	PersistentEntityResourceHandlerMethodArgumentResolver f;
//	RepositoryRestMvcConfiguration d;
//	RepositoryRestConfigurer ff;
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String SEARCH = "/search";
	private static final String BASE_MAPPING = "/{repository}";
	
	
	private final Repositories repositories;
	private final ResourceStatus resourceStatus;
	private ApplicationEventPublisher publisher;

	@Autowired
	public RepositoryAddtionalController(Repositories repositories, PagedResourcesAssembler<Object> pagedResourcesAssembler, HttpHeadersPreparer headersPreparer) {
		super(pagedResourcesAssembler);
		this.repositories = repositories;
		this.resourceStatus = ResourceStatus.of(headersPreparer);
	}


	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	
	@SuppressWarnings("unchecked")
	private <R> R getRepositoryFor(RootResourceInformation information) {
		return repositories.getRepositoryFor(information.getDomainType()).map(repository -> {
			if (repository == null) {
				throw new ResourceNotFoundException("repository is not Found: "+information);
			}
			return (R)repository;
		}).orElseThrow(() -> new ResourceNotFoundException("repository is not Found: "+information));
	}
	
	
	@RequestMapping(value = BASE_MAPPING+"/{id}", method = RequestMethod.POST)
	public ResponseEntity<EntityModel<?>> getItemResource(RootResourceInformation resourceInformation,
			@BackendId Serializable id, final PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {
		
		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.ITEM);

		return resourceInformation.getInvoker().invokeFindById(id).map(it -> {

			//....................
			publisher.publishEvent(new AfterReadEvent(it));
			//...................
			
			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return resourceStatus.getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING+SEARCH, method = RequestMethod.POST)
	public <T> CollectionModel<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestHeader(name = "partTree", required = false) String partTree,
			@RequestParam(name = "unpaged", required = false) boolean unpaged,
			PersistentEntityResource payload, 
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
	
		
		Iterable<?> results = executeQuery(resourceInformation, partTree, unpaged, pageable, sort, payload);

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(getDefaultSelfLink());

		
		return toCollectionModel(results, assembler, metadata.getDomainType(), baseLink);
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Iterable<?> executeQuery(final RootResourceInformation resourceInformation, final String partTree, final boolean unpaged, final DefaultedPageable pageable, final Sort sort, final PersistentEntityResource payload){
		
		Object content = payload.getContent();
		Repository repository = getRepositoryFor(resourceInformation);
		
		if(ClassUtils.isAssignableValue(QuerydslPredicateExecutor.class, repository)) {

			QuerydslPredicateExecutor executor = (QuerydslPredicateExecutor)repository;
						
			
			com.querydsl.core.BooleanBuilder predicate = new com.querydsl.core.BooleanBuilder();
			publisher.publishEvent(new BeforeReadEvent(content, predicate));

			if(unpaged) {
				return executor.findAll(predicate, sort) ;
			}else {
				return executor.findAll(predicate, pageable.getPageable()) ;
			}
			
			
		}else if(ClassUtils.isAssignableValue(JpaSpecificationExecutor.class, repository)) {
			
			
			JpaSpecificationExecutor executor = (JpaSpecificationExecutor)repository;
			
			Specification specification = null;
			if(StringUtils.hasText(partTree)) {
				specification = new PartTreeSpecification<>(partTree, content);
			}else {
				specification = new io.github.u2ware.data.jpa.repository.query.MutableSpecification<>();
				publisher.publishEvent(new BeforeReadEvent(content, specification));
			}
			
			if(unpaged) {
				return executor.findAll(specification, sort) ;
			}else {
				return executor.findAll(specification, pageable.getPageable()) ;
			}
			
			
		}else {
			throw new ResourceNotFoundException();
		}
		
	}	
}
