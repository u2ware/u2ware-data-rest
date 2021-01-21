package io.github.u2ware.data.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public abstract class AbstractApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	@BeforeEach
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
//		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
//				.apply(MockMvcRestDocumentation
//						.documentationConfiguration(restDocumentation)
//						//.uris().withScheme(restUrisSchema).withHost(restUrisHost).withPort(restUrisPort)
//				)
//				.build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}	
	
	
}
