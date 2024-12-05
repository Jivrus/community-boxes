
package com.jivrus.jcp.boxjava.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.query.Result;

public class BoxGateway {

	public static Box getBox(String id) {
		return new BoxImpl(getBoxDescriptor(id));
	}

	public static Map getConfig(String id) {
		Box box = new BoxImpl(getBoxDescriptor(id));
		List<Descriptive> config = box.getConfigParameters(null);
		Map<String, List<Descriptive>> result = new HashMap<>();
		result.put("attributes", config);
		return result;
	}

	private static Map<String, Object> getBoxDescriptor(String id) {
		Map<String, Object> descriptor = new HashMap<String, Object>();
		descriptor.put("__id", id);
		descriptor.put("name", id + " Database");

		// TODO - set more here
		return descriptor;
	}

	public static String setConfig(String id, Map<String, Object> body) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		return box.setConfig(body);
	}

	public static Map<String, Object> getObjects(String id, String boxConfigToken) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		box.setEncryptedConfig(boxConfigToken);
		List<Descriptive> objects = box.getObjects(null);
		List<Map<String, Object>> list = objects.stream().map(obj -> obj.getAll()).collect(Collectors.toList());
		return Map.of("objects", list);
	}

	public static Map<String, Object> getFunctions(String id, String boxConfigToken) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		box.setEncryptedConfig(boxConfigToken);

		List<BoxFunction> funs = box.getFunctions(null);
		List<Map<String, Object>> list = funs.stream().map(obj -> obj.getAll()).collect(Collectors.toList());
		return Map.of("functions", list);
	}

	public static Map<String, Object> getAllFunctions(String id, String boxConfigToken) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		box.setEncryptedConfig(boxConfigToken);

		List<Map<String, Object>> functionDescriptors = new ArrayList<>();
		List<BoxFunction> functions = box.getFunctions(null);

		for (BoxFunction func : functions) {
			Map<String, Object> tempFunMap = func.getAll();
			Map<String, Object> funcMap = new HashMap<>(tempFunMap);
			funcMap.put("__id", funcMap.get("__id")); 
			funcMap.put("name", funcMap.get("name") + " " + box.getName()); 
			functionDescriptors.add(funcMap);
		}

		//Getting all objects
		List<Descriptive> objects = box.getObjects(null);

		//loop through all object and get object function
		for (Descriptive object : objects) {
			BoxObject obj = box.getObject(object.getId());
			List<BoxFunction> objectFunctions = obj.getFunctions(null);
			for (BoxFunction func : objectFunctions) {
				Map<String, Object> tempFunMap = func.getAll();
				Map<String, Object> funcMap = new HashMap<>(tempFunMap);
				//add object id
				funcMap.put("__id", object.getId() + "/" + tempFunMap.get("__id")); 
				funcMap.put("name", funcMap.get("name") + " " + object.getName());
				functionDescriptors.add(funcMap);
			}
		}
		return  Map.of("functions", functionDescriptors);
	}

	public static BoxFunction getObjectFunction(String id, String objectId, String fid, String boxConfigToken) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		box.setEncryptedConfig(boxConfigToken);

		BoxObject obj = box.getObject(objectId);
		BoxFunction fun = obj.getFunction(fid);
		return fun;
	}

	public static Map<String, Object> getObjectFunctions(String id, String objectId, String boxConfigToken) throws Exception {
		Box box = new BoxImpl(getBoxDescriptor(id));
		box.setEncryptedConfig(boxConfigToken);

		BoxObject obj = box.getObject(objectId);
		List<BoxFunction> funs = obj.getFunctions(null);
		List<Map<String, Object>> list = funs.stream().map(map -> map.getAll()).collect(Collectors.toList());
		return Map.of("functions", list);
	}

	public static Map<String, Object> executeFunction(String boxId, String functionId, String boxConfigToken,
			Map<String, Object> functionOptions) throws Exception {
		Map<String, Object> output = null;
		Box box = new BoxImpl(getBoxDescriptor(boxId));
		box.setEncryptedConfig(boxConfigToken);
		BoxFunction fun = box.getFunction(functionId, functionOptions);
		Result result = null;
		try {
			result = fun.execute(functionOptions);
			// List<Map<String, Object>> list = result.getData();
			// output = Map.of("result", list);
			// //if output type is object
			// if(list == null || (list instanceof List && ((List<?>) list).isEmpty())){
			// 	Object obj = result.getDataObject();
			// 	output = Map.of("result", obj);
			// }
			// Attempt to get the list of data
			List<Map<String, Object>> list = result.getData();
			// Determine the output based on the availability of data in `list`
			if (list != null && !list.isEmpty()) {
    			// Use the list if it's not null and not empty
    			output = Map.of("result", list);
			} else {
				Object obj = result.getDataObject();
    			// Fallback to obj if list is null or empty
    			output = Map.of("result", obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			output = Map.of("status", "failure", "message", e.getMessage());
		}
		return output;
	}

	public static Map<String, Object> executeObjectFunction(String boxId, String objectId, String functionId,
			String boxConfigToken, Map<String, Object> functionOptions) throws Exception {
		Map<String, Object> output = null;
		Box box = new BoxImpl(getBoxDescriptor(boxId));
		box.setEncryptedConfig(boxConfigToken);

		Result result = null;

		try {
			BoxObject obj = box.getObject(objectId);
			BoxFunction fun = obj.getFunction(functionId);
			// System.out.println("Fun Connection " +
			// fun.getBox().getConnector().getConnection().getAll());
			result = fun.execute(functionOptions);
			output = Map.of("result", result);
		} catch (Exception e) {
			e.printStackTrace();
			output = Map.of("status", "failure", "message", e.getMessage());
			throw e; // throw the error to propagate it to the higher-level catch block
		}
		return output;
	}

}
