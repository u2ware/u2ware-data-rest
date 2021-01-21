package io.github.u2ware.data.test.example02;

import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.querydsl.EntityPathResolver;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

import io.github.u2ware.data.jpa.repository.support.QuerydslPredicateBuilder;
import io.github.u2ware.data.jpa.repository.support.QuerydslSimpleEntityPathResolver;
import io.github.u2ware.data.test.example02.QuerydslPredicateBuilderTests.Config;


@SpringBootTest
@Import(Config.class)
public class QuerydslPredicateBuilderTests {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired FooQuerydslPredicateExecutor repository;
	private @PersistenceContext EntityManager em;

	@TestConfiguration
	public static class Config{
		//////////////////////////////////////////////
		//  When you don't have "Querydsl Q-File". 
		//////////////////////////////////////////////
		@Bean 
		public EntityPathResolver extendedSimpleEntityPathResolver() {
			return new QuerydslSimpleEntityPathResolver();
		}
	}
	
	
	
	@Test
	public void querydslTest() throws Exception{
		
		repository.save(Foo.builder().name("a").age(1).build());
		repository.save(Foo.builder().name("b").age(1).build());
		repository.save(Foo.builder().name("c").age(2).build());
		repository.save(Foo.builder().name("d").age(2).build());
		
		//#0 Querydsl Origin
		logger.info("\n====================================================\n");
		BooleanBuilder p1 = new BooleanBuilder();
		PathBuilder<Foo> p = new PathBuilderFactory().create(Foo.class);
		p1.and(p.get("name").eq("a"));
		p1.and(p.get("age").eq(1));
		Iterable<Foo> r1 = repository.findAll(p1);
		Assertions.assertEquals(1, StreamSupport.stream(r1.spliterator(), false).count());
		
		
		//#1 QuerydslPredicateBuilder
		logger.info("\n====================================================\n");
		Predicate p2 = QuerydslPredicateBuilder.of(Foo.class).where().and().eq("age", 1).orderBy().asc("name").build();
		Iterable<Foo> r2 = repository.findAll(p2);
		Assertions.assertEquals(2, StreamSupport.stream(r2.spliterator(), false).count());
		
		
		//#2 QuerydslPredicateBuilder
		logger.info("\n====================================================\n");
		Predicate p3 = new BooleanBuilder();
		QuerydslPredicateBuilder.of(Foo.class).where().and().eq("name", "b").orderBy().asc("age").build(p3);
		Iterable<Foo> r3 = repository.findAll(p3);
		Assertions.assertEquals(1, StreamSupport.stream(r3.spliterator(), false).count());
		
		//#3 QuerydslPredicateBuilder 
		logger.info("\n====================================================\n");
		Iterable<Foo> r4 = QuerydslPredicateBuilder.of(Foo.class).where().and().eq("age", null).orderBy().asc("age").build(em);
		Assertions.assertEquals(4, StreamSupport.stream(r4.spliterator(), false).count());
	}
}
