package io.github.u2ware.data.test.example02;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooQuerydslPredicateExecutor extends PagingAndSortingRepository<Foo,Long>, 
QuerydslPredicateExecutor<Foo> 

{

}
