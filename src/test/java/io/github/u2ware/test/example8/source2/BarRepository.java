package io.github.u2ware.test.example8.source2;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface BarRepository extends PagingAndSortingRepository<Bar, UUID> {

}
