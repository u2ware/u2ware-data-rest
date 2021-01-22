package io.github.u2ware.data.test.example08;

import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.util.StringUtils;

@SpringBootTest
public class MultiDataSourceConfigurationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Autowired ApplicationContext context;
	protected @Autowired FooRepository fooRepository;
	
	@Test
	public void contextLoads() throws Exception {
		
		logger.info("------------------------");
		logger.info(StringUtils.arrayToCommaDelimitedString(context.getBeanNamesForType(DataSource.class)));
		logger.info("------------------------");
		
		
		fooRepository.save(new Foo(UUID.randomUUID(), "qq", 1));
		
		try {
			Iterable<Foo> r = fooRepository.findByName("qq");
			Assertions.assertNotNull(r);
		}catch(Exception e) {
		}
		
		try {
			Iterable<Foo> r = fooRepository.findByAge(1);
			Assertions.assertNotNull(r);
		}catch(Exception e) {
			Assertions.assertEquals(InvalidDataAccessResourceUsageException.class, e.getClass());
		}
	}
}
