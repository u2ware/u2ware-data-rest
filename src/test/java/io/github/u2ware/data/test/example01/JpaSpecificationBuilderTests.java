package io.github.u2ware.data.test.example01;

import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import io.github.u2ware.data.jpa.repository.query.JpaSpecificationBuilder;
import io.github.u2ware.data.jpa.repository.query.MutableSpecification;


@SpringBootTest
public class JpaSpecificationBuilderTests {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired FooJpaSpecificationExecutor repository;
	private @PersistenceContext EntityManager em;


	@Test
	public void jpaSpecificationBuilderTest() throws Exception{
		
		repository.deleteAll();
		
		repository.save(Foo.builder().name("a").age(1).build());
		repository.save(Foo.builder().name("b").age(1).build());
		repository.save(Foo.builder().name("c").age(2).build());
		repository.save(Foo.builder().name("d").age(2).build());
		
		
		//#0 Specification Origin
		logger.info("\n====================================================\n");
		Specification<Foo> s1 = Specification.where((r,q,b)->{
			
			Predicate p1 = b.equal(r.get("name"), "a");
			Predicate p2 = b.equal(r.get("age"), 1);
			return b.and(p1,p2);
		});
		Iterable<Foo> r1 = repository.findAll(s1);
		Assertions.assertEquals(1, StreamSupport.stream(r1.spliterator(), false).count());
		
		
		
		
		//#1 JpaSpecificationBuilder
		logger.info("\n====================================================\n");
		Specification<Foo> s2 = JpaSpecificationBuilder.of(Foo.class).where().and().eq("age", 1).orderBy().asc("age").build();
		Iterable<Foo> r2 = repository.findAll(s2);
		Assertions.assertEquals(2, StreamSupport.stream(r2.spliterator(), false).count());

		
		//#2 JpaSpecificationBuilder
		logger.info("\n====================================================\n");
//		Specification<Person> s2 = (r,q,b)->{return null;};//->Error
		Specification<Foo> s3 = new MutableSpecification<>();
		JpaSpecificationBuilder.of(Foo.class).where().and().eq("name", "a").orderBy().desc("name").build(s3);
		Iterable<Foo> r3 = repository.findAll(s3);
		Assertions.assertEquals(1, StreamSupport.stream(r3.spliterator(), false).count());

		
		//#3 JpaSpecificationBuilder
		logger.info("\n====================================================\n");
		Iterable<Foo> r4 = JpaSpecificationBuilder.of(Foo.class).where().and().eq("name", "a").orderBy().desc("name").build(em);
		Assertions.assertEquals(1, StreamSupport.stream(r4.spliterator(), false).count());
	}
	
	public void temp() {
		
//		repository.findAll(JpaSpecificationBuilder.of(Person.class).build());
//		repository.findAll(JpaSpecificationBuilder.of(Person.class).where().and().eq("name", "1").build());
//		repository.findAll(JpaSpecificationBuilder.of(Person.class).where().and().eq("name", "1").and().eq("address", 1).build());
//		repository.findAll(JpaSpecificationBuilder.of(Person.class)
//				.where()
//					.and().eq("name", "1")
//					.or().like("name", null)
//					.or().like("name", "2")
//				.orderBy()
//					.desc("name")
//				.build()
//		);
		repository.findAll(JpaSpecificationBuilder.of(Foo.class)
				.where()
				.andStart()
					.or().eq("name", "1")
					.or().eq("age", 2)
				.andEnd()
				.and().like("address", "1")
				.orderBy()
					.desc("name")
				.build()
		);
		
//		repository.findAll(JpaSpecificationBuilder.of(Person.class)
//				.where()
//				.andStart()
//					.or().eq("age", 2)
//					.or().like("name", "1")
//				.andEnd()
//				.andStart()
//					.or().eq("seq", 1)
//					.or().like("address", "2")
//				.andEnd()
//				.orderBy()
//					.desc("name")
//				.build()
//		);
//		
//		repository.findAll(new PartTreeSpecification<Person>("findByNameAndAge", new Person(1l,"1",1,"1")));
//		repository.findAll(new PartTreeSpecification<Person>("findByNameOrAgeAndAddressIsLike", new Person(1l,"1",1,"1")));
	}
	
}
