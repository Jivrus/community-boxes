package com.jivrus.jcp.boxjava.library.common.query;

import java.util.Map;

public interface Maker {
	public Object make(String name, Map<String, Object> data, Map<String, Object> options);
}
