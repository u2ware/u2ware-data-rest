package io.github.u2ware.data.test.ext02;

import java.util.Collection;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import io.github.u2ware.data.jpa.repository.query.PartTreeSpecification;

	
@SpringBootTest
public class ElementCollectionEntityTests {

	protected Log logger = LogFactory.getLog(getClass());
		
	private @Autowired ElementCollectionEntityRepository repository;
	private @PersistenceContext EntityManager em;

	
	@Test
	public void contextLoads() throws Exception{
		
		repository.save(ElementCollectionEntity.builder().name("a").age(1).phoneNumbers(StringUtils.commaDelimitedListToSet("1,2")).build());
		repository.save(ElementCollectionEntity.builder().name("ba").age(1).build());
		repository.save(ElementCollectionEntity.builder().name("cd").age(2).build());
		repository.save(ElementCollectionEntity.builder().name("dd").age(2).build());
		

		Iterable<ElementCollectionEntity> r2 = repository.findAll((root,query,builder)->{
			Expression<Collection<String>> path = root.get("phoneNumbers");
			Expression<String> v1 = builder.literal("1");
			return builder.isMember(v1, path);
		});
		Assertions.assertEquals(1, StreamSupport.stream(r2.spliterator(), false).count());
		
		
		ElementCollectionEntity s3 = ElementCollectionEntity.builder().name("a").phoneNumbers(StringUtils.commaDelimitedListToSet("2")).build();
		Iterable<ElementCollectionEntity> r3 = repository.findAll(new PartTreeSpecification<ElementCollectionEntity>("findByPhoneNumbersContaining", s3));
		Assertions.assertEquals(1, StreamSupport.stream(r3.spliterator(), false).count());
	}

	
	
	
}
