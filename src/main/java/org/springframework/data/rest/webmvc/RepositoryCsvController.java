package org.springframework.data.rest.webmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.mapping.ResourceType;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectWriter;
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
			@RequestParam(name = "filename", required = false) String filename,
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
		return new CsvView(resourceInformation.getDomainType(), results, filename);
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
