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
import org.springframework.data.jpa.domain.Specification;

import io.github.u2ware.data.jpa.repository.query.MutableSpecification;

@SpringBootTest
public class MutableSpecificationTests {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired FooJpaSpecificationExecutor repository;
	private @PersistenceContext EntityManager em;
	
	@Test
	public void mutableSpecificationTest() throws Exception{
		repository.deleteAll();
		
		repository.save(Foo.builder().name("a").age(1).build());
		repository.save(Foo.builder().name("b").age(1).build());
		repository.save(Foo.builder().name("c").age(2).build());
		repository.save(Foo.builder().name("d").age(2).build());
		
		
		logger.info("\n====================================================\n");
		Specification<Foo> s1 = Specification.where((r,q,b)->{return null;});
		s1.and((r,q,b)->{
			return b.equal(r.get("name"), "a");
		});
		s1.and((r,q,b)->{
			return b.equal(r.get("age"), 1);
		});
		Iterable<Foo> r1 = repository.findAll(s1);
		Assertions.assertEquals(4, StreamSupport.stream(r1.spliterator(), false).count());
		
		
		logger.info("\n====================================================\n");
		Specification<Foo> s2 = Specification.where((r,q,b)->{return null;});
		s2 = s2.and((r,q,b)->{
			return b.equal(r.get("name"), "a");
		});
		s2 = s2.and((r,q,b)->{
			return b.equal(r.get("age"), 1);
		});
		Iterable<Foo> r2 = repository.findAll(s2);
		Assertions.assertEquals(1, StreamSupport.stream(r2.spliterator(), false).count());
		
		
		//#1 JpaBooleanBuilder 
		logger.info("\n====================================================\n");
		Specification<Foo> s3 = new MutableSpecification<>();
		s3.and((r,q,b)->{
			return b.equal(r.get("name"), "a");
		}).and((r,q,b)->{
			return b.equal(r.get("age"), "1");
		});
		Iterable<Foo> r3 = repository.findAll(s3);
		Assertions.assertEquals(1, StreamSupport.stream(r3.spliterator(), false).count());
	}	
}
