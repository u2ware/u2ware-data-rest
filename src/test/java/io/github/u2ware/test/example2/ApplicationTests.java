package io.github.u2ware.test.example2;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
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
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}

	protected @Autowired FooRepository fooRepository;
	protected @Autowired BarRepository barRepository;
	
	@Test
	public void contextLoads() throws Exception {

		fooRepository.save(new Foo(UUID.randomUUID(), "a", 1));		
		fooRepository.save(new Foo(UUID.randomUUID(), "b", 2));		
		fooRepository.save(new Foo(UUID.randomUUID(), "c", 3));		

		barRepository.save(new Bar(UUID.randomUUID(), "a", 4));		
		barRepository.save(new Bar(UUID.randomUUID(), "b", 5));		
		

		fooRepository.findAll((root, query, builder) -> {
			return PredicateBuilder.of(root, query, builder).where().and().eq("name", "a").build();
		}).forEach(foo->{
			logger.info(foo.getCount1());
			logger.info(foo.getCount2());
		});;
	}
	
	
}
