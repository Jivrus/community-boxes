package com.jivrus.jcp.boxjava.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.Descriptor;
import com.jivrus.jcp.boxjava.common.query.Attribute;
import com.jivrus.jcp.boxjava.common.query.Filter;
import com.jivrus.jcp.boxjava.common.query.Filterable;
import com.jivrus.jcp.boxjava.common.query.Formatable;
import com.jivrus.jcp.boxjava.common.query.Maker;
import com.jivrus.jcp.boxjava.common.query.Operator;
import com.jivrus.jcp.boxjava.common.query.Page;
import com.jivrus.jcp.boxjava.common.query.Query;
import com.jivrus.jcp.boxjava.common.query.QueryMaker;
import com.jivrus.jcp.boxjava.common.query.Relational;
import com.jivrus.jcp.boxjava.common.query.Result;
import com.jivrus.jcp.boxjava.common.query.Sort;

public class DatabaseObject extends CObject implements Filterable, Formatable {
	private static Logger logger = Logger.getLogger(DatabaseObject.class.getName());

	public DatabaseObject(Descriptive desc, Connector connector) {
		super(desc, connector);
	}

	@Override
	public String formatObject(String object) {
		return object;
	}

	@Override
	public String formatAttribute(String inputStr) {
		String dataType = getDataTypeFromAttribute(inputStr);
        String action = getActionType(inputStr);
        String actionValue = getActionValue(inputStr, action);
        String attribute = getOnlyAttribute(inputStr);
        String result = attribute;

        if ("format".equals(action)) {
            result = formatAction(attribute, dataType, actionValue);
        }
        if ("aggregate".equals(action)) {
            result = aggregateAction(attribute, dataType, actionValue);
        }
        return result;
	}

	@Override
	public String formatValue(Object value, String dataType, String functionType) {
		if ("date".equals(dataType)) {
			return "'" + value.toString().substring(0, 10) + "'";
		} else if ("datetime".equals(dataType)) {
			String[] splitted = value.toString().split("T");
			// YYYY-MM-DD hh:mm:ss - TODO fix this correctly
			return "CONVERT_TZ('" + splitted[0] + " " + splitted[1].split("\\.")[0] + "', '+00:00', '" + "GMT')";
		} else if ("timestamp".equals(dataType)) {
			return "'" + value.toString() + "'";
		} else if ("number".equals(dataType)) {
			return value == null ? null : "" + value;
		} else if (value instanceof String) {
			return "'" + value + "'";
		}
		return String.valueOf(value);
	}

	/**
     * Formats group attributes based on data type and action.
     * 
     * @param inputStr The input string containing attribute information.
     * @return The formatted attribute.
     */
	// public String formatGroupAttributes(String inputStr) {
    //     String dataType = getDataTypeFromAttribute(inputStr);
    //     String action = getActionType(inputStr);
    //     String actionValue = getActionValue(inputStr, action);
    //     String attribute = getOnlyAttribute(inputStr);
    //     String result = attribute;

    //     if ("format".equals(action)) {
    //         result = formatAction(attribute, dataType, actionValue);
    //     }
    //     if ("aggregate".equals(action)) {
    //         result = aggregateAction(attribute, dataType, actionValue);
    //     }
    //     return result;
    // }

	/**
     * Aggregates the attribute based on data type and aggregation type.
     * 
     * @param attribute The attribute to aggregate.
     * @param dataType  The data type of the attribute.
     * @param aggregate The type of aggregation (e.g., count).
     * @return The aggregated attribute.
     */
	public String aggregateAction(String attribute, String dataType, String aggregate) {
        System.out.println("aggregate: " + aggregate);

        if ("date".equals(dataType)) {
            // Handle date type if needed
        } else if ("datetime".equals(dataType)) {
            // Handle datetime type if needed
        } else if ("timestamp".equals(dataType)) {
            // Handle timestamp type if needed
        } else if ("number".equals(dataType)) {
            String converted = attribute;
            if (aggregate != null && aggregate.equals("count")) {
                converted = "COUNT(*) AS " + attribute;
            }
            attribute = converted;
        } else if ("string".equals(dataType)) {
            // Handle string type if needed
        }
        return attribute;
    }

	/**
     * Formats the attribute based on data type and format.
     * 
     * @param attribute The attribute to format.
     * @param dataType  The data type of the attribute.
     * @param format    The format type (e.g., date format).
     * @return The formatted attribute.
     */
	public String formatAction(String attribute, String dataType, String format) {
        if ("date".equals(dataType)) {
            // Handle date type if needed
        } else if ("datetime".equals(dataType)) {
            String converted = attribute;
            if (format != null && format.equals("date")) {
                converted = "DATE(" + attribute + ") AS " + attribute;
            } else if (format != null) {
                converted = convertDateFormatToSQL(format);
                converted = "DATE_FORMAT(" + attribute + ", '" + converted + "') AS " + attribute;
            }
            attribute = converted;
        //TODO: Implement handling for other data types
        } else if ("timestamp".equals(dataType)) {
            // Handle timestamp type if needed
        } else if ("number".equals(dataType)) {
            // Handle number type if needed
        } else if ("string".equals(dataType)) {
            // Handle string type if needed
        }
        System.out.println("attribute: " + attribute);
        return attribute;
    }

	/**
     * Retrieves the action type from the input string.
     * 
     * @param input The input string containing action information.
     * @return The action type.
     */
	public String getActionType(String input) {
        int secondPipeIndex = input.indexOf('|', input.indexOf('|') + 1);
        int colonIndex = input.indexOf(':');
        if (secondPipeIndex != -1 && colonIndex != -1 && secondPipeIndex < colonIndex) {
            return input.substring(secondPipeIndex + 1, colonIndex).trim();
        }
        return null; // No valid format found
    }

	/**
     * Formats group by fields based attribute
     * 
     * @param inputStr The input string containing group by information.
     * @return The formatted group by field.
     */
	public String formatGroupByFields(String inputStr) {
		System.out.println("formatGroupAttribute inputStr: "+ inputStr);
        String dataType = getDataTypeFromAttribute(inputStr);
        String format = getActionValue(inputStr, "format");
        String attribute = getOnlyAttribute(inputStr);
        Object value = inputStr;

        if ("date".equals(dataType)) {
            // Handle date - TODO
        } else if ("datetime".equals(dataType)) {
            Object converted = attribute;
            if (format != null && format.equals("date")) {
                converted = "DATE(" + attribute + ")";
            } else if (format != null) {
                converted = convertDateFormatToSQL(format);
                converted = "DATE_FORMAT(" + attribute + ", '" + converted + "')";
            }
            attribute = (String) converted;
        } else if ("timestamp".equals(dataType)) {
            // Handle timestamp - TODO
        } else if ("number".equals(dataType)) {
            // Handle number - TODO
        } else if (value instanceof String) {
            // Handle string - TODO
        }
        return attribute;
    }

	public String getDataTypeFromAttribute(String attribute) {
        String value = "";
        Pattern pattern = Pattern.compile("\\|([^|]+)\\|");
        Matcher matcher = pattern.matcher(attribute);
        if (matcher.find()) {
            value = matcher.group(1).trim();
        }
        return value;
    }

	public String getActionValue(String input, String action) {
        Pattern pattern = Pattern.compile(action + ":([^|]+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null; // No format specified
    }

    public String getOnlyAttribute(String input) {
        int pipeIndex = input.indexOf('|');
        return pipeIndex != -1 ? input.substring(0, pipeIndex).trim() : input.trim();
    }

	public String convertDateFormatToSQL(String inputFormat) {
        String[] components = inputFormat.split("-");
        StringBuilder sqlFormat = new StringBuilder();
        
        if (components.length > 0) {
            for (int index = 0; index < components.length; index++) {
                if (index == 0) {
                    sqlFormat.append("%").append(components[index].substring(1));
                } else {
                    sqlFormat.append("-%").append(components[index].substring(1));
                }
            }
            return sqlFormat.toString();
        } else {
            System.err.println("Invalid date format.");
            return null;
        }
    }

	@Override
	public String buildFilter(String left, Operator operator, Object right) {
		System.out.printf("filterMaker: %s %s %s", left, operator, right);
		String operatorStr = operator.toString();

		if ("#".equals(operatorStr)) { // macro
			throw new Error(
					"Attention: You are using macros which is not yet implemented. Your are kindly invited to implement macros.");
			// TODO - implement macros
			/*
			 * Macro macro = MacroBuilder.getMacro(right); if (macro != null) { String
			 * startDate = macro.getStartDate().substring(0, 10); String endDate =
			 * macro.getEndDate().substring(0, 10); return " (" + formatAttribute(left) +
			 * " > '" + startDate + "') AND (" + formatAttribute(left) + " < '" + endDate +
			 * "')"; }
			 */
		}

		return " (" + left + " " + operatorStr + " " + right + ")";
	}

	@Override
	public Result get(Query query, Map<String, Object> options) throws Exception {
		logger.info(String.format("Going to get with query %s and options", query, options));
		String sql = null;
		int pageNumber = 1;
		
		// Check for custom SQL if not there, then handle with Query object
		String customSQL = getCustomSQL(query, options);
		if (customSQL != null)
			sql = customSQL;
		else {
			sql = "SELECT ";

			List<Attribute> groups = query.getGroup();

			if (groups != null && !groups.isEmpty()) {
				sql += getGroupAttributesString(query);
			} else {
				// Add Attributes (fields) 
				sql += getAttributesString(query);
			}
			


			//Add object (table)
			sql += " FROM " + formatObject(getName());

			// Add Filters
			Filter filter = query.getFilter();
			if (filter != null) {
				sql += " WHERE " + filter.traverse(filter, this, this);
			}

			System.out.println("query.getGroup()"+ query.getGroup());

			
			if (groups != null && !groups.isEmpty()) {
				// Add group by
				System.out.println("getGroupString(groups)"+ getGroupString(groups));
				sql += getGroupString(groups);
			}
			

			// Add Sort and Page
			sql += getSortString(query);
			Page page = query.getPage();
			sql += formatPage(page);
			if (page != null)
				pageNumber = page.getNumber();

		}

		System.out.println("sql " + sql);

		Connection connection = connector.getConnection();
		Result result = DatabaseUtility.executeQuery(connection, sql, null);
		// System.out.println("Result " + result);
		result.getPage().setNumber(pageNumber); // Overwrite the page number
		return result;
	}

	private String getGroupString(List<Attribute> groups) {
		String result = "";
		if (groups != null && !groups.isEmpty()) {
			List<String> groupAttribtues = groups.stream().map(attribute -> formatGroupByFields(attribute.getName()))
					.collect(Collectors.toList());
			result = " GROUP BY " + String.join(", ", groupAttribtues);
		}
		return result;
	}

	private String getAttributesString(Query query) {
		String result = "*";
		List<Attribute> attributes = query.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			List<String> fields = attributes.stream().map(attribute -> formatAttribute(attribute.getName()))
					.collect(Collectors.toList());
			result = String.join(", ", fields);
		}

		// Check if the result is empty or contains only spaces
		if (result == null || result.trim().isEmpty()) {
			result = "*";
		}
		return result;
	}

	private String getGroupAttributesString(Query query) {
		String result = "*";
		List<Attribute> attributes = query.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			List<String> fields = attributes.stream().map(attribute -> formatAttribute(attribute.getName()))
					.collect(Collectors.toList());
			result = String.join(", ", fields);
		}

		// Check if the result is empty or contains only spaces
		if (result == null || result.trim().isEmpty()) {
			result = "*";
		}
		return result;
	}

	private String getSortString(Query query) {
		String result = "";
		Sort sort = query.getSort();
		logger.info("Sort : " + sort);
		if (sort != null) {
			List<String> cris = new ArrayList<>();
			for (Sort.SortCriteria cri : sort.getSortBy()) {
				String criStr = String.format(" %s %s ", this.formatAttribute(cri.getAttribute().getName()),
						cri.getOrder());
				cris.add(criStr);
			}
			result = " ORDER BY " + String.join(", ", cris);
		}
		return result;
	}

	@Override
	public String formatPage(Page page) {
		String result = "";
		int pageNumber = page != null ? page.getNumber() : 1;
		int size = page != null ? page.getSize() : 100;
		int skip = (pageNumber - 1) * size;

		if (size > 0) {
			result = " LIMIT " + size;
		}
		if (skip > 0) {
			result += " OFFSET " + skip;
		}
		return result;
	}

	/**
	 * Get custom sql from the options and adjust it with the size mentioned in the
	 * page, with a maximum of 25000
	 * 
	 * @param options
	 * @param total
	 * @return
	 */
	private String getCustomSQL(Query query, Map<String, Object> options) {
		String sql = null;
		
		// Custom SQL
		if (options == null || options.getOrDefault("sql", null) == null || options.getOrDefault("sql", null).equals(""))
			return null;

		Page page = query.getPage();
		int size = page != null ? Math.min(page.getSize(), 25000) : 25000;
		sql = (String) options.get("sql");
		String lowerCaseSql = sql.toLowerCase();
		if (lowerCaseSql.contains(" limit ")) {
			// extract limit number and replace with maximum allowed limit
			int indexLimitNumStarts = lowerCaseSql.indexOf(" limit ") + 7;
			String strAfterLimit = sql.substring(indexLimitNumStarts);
			int indexOfLimitNumEnds = strAfterLimit.indexOf(" ");
			int limitInSql;
			String firstPart = sql.substring(0, indexLimitNumStarts);
			if (indexOfLimitNumEnds != -1) {
				limitInSql = Integer
						.parseInt(sql.substring(indexLimitNumStarts, indexLimitNumStarts + indexOfLimitNumEnds));
				String lastPart = sql.substring(indexLimitNumStarts + limitInSql);
				limitInSql = Math.min(limitInSql, size);
				sql = firstPart + limitInSql + lastPart;
			} else {
				limitInSql = Integer.parseInt(sql.substring(indexLimitNumStarts));
				limitInSql = Math.min(limitInSql, size);
				sql = firstPart + limitInSql;
			}
		} else {
			sql += formatPage(page);//" limit " + size;
		}
		return sql;
	}

	public Result executeQuery(String sql) throws Exception {
		Connection connection = connector.getConnection();
		Result result = DatabaseUtility.executeQuery(connection, sql, null);
		return result;
	}

	public Result save(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {
		Connection connection = connector.getConnection();
		Result result = null;
		try {

			// get the first record keys as columns
			List<String> columns = new ArrayList<>(data.get(0).keySet()); // TODO - fix uneven objects
			String sql = "INSERT INTO " + formatObject(getName()) + " (" + String.join(", ", columns) + ") VALUES";

			for (Map<String, Object> record : data) {
				List<String> values = new ArrayList<>();
				for (String key : record.keySet()) {
					Object value = record.get(key);
					values.add(this.formatValue(value, null, null));
				}
				sql += " (" + String.join(", ", values) + "),";
			}
			sql = sql.substring(0, sql.length() - 1); // remove last comma

			sql += " ON DUPLICATE KEY UPDATE";
			for (String column : columns) {
				sql += " " + column + " = VALUES(" + column + "),";
			}
			sql = sql.substring(0, sql.length() - 1);

			System.out.println("[MYSQL SAVE] sql : " + sql);
			result = DatabaseUtility.executeQuery(connection, sql, null);

		} catch (Exception e) {
			// await client.rollback();
			System.out.println("[MYSQL SAVE] failed and rollback" + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public Result create(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {

		List<String> attributes = new ArrayList<>();
		List<List<Object>> values = new ArrayList<>();

		// Constructing all keys
		Set<String> keys = new HashSet<>();
		for (Map<String, Object> record : data) {
			keys.addAll(record.keySet());
		}
		attributes.addAll(keys);

		String attributeSql = String.join(", ", attributes);
		String valueSql = String.join(", ", Collections.nCopies(attributes.size(), " ? "));
		String sql = String.format("INSERT INTO %s ( %s ) VALUES ( %s )", formatObject(getName()), attributeSql,
				valueSql);
		List<Map<String, Object>> outputData = new ArrayList<>();
		// Constructing values
		for (Map<String, Object> record : data) {
            Map<String, Object> formattedRecord = DatabaseUtility.formatRecord(record);
            List<Object> valueArr = new ArrayList<>();
            for (String key : keys) {
                Object value = formattedRecord.get(key);
                if (value == null) {
                    value = null; // If value is null
                } else if (value instanceof Map<?, ?> || value instanceof List<?>) {
                    value = value.toString();
                }
                valueArr.add(value);
            }
            values.add(valueArr);

			// Add status and data to outputData
			Map<String, Object> outputRecord = new HashMap<>();
			outputRecord.put("status", "SUCCESS");
			outputRecord.put("data", record);
			outputData.add(outputRecord);
        }

		// logger.info("Constructed Values " + values);
		Connection connection = connector.getConnection();
		Result result = DatabaseUtility.executeBatch(connection, sql, values);
		result.setData(outputData);
		result.setTotalCount(data.size());
		result.setSuccessCount(data.size());
		return result;
	}

	@Override
	public Result update(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {
		List<String> attributes = new ArrayList<>();
		List<List<Object>> values = new ArrayList<>();
		// Constructing all keys
		Set<String> keys = new HashSet<>();
		for (Map<String, Object> record : data) {
			keys.addAll(record.keySet());
		}
		attributes.addAll(keys);
		List<Map<String, Object>> outputData = new ArrayList<>();
		String table = formatObject(getName());

		// Get primary keys from options if passed, else use default keys.
		List<String> primaryKeys;
		if (options != null && options.get("uniquekeys") != null) {
			primaryKeys = (List<String>) options.get("uniquekeys");
		} else {
			primaryKeys = connector.getPrimaryKeys(getName()).stream().map(obj -> obj.getId()).collect(Collectors.toList());
		}
		System.out.println("[MYSQL UPDATE] privateKeys: " + primaryKeys);
		System.out.println("[MYSQL UPDATE] attributes: " + attributes);

		// Construct WHERE clause
		String whereClauseStr = primaryKeys.stream().map(key -> key + " = ?").collect(Collectors.joining(" AND "));

		// Remove privateKeys from attribtues so that we do not update those
		List<String> setAttribtues = new ArrayList(attributes);
		setAttribtues.removeAll(primaryKeys); // set only non primary keys
		String attributeSql = setAttribtues.stream().map(key -> key + " = ?").collect(Collectors.joining(", "));
		

		String sql = String.format("UPDATE %s SET  %s  WHERE  %s ", table, attributeSql, whereClauseStr);
		System.out.println("[MYSQL UPDATE QUERY] ----->" + sql);

		// Result result = null;

		List<String> orderedAttributes = new ArrayList(setAttribtues);
		orderedAttributes.addAll(primaryKeys);
		for (Map<String, Object> record : data) {
			List<Object> valueArr = new ArrayList<>();
			for (String key : orderedAttributes) {
				Object value = record.get(key);
				Object valueStr = "" + value;
				valueArr.add(valueStr);
			}
			values.add(valueArr);

			// Add status and data to outputData
			Map<String, Object> outputRecord = new HashMap<>();
			outputRecord.put("status", "SUCCESS");
			outputRecord.put("data", record);
			outputData.add(outputRecord);
		}

		Connection connection = connector.getConnection();
		Result result = DatabaseUtility.executeBatch(connection, sql, values);
		result.setData(outputData);
		return result;
	}

	public Result delete(Map<String, Object> record) throws Exception {
		System.out.println("[BOX] - MySQL DeleteById : " + record);
		String table = formatObject(getName());
		Connection connection = connector.getConnection();
		List<Attribute> keyMaps = connector.getPrimaryKeys(table);
		if (keyMaps == null || keyMaps.size() == 0) throw new Exception("No primary key found."); 
		String sql = "DELETE FROM " + table + " WHERE ";
		for (int i = 0; i < keyMaps.size(); i++) {
			Attribute attr = keyMaps.get(i);
			String key = attr.getId();
			String type = attr.getDataType();
			Object value = record.get(key);
			if (i > 0) sql += " AND ";
			sql += this.formatAttribute(key) + " = " + this.formatValue(value, type, null); 
		}
		System.out.println("[MYSQL DELETE] sql-Query" + sql);

		return DatabaseUtility.executeUpdate(connection, sql, record);
	}

	@Override
	public Result delete(Query query, Map<String, Object> options) throws Exception {
		String filter = query.getFilter().toString();
		String table = formatObject(getName());
		Connection connection = connector.getConnection();
		String sql = "DELETE FROM " + table + " WHERE " + filter;
		System.out.println("[MYSQL DELETE] sql-Query" + sql);
		return DatabaseUtility.executeQuery(connection, sql);
	}

	@Override
	public long getCount(Query query, Map<String, Object> options) throws Exception {
		String filter = query.getFilter().toString();
		String table = formatObject(getName());
		Connection connection = connector.getConnection();
		String sql = "SELECT count(*) as count FROM " + table + " WHERE " + filter;
		System.out.println("[MYSQL COUNT] sql-Query" + sql);
		Result result = DatabaseUtility.executeQuery(connection, sql);

		Map<String, Object> firstMap = result.getData().get(0);
    	// Retrieve the value associated with the "count" key
    	Object countValue = firstMap.get("count");
    	// Convert countValue to a long (assuming it is a numeric value)
    	long count = (countValue instanceof Number) ? ((Number) countValue).longValue() : 0;
		// long count = (long) result.getData().getFirst().get("count");
		return count;
	}

	@Override
	public Descriptive getFunction(String id) {
		List<Descriptive> descs = this.getFunctions();
		Descriptive fun = null;
		Iterator<Descriptive> it = descs.iterator();
		while (it.hasNext()) {
			Descriptive desc = it.next();
			if (desc.getId().equals(id)) {
				fun = desc;
				break;
			}
		}
		return fun;
	}

	@Override
	public List<Descriptive> getFunctions() {
		// Initialize from super class
		List<Descriptive> funs = super.getFunctions();

		// TODO - add meta functions specific to the box

		// Add standard functions to that
		final String[] standardFunctions = { "Create", "Update", "Save", "Delete", "Get" , "GetById", "DeleteById"}; // TODO - refine this to
		
		//corresponding input map
        Map<String, Map<String, Object>> inputMapByFnId = new HashMap<>();
        inputMapByFnId.put("get", Map.of( "input", Map.of("list", List.of("query", "options"),
                                            "query", Map.of("name", "query", "dataType", "query", "required", true),
                                            "options", Map.of("name", "options", "dataType", "object")), 
											"crudType", "R", 
											"options", Map.of("filter", true, "sort", true),
											"dataFlow", "pull"));
        
		inputMapByFnId.put("create", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "C", "dataFlow", "push"));

		inputMapByFnId.put("update", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "U", "dataFlow", "push"));
		inputMapByFnId.put("save", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "S"));
		inputMapByFnId.put("getbyid", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "R"));
		inputMapByFnId.put("deletebyid", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "D"));
		inputMapByFnId.put("delete", Map.of( "input",Map.of("list", List.of("data"),
                                            "data", Map.of("name", "data", "dataType", "array")), "crudType", "D"));
		
		
		// String name = getName();
		// String id = getId();
		for (int i = 0; i < standardFunctions.length; i++) {
			String fnName = standardFunctions[i].toLowerCase();
			String fnId = standardFunctions[i].toLowerCase();

			// Get the input map based on fnId
            Map<String, Object> metaMap = inputMapByFnId.getOrDefault(fnId, new HashMap<>());

			Map<String, Object> map =   new HashMap<>(Collections.singletonMap("__id", fnId)); 
			map.put("name", fnName);
			map.put("functionName", fnName);
			map.putAll(metaMap);

			Descriptive fun = new Descriptor(map);
			
			funs.add(fun);
		}

		return funs;
	}

	@Override
	public Result executeFunction(String functionName, Map<String, Object> parameters) throws Exception {
		Result result = null;
		switch (functionName) {

		case "getAttributes":
			String objectPattern = parameters != null ? (String) parameters.get("pattern") : null;
			System.out.println("Fun Connection " + connector.getConnection().getAll());
			List<Attribute> attributes = this.getAttributes(objectPattern);
			List<Map<String, Object>> dataAttributes = attributes.stream().map(attr -> attr.getAll())
					.collect(Collectors.toList());
			result = new Result(dataAttributes);
			break;
		case "executeQuery":
			String sql = parameters != null ? (String) parameters.get("sql") : null;
			result = this.executeQuery(sql);
			break;
		case "get":
			Query query;
			if (parameters != null) {
				Maker maker = new QueryMaker();
				query = (Query) maker.make("query", parameters, parameters);
				logger.info("Query Maker : " + query);
			} else {
				query = new Query(null, null, null, null);
			}

			Map<String, Object>  queryOptions = (Map<String, Object>) parameters.getOrDefault("options", null);
			result = this.get(query, queryOptions);
			break;
		case "getbyid":
			Query queryById;
			if (parameters != null) {
				Filter filter = null;
				Iterator<String> keyIterator = parameters.keySet().iterator();
				while (keyIterator.hasNext()) {
					String key = keyIterator.next();
					Object value = parameters.get(key);
					Filter newFilter = new Filter(key, Relational.EQUAL, value);
					if (filter == null) filter = newFilter; else filter.and(newFilter);
				}
				queryById = new Query(filter);
			} else {
				throw new IllegalArgumentException("Invalid parameters");
			}
			result = this.get(queryById, parameters);
			break;
		case "create":
			List <Map<String, Object>>  data = (List<Map<String, Object>>) parameters.get("data");
			Map<String, Object>  options = (Map<String, Object>) parameters.get("options");
		
			if (data == null) throw new IllegalArgumentException("No data provided");
			result = this.create(data, options);
			break;
		case "update":
			List <Map<String, Object>>  dataUpdate = (List<Map<String, Object>>) parameters.get("data");
			Map<String, Object>  optionsUpdate = (Map<String, Object>) parameters.get("options");
		
			if (dataUpdate == null) throw new IllegalArgumentException("No data provided");
			result = this.update(dataUpdate, optionsUpdate);
			break;
		case "save":
			List <Map<String, Object>>  dataSave = (List<Map<String, Object>>) parameters.get("data");
			Map<String, Object>  optionsSave = (Map<String, Object>) parameters.get("options");
		
			if (dataSave == null) throw new IllegalArgumentException("No data provided");
			result = this.update(dataSave, optionsSave);
			break;
//		case "delete":
//			List <Map<String, Object>>  deleteData = (List<Map<String, Object>>) parameters.get("data");
//			Map<String, Object>  deleteOptions = (Map<String, Object>) parameters.get("options");
//		
//			if (data == null) throw new IllegalArgumentException("No data provided");
//			result = this.delete(deleteData);
//			break;
		case "deletebyid":
			if (parameters == null) throw new IllegalArgumentException("No data provided");
			result = this.delete(parameters);
			break;
		default:
			throw new Exception("No function with name: " + functionName);
		}

		return result;
	}

	public static void main(String args[]) throws Exception {
		DatabaseObject dao = new DatabaseObject(new Descriptor("contact"), null);
		Filter filter = new Filter("id", Relational.EQUAL, "1212");
		System.out.println(filter.traverse(filter, dao, dao));
	}

}
