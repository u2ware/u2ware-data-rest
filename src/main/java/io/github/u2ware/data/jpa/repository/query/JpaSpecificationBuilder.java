package io.github.u2ware.data.jpa.repository.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ClassUtils;

public class JpaSpecificationBuilder<T> {

	public static <X> JpaSpecificationBuilder<X> of(Class<X> type) {
		return new JpaSpecificationBuilder<X>(type, new MutableSpecification<>());
	}
	
	private Class<T> domainClass;
	private Specification<T> specification;

	private JpaSpecificationBuilder(Class<T> domainClass, Specification<T> specification) {
		this.domainClass = domainClass;
		this.specification = specification;
	}

	
	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	public Specification<T> build() {
		return specification;
	}
	public Specification<T> build(Specification<T> criteria) {
		if(! ClassUtils.isAssignableValue(MutableSpecification.class, criteria)) {
			throw new RuntimeException("Specification is not "+MutableSpecification.class);
		}
		return criteria.and(specification);
	}
	public List<T> build(EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(domainClass);
		Root<T> root = query.from(domainClass);
		query.select(root);
		Predicate predicate = this.specification.toPredicate(root, query, em.getCriteriaBuilder());
		if (predicate != null) {
			query.where(predicate);
		}
		return em.createQuery(query).getResultList();
	}
	
	public WhereBuilder<T> where() {
		return new WhereBuilder<>(specification, this);
	}
	public OrderBuilder<T> orderBy() {
		return new OrderBuilder<>(specification, this);
	}
	
	
	/////////////////////
	//
	//////////////////////
	public static class WhereBuilder<T> {
		
		private JpaSpecificationBuilder<T> parent;
		private Specification<T> specification;

		private WhereBuilder(Specification<T> specification, JpaSpecificationBuilder<T> parent) {
			this.specification = specification;
			this.parent = parent;
		}
		
		public Specification<T> build() {
			return parent.build();
		}
		public Specification<T> build(Specification<T> criteria) {
			return parent.build(criteria);
		}
		public List<T> build(EntityManager em) {
			return parent.build(em);
		}
		public OrderBuilder<T> orderBy() {	
			return parent.orderBy();
		}
		
		
		public AndBuilder<T,WhereBuilder<T>> and() {
			return new AndBuilder<>(specification, this);
		}
		public OrBuilder<T, WhereBuilder<T>> or() {
			return new OrBuilder<>(specification, this);
		}
		public AndStartBuilder<T,WhereBuilder<T>> andStart() {
			return new AndStartBuilder<>(specification, this);
		}
		public OrStartBuilder<T, WhereBuilder<T>> orStart() {
			return new OrStartBuilder<>(specification, this);
		}
	}
	
	
	
	
	////////////////////////////////////////////////
	//
	////////////////////////////////////////////////
	public static class OrderBuilder<T> {
		
		private Specification<T> specification;
		private JpaSpecificationBuilder<T> parent;

		private OrderBuilder(Specification<T> specification, JpaSpecificationBuilder<T> parent) {
			this.specification = specification;
			this.parent = parent;
		}
		
		public Specification<T> build() {
			return parent.build();
		}
		public Specification<T> build(Specification<T> criteria) {
			return parent.build(criteria);
		}
		public List<T> build(EntityManager em) {
			return parent.build(em);
		}
		
		public OrderBuilder<T> asc(String property) {
			specification.and(new PartTreeSpecification<T>(Sort.by(Direction.ASC, property)));
			return this;
		}
		public OrderBuilder<T> desc(String property){
			specification.and(new PartTreeSpecification<T>(Sort.by(Direction.DESC, property)));
			return this;
		}
	}
	
	
	
	public static class AndStartBuilder<T, P> {
		
		private Specification<T> specification;
		private Specification<T> childSpecification;
		private P parent;

		private AndStartBuilder(Specification<T> specification, P parent) {
			this.specification = specification;
			this.childSpecification = new MutableSpecification<T>();
			this.parent = parent;
		}
		public AndBuilder<T,AndStartBuilder<T,P>> and() {
			return new AndBuilder<>(childSpecification, this);
		}
		public OrBuilder<T, AndStartBuilder<T,P>> or() {
			return new OrBuilder<>(childSpecification, this);
		}
		
		public P andEnd() {
			specification.and(childSpecification);
			return parent;
		}
	}
	public static class OrStartBuilder<T, P> {
		
		private Specification<T> specification;
		private Specification<T> childSpecification;
		private P parent;

		private OrStartBuilder(Specification<T> specification, P parent) {
			this.specification = specification;
			this.childSpecification = new MutableSpecification<T>();
			this.parent = parent;
		}

		public AndBuilder<T,OrStartBuilder<T,P>> and() {
			return new AndBuilder<>(childSpecification, this);
		}
		public OrBuilder<T, OrStartBuilder<T,P>> or() {
			return new OrBuilder<>(childSpecification, this);
		}
		public P orEnd() {
			specification.or(childSpecification);
			return parent;
		}
	}
	
	
	
	public static class AndBuilder<T, P> extends OperationBuilder<T, P>{
		
		private AndBuilder(Specification<T> specification, P parent) {
			super(specification,parent);
		}

		protected P criteria(Specification<T> other) {
			specification.and(other);
			return parent;
		}
	}

	public static class OrBuilder<T, P> extends OperationBuilder<T, P>{
		
		private OrBuilder(Specification<T> specification, P parent) {
			super(specification,parent);
		}

		protected P criteria(Specification<T> other) {
			specification.or(other);
			return parent;
		}
	}
	

	public abstract static class OperationBuilder<T, P> {
		
		protected Specification<T> specification;
		protected P parent;

		private OperationBuilder(Specification<T> specification, P parent) {
			this.specification = specification;
			this.parent = parent;
		}
		
		protected abstract P criteria(Specification<T> other);

		private P criteria(String source, Object value) {
			if(value == null) return parent;
			return criteria( new PartTreeSpecification<>(source, value));
		}
				
		public P isNull(String property){
			return criteria(property+"IsNull", true);
		}
		public P isNotNull(String property){
			return criteria(property+"IsNotNull", false);
		}
		public P eq(String property, Object value){
			return criteria(property, value);
		}
		public P notEq(String property, Object value){
			return criteria(property+"Not", value);
		}
		public P like(String property, Object value){
			return criteria(property+"ContainingIgnoreCase", value);
		}
		public P notLike(String property, Object value){
			return criteria(property+"NotContainingIgnoreCase", value);
		}
		public P between(String property, Object value) {
			return criteria(property+"IsBetween", value);
		}
		public P gt(String property, Object value) {
			return criteria(property+"IsGreaterThan", value);
		}
		public P gte(String property, Object value) {
			return criteria(property+"IsGreaterThanEqual", value);
		}
		public P lt(String property, Object value) {
			return criteria(property+"IsLessThan", value);
		}
		public P lte(String property, Object value) {
			return criteria(property+"IsLessThanEqual", value);
		}
		public P in(String property, Object value) {
			return criteria(property+"IsIn", value);
		}
		public P notIn(String property, Object value) {
			return criteria(property+"IsNotIn", value);
		}

	}	
}
