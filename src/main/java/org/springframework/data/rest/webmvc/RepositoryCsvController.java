package org.springframework.data.rest.webmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@BasePathAwareController
public class RepositoryCsvController extends AbstractRepositoryController {

	private static final String BASE_MAPPING = "/{repository}";
	private static final String QUERY_HEADERS = "query=true";
	private static final String CSV_HEADERS = "csv=true";
	
	@RequestMapping(value = BASE_MAPPING, method = {RequestMethod.GET,RequestMethod.POST}, headers = {QUERY_HEADERS,CSV_HEADERS})
	public View executeQuery(@QuerydslPredicate RootResourceInformation resourceInformation,
			@RequestHeader(name = "partTree", required = false) String partTree,
			@RequestParam(name = "unpaged", required = false) boolean unpaged,
			DefaultedPageable pageable, Sort sort, 
			PersistentEntityResource payload, 
			PersistentEntityResourceAssembler assembler)throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		
		resourceInformation.verifySupportedMethod(HttpMethod.GET, ResourceType.COLLECTION);

		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		RepositoryInvoker invoker = resourceInformation.getInvoker();
		if (null == invoker) {
			throw new ResourceNotFoundException();
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		Iterable<?> results = super.getCollectionResourceWithEvent(resourceInformation, partTree, true, pageable, sort, payload.getContent());
		if (null == results) {
			throw new ResourceNotFoundException();
		}
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//////////////////////////////////////////////////////////////////////////////////////////////
		return new CsvView(resourceInformation.getDomainType(), results);
	}
	
	
	
	
	public static class CsvView extends AbstractView{

		private CsvMapper csvMapper;
		
		private CsvView(Class<?> type, Iterable<?> results) {
			csvMapper = new CsvMapper();
			CsvSchema csvSchema = csvMapper.schemaFor(type);
			csvMapper.writerFor(Iterable.class).with(csvSchema);
		}

		@Override
		public String getContentType() {
			return "text/csv";
		}
		
		@Override
		protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			csvMapper.writeValue(response.getOutputStream(), model);
		}

	}
}
