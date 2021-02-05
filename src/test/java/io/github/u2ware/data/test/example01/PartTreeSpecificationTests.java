package io.github.u2ware.data.test.example01;

import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import io.github.u2ware.data.jpa.repository.query.PartTreeSpecification;


	
@SpringBootTest
public class PartTreeSpecificationTests {

	protected Log logger = LogFactory.getLog(getClass());
		
	private @Autowired FooJpaSpecificationExecutor repository;
	private @PersistenceContext EntityManager em;


	@Test
	public void jpaPartTreeSpecificationTest() throws Exception{
		repository.deleteAll();
		
		repository.save(Foo.builder().name("a").age(1).build());
		repository.save(Foo.builder().name("ba").age(1).build());
		repository.save(Foo.builder().name("c").age(2).build());
		repository.save(Foo.builder().name("d").age(2).build());
		
		Iterable<Foo> r1 = repository.findAll(new PartTreeSpecification<Foo>("findByName", new Object[]{"a"}));
		Assertions.assertEquals(1, StreamSupport.stream(r1.spliterator(), false).count());
		
		
		Foo s4 = Foo.builder().name("a").build();
		Iterable<Foo> r4 = repository.findAll(new PartTreeSpecification<Foo>("findByNameContaining", s4));
		Assertions.assertEquals(2, StreamSupport.stream(r4.spliterator(), false).count());
		
		
		Iterable<Foo> r5 = repository.findAll(new PartTreeSpecification<Foo>("findByOrderByName", new Object[] {}));
		Assertions.assertEquals(4, StreamSupport.stream(r5.spliterator(), false).count());
		
		
		Iterable<Foo> r6 = repository.findAll(new PartTreeSpecification<Foo>(Sort.by("age")));
		Assertions.assertEquals(4, StreamSupport.stream(r6.spliterator(), false).count());
	}

	
	
	
}
