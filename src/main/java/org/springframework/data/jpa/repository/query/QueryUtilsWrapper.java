package org.springframework.data.jpa.repository.query;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import org.springframework.data.mapping.PropertyPath;

public class QueryUtilsWrapper {

	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {
		return QueryUtils.toExpressionRecursively(from, property, false);
	}

}
