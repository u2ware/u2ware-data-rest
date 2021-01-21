package io.github.u2ware.data.test.example03;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BarJpaSpecificationExecutor extends PagingAndSortingRepository<Bar,Long>, 
JpaSpecificationExecutor<Bar>
{

}
