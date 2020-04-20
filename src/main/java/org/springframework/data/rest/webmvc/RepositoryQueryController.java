package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.PartTreeSpecification;
import org.springframework.data.jpa.repository.query.SpecificationBuilder;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.rest.core.event.AfterReadEvent;
import org.springframework.data.rest.core.event.BeforeReadEvent;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.querydsl.core.BooleanBuilder;

@RepositoryRestController
public class RepositoryQueryController extends AbstractSpringDataRestController{ 
	
	private static final String BASE_MAPPING = "/{repository}";
	private static final String QUERY_HEADERS = "query=true";
	private static final String CSV_HEADERS = "csv=true";
	
	@RequestMapping(value = BASE_MAPPING+"/{id}", method = {RequestMethod.GET,RequestMethod.POST}, headers = QUERY_HEADERS)
	public ResponseEntity<Resource<?>> getItemResourceWithEvent(RootResourceInformation resourceInformation,
			@BackendId Serializable id, 
			final PersistentEntityResourceAssembler assembler, 
			@RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return getItemResource(resourceInformation, id).map(it -> {
			//....................
			publisher.publishEvent(new AfterReadEvent(it));
			//...................
			
			return toResponseEntity(it, assembler, headers, resourceInformation);

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	
	
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING + "/search/{search}", method = {RequestMethod.GET,RequestMethod.POST}, headers = QUERY_HEADERS)
	public ResponseEntity<?> executeSearch(RootResourceInformation resourceInformation,
			@RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search, DefaultedPageable pageable,
			Sort sort, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) {

		Method method = checkExecutability(resourceInformation, search);
		Optional<Object> result = executeQueryMethod(resourceInformation.getInvoker(), parameters, method, pageable, sort,
				assembler);

		return result.map(it -> {
			return toResponseEntity(it, assembler, headers, resourceInformation, search, Optional.empty());
			
		}).orElseThrow(() -> new ResourceNotFoundException());
	}	
	
	
	
	@ResponseBody
	@RequestMapping(value = BASE_MAPPING, method = {RequestMethod.GET,RequestMethod.POST}, headers = QUERY_HEADERS)
	public <T> Resources<?> executeQuery(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestHeader(name = "partTree", required = false) String partTree,
			@RequestHeader(name = "unpaged", required = false) boolean unpaged,
			PersistentEntityResource payload, 
			DefaultedPageable pageable, 
			Sort sort, 
			PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		
		
		logger.info("resourceInformation: "+resourceInformation);
		logger.info("partTree: "+partTree);
		logger.info("unpaged: "+unpaged);
		
		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		
		Optional<Object> repository = getRepositoryFor(resourceInformation.getDomainType());
		
		if(! repository.isPresent()) {
			throw new ResourceNotFoundException();
		}

		Iterable<?> results = getCollectionResource(repository.get(), partTree, unpaged, pageable, sort, payload.getContent());
		if (null == results) {
			throw new ResourceNotFoundException();
		}

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
				pageable.isDefault() ? null : pageable.getPageable()));
		
		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(resourceInformation, pageable));
		return result;
	}
	
	
	

	@RequestMapping(value = BASE_MAPPING, method = {RequestMethod.GET,RequestMethod.POST}, headers = {QUERY_HEADERS,CSV_HEADERS})
	public View executeQuery(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestHeader(name = "partTree", required = false) String partTree,
			@RequestHeader(name = "unpaged", required = false) boolean unpaged,
			@RequestHeader(name = "filename", required = false) String filename,
			PersistentEntityResource payload, 
			DefaultedPageable pageable, 
			Sort sort, 
			PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		
		Optional<Object> repository = getRepositoryFor(resourceInformation.getDomainType());
		
		if(! repository.isPresent()) {
			throw new ResourceNotFoundException();
		}

		Iterable<?> results = getCollectionResource(repository.get(), partTree, unpaged, pageable, sort, payload.getContent());
		if (null == results) {
			throw new ResourceNotFoundException();
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		return new CsvView(resourceInformation.getDomainType(), results, filename);
	}
	
	
	///////////////////////////////////////////////////////////////
	// Addon #3
	///////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Iterable<?> getCollectionResource(final Object repository, final String partTree, final boolean unpaged, final DefaultedPageable pageable, final Sort sort, final Object content){
		
		if(ClassUtils.isAssignableValue(QuerydslPredicateExecutor.class, repository)) {

			QuerydslPredicateExecutor executor = (QuerydslPredicateExecutor)repository;

			BooleanBuilder predicate = new BooleanBuilder();
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
				specification = new PartTreeSpecification(partTree, content);
			}else {
				specification = new SpecificationBuilder();
				publisher.publishEvent(new BeforeReadEvent(content, specification));
			}
			
			
			if(unpaged) {
				return executor.findAll(specification, sort) ;
			}else {
				return executor.findAll(specification, pageable.getPageable()) ;
			}
			
		}else {
			return null;
		}
		
	}
	

	
	public static class CsvView implements View{

		protected Log logger = LogFactory.getLog(getClass());

		private Class<?> type;
		private Iterable<?> results;
		private String filename;
		
		private CsvView(Class<?> type, Iterable<?> results, String filename) {
			this.type = type;
			this.results = results;
			this.filename = StringUtils.hasText(filename) ? filename : System.currentTimeMillis()+".csv";
		}

		@Override
		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			try {
				response.setContentType("application/octet-stream;charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				response.setHeader("Content-Transfer-Encoding", "binary");

				
				CsvMapper csvMapper = new CsvMapper();
				CsvSchema csvSchema = csvMapper.schemaFor(type).withHeader();
				ObjectWriter objectWriter = csvMapper.writerFor(type).with(csvSchema);
				
				objectWriter.writeValues(response.getOutputStream()).writeAll(results);
				
			}catch(Exception e) {
				logger.info("", e);
				response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
			}
		}

	}
	
}
