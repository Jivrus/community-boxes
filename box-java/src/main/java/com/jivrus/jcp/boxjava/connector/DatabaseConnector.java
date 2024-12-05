package com.jivrus.jcp.boxjava.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;

import com.jivrus.jcp.boxjava.common.DataType;
import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.Descriptor;
import com.jivrus.jcp.boxjava.common.query.Attribute;
import com.jivrus.jcp.boxjava.common.query.Result;

public class DatabaseConnector extends Descriptor implements Connector {

	private Connection connection;

	public DatabaseConnector(Descriptive desc) {
		super(desc);
	}

	@Override
	public List<Descriptive> getConnectionParameters(String type) {
		List<Descriptive> descriptors = new ArrayList<>();
		descriptors.add(new Descriptor(Map.of("__id", "host", "name", "Host", "dataType", "string", "icon", "computer")));
		descriptors.add(new Descriptor(Map.of("__id", "port", "name", "Port", "defaultValue", "1521", "dataType", "string", "icon", "dns")));
		descriptors.add(new Descriptor(Map.of("__id", "database", "name", "Database", "dataType", "string", "icon", "view_agenda")));
		descriptors.add(new Descriptor(Map.of("__id", "user", "name", "User", "dataType", "string", "icon", "account_circle")));
		descriptors.add(new Descriptor(Map.of("__id", "password", "name", "Password", "dataType", "password", "icon", "security")));
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
		connection = new Connection(parameters);
		java.sql.Connection con = DatabaseUtility.getSQLConnection(connection);
		con.close();
		return connection;
		
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public List<Descriptive> getObjectTypes() {
		List<Descriptive> list = List.of(new Descriptor("TABLE"), new Descriptor("VIEW"), new Descriptor("TRIGGER"),
				new Descriptor("PROCEDURE"));
		return list;
	}

	@Override
	public List<Descriptive> getObjects(String pattern) throws Exception {
		String SQL_GET_OBJECTS = "__sql_get_objects";
		String SQL_GET_OBJECTS_NAME = "__sql_get_objects_name";
		String SQL_GET_OBJECTS_TYPE = "__sql_get_objects_type";
		String sql = (String) this.getValue(SQL_GET_OBJECTS);
		String name = (String) this.getValue(SQL_GET_OBJECTS_NAME);
		String type = (String) this.getValue(SQL_GET_OBJECTS_TYPE);
		
		if (sql == null) 
			sql = "SELECT table_name, table_type FROM information_schema.tables WHERE table_schema = '${database}' ORDER BY table_name";
		if (name == null)
			name = "table_name";
		if (type == null)
			type = "table_type";
		
		StringSubstitutor substitutor = new StringSubstitutor(connection.getAll());
		sql = substitutor.replace(sql);

		Result result = DatabaseUtility.executeQuery(connection, sql);
		List<Map<String, Object>> objectData = result.getData();

		
		List<Descriptive> list = new ArrayList<>();
		for (Map<String, Object> map : objectData) {
			Map<String, Object> lowercaseMap = new HashMap<>();

			// Convert keys to lowercase as database result can be in uppercase and lowercase
    	map.forEach((key, value) -> lowercaseMap.put(key.toLowerCase(), value));
			list.add(new Descriptor(Map.of("__id", lowercaseMap.get(name), "name", lowercaseMap.get(name), "type",
			lowercaseMap.getOrDefault(type, "Table"))));
		}
		return list;
	}

	@Override
	public CObject getObject(String objectId) {
		Descriptive desc = new Descriptor(objectId);
		return new DatabaseObject(desc, this);
	}

	@Override
	public List<Attribute> getAttributes(String objectName, String pattern) throws Exception {
		final String SQL_GET_ATTRIBUTES = "__sql_get_attributes";
		final String SQL_GET_ATTRIBUTES_NAME = "__sql_get_attributes_name";
		final String SQL_GET_ATTRIBUTES_TYPE = "__sql_get_attributes_type";

		String sql = (String) this.getValue(SQL_GET_ATTRIBUTES);
		String name = (String) this.getValue(SQL_GET_ATTRIBUTES_NAME);
		String type = (String) this.getValue(SQL_GET_ATTRIBUTES_TYPE);
		String colKey = (String) this.getOrDefaultValue("__sql_get_attributes_col_key", "column_key");
		String primaryKeyValue = (String) this.getOrDefaultValue("__sql_get_attributes_primarykey_value", "pri");

		// Set defaults;
		if (sql == null) // defaulting to MySQL
			sql = "SELECT column_name,data_type FROM information_schema.columns WHERE table_schema = '${database}' AND table_name = '${objectName}'";
		if (name == null)
			name = "column_name";
		if (type == null)
			type = "data_type";

		Map<String, Object> config = this.getConnection().getAll();
		config.put("objectName", objectName);

		StringSubstitutor substitutor = new StringSubstitutor(config);
		sql = substitutor.replace(sql);

		Result result = DatabaseUtility.executeQuery(connection, sql);
		List<Map<String, Object>> objectData = result.getData();
		System.out.println("Fun Object " + objectData);

		// Construct the list of attributes
		List<Attribute> list = new ArrayList<>();
		for (Map<String, Object> map : objectData) {
			Map<String, Object> lowercaseMap = new HashMap<>();
			// Convert keys to lowercase as database result can be in uppercase and lowercase
    		map.forEach((key, value) -> lowercaseMap.put(key.toLowerCase(), value));
			Attribute att = new Attribute((String) lowercaseMap.get(name));
			att.setDataType(getMappedDataType((String) lowercaseMap.get(type)));
			att.setValue("sortable", true);
			att.setValue("filterable", true);

			String colValue = (String) lowercaseMap.get(colKey);
			if(colValue != null && primaryKeyValue.equals(colValue.toLowerCase())) {
				att.setValue("primary", true);
			}

			list.add(att);
		}
		return list;
	}

	@Override
	public String getConnectorDataType(String databaseDataType){
		// final Map<String, String> dataTypeMap = Map.ofEntries(
		// 	Map.entry(DataType.STRING, "VARCHAR2"),
		// 	Map.entry(DataType.NUMBER, "NUMBER"),
		// 	Map.entry(DataType.DATE, "DATE"),
		// 	Map.entry(DataType.TIMESTAMP, "TIMESTAMP")
		// );
		String dataType = null; // = databaseDataType == null ? DataType.STRING : dataTypeMap.get(databaseDataType);
		// default so that it reminds us to do the mapping here.
		if (dataType == null) dataType = databaseDataType; // DataType.STRING; // default - TODO - showing database data type as
		return dataType;
	}

	@Override
	public String getMappedDataType(String databaseDataType) {

		// TODO - write more mapping here.
		final Map<String, String> dataTypeMap = Map.ofEntries(
			Map.entry("VARCHAR2", DataType.STRING),
			Map.entry("NCHAR", DataType.STRING),
			Map.entry("varchar", DataType.STRING),
			Map.entry("NVARCHAR2", DataType.STRING),
			Map.entry("CHAR", DataType.STRING),
			Map.entry("BINARY_FLOAT", DataType.NUMBER),
			Map.entry("NUMBER", DataType.NUMBER),
			Map.entry("number", DataType.NUMBER),
			Map.entry("int", DataType.NUMBER),
			Map.entry("date", DataType.DATE),
			Map.entry("datetime", DataType.DATETIME),
			Map.entry("DATE", DataType.DATE),
			Map.entry("TIMESTAMP", DataType.TIMESTAMP),
			Map.entry("timestamp", DataType.TIMESTAMP)
		);
		String dataType = databaseDataType == null ? DataType.STRING : dataTypeMap.get(databaseDataType);
		
		// default so that it reminds us to do the mapping here.
		if (dataType == null) dataType = databaseDataType; // DataType.STRING; // default - TODO - showing database data type as
		return dataType;
	}

	@Override
	public Descriptive createBase(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Descriptive createObject(String name, List<Map<String, Object>> attributes) throws Exception {
		// Initialize the list to store column definitions
		List<String> finalColumnsArr = new ArrayList<>();
		for (Map<String, Object> col: attributes) {
			// Map data type from input to database-compatible type
			String dataType = getConnectorDataType((String) col.get("dataType"));
			String currColName = (String) col.get("name"); //.replaceAll("[\\W_]", "_");
			String escapedColName = "\"" + currColName + "\"";

			// Adjust the type if it's a primary key (e.g., text to varchar)
			if (Boolean.TRUE.equals(col.get("primary"))) {
				dataType = getConnectorDataType((String) "string");
			}
	
			// Build the column definition
			StringBuilder currCol = new StringBuilder(escapedColName + " " + dataType);
			// if ("VARCHAR".equalsIgnoreCase(dataType)) {
			// 	Integer length = (Integer) col.getOrDefault("length", 255);
			// 	currCol.append("(").append(length).append(")");
			// }
	
			// Add the column to the list
			finalColumnsArr.add(currCol.toString());

			// Add NOT NULL for primary keys
			if (Boolean.TRUE.equals(col.get("primary"))) {
				currCol.append(" NOT NULL");
				finalColumnsArr.add("PRIMARY KEY (" + escapedColName + ")");
			}
		}

		System.out.println("finalColumnsArr" + finalColumnsArr);
		// Build the final SQL query for table creation
		String sql = "CREATE TABLE " + name + " (" + String.join(", ", finalColumnsArr) + ")";
		DatabaseUtility.executeQuery(connection, sql);
		Descriptor descriptor = new Descriptor(Map.of("__id", name, "name", name, "type", "TABLE"));
		
		return descriptor;
	}

	@Override
	public List<Attribute> createAttribute(String name, List<Map<String, Object>> attributes) throws Exception {
        // List<String> finalColumnsArr = new ArrayList<>();
		List<Attribute> list = new ArrayList<>();

		StringBuilder buildSql = new StringBuilder();
        buildSql.append(String.format("ALTER TABLE %s ADD (", name));

        for (int i = 0; i < attributes.size(); i++) {
            Map<String, Object> col = attributes.get(i);
            String dataType = getConnectorDataType((String) col.get("dataType"));
            buildSql.append(String.format("%s %s", col.get("name"), dataType));
            Integer length = (Integer) col.getOrDefault("length", 0);
            if (length > 0) {
                buildSql.append(String.format("(%d)", length));
            }
            if (i < attributes.size() - 1) {
                buildSql.append(", ");
            }

			Attribute att = new Attribute((String) col.get("name"));
			att.setDataType(getMappedDataType((String) col.get("dataType")));
            list.add(att);
        }
        buildSql.append(")");

        // Iterate over the columns and prepare the SQL fragment for each column
        // for (Map<String, Object> col: attributes) {
        //     String dataType = getConnectorDataType((String) col.get("dataType"));
        //     StringBuilder currCol = new StringBuilder("ADD " + col.get("name") + " " + dataType);
            
        //     finalColumnsArr.add(currCol.toString());
		// 	// Map columns to attributes
		// 	Attribute att = new Attribute((String) col.get("name"));
		// 	att.setDataType(getMappedDataType((String) col.get("dataType")));
        //     list.add(att);
        // }

        // Construct the final SQL query
        // String sql = "ALTER TABLE " + name + " " + String.join(", ", finalColumnsArr);
		
		String sql = buildSql.toString();
		DatabaseUtility.executeQuery(connection, sql);
		return list;
	}

	@Override
	public Descriptive updateAttribute(String name, Map<String, Object> attributes) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatabaseObject createBoxObject(String name, Map<String, Object> parameters) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Descriptive> getFunctions() {
		List<Descriptive> funs = new ArrayList<>();
		funs.add(new Descriptor(
				Map.of("__id", "getobjecttypes", "name", "Get Object Types", "functionName", "getObjectTypes", "input",
						Map.of("list", List.of("type"), "type", Map.of("name", "type", "type", DataType.STRING)))));
		funs.add(new Descriptor(Map.of("__id", "getobjects", "name", "Get Objects", "functionName", "getObjects",
				"input",
				Map.of("list", List.of("options"), "options", Map.of("name", "options", "type", DataType.STRING)))));
		funs.add(new Descriptor(Map.of("__id", "getattributes", "name", "Get Attributes", "functionName", "getAttributes",
				"input",
				Map.of("list", List.of("object"), "object", Map.of("name", "object", "type", DataType.STRING)))));
		funs.add(new Descriptor(
				Map.of(
            	"__id", "createobject",
            	"name", "Create Object",
            	"functionName", "createObject",
            	"input", Map.of(
                	"list", List.of("object", "attributes"),
                    "object", Map.of(
                    	"__id", "object",
                    	"name", "Object",
                    	"dataType", DataType.STRING,
                    	"required", true
                	),
                	"attributes", Map.of(
                    	"__id", "attributes",
                    	"name", "Attributes",
                    	"dataType", DataType.ARRAY,
                    	"array", Map.of(
                        	"elementType", "object",
                        	"list", List.of("name", "dataType"),
                        	"name", Map.of(
                            "__id", "name",
                            "name", "Name",
                            "dataType", DataType.STRING
                        	),
                       	 	"dataType", Map.of(
                            "__id", "dataType",
                            "name", "Data Type",
                            "dataType", DataType.STRING
                        	)
                    	)
                	)
            	)
			)
		));
		funs.add(new Descriptor(
				Map.of(
            	"__id", "createattribute",
            	"name", "Create Attribute",
            	"functionName", "createAttribute",
            	"input", Map.of(
                	"list", List.of("object", "attributes"),
                    "object", Map.of(
                    	"__id", "object",
                    	"name", "Object",
                    	"dataType", DataType.STRING,
                    	"required", true
                	),
                	"attributes", Map.of(
                    	"__id", "attributes",
                    	"name", "Attributes",
                    	"dataType", DataType.ARRAY,
                    	"array", Map.of(
                        	"elementType", "object",
                        	"list", List.of("name", "dataType"),
                        	"name", Map.of(
                            "__id", "name",
                            "name", "Name",
                            "dataType", DataType.STRING
                        	),
                       	 	"dataType", Map.of(
                            "__id", "dataType",
                            "name", "Data Type",
                            "dataType", DataType.STRING
                        	)
                    	)
                	)
            	)
			)
		));;
		return funs;

	}

	@Override
	public Descriptive getFunction(String id) {

		return null;
	}

	@Override
	public List<Attribute> getPrimaryKeys(String objectName) throws Exception {
		final String SQL_GET_PRIMARY_ATTRIBUTES = "__sql_get_primary_attributes";
		final String SQL_GET_ATTRIBUTES_NAME = "__sql_get_attributes_name";
		final String SQL_GET_ATTRIBUTES_TYPE = "__sql_get_attributes_type";

		String sql = (String) this.getValue(SQL_GET_PRIMARY_ATTRIBUTES);
		String name = (String) this.getValue(SQL_GET_ATTRIBUTES_NAME);
		String type = (String) this.getValue(SQL_GET_ATTRIBUTES_TYPE);

		// Set defaults;
		if (sql == null) // defaulting to MySQL
			sql = "SELECT k.column_name FROM information_schema.table_constraints t JOIN information_schema.key_column_usage k "
					+ "USING(constraint_name,table_schema,table_name) WHERE t.constraint_type='PRIMARY KEY' "
					+ "AND t.table_schema='${database}' AND t.table_name='${objectName}'";
		if (name == null)
			name = "column_name";
		if (type == null)
			type = "data_type";

		Map<String, Object> config = this.getConnection().getAll();
		config.put("objectName", objectName);
		StringSubstitutor substitutor = new StringSubstitutor(config);
		sql = substitutor.replace(sql);
		
		Result result = DatabaseUtility.executeQuery(connection, sql);
		List<Map<String, Object>> objectData = result.getData();
		System.out.println("Primary " + objectData);

		// Construct the list of attributes
		List<Attribute> list = new ArrayList<>();
		for (Map<String, Object> map : objectData) {
			Map<String, Object> lowercaseMap = new HashMap<>();
			map.forEach((key, value) -> lowercaseMap.put(key.toLowerCase(), value));
			Attribute att = new Attribute((String) lowercaseMap.get(name));
			att.setDataType(getMappedDataType((String) lowercaseMap.get(type)));
			list.add(att);
		}
		return list;
	}

	public Result executeFunction(String functionName, Map<String, Object> parameters) throws Exception {
		
		Result result = null;
		switch (functionName) {
		case "getObjectTypes":
			List<Descriptive> objectTypes = this.getObjectTypes();
			List<Map<String, Object>> dataObjectTypes = objectTypes.stream().map(type -> type.getAll())
					.collect(Collectors.toList());
			result = new Result(dataObjectTypes);
			break;
		case "getObjects":
			String pattern = parameters != null ? (String) parameters.get("pattern") : null;
			List<Descriptive> objects = this.getObjects(pattern);
			List<Map<String, Object>> data = objects.stream().map(attr -> attr.getAll()).collect(Collectors.toList());
			result = new Result(data);
			break;
		case "getAttributes":
			String objectName = (String) parameters.get("object");
			String objectPattern = (String) parameters.get("pattern");
			List<Attribute> attributes = this.getAttributes(objectName, objectPattern);
			List<Map<String, Object>> dataAttributes = attributes.stream().map(attr -> attr.getAll())
					.collect(Collectors.toList());
			result = new Result(dataAttributes);
			break;
		case "createObject":
			String oName = (String) parameters.get("object");
			List<Map<String, Object>> oAttributes = (List<Map<String, Object>>) parameters.get("attributes");
			Object dataObjects = this.createObject(oName, oAttributes);
			result = new Result(dataObjects);
			break;
		case "createAttribute":
			String objName = (String) parameters.get("object");
			List<Map<String, Object>> objAttributes = (List<Map<String, Object>>) parameters.get("attributes");
			List<Attribute> dataAttrbutes = this.createAttribute(objName, objAttributes);
			List<Map<String, Object>> resAttributes = dataAttrbutes.stream().map(attr -> attr.getAll())
					.collect(Collectors.toList());
			result = new Result(resAttributes);
			break;
		default:
			throw new Exception("No function with name: " + functionName);
		}

		return result;
	}

}
