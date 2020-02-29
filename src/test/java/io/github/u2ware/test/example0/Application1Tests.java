package io.github.u2ware.test.example0;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.BeanWrapperFactory;
import org.springframework.data.jpa.repository.query.PartTreePredicate;
import org.springframework.data.jpa.repository.query.PartTreeSpecification;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.jpa.repository.query.SpecificationBuilder;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application1Tests {

	protected Log logger = LogFactory.getLog(getClass());

	
//	protected @Autowired WebApplicationContext context;
//	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
//	protected RestMockMvc $;
	
	private @Autowired FooRepository repository; 
	
	
	@Before
	public void before() throws Exception {
//		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
//		this.$ = new RestMockMvc(mvc, springDataRestBasePath);

		if(repository.count() > 0) return;
		
		repository.save(new Foo("a", 1, "1"));		
		repository.save(new Foo("b", 2, "1"));		
		repository.save(new Foo("c", 2, "1"));		
		repository.save(new Foo("d", 1, "2"));		
		repository.save(new Foo("e", 2, "2"));		
		repository.save(new Foo("f", 3, "2"));		
	}

//	@Test
	public void criteriaBuilderTests() throws Exception{

		List<Foo> foos1 = repository.findAll((root, query, builder)->{

			logger.info(builder.getClass());
			logger.info(builder.getClass());
			logger.info(builder.getClass());
			
//			CriteriaBuilderImpl f;
//			f.equal(x, y);
			
			Expression<?> title = PartTreePredicate.toExpressionRecursively(root, "title");
			Expression<?> name = PartTreePredicate.toExpressionRecursively(root, "name");
			Expression<?> age = PartTreePredicate.toExpressionRecursively(root, "age");
			
			
			Predicate p1 = builder.equal(title, "1");
			Predicate p2 = builder.equal(name, "a");
			Predicate p3 = builder.equal(age, 1);

//			Predicate r1 = builder.and( p1, builder.or(  p2, p3 ) ); //->2
//			Predicate r2 = builder.or( p1, builder.and( p2, p3 ) ) ; //->3
//			return age.in(new String[] {"1", "2"});
			
			Predicate r3 = builder.and(  p2, p3 );
			r3 = builder.and( p1, r3 ); //->2
			
			
			return r3;
		});
		logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(foos1));

	}
	
	
	
	
	
	
//	@Test
	public void partTreePredicateBuilderTest() throws Exception{
		
		repository.findAll((root, query, builder)->{
			Part part = new Part("NameNot", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, "b");
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("nameIsLike", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, new String[] {"avvv","bvvv"});
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("nameIsContainingIgnoreCase", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, "d");
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("age", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, "123");
		});
		
		repository.findAll((root, query, builder)->{
			Part part = new Part("ageGreaterThan", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, "a");
		});
		
		repository.findAll((root, query, builder)->{
			Part partTree = new Part("ageIn", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(partTree, new String[] {"2","3"});
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("ageBetween", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(part, new String[] {"2","3"});
		});
		
		Foo foo = new Foo("a1", 1, "b1");
		repository.findAll((root, query, builder)->{
			PartTree partTree = new PartTree("findByNameIgnoreCase", root.getJavaType());
			return new PartTreePredicate<>(root, query, builder).build(partTree, BeanWrapperFactory.getInstance(foo));
		});
		repository.findAll((root, query, builder)->{
			PartTree partTree = new PartTree("ageIn", Foo.class);
			return new PartTreePredicate<>(root, query, builder).build(partTree, BeanWrapperFactory.getInstance(foo));
		});
	}
	
//	@Test
	public void partTreeSpecificationTests() throws Exception{
		
		//////////////////////////////////////////////////////////////////////////
		// PartTreeSpecification
		///////////////////////////////////////////////////////////////////////////
		Foo params1 = new Foo("a", 1, null);
		
		Foo params2 = new Foo("b", 2, null);
		
		Foo params3 = new Foo("b", null, null);
		
		Foo params4 = new Foo(null, 1, null);
		
		
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params1)).size());
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params2)).size());
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params3)).size());
		Assert.assertEquals(2, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params4)).size());
		Assert.assertEquals(2, repository.findAll(new PartTreeSpecification<Foo>("findByNameOrAge", params1)).size());
		try {
			Assert.assertEquals(6, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndXXXX", params1)).size());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", "a", 1)).size());
		
	}
	
	@Test
	public void predicateBuilderTest() throws Exception {
		
		///////////////////////////////////////////////////
		//
		////////////////////////////////////////////////////
		repository.findAll((root, query, builder)->{
			
			
			
			Expression<?> title = PartTreePredicate.toExpressionRecursively(root, "title");
			Expression<?> name = PartTreePredicate.toExpressionRecursively(root, "name");
			Expression<?> age = PartTreePredicate.toExpressionRecursively(root, "age");
			
			
			Predicate p1 = builder.equal(title, "1");
			Predicate p2 = builder.equal(name, "a");
			Predicate p3 = builder.equal(age, 1);
		
			
			
			return PredicateBuilder.of(root, query, builder)
					.where()
//						.and(p1)
//						.or(p3)
					
//						.and().eq("name", "1")
//						.or().eq("title", "a")

//						.andStart()
//							.and(p1)
//							.or(p2)
//						.andEnd()
//						.andStart()
//							.and(p2)
//							.or(p3)
//						.andEnd()
						
//						.orStart()
//							.and().eq("name", "a")
//							.or().eq("age", 2)
//						.orEnd()
//						.orStart()
//							.and().eq("name", "a")
//							.or().eq("age", 2)
//						.orEnd()
						
						
						.and().eq("name", "a")
						.andStart()
							.andStart()
								.and().eq("age",1)
								.or().eq("name","b")
							.andEnd()
							.andStart()
								.and().eq("age", 2)
								.or().eq("name", "c")
							.andEnd()
						.andEnd()
						.and().eq("name", "d")
						
						

					.orderBy()
						.asc("name")
						.desc("age")
					.build();
			
		});
	}
	
	
//	@Test
	public void specificationBuilderTests() throws Exception{
	
		Specification<Foo> spec = new SpecificationBuilder<>();
		spec.and((r, q, b) -> {return PredicateBuilder.of(r,q,b).where().and().eq("name", "a").build();})
//		;spec
		.or((r, q, b) -> {return PredicateBuilder.of(r,q,b).where().and().eq("age", 1).build();});		
		List<Foo> foos = repository.findAll(spec);
		
		Assert.assertEquals(2, foos.size());
	}
	
	
	
	
	
}
