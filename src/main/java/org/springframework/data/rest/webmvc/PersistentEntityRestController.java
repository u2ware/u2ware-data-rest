package org.springframework.data.rest.webmvc;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.mapping.MethodResourceMapping;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.core.mapping.SupportedHttpMethods;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.ETag;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public class PersistentEntityRestController extends AbstractRepositoryEntityAndSearchController {

	
	////////////////////////////////////////////////
	// RepositoryEntityController
	///////////////////////////////////////////////
	private static final List<String> ACCEPT_PATCH_HEADERS = Arrays.asList(//
			RestMediaTypes.MERGE_PATCH_JSON.toString(), //
			RestMediaTypes.JSON_PATCH_JSON.toString(), //
			MediaType.APPLICATION_JSON_VALUE);

	private static final String ACCEPT_HEADER = "Accept";
	private static final String LINK_HEADER = "Link";


//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForCollectionResource(RootResourceInformation information) {

		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = information.getSupportedMethods();

		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.COLLECTION).toSet());

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}

//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public ResponseEntity<?> headCollectionResource(RootResourceInformation resourceInformation, DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.HEAD, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		List<Link> links = getCollectionResourceLinks(resourceInformation, pageable);
		links.add(0, getDefaultSelfLink());

		HttpHeaders headers = new HttpHeaders();
		headers.add(LINK_HEADER, new Links(links).toString());

		return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);
	}
	
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public Resources<?> getCollectionResource(@QuerydslPredicate RootResourceInformation resourceInformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		RepositoryInvoker invoker = resourceInformation.getInvoker();

		if (null == invoker) {
			throw new ResourceNotFoundException();
		}

		Iterable<?> results = pageable.getPageable() != null ? invoker.invokeFindAll(pageable.getPageable())
				: invoker.invokeFindAll(sort);

		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		Optional<Link> baseLink = Optional.of(entityLinks.linkToPagedResource(resourceInformation.getDomainType(),
				pageable.isDefault() ? null : pageable.getPageable()));

		Resources<?> result = toResources(results, assembler, metadata.getDomainType(), baseLink);
		result.add(getCollectionResourceLinks(resourceInformation, pageable));
		return result;
	}
	
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET,
//			produces = { "application/x-spring-data-compact+json", "text/uri-list" })
	@SuppressWarnings({ "unchecked" })
	public Resources<?> getCollectionResourceCompact(@QuerydslPredicate RootResourceInformation resourceinformation,
			DefaultedPageable pageable, Sort sort, PersistentEntityResourceAssembler assembler)
			throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		Resources<?> resources = getCollectionResource(resourceinformation, pageable, sort, assembler);
		List<Link> links = new ArrayList<Link>(resources.getLinks());

		for (Resource<?> resource : ((Resources<Resource<?>>) resources).getContent()) {
			PersistentEntityResource persistentEntityResource = (PersistentEntityResource) resource;
			links.add(resourceLink(resourceinformation, persistentEntityResource));
		}
		if (resources instanceof PagedResources) {
			return new PagedResources<Object>(Collections.emptyList(), ((PagedResources<?>) resources).getMetadata(), links);
		} else {
			return new Resources<Object>(Collections.emptyList(), links);
		}
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST)
	public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, PersistentEntityResourceAssembler assembler,
			@RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.POST, ResourceType.COLLECTION);

		return createAndReturn(payload.getContent(), resourceInformation.getInvoker(), assembler,
				config.returnBodyOnCreate(acceptHeader));
	}
	
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> optionsForItemResource(RootResourceInformation information) {

		HttpHeaders headers = new HttpHeaders();
		SupportedHttpMethods supportedMethods = information.getSupportedMethods();

		headers.setAllow(supportedMethods.getMethodsFor(ResourceType.ITEM).toSet());
		headers.put("Accept-Patch", ACCEPT_PATCH_HEADERS);

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<?> headForItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
			PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {

		return getItemResource(resourceInformation, id).map(it -> {

			Links links = new Links(assembler.toResource(it).getLinks());

			HttpHeaders headers = headersPreparer.prepareHeaders(resourceInformation.getPersistentEntity(), it);
			headers.add(LINK_HEADER, links.toString());

			return new ResponseEntity<Object>(headers, HttpStatus.NO_CONTENT);

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource<?>> getItemResource(RootResourceInformation resourceInformation,
			@BackendId Serializable id, final PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers)
			throws HttpRequestMethodNotSupportedException {

		return getItemResource(resourceInformation, id).map(it -> {

			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			return resourceStatus().getStatusAndHeaders(headers, it, entity).toResponseEntity(//
					() -> assembler.toFullResource(it));

		}).orElseGet(() -> new ResponseEntity<Resource<?>>(HttpStatus.NOT_FOUND));
	}
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<? extends ResourceSupport> putItemResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.PUT, ResourceType.ITEM);

		if (payload.isNew()) {
			resourceInformation.verifyPutForCreation();
		}

		RepositoryInvoker invoker = resourceInformation.getInvoker();
		Object objectToSave = payload.getContent();
		eTag.verify(resourceInformation.getPersistentEntity(), objectToSave);

		return payload.isNew() ? createAndReturn(objectToSave, invoker, assembler, config.returnBodyOnCreate(acceptHeader))
				: saveAndReturn(objectToSave, invoker, PUT, assembler, config.returnBodyOnUpdate(acceptHeader));
	}
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<ResourceSupport> patchItemResource(RootResourceInformation resourceInformation,
			PersistentEntityResource payload, @BackendId Serializable id, PersistentEntityResourceAssembler assembler,
			ETag eTag, @RequestHeader(value = ACCEPT_HEADER, required = false) String acceptHeader)
			throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {

		resourceInformation.verifySupportedMethod(HttpMethod.PATCH, ResourceType.ITEM);

		Object domainObject = payload.getContent();

		eTag.verify(resourceInformation.getPersistentEntity(), domainObject);

		return saveAndReturn(domainObject, resourceInformation.getInvoker(), PATCH, assembler,
				config.returnBodyOnUpdate(acceptHeader));
	}
	
	
//	@RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteItemResource(RootResourceInformation resourceInformation, @BackendId Serializable id,
			ETag eTag) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

		resourceInformation.verifySupportedMethod(HttpMethod.DELETE, ResourceType.ITEM);

		RepositoryInvoker invoker = resourceInformation.getInvoker();
		Optional<Object> domainObj = invoker.invokeFindById(id);

		return domainObj.map(it -> {

			PersistentEntity<?, ?> entity = resourceInformation.getPersistentEntity();

			eTag.verify(entity, it);

			publisher.publishEvent(new BeforeDeleteEvent(it));
			invoker.invokeDeleteById(entity.getIdentifierAccessor(it).getIdentifier());
			publisher.publishEvent(new AfterDeleteEvent(it));

			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);

		}).orElseThrow(() -> new ResourceNotFoundException());
	}
	
	////////////////////////////////////////////////
	// RepositoryEntityController
	///////////////////////////////////////////////
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.OPTIONS)
	public HttpEntity<?> optionsForSearches(RootResourceInformation resourceInformation) {

		verifySearchesExposed(resourceInformation);

		HttpHeaders headers = new HttpHeaders();
		headers.setAllow(Collections.singleton(HttpMethod.GET));

		return ResponseEntity.ok().headers(headers).build();
	}
	
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.HEAD)
	public HttpEntity<?> headForSearches(RootResourceInformation resourceInformation) {

		verifySearchesExposed(resourceInformation);

		return ResponseEntity.noContent().build();
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public RepositorySearchesResource listSearches(RootResourceInformation resourceInformation) {

		verifySearchesExposed(resourceInformation);

		Links queryMethodLinks = entityLinks.linksToSearchResources(resourceInformation.getDomainType());

		if (queryMethodLinks.isEmpty()) {
			throw new ResourceNotFoundException();
		}

		RepositorySearchesResource result = new RepositorySearchesResource(resourceInformation.getDomainType());
		result.add(queryMethodLinks);
		result.add(getDefaultSelfLink());

		return result;
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET)
	public ResponseEntity<?> executeSearch(RootResourceInformation resourceInformation,
			@RequestParam MultiValueMap<String, Object> parameters, @PathVariable String search, DefaultedPageable pageable,
			Sort sort, PersistentEntityResourceAssembler assembler, @RequestHeader HttpHeaders headers) {

		Method method = checkExecutability(resourceInformation, search);
		Optional<Object> result = executeQueryMethod(resourceInformation.getInvoker(), parameters, method, pageable, sort,
				assembler);

		SearchResourceMappings searchMappings = resourceInformation.getSearchMappings();
		MethodResourceMapping methodMapping = searchMappings.getExportedMethodMappingForPath(search);
		Class<?> domainType = methodMapping.getReturnedDomainType();

		return toResource(result, assembler, domainType, Optional.empty(), headers, resourceInformation);
	}
	
//	@ResponseBody
//	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.GET, //
//			produces = { "application/x-spring-data-compact+json" })
	public ResourceSupport executeSearchCompact(RootResourceInformation resourceInformation,
			@RequestHeader HttpHeaders headers, @RequestParam MultiValueMap<String, Object> parameters,
			@PathVariable String repository, @PathVariable String search, DefaultedPageable pageable, Sort sort,
			PersistentEntityResourceAssembler assembler) {

		Method method = checkExecutability(resourceInformation, search);
		Optional<Object> result = executeQueryMethod(resourceInformation.getInvoker(), parameters, method, pageable, sort,
				assembler);
		ResourceMetadata metadata = resourceInformation.getResourceMetadata();
		ResponseEntity<?> entity = toResource(result, assembler, metadata.getDomainType(), Optional.empty(), headers,
				resourceInformation);
		Object resource = entity.getBody();

		List<Link> links = new ArrayList<Link>();

		if (resource instanceof Resources && ((Resources<?>) resource).getContent() != null) {

			for (Object obj : ((Resources<?>) resource).getContent()) {
				if (null != obj && obj instanceof Resource) {
					Resource<?> res = (Resource<?>) obj;
					links.add(resourceLink(resourceInformation, res));
				}
			}

		} else if (resource instanceof Resource) {

			Resource<?> res = (Resource<?>) resource;
			links.add(resourceLink(resourceInformation, res));
		}

		return new Resources<Resource<?>>(EMPTY_RESOURCE_LIST, links);
	}
	
//	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.OPTIONS)
	public ResponseEntity<Object> optionsForSearch(RootResourceInformation information, @PathVariable String search) {

		checkExecutability(information, search);

		HttpHeaders headers = new HttpHeaders();
		headers.setAllow(Collections.singleton(HttpMethod.GET));

		return new ResponseEntity<Object>(headers, HttpStatus.OK);
	}

//	@RequestMapping(value = BASE_MAPPING + "/{search}", method = RequestMethod.HEAD)
	public ResponseEntity<Object> headForSearch(RootResourceInformation information, @PathVariable String search) {

		checkExecutability(information, search);
		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
	}
	
}
