package io.github.u2ware.test.example9;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.test.RestMockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	
	public final @Rule JUnitRestDocumentation restDocumentation  = new JUnitRestDocumentation();
//	public final String restUrisSchema = "http";
//	public final String restUrisHost = "www.u2ware.com";
//	public final Integer restUrisPort = 8080;	
	
	@Before
	public void before() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation
						.documentationConfiguration(restDocumentation)
						//.uris().withScheme(restUrisSchema).withHost(restUrisHost).withPort(restUrisPort)
				)
				.build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
		
	}
	
	
	private @Autowired FooDocs fooDocs;
	
	@Test
	public void contextLoads() throws Exception {
		
		$.POST("/foos").C(fooDocs.get()).is2xx("foo1").andDo(fooDocs.create());
		$.GET("{foo1}").is2xx().andDo(fooDocs.put("e1")).andDo(fooDocs.findById());
		$.PATCH("{foo1}").C(fooDocs.get("e1")).is2xx().andDo(fooDocs.update());
		$.PUT("{foo1}").C(fooDocs.get("e1")).is2xx().andDo(fooDocs.update());
		
		
		
		$.GET("/foos").is2xx().andDo(fooDocs.findAll());
		$.GET("/foos/search/findByName").P("name","hello").is2xx().andDo(fooDocs.findByName());
		
		
		$.GET("{foo1}").H("query", "true").is2xx().andDo(fooDocs.read());
		$.GET("/foos").H("query", "true").C("name","hello").is2xx().andDo(fooDocs.search());

	}
}
