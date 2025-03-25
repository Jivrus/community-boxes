package com.jivrus.jcp.boxjava.library.common.query;

public interface Formatable {

	String formatObject(String object);

    String formatAttribute(String attribute);

    String formatValue(Object value, String dataType, String functionType);

    String formatPage(Page page);
}
