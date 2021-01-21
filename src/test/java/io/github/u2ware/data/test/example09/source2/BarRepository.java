package io.github.u2ware.data.test.example09.source2;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface BarRepository extends PagingAndSortingRepository<Bar, UUID> {

}
