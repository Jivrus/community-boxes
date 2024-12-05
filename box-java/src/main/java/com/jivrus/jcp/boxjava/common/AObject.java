package com.jivrus.jcp.boxjava.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jivrus.jcp.boxjava.common.query.Attribute;
import com.jivrus.jcp.boxjava.common.query.Query;
import com.jivrus.jcp.boxjava.common.query.Result;

public abstract class AObject extends Descriptor {

	public AObject(Descriptive desc) {
		super(desc);
	}

	public AObject(Map<String, Object> descriptor) {
		super(descriptor);
	}

	// Shortcut Override functions for convenience
	public Result get(Query query) throws Exception {
		return this.get(query, null);
	}

	public Result create(List<Map<String, Object>> data) throws Exception {
		return this.create(data);
	}

	public Result update(List<Map<String, Object>> data) throws Exception {
		return this.update(data, null);
	}

	public Result save(List<Map<String, Object>> data) throws Exception {
		return this.save(data, null);
	}

	public Result save(Map<String, Object> data) throws Exception {
		return this.save(data, null);
	}

	public Result save(Map<String, Object> data, Map<String, Object> options) throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(data);
		return this.save(list, options);
	}

	public Result delete(Query query) throws Exception {
		return this.delete(query, null);
	}
	
	
	

	// Record Life Cycle Functions - Core functions
	public abstract Result get(Query query, Map<String, Object> options) throws Exception;
	public abstract Result create(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
	public abstract Result update(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
	public abstract Result save(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
	public abstract Result delete(Query query, Map<String, Object> options) throws Exception;

	// Record Life Cycle Functions - Convenient shortcut functions
	public abstract Map<String, Object> updateById(Map<String, Object> data, String id) throws Exception;
	public abstract Result deleteById(String id) throws Exception;
	public abstract Map<String, Object> getById(String id, Map<String, Object> options) throws Exception;
	public abstract Result getByKeyValue(String name, String value) throws Exception;
	public abstract Result getByIds(List<String> ids, Map<String, Object> options) throws Exception;
	public abstract Result cloneByIds(List<String> ids, Map<String, Object> options) throws Exception;

	
	// Object Definition Functions
	public abstract List<Attribute> getAttributes(String pattern) throws Exception;

	// Summary functions
	public abstract long getCount(Query query) throws Exception;

	// Get Common functions of all connectors
	public List<Descriptive> getFunctions() {
		List<Descriptive> descriptors = new ArrayList<>();

		// TODO - Need to think what goes here.
		descriptors.add(new Descriptor(
				Map.of("__id", "getattributes", "name", "getAttributes", "crudType", "R", "hidden", true, "input",
						Map.of("list", List.of("object", "options"), "object",
								Map.of("name", "object", "dataType", "string", "required", true)),
						"options", Map.of("name", "options", "dataType", "object")

				)));
		/*
		 * descriptors.add(new Descriptor( "Create", DescriptorType.CREATE, "create",
		 * false, new Input[]{ new Input("data", DataTypes.ARRAY) } ));
		 * descriptors.add(new Descriptor( "Update", DescriptorType.UPDATE, "update",
		 * false, new Input[]{ new Input("data", DataTypes.ARRAY), new Input("options",
		 * DataTypes.OBJECT) } )); descriptors.add(new Descriptor( "Delete By Id",
		 * DescriptorType.DELETE, "deleteById", false, new Input[]{ new Input("id",
		 * DataTypes.STRING) } )); descriptors.add(new Descriptor( "Get",
		 * DescriptorType.READ, "get", false, new Input[]{ new Input("query",
		 * DataTypes.QUERY, true), new Input("options", DataTypes.OBJECT) } ));
		 * descriptors.add(new Descriptor( "Get Count", DescriptorType.READ, "getCount",
		 * false, new Input[]{ new Input("query", DataTypes.QUERY, true), new
		 * Input("options", DataTypes.OBJECT) } ));
		 */

		return descriptors;
	}

	public AObject create(Map<String, Object> data) {
		// TODO Auto-generated method stub
		return null;
	}
}
