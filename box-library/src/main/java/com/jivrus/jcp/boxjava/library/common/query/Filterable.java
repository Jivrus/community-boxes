package com.jivrus.jcp.boxjava.library.common.query;

public interface Filterable {
	String buildFilter(String left, Operator operator, Object right);
}
