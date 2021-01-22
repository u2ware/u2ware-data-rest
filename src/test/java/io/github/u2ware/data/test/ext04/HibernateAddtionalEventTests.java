package io.github.u2ware.data.test.ext04;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import io.github.u2ware.data.jpa.support.HibernateAddtionalConfiguration;
import io.github.u2ware.data.jpa.support.HibernateAddtionalEvent.HibernatePreInsertEvent;
import io.github.u2ware.data.test.ext04.HibernateAddtionalEventTests.HibernateAddtionalEventTestsConfig;


@SpringBootTest
@Import(HibernateAddtionalEventTestsConfig.class)
public class HibernateAddtionalEventTests {

	
	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired FooRepository repository;
	private @Autowired HibernateAddtionalEventTestsConfig config;

	@Test
	public void test1() throws Exception{
		repository.save(Foo.builder().name("a").age(1).build());
		repository.save(Foo.builder().name("b").age(1).build());
		repository.save(Foo.builder().name("c").age(2).build());
		repository.save(Foo.builder().name("d").age(2).build());
		
		Assertions.assertEquals(4, config.count);
	}
	
	@TestConfiguration
	public static class HibernateAddtionalEventTestsConfig{
		@Bean 
		public HibernateAddtionalConfiguration hibernateAddtionalConfiguration(EntityManagerFactory emf) {
			HibernateAddtionalConfiguration c = new HibernateAddtionalConfiguration(emf);
			c.setEnableHandleLoad(true);
			return c;
		}

		private int count = 0;
		
		@EventListener
		public void HibernatePreInsertEvent(HibernatePreInsertEvent e) {
			System.err.println("HibernatePreInsertEvent "+e);
			count++;
		}
	}
}
