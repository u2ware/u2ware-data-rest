package io.github.u2ware.data.test.example00;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultHandler;

import io.github.u2ware.data.test.RestMockMvcDocs;

@Component
public class FooDocs extends RestMockMvcDocs {

	protected Log logger = LogFactory.getLog(getClass());

	@Override
	protected void random(Map<String,Object> entity) {
		entity.put("name", "hello");
		logger.info("random : "+entity);
	}

	@Override
	protected void randomFrom(Map<String,Object> entity) {
		entity.put("name", "hello-update");
		logger.info("randomFrom : "+entity);
	}
	
	//////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////
	public ResultHandler create() {
		return document("foos-create", descriptors->{
			
		});
	}

	public ResultHandler read() {
		return document("foos-read", descriptors->{
			descriptors.requestHeaders().headerWithName("query").description("query");
		});
	}

	public ResultHandler update() {
		return document("foos-update", descriptors->{
		});
	}

	public ResultHandler delete() {
		return document("foos-delete", descriptors->{
		});
	}

	public ResultHandler search() {
		return document("foos-search", descriptors->{
		});
	}
	
	

	public ResultHandler findById() {
		return document("foos-findById", builder->{
			
		});
	}

	public ResultHandler findByName() {
		return document("foos-findByName", builder->{
			
		});
	}
	
	public ResultHandler findAll() {
		return document("foos-findAll", builder->{
			
		});
	}

}
