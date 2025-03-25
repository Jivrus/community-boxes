package com.jivrus.jcp.boxjava.library.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jivrus.jcp.boxjava.library.common.AObject;
import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.common.Descriptor;
import com.jivrus.jcp.boxjava.library.common.query.Attribute;
import com.jivrus.jcp.boxjava.library.common.query.Filter;
import com.jivrus.jcp.boxjava.library.common.query.Query;
import com.jivrus.jcp.boxjava.library.common.query.Relational;
import com.jivrus.jcp.boxjava.library.common.query.Result;

public abstract class CObject extends AObject {

    protected Connector connector;
    public Descriptive descriptor;

    public CObject(Descriptive desc, Connector connector) {
       super(desc);
       this.connector = connector;
       this.descriptor = desc;
    }

    public Connector getConnector() {
        return connector;
    }

    public Map<String, Object> updateById(Map<String, Object> data, String id) throws Exception {
        Map<String, Object> query = new HashMap<>();
        query.put("id", id);
        Result result = update(List.of(data), query);
        Map<String, Object> output = result.getData().get(0); //.getFirst()
        return output;
    }

    public Result deleteById(String id) throws Exception {
    	List<Attribute> keys = connector.getPrimaryKeys(this.getId());
    	Attribute key = (keys != null && keys.size() > 0) ? keys.get(0) : new Attribute ("__id");
    	Filter filter = new Filter(key, Relational.EQUAL, id);
    	Query query = new Query(filter); 
        Result result = delete(query);
        return result;
    }

    public Map<String, Object> getById(String id, Map<String, Object> options) throws Exception {
    	List<Attribute> keys = connector.getPrimaryKeys(this.getId());
    	Attribute key = (keys != null && keys.size() > 0) ? keys.get(0) : new Attribute ("__id");
    	Filter filter = new Filter(key, Relational.EQUAL, id);
    	Query query = new Query(filter);
    	
        Result result = get(query, options);
        Map<String, Object> data = result.getData().get(0);
        return data;
    }

    public Result getByIds(List<String> ids, Map<String, Object> options) throws Exception {
      	List<Attribute> keys = connector.getPrimaryKeys(this.getId());
    	Attribute key = (keys != null && keys.size() > 0) ? keys.get(0) : new Attribute ("__id");
    	Filter filter = null;
    	for (int i = 0; i < ids.size(); i++) {
    		Filter newFilter = new Filter(key, Relational.EQUAL, ids.get(i));
    		if (filter == null) filter = newFilter; else filter.or(newFilter); 
    	}
    	Query query = new Query(filter);
    	
        Result result = get(query, options);
        return result;
    }

    public Result cloneByIds(List<String> ids, Map<String, Object> options) throws Exception {
       //TODO  - implement
    	return null;
    }

    public Result getByKeyValue(String name, String value) throws Exception {
    	Filter filter = new Filter(name, Relational.EQUAL, value);
    	Query query = new Query(filter);
    	
        return get(query, null);
    }

    public List <Attribute> getAttributes(String pattern) throws Exception {
        return connector.getAttributes(getId(), pattern);
    }

    public List<Descriptive> getFunctions() {
    	List<Descriptive> funs = new ArrayList<>();
    	Descriptive fun = new Descriptor(Map.of("__id", "getattributes", "name", "Get Attribtures", "functionName", "getAttributes"));
    	funs.add(fun);
    	return funs;
    }

    public long getCount(Query query) throws Exception {
    	return this.getCount(query, null);
    }
    
    // To be implemented by sub classes
    public abstract Result create(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
    public abstract Result update(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
    public abstract Result save(List<Map<String, Object>> data, Map<String, Object> options) throws Exception;
    public abstract Result delete(Query query, Map<String, Object> options) throws Exception;
    public abstract Result get(Query query, Map<String, Object> options) throws Exception;
    
    
    
    public abstract long getCount(Query query, Map<String, Object> options) throws Exception;

	public abstract Result executeFunction(String functionId, Map<String, Object> functionOptions) throws Exception;

	public abstract Descriptive getFunction(String id);
}
