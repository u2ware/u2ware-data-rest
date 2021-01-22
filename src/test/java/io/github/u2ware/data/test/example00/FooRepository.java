package io.github.u2ware.data.test.example00;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long>, JpaSpecificationExecutor<Foo>{

	
	Page<Foo> findByName(String name, Pageable pageable);
}
