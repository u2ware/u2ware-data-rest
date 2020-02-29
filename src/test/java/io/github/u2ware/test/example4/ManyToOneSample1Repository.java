package io.github.u2ware.test.example4;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=true) //Default
public interface ManyToOneSample1Repository extends PagingAndSortingRepository<ManyToOneSample1, Long>{

}
