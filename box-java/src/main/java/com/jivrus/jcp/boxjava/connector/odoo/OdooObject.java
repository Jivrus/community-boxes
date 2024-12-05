package com.jivrus.jcp.boxjava.connector.odoo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.jivrus.jcp.boxjava.common.Descriptive;
import com.jivrus.jcp.boxjava.common.Descriptor;
import com.jivrus.jcp.boxjava.common.query.Attribute;
import com.jivrus.jcp.boxjava.common.query.Logical;
import com.jivrus.jcp.boxjava.common.query.Maker;
import com.jivrus.jcp.boxjava.common.query.Page;
import com.jivrus.jcp.boxjava.common.query.Query;
import com.jivrus.jcp.boxjava.common.query.QueryMaker;
import com.jivrus.jcp.boxjava.common.query.Result;
import com.jivrus.jcp.boxjava.connector.CObject;
import com.jivrus.jcp.boxjava.connector.Connection;
import com.jivrus.jcp.boxjava.connector.Connector;

public class OdooObject extends CObject {

  public OdooObject(Descriptive desc, Connector connector) {
    super(desc, connector);
  }

  public Map<String, String> operatorMap = new HashMap<>();
  {
    operatorMap.put("=", "=");
    operatorMap.put("!=", "!=");
    operatorMap.put("<", "<");
    operatorMap.put(">", ">");
    operatorMap.put("<=", "<=");
    operatorMap.put(">=", ">=");
    operatorMap.put("%", "like");
    operatorMap.put("!%", "not like");
  }

  private String ObjectId;

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
    final String[] standardFunctions = { "Get", "Create", "Update", "DeleteById" }; // { "Create", "Update", "Save",
                                                                                    // "Delete", "Get" ,
                                                                                    // "GetById","DeleteById"}; // TODO
                                                                                    // - refine this to

    // corresponding input map
    Map<String, Map<String, Object>> inputMapByFnId = new HashMap<>();
    inputMapByFnId.put("get", Map.of("input", Map.of("list", List.of("query", "options"),
        "query", Map.of("name", "query", "dataType", "query", "required", true),
        "options", Map.of("name", "options", "dataType", "object")), "crudType", "R", "dataFlow", "pull"));

    inputMapByFnId.put("create", Map.of("input", Map.of("list", List.of("data"),
        "data", Map.of("name", "data", "dataType", "array")), "crudType", "C",
        "dataFlow", "push"));

    inputMapByFnId.put("update", Map.of("input", Map.of("list", List.of("data"),
        "data", Map.of("name", "data", "dataType", "array")), "crudType", "U",
        "dataFlow", "push"));

    inputMapByFnId.put("deletebyid", Map.of("input", Map.of("list",
        List.of("data"),
        "data", Map.of("name", "data", "dataType", "array")), "crudType", "D"));
    // inputMapByFnId.put("delete", Map.of( "input",Map.of("list", List.of("data"),
    // "data", Map.of("name", "data", "dataType", "array")), "crudType", "D"));
    // inputMapByFnId.put("save", Map.of( "input",Map.of("list", List.of("data"),
    // "data", Map.of("name", "data", "dataType", "array")), "crudType", "U"));
    // inputMapByFnId.put("getbyid", Map.of( "input",Map.of("list", List.of("data"),
    // "data", Map.of("name", "data", "dataType", "array")), "crudType", "R"));

    // String name = getName();
    // String id = getId();
    for (int i = 0; i < standardFunctions.length; i++) {
      String fnName = standardFunctions[i].toLowerCase();
      String fnId = standardFunctions[i].toLowerCase();

      // Get the input map based on fnId
      Map<String, Object> metaMap = inputMapByFnId.getOrDefault(fnId, new HashMap<>());

      Map<String, Object> map = new HashMap<>(Collections.singletonMap("__id", fnId));
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
    System.out.println("EXECUTE FUNC ODOO ");
    this.ObjectId = descriptor.getId();
    System.out.println("OBJECT ID "+ this.ObjectId);

    switch (functionName) {

      case "getAttributes" -> {
        String objectPattern = parameters != null ? (String) parameters.get("pattern") : null;
        // System.out.println("Fun Connection " + connector.getConnection().getAll());
        List<Attribute> attributes = this.getAttributes(objectPattern);
        List<Map<String, Object>> dataAttributes = attributes.stream().map(attr -> attr.getAll())
            .collect(Collectors.toList());
        result = new Result(dataAttributes);
      }
      case "get" -> {
        // System.out.println("GET Odoo");
        // System.out.println("Fun Connection " + connector.getConnection().getAll());
        Query query;
        if (parameters != null) {
          Maker maker = new QueryMaker();
          query = (Query) maker.make("query", parameters, parameters);
          // System.out.println("FILTER STR "+ query.toString());
        } else {
          query = new Query(null, null, null, null);
        }
        result = this.get(query, parameters);
      }
      case "create" -> {
        // System.out.println("Create Odoo");
        List<Map<String, Object>> data = (List<Map<String, Object>>) parameters.get("data");
        Map<String, Object> options = (Map<String, Object>) parameters.get("options");

        if (data == null)
          throw new IllegalArgumentException("No data provided");

        result = this.create(data, options);
        break;
      }
      case "update" -> {
        System.out.println("Update Odoo");
        List<Map<String, Object>> dataUpdate = (List<Map<String, Object>>) parameters.get("data");
        Map<String, Object> optionsUpdate = (Map<String, Object>) parameters.get("options");

        if (dataUpdate == null)
          throw new IllegalArgumentException("No data provided");
        result = this.update(dataUpdate, optionsUpdate);
        break;
      }
      case "deletebyid" -> {
        if (parameters == null)
          throw new IllegalArgumentException("No data provided");
        result = this.delete(parameters);
        break;
      }
      default -> throw new Exception("No function with name: " + functionName);
    }

    return result;
  }

  /**
   * Support Query
   * Filters
   * Pagination - done
   * Sort - NA
   */
  @Override
  public Result get(Query query, Map<String, Object> parameters) throws Exception {
    // System.out.println(String.format("Going to get with query", query));
    // Map<String, Object> options = (Map<String, Object>) parameters.getOrDefault("options", null);
    // System.out.println(String.format("Options", options));
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    Connection connection = connector.getConnection();
    String password = (String) connection.getValue("password");
    String domainURL = (String) connection.getValue("domain");
    String database = (String) connection.getValue("database");
    // Configure XML-RPC client for object service
    Integer userId = (Integer) connection.getValue("userId");
    // System.out.println("VALUES " + connection.getValue("boxId") + "  " + connection.getValue("boxObjectId"));
    // this.getObject
    List<Attribute> attributes = query.getAttributes();
    List<String> fields = new ArrayList<String>();
    if (attributes != null && !attributes.isEmpty()) {
      fields = attributes.stream().map(attribute -> attribute.getName())
          .collect(Collectors.toList());
    }

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

    // Add Filters
    List<List<String>> searchCriteria = this.formatFilters(parameters);
    System.out.println("SEARCH " + Arrays.asList(searchCriteria));
    Map<String, Object> pageParams = this.getPageParams(query.getPage());

    // this.buildFilter();

    Object[] ids = (Object[]) models.execute("execute_kw", Arrays.asList(
        database, userId, password,
        this.ObjectId, "search",
        Arrays.asList(searchCriteria),
        pageParams));

    List<Integer> recordIds = new ArrayList<>();
    for (Object id : ids) {
      Number numberId = (Number) id; // Adjust the cast based on actual type
      recordIds.add(numberId.intValue());
    }

    // Step 2: Read records with specified fields
    Map<String, Object> readParams = new HashMap<>();
    // readParams.put("fields", Arrays.asList("contact_name", "country_id", "phone",
    // "phone_mobile_search", "partner_name"));
    readParams.put("fields", fields);

    Object[] records = (Object[]) models.execute("execute_kw", Arrays.asList(
        database, userId, password,
        this.ObjectId, "read",
        Arrays.asList(recordIds),
        readParams));

    // Print the details of each record
    // System.out.println("Record Details:");
    // System.out.println(records);

    for (Object recordObj : records) {
      Map<String, Object> record = (Map<String, Object>) recordObj;
      Map<String, Object> transformedObject = new HashMap<>();
      for (Map.Entry<String, Object> entry : record.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        // Transform the key-value pair as needed, here we're just copying it
        transformedObject.put(key, value);
      }
      // Add the transformed object to the new array
      result.add(transformedObject);
    }
    return new Result(result);
  }

  private Map<String, Object> getPageParams(Page pageInfo) throws Exception {
    Map<String, Object> pageParams = new HashMap<>();
    // System.out.println("PAGE INFO "+ pageInfo);
    if (pageInfo != null) {
      Integer pageNumber = pageInfo.getNumber();
      Integer pageSize = pageInfo.getSize();

      if (pageNumber > -1 && pageSize > -1) {
        pageParams.put("offset", pageNumber);
        pageParams.put("limit", pageSize);
      }
    }
    return pageParams;

  }

  private List<List<String>> formatFilters(Map<String, Object> parameter) throws Exception {
    System.out.println(parameter);
    Map<String, Object> queryObj = (Map<String, Object>) parameter.get("query");
    String filtersStr = (String) queryObj.get("filter");
    List<List<String>> filters = new ArrayList<>();
    return this.buildFilter(filtersStr, filters);

  }

  private List<List<String>> buildFilter(String filterStr, List<List<String>> filters) {
    // Handle "AND" conditions and "OR" conditions
    if (filterStr != null) {
      if (filterStr.length() > 0 && filterStr.contains(Logical.andNotation)) { // TODO - make the andNotiation stronger
        String[] andParts = filterStr.split(Logical.andNotation);
        for (String andPart : andParts) {
          List<String> list = buildSimpleFilter(andPart);
          filters.add(list);
        }
      } else if (filterStr.length() > 0 && filterStr.contains(Logical.orNotation)) { // TODO - make the orNotiation
                                                                                     // stronger
        String[] andParts = filterStr.split(Logical.orNotation);
        for (String andPart : andParts) {
          List<String> list = buildSimpleFilter(andPart);
          filters.add(list);
        }
      } else {
        filters.add(buildSimpleFilter(filterStr));
      }
    }

    return filters;
  }

  private List<String> buildSimpleFilter(String str) {

    // Check if the input string is empty or contains only whitespace
    if (str == null || str.trim().isEmpty()) {
      return null;
    }
    // "(\\w+)\\s*(<=|>=|<|!%|%|!=|>|=|#)\\s*((?:\\w+|\\S+))"
    String regex = "(\\w+)\\s*(<=|>=|<|!%|%|!=|>|=|#)\\s*([^@]+(?:@[^@]+)*)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(str);
    if (matcher.find()) {
      String left = matcher.group(1);
      String operator = matcher.group(2); // Add the operator
      String right = matcher.group(3);
      List<String> filter = new ArrayList<>();
      String odooOp = operatorMap.get(operator);
      filter.add(left);
      filter.add(odooOp);
      filter.add(right);
      // System.out.println("FILTER LIST " + filter);
      return filter;
    } else
      throw new IllegalArgumentException("Invalid filter part: " + str);
  }

  @Override
  public Result create(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {
    // TODO Auto-generated method stub
    List<Map<String, Object>> result = new ArrayList<>();
    System.out.println("DATA :" + data);
    System.out.println("Options :" + options);
    Connection connection = connector.getConnection();
    String password = (String) connection.getValue("password");
    String domainURL = (String) connection.getValue("domain");
    String database = (String) connection.getValue("database");
    // Configure XML-RPC client for object service
    Integer userId = (Integer) connection.getValue("userId");
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

    for (Object record : data) {
      Integer id = (Integer) models.execute("execute_kw", Arrays.asList(
          database, userId, password,
          this.ObjectId, "create",
          Arrays.asList(record)));

      Map<String, Object> idMap = new HashMap<>();
      {
        idMap.put("id", id);
      }
      result.add(idMap);
    }

    return new Result(result);
  }

  @Override
  public Result update(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {
    // TODO Auto-generated method stub
    // Result result = null;
    // TODO Auto-generated method stub
    // List<Map<String, Object>> result = new ArrayList<>();
    System.out.println("DATA :" + data);
    System.out.println("Options :" + options);
    Connection connection = connector.getConnection();
    String password = (String) connection.getValue("password");
    String domainURL = (String) connection.getValue("domain");
    String database = (String) connection.getValue("database");
    // Configure XML-RPC client for object service
    Integer userId = (Integer) connection.getValue("userId");
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

    for (Map<String, Object> record : data) {
      Integer recordId = (Integer) record.get("id");

      Boolean id = (Boolean) models.execute("execute_kw", Arrays.asList(
          database, userId, password,
          this.ObjectId, "write",
          List.of(
              List.of(recordId),
              record)));

      // Map<String, Object> idMap = new HashMap<>();
      // {
      // idMap.put("id", id);
      // }
      // result.add(idMap);
    }

    return new Result(data);
  }

  public Result delete(Map<String, Object> record) throws Exception {
    List<Map<String, Object>> result = new ArrayList<>();
    Connection connection = connector.getConnection();
    String password = (String) connection.getValue("password");
    String domainURL = (String) connection.getValue("domain");
    String database = (String) connection.getValue("database");
    // Configure XML-RPC client for object service
    Integer userId = (Integer) connection.getValue("userId");
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

    Integer recordId = (Integer) record.get("id");

    models.execute("execute_kw", Arrays.asList(
        database, userId, password,
        this.ObjectId, "unlink",
        List.of(List.of(recordId))));

    Map<String, Object> response = new HashMap<>();
    {
      response.put("id", recordId);
      response.put("deleted", true);
    }
    result.add(response);
    return new Result(result);
  }

  @Override
  public Result delete(Query query, Map<String, Object> options) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  @Override
  public Result save(List<Map<String, Object>> data, Map<String, Object> options) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

  @Override
  public long getCount(Query query, Map<String, Object> options) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCount'");
  }
  // https://www.odoo.com/forum/help-1/v8-what-is-the-full-list-of-available-term-operators-in-the-expression-domain-75580
  // Operators Supported

}
