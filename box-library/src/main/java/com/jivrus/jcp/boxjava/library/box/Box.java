package com.jivrus.jcp.boxjava.library.box;

import java.util.List;
import java.util.Map;

import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.manager.Connector;

public interface Box extends Descriptive {
    
    Connector getConnector();
    BoxObject getObject(String id);
    List<Descriptive> getObjects(String pattern);
    BoxFunction getFunction(String id, Map<String, Object> options);
    List<BoxFunction> getFunctions(String pattern);

    // Configuration related
    Map<String, Object> getTemplates();
    List <Descriptive> getConfigParameters(String type);
    
    String setConfig(Map<String, Object> parameters) throws Exception;
    
    String getEncryptedConfig();
    void setEncryptedConfig(String cipher) throws Exception;

    // TODO - not sure why do we need this function
    Descriptive createBoxObject(String name, Map<String, Object> parameters);
}
