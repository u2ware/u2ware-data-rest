package io.github.u2ware.data.test.example01;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooJpaSpecificationExecutor extends PagingAndSortingRepository<Foo,Long>, 
JpaSpecificationExecutor<Foo> 
{

}
