package com.jivrus.jcp.boxjava.common.query;

public interface Filterable {
	String buildFilter(String left, Operator operator, Object right);
}
