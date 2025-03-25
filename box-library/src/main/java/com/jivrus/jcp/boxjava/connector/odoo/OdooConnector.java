package com.jivrus.jcp.boxjava.connector.odoo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.jivrus.jcp.boxjava.library.common.DataType;
import com.jivrus.jcp.boxjava.library.common.Descriptive;
import com.jivrus.jcp.boxjava.library.common.Descriptor;
import com.jivrus.jcp.boxjava.library.common.query.Attribute;
import com.jivrus.jcp.boxjava.library.common.query.Result;
import com.jivrus.jcp.boxjava.library.manager.CObject;
import com.jivrus.jcp.boxjava.library.manager.Connection;
import com.jivrus.jcp.boxjava.library.manager.Connector;


/**
 * IN Odoo
 * Module - Represent the App
 *  Model - A model represent the Object inside an app
 */
public class OdooConnector extends Descriptor implements Connector {
	public OdooConnector(Descriptive desc) {
		super(desc);
	}

	public Connection connection;
	private Object userId;
	private String modelId;

	@Override
	public List<Descriptive> getConnectionParameters(String type) {
		List<Descriptive> descriptors = new ArrayList<>();

		descriptors.add(new Descriptor(
				Map.of("__id", "domain", "name", "Domain URL", "dataType", "string", "icon", "http", "required", true)));
		descriptors.add(new Descriptor(
				Map.of("__id", "database", "name", "Database", "dataType", "string", "icon", "database", "required", true)));
		descriptors.add(new Descriptor(
				Map.of("__id", "username", "name", "Username", "dataType", "string", "icon", "person", "required", true)));
		descriptors.add(new Descriptor(
				Map.of("__id", "password", "name", "Password or API Key", "dataType", "password", "icon", "key", "required", true)));
		return descriptors;
	}

	@Override
	public Connection connect(String name, Map<String, Object> parameters, Map<String, Object> options)
			throws Exception {
		Map<String, Object> existing = getAll();
		for (String key : existing.keySet()) {
			if (parameters.get(key) == null) {
				parameters.put(key, existing.get(key));
			}
		}
		// System.err.println("parameters" + parameters);
		connection = new Connection(parameters);
		this.authenticate(connection);
		return connection;
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	public Object getUserId() {
		return this.connection.getValue("userId");
	}

	public Object authenticate(Connection connection)
			throws ClassNotFoundException, XmlRpcException, MalformedURLException {

		String username = (String) connection.getValue("username");
		String password = (String) connection.getValue("password");

		String domainURL = (String) connection.getValue("domain");
		String database = (String) connection.getValue("database");

		XmlRpcClient client = new XmlRpcClient();
		XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
		commonConfig.setServerURL(new URL(String.format("%s/xmlrpc/2/common", domainURL)));

		// Authenticate and get UID
		Object user;
		user = client.execute(commonConfig, "authenticate",
				java.util.Arrays.asList(database, username, password, Collections.emptyMap()));

		if (user instanceof Integer intValue) {
			System.out.println("Authentication Successfull to UID #####");
			System.out.println(intValue);
			user = intValue;
			System.out.println("Set UserID");

		} else if (user instanceof Boolean boolValue) {
			System.out.println("Authentication Failed. Please check the credentials");
			System.out.println(boolValue);
			user = boolValue;
		}

		this.userId = user;
		connection.setValue("userId", user);
		this.modelId = (String) descriptor.get("odoo-model");
		return user;
	}


	@Override
	public List<Descriptive> getFunctions() {
		List<Descriptive> funs = new ArrayList<>();
		funs.add(new Descriptor(Map.of("__id", "getobjects", "name", "Get Objects", "functionName", "getObjects",
				"input",
				Map.of("list", List.of("options"), "options", Map.of("name", "options", "type", DataType.STRING)))));
		funs.add(new Descriptor(Map.of("__id", "getattributes", "name", "Get Attributes", "functionName", "getAttributes",
				"input",
				Map.of("list", List.of("object"), "object", Map.of("name", "object", "type", DataType.STRING)))));
		return funs;
	}

	@Override
	public List<Descriptive> getObjects(String pattern) throws Exception {

		String password = (String) connection.getValue("password");
		String domainURL = (String) connection.getValue("domain");
		String database = (String) connection.getValue("database");
		// Configure XML-RPC client for object service
		System.err.println(domainURL);
		System.err.println(this.userId);
		System.err.println(database + this.modelId);
		// Step 1: Retrieve all model IDs
		// Instantiate models client using the provided method
		final XmlRpcClient models = new XmlRpcClient() {
			{
				setConfig(new XmlRpcClientConfigImpl() {
					{
						setServerURL(new URL(String.format("%s/xmlrpc/2/object", domainURL)));
					}
				});
			}
		};

		List<Descriptive> list = new ArrayList<>();
		Object[] modelData = (Object[]) models.execute("execute_kw", List.of(
				database, this.userId, password,
				"ir.model", "search_read",
				Arrays.asList(Arrays.asList(
					Arrays.asList("model", "ilike", this.modelId)
					// Arrays.asList("state", "=", "installed")
				)),
				Map.of("fields", Arrays.asList("name", "state", "model", "modules", "info", "display_name" ))));

		// Print the list of models
		System.out.println("Available Models (Objects) in Odoo:");
		for (Object map : modelData) {
			Map<String, Object> modelMap = (HashMap<String, Object>) map;
			System.out.println(modelMap);
			// Extract fields
			String name = (String) modelMap.get("name");
			String id = (String) modelMap.get("model");
			String state = (String) modelMap.get("state");
			String displayName = (String) modelMap.get("display_name");
			String info = (String) modelMap.get("info");
			String module = (String) modelMap.get("modules");
	
			// Print model details
			// System.out.println("Model Name: " + name);
			// System.out.println("Model (Technical Name): " + id);
			// System.out.println("State: " + state);
			// System.out.println("---------------");
				// Convert keys to lowercase as database result can be in uppercase and lowercase
			list.add(new Descriptor(Map.of("__id", id, "name", name, "state", state, "displayName", displayName, "info", info, "module", module )));
		}


		return list;
	}

	@Override
	public List<Attribute> getAttributes(String objectName, String pattern) throws Exception {

		String password = (String) connection.getValue("password");
		String domainURL = (String) connection.getValue("domain");
		String database = (String) connection.getValue("database");
		// Configure XML-RPC client for object service
		System.err.println("objectName" + objectName);

		// Instantiate models client using the provided method
		final XmlRpcClient models = new XmlRpcClient() {
			{
				setConfig(new XmlRpcClientConfigImpl() {
					{
						setServerURL(new URL(String.format("%s/xmlrpc/2/object", domainURL)));
					}
				});
			}
		};

		// Convert Object[] to List<Integer>
		// List<Attribute> list = new ArrayList<>();
		Map<String, Map<String, Object>> modelData = (Map<String, Map<String, Object>>) models.execute("execute_kw",
				List.of(
						database, this.userId, password,
						objectName, "fields_get",
						List.of(),
						Map.of("attributes", List.of("string", "help", "type", "required"))));

		// Array of formatted objects
		List<Attribute> formattedModelData = new ArrayList<>();

		// Iterate over each field in modelData
		for (Map.Entry<String, Map<String, Object>> entry : modelData.entrySet()) {
			String fieldId = entry.getKey();
			Map<String, Object> attributes = entry.getValue();
			// System.out.println("ATTR " + attributes);

			Attribute att = new Attribute((String) fieldId);
			att.setDataType((String) attributes.get("type"));
			att.setValue("type", attributes.get("type"));
			att.setValue("help", attributes.get("help"));
			att.setValue("name", attributes.get("string"));
			att.setValue("required", attributes.get("required"));

			// Add to the list
			formattedModelData.add(att);
		}

		return formattedModelData;
	}

@Override
	public CObject getObject(String objectId) {
		Descriptive desc = new Descriptor(objectId);
		return new OdooObject(desc, this);
	}

@Override
	public Result executeFunction(String functionName, Map<String, Object> parameters) throws Exception {
    Result result = null;
    System.out.println("EXECUTE FUNC ODOO ");
		// this.modelId = (String) descriptor.get("odoo-model");
    System.out.println("Model ID "+ this.modelId);

    switch (functionName) {
			case "getObjects"-> {
			String pattern = parameters != null ? (String) parameters.get("pattern") : null;
			List<Descriptive> objects = this.getObjects(pattern);
			List<Map<String, Object>> data = objects.stream().map(attr -> attr.getAll()).collect(Collectors.toList());
			result = new Result(data);
			break;
			}
      case "getAttributes" -> {
        String objectPattern = parameters != null ? (String) parameters.get("pattern") : null;
				String objectName = (String) parameters.get("object");
	
        System.out.println("Fun Connection " + this.getConnection().getAll());
        List<Attribute> attributes = this.getAttributes(objectName, objectPattern);
        List<Map<String, Object>> dataAttributes = attributes.stream().map(attr -> attr.getAll())
            .collect(Collectors.toList());
        result = new Result(dataAttributes);
      }
      default -> throw new Exception("No function with name: " + functionName);
    }

    return result;
  }
				
	@Override
	public Descriptive getFunction(String id) {
		return null;
	}

	@Override
	public List<Descriptive> getObjectTypes() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getObjectTypes'");
	}

	@Override
	public Descriptive createBase(String name) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createBase'");
	}

	@Override
	public Descriptive updateAttribute(String name, Map<String, Object> attributes) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateAttribute'");
	}

	@Override
	public List<Attribute> getPrimaryKeys(String object) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getPrimaryKeys'");
	}

	@Override
	public String getMappedDataType(String databaseDataType) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getMappedDataType'");
	}

	@Override
	public String getConnectorDataType(String databaseDataType) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getConnectorDataType'");
	}

	@Override
	public Descriptive createObject(String name, List<Map<String, Object>> attributes) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createObject'");
	}

	@Override
	public List<Attribute> createAttribute(String name, List<Map<String, Object>> attributes) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createAttribute'");
	}
}
