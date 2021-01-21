package io.github.u2ware.data.test.example09.source1;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, UUID> {

	
}
