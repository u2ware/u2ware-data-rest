package io.github.u2ware.data.jpa.repository.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("serial")
public class MutableSpecification<T> implements Specification<T>{

	
	private Specification<T> specification;

	public MutableSpecification() {
		specification = ((r,q,b)->{return null;});
	}
	public MutableSpecification(Specification<T> init) {
		specification = Specification.where(init);
	}
	
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		if(specification == null) return  null;
		return specification.toPredicate(root, query, criteriaBuilder);
	}
	
	@Override
	public Specification<T> and(Specification<T> other) {
		if(other == null) return this;
		specification = other.and(specification);
		return this;
	}

	@Override
	public Specification<T> or(Specification<T> other) {
		if(other == null) return this;
		specification = other.or(specification);
		return this;
	}
}
