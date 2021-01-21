package io.github.u2ware.data.jpa.repository.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

public class QuerydslPredicateBuilder<T> {
	
	public static <X> QuerydslPredicateBuilder<X> of(Class<X> type) {
		return new QuerydslPredicateBuilder<X>(type, new BooleanBuilder());
	}
	
	private PathBuilder<T> path;
	private BooleanBuilder predicate;
	private ArrayList<OrderSpecifier<?>> orders;
	
	private QuerydslPredicateBuilder(Class<T> type, BooleanBuilder predicate) {
		this.path = new PathBuilderFactory().create(type);
		this.predicate = predicate;
		this.orders = new ArrayList<>();
	}

	
	public Predicate build() {
		return predicate;
	}	
	public Predicate build(Predicate criteria) {
		if(! ClassUtils.isAssignableValue(BooleanBuilder.class, criteria)) {
			throw new RuntimeException("Predicate is not "+BooleanBuilder.class);
		}
		return ((BooleanBuilder)criteria).and(predicate);
	}	
	public List<T> build(EntityManager em) {
		
		OrderSpecifier<?>[] oo = new OrderSpecifier[orders.size()];
		orders.toArray(oo);
		
		JPAQuery<T> query = new JPAQuery<T>(em);
		return query.from(path).where(predicate).orderBy(oo).fetch();
	}

	public OrderBuilder<T> orderBy() {
		return new OrderBuilder<>(path, orders, this);
	}
	public WhereBuilder<T> where() {
		return new WhereBuilder<>(path, predicate, this);
	}
	
	public static class OrderBuilder<T> {
		
		private PathBuilder<T> path;
		private List<OrderSpecifier<?>> orders;
		private QuerydslPredicateBuilder<T> parent;

		private OrderBuilder(PathBuilder<T> path, List<OrderSpecifier<?>> orders, QuerydslPredicateBuilder<T> parent) {
			this.path = path;
			this.orders = orders;
			this.parent = parent;
		}
		
		public OrderBuilder<T> asc(String property) {
			orders.add(new OrderSpecifier<>(Order.ASC, path.getComparable(property, Comparable.class)));
			return this;
		}
		public OrderBuilder<T> desc(String property){
			orders.add(new OrderSpecifier<>(Order.DESC, path.getComparable(property, Comparable.class)));
			return this;
		}
		
		public Predicate build() {
			return parent.build();
		}	
		public Predicate build(Predicate criteria) {
			return parent.build(criteria);
		}	
		public List<T> build(EntityManager em) {
			return parent.build(em);
		}		
	}
	
	
	public static class WhereBuilder<T> {
		
		private PathBuilder<T> path;
		private BooleanBuilder predicate;
		private QuerydslPredicateBuilder<T> parent;


		private WhereBuilder(PathBuilder<T> path, BooleanBuilder predicate, QuerydslPredicateBuilder<T> parent) {
			this.path = path;
			this.predicate = predicate;
			this.parent = parent;
		}
		
		public Predicate build() {
			return parent.build();
		}	
		public Predicate build(Predicate criteria) {
			return parent.build(criteria);
		}	
		public List<T> build(EntityManager em) {
			return parent.build(em);
		}		
		public OrderBuilder<T> orderBy() {	
			return parent.orderBy();
		}
		
		
		public AndBuilder<T,WhereBuilder<T>> and() {
			return new AndBuilder<>(path, predicate, this);
		}
		public OrBuilder<T, WhereBuilder<T>> or() {
			return new OrBuilder<>(path, predicate, this);
		}
		public AndStartBuilder<T,WhereBuilder<T>> andStart() {
			return new AndStartBuilder<>(path, predicate, this);
		}
		public OrStartBuilder<T, WhereBuilder<T>> orStart() {
			return new OrStartBuilder<>(path, predicate, this);
		}
		
//		public <P> AndBuilder<T,P> and(P parent) {
//			return new AndBuilder<>(path, predicate, parent);
//		}
//		public <P> OrBuilder<T, P> or(P parent) {
//			return new OrBuilder<>(path, predicate, parent);
//		}
//		public <P> AndStartBuilder<T,P> andStart(P parent) {
//			return new AndStartBuilder<>(path, predicate, parent);
//		}
//		public <P> OrStartBuilder<T, P> orStart(P parent) {
//			return new OrStartBuilder<>(path, predicate, parent);
//		}
	}
	
	public static class AndStartBuilder<T, P> {
		
		protected BooleanBuilder predicate;
		protected BooleanBuilder childPredicate;
		protected PathBuilder<T> path;
		protected P parent;

		private AndStartBuilder(PathBuilder<T> path, BooleanBuilder predicate, P parent) {
			this.path = path;
			this.predicate = predicate;
			this.childPredicate = new BooleanBuilder();
			this.parent = parent;
		}

		public AndBuilder<T,AndStartBuilder<T,P>> and() {
			return new AndBuilder<>(path, childPredicate, this);
		}
		public OrBuilder<T, AndStartBuilder<T,P>> or() {
			return new OrBuilder<>(path, childPredicate, this);
		}
		
		public P andEnd() {
			predicate.and(childPredicate);
			return parent;
		}
	}
	public static class OrStartBuilder<T, P> {
		
		protected BooleanBuilder predicate;
		protected BooleanBuilder childPredicate;
		protected PathBuilder<T> path;
		protected P parent;

		private OrStartBuilder(PathBuilder<T> path, BooleanBuilder predicate, P parent) {
			this.parent = parent;
			this.path = path;
			this.predicate = predicate;
			this.childPredicate = new BooleanBuilder();
		}

		public AndBuilder<T,OrStartBuilder<T,P>> and() {
			return new AndBuilder<>(path, childPredicate, this);
		}
		public OrBuilder<T, OrStartBuilder<T,P>> or() {
			return new OrBuilder<>(path, childPredicate, this);
		}
		public P orEnd() {
			predicate.or(childPredicate);
			return parent;
		}
	}
	
	
	public static class AndBuilder<T, P> extends OperationBuilder<T, P>{
		
		private AndBuilder(PathBuilder<T> path, BooleanBuilder predicate, P parent) {
			super(path,predicate,parent);
		}

		protected P criteria(Predicate p) {
			predicate.and(p);
			return parent;
		}
	}

	public static class OrBuilder<T, P> extends OperationBuilder<T, P>{
		
		private OrBuilder(PathBuilder<T> path, BooleanBuilder predicate, P parent) {
			super(path,predicate,parent);
		}

		protected P criteria(Predicate p) {
			predicate.or(p);
			return parent;
		}
	}
	

	public abstract static class OperationBuilder<T, P> {
		

		protected PathBuilder<T> path;
		protected BooleanBuilder predicate;
		protected P parent;

		private OperationBuilder(PathBuilder<T> path, BooleanBuilder predicate, P parent) {
			this.path = path;
			this.predicate = predicate;
			this.parent = parent;
		}
		
		protected abstract P criteria(Predicate p);


		public P isNull(String property) {
			return criteria(path.get(property).isNull());
		}
		public P isNotNull(String property) {
			return criteria(path.get(property).isNotNull());
		}
		public P eq(String property, Object right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.get(property).eq(right));
		}
		public P ne(String property, Object right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.get(property).ne(right));
		}
		public P like(String property, String right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getString(property).like(right));
		}
		public P notLike(String property, String right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getString(property).notLike(right));
		}
		public P between(String property, Comparable<?> from, Comparable<?> to) {
			if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) return parent;
			return criteria(path.getComparable(property, Comparable.class).between(from, to));
		}
		public P gt(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getComparable(property, Comparable.class).gt(right));
		}
		public P goe(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getComparable(property, Comparable.class).goe(right));
		}
		public P lt(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getComparable(property, Comparable.class).lt(right));
		}
		public P loe(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.getComparable(property, Comparable.class).loe(right));
		}
		public P in(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.get(property).in(right));
		}
		public P notIn(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			return criteria(path.get(property).notIn(right));
		}
		public P containsAll(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			BooleanBuilder b = new BooleanBuilder();
			right.forEach(r->{
				b.and(path.getCollection(property, Object.class).contains(r));
			});
			return criteria(b);
		}
		public P containsAny(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return parent;
			BooleanBuilder b = new BooleanBuilder();
			right.forEach(r->{
				b.or(path.getCollection(property, Object.class).contains(r));
			});
			return criteria(b);
		}
	}
}
