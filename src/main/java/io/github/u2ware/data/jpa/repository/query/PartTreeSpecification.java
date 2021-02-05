package io.github.u2ware.data.jpa.repository.query;

//import static org.springframework.data.jpa.repository.query.QueryUtils.toExpressionRecursively;
import static org.springframework.data.repository.query.parser.Part.Type.IS_NOT_EMPTY;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_CONTAINING;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_LIKE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.query.QueryUtilsWrapper;
//import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.repository.query.parser.PartTree.OrPart;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"serial", "rawtypes", "unchecked" })
public class PartTreeSpecification<T> implements Specification<T>{

	private static class BeanWrapperMultiValue extends BeanWrapperImpl {
		
		private Map<String, ?> source;
		
		protected BeanWrapperMultiValue(Map<String, ?> source) {
			this.source = source;
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			return source.get(propertyName);
		}
	}
	
	private static class BeanWrapperObjectArray extends BeanWrapperImpl {
		
		private Object[] source;
		private AtomicInteger index;
		private Map<String,Integer> propertyIndex = new HashMap<>();
		
		BeanWrapperObjectArray(Object[] source ) {
			this.source = source;
			this.index = new AtomicInteger(0);
		}
		
		@Override
		public Object getPropertyValue(String propertyName) throws BeansException {
			if(propertyIndex.containsKey(propertyName)) {
				return source[propertyIndex.get(propertyName)];
			}
			
			int idx = index.getAndAdd(1);
			if(source.length <= idx) return null;
			propertyIndex.put(propertyName, idx);
			return source[idx];
		}
	}
	
	//AbstractQueryCreator
	//JpaQueryCreator
	//ParameterMetadataProvider
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private String source;
	private Sort sort;
	private BeanWrapper params;
	private PartTree tree;
	
	
	public PartTreeSpecification(String source, T params) {
		this(source, new BeanWrapperImpl(params));
	}
	public PartTreeSpecification(String source, Object[] params) {
		this(source, new BeanWrapperObjectArray(params));
	}
	public PartTreeSpecification(String source, MultiValueMap<String,Object> params) {
		this(source, new BeanWrapperMultiValue(params));
	}
	public PartTreeSpecification(String source, BeanWrapper params) {
		Assert.notNull(source, "treeSource is requried");
		Assert.notNull(params, "iterator is requried");
		this.source = source;
		this.params = params;
	}
	public PartTreeSpecification(Sort sort) {
		Assert.notNull(sort, "sort is requried");
		this.sort = sort;
	}
	
	
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		
		if(sort != null) {
			List<javax.persistence.criteria.Order> orders = new ArrayList<>(query.getOrderList());
			orders.addAll(0, QueryUtils.toOrders(sort, root, builder));
			query.orderBy(orders);
			return null;
		}

		if(source != null) {
			tree = new PartTree(source, root.getJavaType());
			
			List<javax.persistence.criteria.Order> orders = new ArrayList<>(query.getOrderList());
			orders.addAll(0, QueryUtils.toOrders(tree.getSort(), root, builder));
			query.orderBy(orders);
		}
		
		if(tree == null) {
			return null;
		}
		
		Predicate base = null;
		for (OrPart node : tree) {

			Iterator<Part> parts = node.iterator();

			if (!parts.hasNext()) {
				throw new IllegalStateException(String.format("No part found in PartTree %s!", tree));
			}

			Predicate criteria = create(root, query, builder, parts.next());

			while (parts.hasNext()) {
				criteria = and(root, query, builder, parts.next(), criteria);
			}

			base = base == null ? criteria : or(root, query, builder, base, criteria);
		}
		return base;
	}

	private Predicate create(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part) {
		return toPredicate(root, query, builder, part);
	}

	private Predicate and(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part, Predicate base) {
		return builder.and(base, toPredicate(root, query, builder, part));
	}

	private Predicate or(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Predicate base, Predicate predicate) {
		return builder.or(base, predicate);
	}

	private Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part) {

		PropertyPath property = part.getProperty();
		Type type = part.getType();
		Object value = params.getPropertyValue(part.getProperty().getSegment());
		if(value == null) return null;

		switch (type) {
			case BETWEEN:
//				ParameterMetadata<Comparable> first = provider.next(part);
//				ParameterMetadata<Comparable> second = provider.next(part);
//				return builder.between(getComparablePath(root, part), first.getExpression(), second.getExpression());
				Collection<Comparable> parameters = parameters(root, query, builder, part, value);
				Iterator<Comparable> iterator = parameters.iterator();
				return builder.between(getComparablePath(root, part), iterator.next(), iterator.next());
				
			case AFTER:
			case GREATER_THAN:
//				return builder.greaterThan(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.greaterThan(getComparablePath(root, part), parameter(root, query, builder, part, value));

			case GREATER_THAN_EQUAL:
//				return builder.greaterThanOrEqualTo(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.greaterThanOrEqualTo(getComparablePath(root, part), parameter(root, query, builder, part, value));
			case BEFORE:
			case LESS_THAN:
//				return builder.lessThan(getComparablePath(root, part), provider.next(part, Comparable.class).getExpression());
				return builder.lessThan(getComparablePath(root, part), parameter(root, query, builder, part, value));
				
			case LESS_THAN_EQUAL:
//				return builder.lessThanOrEqualTo(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.lessThanOrEqualTo(getComparablePath(root, part), parameter(root, query, builder, part, value));
				
			case IS_NULL:
				return getTypedPath(root, part).isNull();
			case IS_NOT_NULL:
				return getTypedPath(root, part).isNotNull();
			case NOT_IN:
				// cast required for eclipselink workaround, see DATAJPA-433
//				return upperIfIgnoreCase(getTypedPath(root, part)).in((Expression<Collection<?>>) provider.next(part, Collection.class).getExpression()).not();
				return upperIfIgnoreCase(builder, getTypedPath(root, part), part).in(parameters(root, query, builder, part, value)).not();
			case IN:
				// cast required for eclipselink workaround, see DATAJPA-433
//				return upperIfIgnoreCase(getTypedPath(root, part)).in((Expression<Collection<?>>) provider.next(part, Collection.class).getExpression());
				return upperIfIgnoreCase(builder, getTypedPath(root, part), part).in(parameters(root, query, builder, part, value));
			case STARTING_WITH:
			case ENDING_WITH:
			case CONTAINING:
			case NOT_CONTAINING:

				if (property.getLeafProperty().isCollection()) {

//					Expression<Collection<Object>> propertyExpression = traversePath(root, property);
//					ParameterExpression<Object> parameterExpression = provider.next(part).getExpression();
//
//					// Can't just call .not() in case of negation as EclipseLink chokes on that.
//					return type.equals(NOT_CONTAINING) ? isNotMember(builder, parameterExpression, propertyExpression)
//							: isMember(builder, parameterExpression, propertyExpression);
					Expression<Collection<Object>> propertyExpression = traversePath(root, property);
					Expression<Object> parameterExpression = parameter(root, query, builder, part, value);
					return type.equals(NOT_CONTAINING) 
							? isNotMember(builder, parameterExpression, propertyExpression)
							: isMember(builder, parameterExpression, propertyExpression);
					
				}

			case LIKE:
			case NOT_LIKE:
//				Expression<String> stringPath = getTypedPath(root, part);
//				Expression<String> propertyExpression = upperIfIgnoreCase(stringPath);
//				Expression<String> parameterExpression = upperIfIgnoreCase(provider.next(part, String.class).getExpression());
//				Predicate like = builder.like(propertyExpression, parameterExpression, escape.getEscapeCharacter());
//				return type.equals(NOT_LIKE) || type.equals(NOT_CONTAINING) ? like.not() : like;
				Predicate like = builder.like(upperIfIgnoreCase(builder, getTypedPath(root, part), part), parameter(root, query, builder, part, value));
				return type.equals(NOT_LIKE) || type.equals(NOT_CONTAINING) ? like.not() : like;

			case TRUE:
				Expression<Boolean> truePath = getTypedPath(root, part);
				return builder.isTrue(truePath);
			case FALSE:
				Expression<Boolean> falsePath = getTypedPath(root, part);
				return builder.isFalse(falsePath);
			case SIMPLE_PROPERTY:
//				ParameterMetadata<Object> expression = provider.next(part);
//				Expression<Object> path = getTypedPath(root, part);
//				return expression.isIsNullParameter() ? path.isNull()
//						: builder.equal(upperIfIgnoreCase(path), upperIfIgnoreCase(expression.getExpression()));
				return builder.equal(upperIfIgnoreCase(builder, getTypedPath(root, part), part), parameter(root, query, builder, part, value));

				
				
			case NEGATING_SIMPLE_PROPERTY:
//				return builder.notEqual(upperIfIgnoreCase(getTypedPath(root, part)),
//						upperIfIgnoreCase(provider.next(part).getExpression()));
				return builder.notEqual(upperIfIgnoreCase(builder, getTypedPath(root, part), part), parameter(root, query, builder, part, value));
			case IS_EMPTY:
			case IS_NOT_EMPTY:

				if (!property.getLeafProperty().isCollection()) {
					throw new IllegalArgumentException("IsEmpty / IsNotEmpty can only be used on collection properties!");
				}

				Expression<Collection<Object>> collectionPath = traversePath(root, property);
				return type.equals(IS_NOT_EMPTY) ? builder.isNotEmpty(collectionPath) : builder.isEmpty(collectionPath);

			default:
				throw new IllegalArgumentException("Unsupported keyword " + type);
		}
	}

	private <X> Predicate isMember(CriteriaBuilder builder, Expression<X> parameter, Expression<Collection<X>> property) {
		return builder.isMember(parameter, property);
	}

	private <X> Predicate isNotMember(CriteriaBuilder builder, Expression<X> parameter, Expression<Collection<X>> property) {
		return builder.isNotMember(parameter, property);
	}

	private <X> Expression<X> upperIfIgnoreCase(CriteriaBuilder builder, Expression<? extends X> expression, Part part) {
		switch (part.shouldIgnoreCase()) {
			case ALWAYS:
				Assert.state(canUpperCase(expression), "Unable to ignore case of " + expression.getJavaType().getName()
						+ " types, the property '" + part.getProperty().getSegment() + "' must reference a String");
				return (Expression<X>) builder.upper((Expression<String>) expression);

			case WHEN_POSSIBLE:

				if (canUpperCase(expression)) {
					return (Expression<X>) builder.upper((Expression<String>) expression);
				}

			case NEVER:
			default:

				return (Expression<X>) expression;
		}
	}

	private boolean canUpperCase(Expression<?> expression) {
		return String.class.equals(expression.getJavaType());
	}

	private Expression<? extends Comparable> getComparablePath(Root<?> root, Part part) {
		return getTypedPath(root, part);
	}

	private <X> Expression<X> getTypedPath(Root<?> root, Part part) {
		return QueryUtilsWrapper.toExpressionRecursively(root, part.getProperty());
	}

	private <X> Expression<X> traversePath(Path<?> root, PropertyPath path) {
		Path<Object> result = root.get(path.getSegment());
		return (Expression<X>) (path.hasNext() ? traversePath(result, path.next()) : result);
	}
	
	
	
	
	///////////////////////////
	// Add
	//////////////////////////
	private <X> Collection<X> parameters(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part, Object value) {

//		Object value = params.getPropertyValue(part.getProperty().getSegment());
//		if(value == null) return null;
		
		
		Collection<X> collection = null;
		if (value instanceof Collection) {
			collection = (Collection<X>) value;
		}else if (ObjectUtils.isArray(value)) {
			collection = (Collection<X>)Arrays.asList(ObjectUtils.toObjectArray(value));
		}else {
			collection = (Collection<X>) Collections.singleton(value);
		}
		
		Expression<? extends X> expression = getTypedPath(root, part);

		Collection<X> result = new ArrayList<X>();
		collection.forEach((v)->{ 
			if(! ClassUtils.isAssignableValue(expression.getJavaType(), v)) {
				result.add(objectMapper.convertValue(v, expression.getJavaType()));
			}else {
				result.add((X)v);
			}
		});
		return result;//.iterator();
	}
	
	private <X> Expression<X> parameter(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Part part, Object value) {
		
//		Object value = params.getPropertyValue(part.getProperty().getSegment());
//		System.err.println(getClass()+" 4 "+value);
//		System.err.println(getClass()+" 4 "+value);
//		if(value == null) return null;

		Object result = value;
		if (value instanceof Collection) {
			result = ((Collection) value).iterator().next();
			return (Expression<X>) builder.literal(result);
		}
		if (ObjectUtils.isArray(value)) {
			result = ((Collection)Arrays.asList(ObjectUtils.toObjectArray(value))).iterator().next();
			return (Expression<X>) builder.literal(result);
		}

		Expression<? extends X> expression = getTypedPath(root, part);
		if(! ClassUtils.isAssignableValue(expression.getJavaType(), result)) {
			result = objectMapper.convertValue(result, expression.getJavaType()) ;
			return (Expression<X>) builder.literal(result);
		}
		
		if (String.class.equals(expression.getJavaType())) {
			
			String stringValue = (String)result;

			IgnoreCaseType shouldIgnoreCase = part.shouldIgnoreCase();
			Type type = part.getType();
			
			if(IgnoreCaseType.ALWAYS.equals(shouldIgnoreCase)) {
				stringValue = stringValue.toUpperCase();
			}else if(IgnoreCaseType.WHEN_POSSIBLE.equals(shouldIgnoreCase)) {
				if (canUpperCase(expression)) {
					stringValue = stringValue.toUpperCase();
				}
			}
			
			if(Type.STARTING_WITH.equals(type)) {
				stringValue = String.format("%s%%", stringValue);
			}else if(Type.ENDING_WITH.equals(type)) {
				stringValue = String.format("%%%s", stringValue);
			}else if(Type.CONTAINING.equals(type)) {
				stringValue = String.format("%%%s%%", stringValue);
			}else if(Type.NOT_CONTAINING.equals(type)) {
				stringValue = String.format("%%%s%%", stringValue);
			}
			
			result = stringValue;
		} 
		return (Expression<X>) builder.literal(result);
	}
	
}
