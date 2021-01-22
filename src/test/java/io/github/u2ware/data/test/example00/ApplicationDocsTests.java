package io.github.u2ware.data.test.example00;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.data.test.RestMockMvc;
import io.github.u2ware.data.test.example00.ApplicationDocsTests.ApplicationTestsRepositoryRestConfigurer;



@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@Import(ApplicationTestsRepositoryRestConfigurer.class)
@AutoConfigureMockMvc
public class ApplicationDocsTests {

	protected Log logger = LogFactory.getLog(getClass());

	@TestConfiguration
	public static class ApplicationTestsRepositoryRestConfigurer implements RepositoryRestConfigurer{
		@Override
		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
			config.setBasePath("/welcome");
//			config.exposeIdsFor(ManyToOneEntity.class);
//			config.setReturnBodyOnCreate(true);
//			config.setReturnBodyOnUpdate(true);
			
//			 RestDocumentation f;
		}
	}
	
	protected @Autowired WebApplicationContext context;
	
	protected @Autowired FooDocs fooDocs;
	protected RestMockMvc $;

	@BeforeEach
    public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
		this.$ = new RestMockMvc(context, restDocumentation);
    }	
	
	@Test
	public void contextLoads() throws Exception {
		
		$.POST("/foos").C(fooDocs.random()).is2xx().andDo(fooDocs.create()).andReturn("foo1");
		$.GET("{foo1}").is2xx().andDo(fooDocs.findById()).andDo(fooDocs.randomTo());
		$.PATCH("{foo1}").C(fooDocs.random()).is2xx().andDo(fooDocs.update());
		$.PUT("{foo1}").C(fooDocs.random()).is2xx().andDo(fooDocs.update());
		
		$.GET("/foos").is2xx().andDo(fooDocs.findAll());
		$.GET("/foos/search/findByName").P("name","hello").is2xx().andDo(fooDocs.findByName());
		
		
		$.GET("{foo1}").H("query", "true").is2xx().andDo(fooDocs.read());
		$.GET("/foos").H("query", "true").C("name","hello").is2xx().andDo(fooDocs.search());
	}
	
	
	
}
