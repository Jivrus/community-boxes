package com.jivrus.jcp.boxjava.connector;

import java.util.List;
import java.util.Map;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.Descriptor;
import com.jivrus.jcp.boxjava.common.query.Attribute;
import com.jivrus.jcp.boxjava.common.query.Result;

public interface Connector extends Descriptive {

	List<Descriptive> getConnectionParameters(String type);

    Connection connect(String name, Map<String, Object> parameters, Map<String, Object> options) throws Exception;

    Connection getConnection();
    
    

    List<Descriptive> getObjectTypes();

    List<Descriptive> getObjects(String type) throws Exception;
    CObject getObject(String objectId);

    List<Attribute> getAttributes(String objectName, String pattern) throws Exception;
    String getMappedDataType(String databaseDataType) throws Exception;;
    String getConnectorDataType(String databaseDataType) throws Exception;;

    Descriptive  createBase(String name) throws Exception;

    Descriptive createObject(String name, List<Map<String, Object>> attributes) throws Exception;

    List<Attribute> createAttribute(String name, List<Map<String, Object>> attributes) throws Exception;

    Descriptive updateAttribute(String name, Map<String, Object> attributes) throws Exception;

    DatabaseObject createBoxObject(String name, Map<String, Object> parameters) throws Exception;

    List<Descriptive>  getFunctions();

	List<Attribute> getPrimaryKeys(String object) throws Exception;

	Descriptive getFunction(String id);

	Result executeFunction(String functionName, Map<String, Object> functionOptions) throws Exception;
}
