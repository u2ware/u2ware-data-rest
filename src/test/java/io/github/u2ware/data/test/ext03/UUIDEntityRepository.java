package io.github.u2ware.data.test.ext03;

import java.util.List;
import java.util.UUID;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UUIDEntityRepository extends PagingAndSortingRepository<UUIDEntity,UUID>, 
QuerydslPredicateExecutor<UUIDEntity>{

	
	
	List<UUIDEntity> findByStringValue(String stringValue);
	
}

