package io.github.u2ware.test.example9;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultHandler;

import io.github.u2ware.test.RestMockMvc.Docs;
import io.github.u2ware.test.RestMockMvc.DocsBuilder;

@Component
public class FooDocs extends Docs<Foo>{

	
	@Override
	protected void random(Foo entity) {
		entity.setName("hello");
	}

	@Override
	protected void random(Map<String, Object> entity) {
		// TODO Auto-generated method stub
	}

	
	//////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////
	public ResultHandler create() {
		return DocsBuilder.document("foos-create", builder->{
			
		});
	}

	public ResultHandler read() {
		return DocsBuilder.document("foos-read", builder->{
			builder.requestHeaders().headerWithName("query").description("query");
		});
	}

	public ResultHandler update() {
		return DocsBuilder.document("foos-update", builder->{
		});
	}

	public ResultHandler delete() {
		return DocsBuilder.document("foos-delete", builder->{
		});
	}

	public ResultHandler search() {
		return DocsBuilder.document("foos-search", builder->{
		});
	}
	
	

	public ResultHandler findById() {
		return DocsBuilder.document("foos-findById", builder->{
			
		});
	}

	public ResultHandler findByName() {
		return DocsBuilder.document("foos-findByName", builder->{
			
		});
	}
	
	public ResultHandler findAll() {
		return DocsBuilder.document("foos-findAll", builder->{
			
		});
	}
}
