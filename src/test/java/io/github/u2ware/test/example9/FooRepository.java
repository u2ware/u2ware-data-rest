package io.github.u2ware.test.example9;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long>, QuerydslPredicateExecutor<Foo>, JpaSpecificationExecutor<Foo>{

	
	Page<Foo> findByName(String name, Pageable pageable);
}
