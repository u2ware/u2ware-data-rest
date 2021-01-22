package io.github.u2ware.data.test.example07;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompositeIdEntityRepository extends PagingAndSortingRepository<CompositeIdEntity,CompositeIdEntity.ID>
,JpaSpecificationExecutor<CompositeIdEntity> {

}
