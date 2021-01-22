package io.github.u2ware.data.test.ext02;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ElementCollectionEntityRepository extends PagingAndSortingRepository<ElementCollectionEntity,Long>, 
QuerydslPredicateExecutor<ElementCollectionEntity>, 
JpaSpecificationExecutor<ElementCollectionEntity>
{

}
