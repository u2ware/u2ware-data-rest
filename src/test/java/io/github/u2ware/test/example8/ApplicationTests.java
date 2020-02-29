package io.github.u2ware.test.example8;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.u2ware.test.RestMockMvc;
import io.github.u2ware.test.example8.source1.Foo;
import io.github.u2ware.test.example8.source1.FooRepository;
import io.github.u2ware.test.example8.source2.Bar;
import io.github.u2ware.test.example8.source2.BarRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	protected @Autowired FooRepository fooRepository;
	protected @Autowired BarRepository barRepository;

//	RepositoryRestMvcConfiguration v;
//	https://www.baeldung.com/spring-data-jpa-multiple-databases
	
	@Test
	public void contextLoads() throws Exception {
		
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
		

		$.POST("/foos").C("id", UUID.randomUUID()).C("bar", b1).C("bars", Arrays.asList(b1)).is2xx();
		$.POST("/foos").C("id", UUID.randomUUID()).C("bar", bar1).C("bars", Arrays.asList(bar1)).is2xx();
		
		
	}
}
