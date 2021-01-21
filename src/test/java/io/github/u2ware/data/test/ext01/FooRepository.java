package io.github.u2ware.data.test.ext01;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long>, JpaSpecificationExecutor<Foo>{

}
