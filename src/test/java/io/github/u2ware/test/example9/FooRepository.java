package io.github.u2ware.test.example9;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long>, QuerydslPredicateExecutor<Foo>, JpaSpecificationExecutor<Foo>{

	
	Page<Foo> findByName(String name, Pageable pageable);
	
	
	@Query("SELECT distinct(f.name) FROM Foo f")
	List<String> searchAllName();
	
	@Query("SELECT count(*) FROM Foo f")
	int searchCount();
	
	@Query("SELECT max(f.name) FROM Foo f")
	String searchName();
	
}
