package io.github.u2ware.data.test.mto2;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ManyToOneEntityRepository extends PagingAndSortingRepository<ManyToOneEntity, Long>,
JpaSpecificationExecutor<ManyToOneEntity>{

}
