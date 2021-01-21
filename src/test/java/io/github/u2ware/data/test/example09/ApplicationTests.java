package io.github.u2ware.data.test.example09;

import java.net.URI;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.data.test.RestMockMvc;
import io.github.u2ware.data.test.example09.source1.Foo;
import io.github.u2ware.data.test.example09.source1.FooRepository;
import io.github.u2ware.data.test.example09.source2.Bar;
import io.github.u2ware.data.test.example09.source2.BarRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
//	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
//	@Before
//	public void before() throws Exception {
//		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
//		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
//	}
	
	protected @Autowired WebApplicationContext context;
	protected @Autowired FooRepository fooRepository;
	protected @Autowired BarRepository barRepository;
	protected @Autowired MockMvc mockMvc;

//	RepositoryRestMvcConfiguration v;
//	https://www.baeldung.com/spring-data-jpa-multiple-databases
	
	@Test
	public void contextLoads() throws Exception {
		RestMockMvc $ = new RestMockMvc(mockMvc, "");

		logger.info(fooRepository);
		logger.info(barRepository);
		
		UUID u1 = UUID.randomUUID();
		UUID u2 = UUID.randomUUID();
		
		Foo f1 = new Foo(u1, "aa", 1);
		Bar b1 = new Bar(u2, "aa", 1);
		
		fooRepository.save(f1);
		fooRepository.findAll().forEach(i->{
			logger.info(i);
		});

		
		barRepository.save(b1);
		barRepository.findAll().forEach(i->{
			logger.info(i);
		});

		String foo1 = $.GET("/foos/"+u1).is2xx().andReturn().link();
		String bar1 = $.GET("/bars/"+u2).is2xx().andReturn().link();
		logger.info(foo1);
		logger.info(bar1);
		
		
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		String[] aa = context.getBeanNamesForType(ConversionService.class);
		for(String a : aa) {
			logger.info(a);
			
			ConversionService c = context.getBean(a, ConversionService.class);
			
			logger.info(c.canConvert(URI.class, Bar.class));
			logger.info(c.canConvert(URI.class, Foo.class));
			
			if(c.canConvert(URI.class, Foo.class)) {
				Foo f = c.convert(new URI(foo1), Foo.class);
				logger.info(f);
			}
			if(c.canConvert(URI.class, Bar.class)) {
				Bar b = c.convert(new URI(foo1), Bar.class);
				logger.info(b);
			}
			
		}
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
	}
}
