package io.github.u2ware.data.test.example03;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.github.u2ware.data.test.RestMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationRepositoryHandlerTests {

	
	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired FooQuerydslPredicateExecutor fooRepository;
	private @Autowired BarJpaSpecificationExecutor barRepository;
	
	private @Autowired MockMvc mockMvc;

	
	@Test
	public void test1() throws Exception{
		
		fooRepository.save(Foo.builder().name("a").age(1).build());
		fooRepository.save(Foo.builder().name("b").age(1).build());
		fooRepository.save(Foo.builder().name("a").age(2).build());
		fooRepository.save(Foo.builder().name("b").age(2).build());

		barRepository.save(Bar.builder().name("a").age(1).build());
		barRepository.save(Bar.builder().name("b").age(1).build());
		barRepository.save(Bar.builder().name("a").age(2).build());
		barRepository.save(Bar.builder().name("b").age(2).build());
		
		RestMockMvc $ = new RestMockMvc(mockMvc, "");
		$.GET("/profile").is2xx();
		
		//#
		$.POST("/foos/search").C().is2xx().andExpect("page.totalElements", 4);
		$.POST("/foos/search").C("name", "a").is2xx().andExpect("page.totalElements", 4);
		$.POST("/foos/search").C("age", 1).is2xx().andExpect("page.totalElements", 2).andReturn("x1");
		
		//#
		$.POST("{x1._embedded.foos[0]._links.self.href}").is2xx().andExpect("name", FooRepositoryHandler.NAME);
		$.GET("{x1._embedded.foos[0]._links.self.href}").is2xx().andExpect("name", "a");

		
		//#
		$.POST("/bars/search").C().is2xx().andExpect("page.totalElements", 4);
		$.POST("/bars/search").C("name", "a").is2xx().andExpect("page.totalElements", 2);
		$.POST("/bars/search").C("age", 1).is2xx().andExpect("page.totalElements", 4).andReturn("x2");
		
		//#
		$.POST("{x2._embedded.bars[0]._links.self.href}").is2xx().andExpect("name", BarRepositoryHandler.NAME);
		$.GET("{x2._embedded.bars[0]._links.self.href}").is2xx().andExpect("name", "a");
		
		
		//# Pagenation
		$.POST("/foos/search").C().P("size", "1").is2xx().andExpect("_embedded.foos.length()", 1).andExpect("page.totalElements", 4);
		$.POST("/foos/search").C().P("size", "1").P("unpaged", "true").is2xx().andExpect("_embedded.foos.length()", 4);
		
		
		//# PartTree
		$.POST("/bars/search").H("partTree", "findByName").C("name", "b").is2xx().andExpect("page.totalElements", 2);
		 
	}
}