package com.jivrus.jcp.boxjava.library.box;

import java.util.Map;

import com.jivrus.jcp.boxjava.library.common.Descriptor;
import com.jivrus.jcp.boxjava.library.common.query.ParameterMaker;
import com.jivrus.jcp.boxjava.library.common.query.Result;
import com.jivrus.jcp.boxjava.library.manager.Connector;

// Assuming necessary imports are in place
public class BoxFunctionImpl extends Descriptor implements BoxFunction {

    private final Box box;
    private final BoxObject boxObject; 
    private final Map<String, Object> input;

	public BoxFunctionImpl(Map<String, Object> data, Box box, BoxObject boxObject) {
        super(data);
        this.box = box;
        this.input = (Map<String, Object>)data.get("input");
        this.boxObject = boxObject;
    }
    
	public BoxFunctionImpl(Map<String, Object> data, Box box) {
        super(data);
        this.box = box;
		this.boxObject = null;
        this.input = (Map<String, Object>) data.get("input");
    }
    

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public BoxObject getBoxObject() {
        return boxObject; 
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

    @Override
    public Result execute(Map<String, Object> data) throws Exception {
        System.out.println(String.format("Going to execute function %s of box %s object %s with values %s", getId(), box.getId(), boxObject != null ? boxObject.getId() : "", data));
        Result result = new Result();

        Connector connector = box.getConnector();
        if (connector != null) {
            String functionName = (String) getValue("functionName");
            if (functionName == null) {
                throw new IllegalArgumentException("Function Name is missing");
            }
            Map<String, Object> parameters = ParameterMaker.makeParams(this, data);
            if (boxObject != null) {
            	String objectId = this.boxObject.getId();
            	result = connector.getObject(objectId).executeFunction(functionName, parameters);
            } else {
            	result = connector.executeFunction(functionName, parameters);
            }
        }

        return result;
    }
}
