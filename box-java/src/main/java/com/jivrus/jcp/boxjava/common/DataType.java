package com.jivrus.jcp.boxjava.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataType {

	public static final String NUMBER = "number";
	public static final String STRING = "string";
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String DATETIME = "datetime";
	public static final String OBJECT = "object";
	public static final String QUERY = "query";
	public static final String FILTER = "filter";
	public static final String BOOLEAN = "boolean";
	public static final String PASSWORD = "password";
	public static final String IMAGE_URL = "image_url";
	public static final String ARRAY = "array";
	public static final String TIMESTAMP = "timestamp";
        
    private static final Map<String, Descriptive> DESCRIPTOR_MAP = new HashMap<>();
	

    static {
        // Initialize the descriptor map with type-specific descriptors
        DESCRIPTOR_MAP.put("query", createQueryDescriptor());
        //TODO - define more data type structures in the future
    }

    public static Descriptive getDescriptor(String type) {
    	Descriptive descriptor = DESCRIPTOR_MAP.get(type);
        if (descriptor == null) {
            // Handle unknown types gracefully, potentially with a custom type
            descriptor = createDefaultDescriptor(type);
        }
        return descriptor;
    }

    private static Descriptive createQueryDescriptor() {
    	Map <String, Object> attributes = Map.of("name", "attributes", "type", STRING);
    	Map <String, Object> filter = Map.of("name", "filter", "type", STRING);
    	Map <String, Object> sort = Map.of("name", "sort", "type", STRING);
    	List <String> list = List.of("attributes", "filter", "sort");
    	Map <String, Object> map = Map.of("list", list, "attributes", attributes, "filter", filter, "sort", sort);
    	return new Descriptor(map);
    }

    private static Descriptive createDefaultDescriptor(String type) {
    	Map <String, Object> map = Map.of("name", type, "type", STRING);
    	return new Descriptor(map);
    }
}
