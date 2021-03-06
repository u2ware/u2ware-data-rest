package io.github.u2ware.data.test.example03;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Predicate;

import io.github.u2ware.data.jpa.repository.query.JpaSpecificationBuilder;
import io.github.u2ware.data.jpa.repository.support.QuerydslPredicateBuilder;
import io.github.u2ware.data.rest.core.annotation.HandleAfterRead;
import io.github.u2ware.data.rest.core.annotation.HandleBeforeRead;

@Component
@RepositoryEventHandler
public class FooRepositoryHandler {

	public static final String NAME = BarRepositoryHandler.class.getName();


	private Log logger = LogFactory.getLog(getClass());

	
	@HandleBeforeRead
	protected void handleBeforeRead(Foo e, Specification<Foo> criteria) {
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		
		JpaSpecificationBuilder.of(Foo.class).where().and().eq("name", e.getName()).build(criteria);
	}

	@HandleBeforeRead
	protected void handleBeforeRead(Foo e, Predicate criteria) {
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		logger.info("handleBeforeRead "+criteria);
		QuerydslPredicateBuilder.of(Foo.class).where().and().eq("age", e.getAge()).build(criteria);
	}
	
	
	@HandleAfterRead
	protected void handleAfterRead(Foo e) {
		logger.info("handleAfterRead");
		logger.info("handleAfterRead");
		logger.info("handleAfterRead");
		logger.info("handleAfterRead");
		
		e.setName(NAME);
	}
	
	

}
