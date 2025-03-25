package com.jivrus.jcp.boxjava.library.box;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.common.Descriptor;
import com.jivrus.jcp.boxjava.library.common.query.Attribute;
import com.jivrus.jcp.boxjava.library.common.query.Result;

// Assuming necessary imports are in place
public class BoxObjectImpl extends Descriptor implements BoxObject {

    private final Box box;

    public BoxObjectImpl(Map<String, Object> data, Box box) {
        super(data);
        this.box = box;
    }

    public Box getBox() {
        return box;
    }


    @Override
    public List<Attribute> getAttributes(String id, String pattern) throws Exception {
        return box.getConnector().getAttributes(id, pattern);
    }

    @Override
    public BoxFunction getFunction(String id) throws Exception {
        Descriptive desc = box.getConnector().getObject(getId()).getFunction(id);
        if (desc == null) throw new Exception("No function found for id: " + id);
        return new BoxFunctionImpl(desc.getAll(), box, this);
    }

    @Override
    public List<BoxFunction> getFunctions(String pattern) {
    	 List<Descriptive> descriptives = box.getConnector().getObject(getId()).getFunctions();
    	 return descriptives.stream().map(desc -> new BoxFunctionImpl(desc.getAll(), box, this)).collect(Collectors.toList());
    }

	@Override
	public Result executeFunction(String functionId, Map<String, Object> functionOptions) throws Exception {
		return box.getConnector().getObject(getId()).executeFunction(functionId, functionOptions);
	}



}
