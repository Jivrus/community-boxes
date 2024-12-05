package com.jivrus.jcp.boxjava.connector;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.text.StringSubstitutor;

import com.jivrus.jcp.boxjava.common.query.Result;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseUtility {
	static Logger logger = Logger.getLogger(DatabaseUtility.class.getName());
	private static Map<String, HikariDataSource> dsMap = new HashMap<>();

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// String databaseType = "sqlite";
		// String host = "192.168.1.37";
		// String port = "22";
		// String databaseName = "testdb.db";
		// String username = "BeSt"; // MySQL credentials
		// String password = "2301";

		// String driverName = "";

		// Map descriptor = Map.of("databaseType", databaseType, "host", host, "port", port, "databaseName", databaseName,
		// 		"username", username, "password", password, "driverName", driverName);
		// Connection connection = new Connection(descriptor);
		// System.out.println("In print");
		// String query = "select * from example_table"; // query to be run

		// executeQuery(connection, query, null);

		System.out.println("In print");
		String databaseType = "oracle";
		String host = "database-3.c2djb5rbn5rs.us-east-2.rds.amazonaws.com";
		String port = "3306";
		String databaseName = "testdb";
		String username = "admin"; // MySQL credentials
		String password = "12345678";

		String driverName = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@database-5.c2djb5rbn5rs.us-east-2.rds.amazonaws.com:1521/testdb";

		System.out.println(String.format("The url: %s Driver: %s", url, driverName));

		// System.out.println("box id in" + connection.getValue("name") + "driver" + connection.getValue("driver"));

		if(driverName != "") Class.forName(driverName); // Driver name
		java.sql.Connection con = null;
		boolean useConnectionPool = (boolean) true;//connection.getOrDefaultValue("__useconnectionpool", true);; // TODO - test fully if there is any side effect and tune Connection Pooling
		// if (useConnectionPool) {
			con = getDataSource(url, username, password).getConnection();
		// } else {
		// 	con = DriverManager.getConnection(url, username, password);
		// }

		System.out.println("Connection Established successfully");
		String query = "select * from user"; // query to be run

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query); // Execute query
		ResultSetMetaData rsMeta = rs.getMetaData();

		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		while (rs.next()) {
			// System.out.println("res"+ rs);
			Map<String, Object> row = new LinkedHashMap<String, Object>();
			for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
				String columnName = rsMeta.getColumnLabel(i + 1);
				Object value = null;
				try {
					value = rs.getObject(i + 1);
				} catch (Exception e) {
					logger.warning("Could not get value for " + columnName);
					// e.printStackTrace();
				}
				row.put(columnName, value);
			}
			rows.add(row);
		}

		System.out.println("Rows: " + rows.size());
		st.close(); // close statement
		con.close(); // close connection
		System.out.println("Connection Closed....");
	}

	static java.sql.Connection getSQLConnection(Connection connection)
			throws ClassNotFoundException, SQLException {

		// String databaseType = (String) connection.getId();
		// String host = (String) connection.getValue("host");
		// String port = (String) connection.getValue("port");
		// String databaseName = (String) connection.getValue("database");
		String username = (String) connection.getValue("user");
		String password = (String) connection.getValue("password");
		
		String templateUrl = (String) connection.getValue("__sql_connect_url");

		String driverName = (String) connection.getOrDefaultValue("driver", "");

		StringSubstitutor substitutor = new StringSubstitutor(connection.getAll());
		String url = substitutor.replace(templateUrl);

		System.out.println("Connect url: " + url);

		// String url = "jdbc:" + databaseType + "://" + host + ":" + port + "/" + databaseName;
		// String url = "jdbc:oracle:thin:@database-5.c2djb5rbn5rs.us-east-2.rds.amazonaws.com:1521/TESTDB";

		System.out.println(String.format("The url: %s Driver: %s", url, driverName));

		// System.out.println("box id in" + connection.getValue("name") + "driver" + connection.getValue("driver"));

		if(driverName != "") Class.forName(driverName); // Driver name
		java.sql.Connection con = null;
		boolean useConnectionPool = (boolean) connection.getOrDefaultValue("__useconnectionpool", true);; // TODO - test fully if there is any side effect and tune Connection Pooling
		if (useConnectionPool) {
			con = getDataSource(url, username, password).getConnection();
		} else {
			con = DriverManager.getConnection(url, username, password);
		}

		System.out.println("Connection Established successfully");
		return con;

	}

	private static HikariDataSource getDataSource(String url, String username, String password) {
		String urlUserPass = url + ":" + username + ":" + password;
		HikariDataSource ds = dsMap.get(urlUserPass);
		if (ds == null || ds.isClosed()) {
			System.out.println("**** Making new Connection Pool ****");
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			config.setMaximumPoolSize(2); 
        	config.setMinimumIdle(2);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			// TODO - make more fine tuning by setting up properties

			ds = new HikariDataSource(config);
			dsMap.put(urlUserPass, ds);
		}

		return ds;
	}

	public static Result executeQuery(Connection connection, String sql) throws SQLException, ClassNotFoundException {
		
		Result result =  executeQuery(connection, sql, null);
		return result;
	}

	public static Result executeQuery(Connection connection, String sql, Map<String, Object> values)
			throws SQLException, ClassNotFoundException {
		logger.info(String.format("[SQL][Query]: %s", sql));
		java.sql.Connection con = getSQLConnection(connection);
		logger.info(String.format("[SQL][Query]: connected"));
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql); // Execute query
		ResultSetMetaData rsMeta = rs.getMetaData();

		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		
		while (rs.next()) {
			// System.out.println("res"+ rs);
			Map<String, Object> row = new LinkedHashMap<String, Object>();
			for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
				String columnName = rsMeta.getColumnLabel(i + 1);
				Object value = null;
				try {
					value = rs.getObject(i + 1);
				} catch (Exception e) {
					logger.warning("Could not get value for " + columnName);
					// e.printStackTrace();
				}
				row.put(columnName, value);
			}
			rows.add(row);
		}

		System.out.println("Rows: " + rows.size());
		st.close(); // close statement
		con.close(); // close connection
		System.out.println("Connection Closed....");
		Result result = new Result(rows);
		return result;
	}

	public static Result executeBatch(Connection connection, String sql, List<List<Object>> parameters)
			throws SQLException, ClassNotFoundException {
		logger.info(String.format("[SQL][Batch]: %s Vaues: %s", sql, parameters));
		java.sql.Connection con = getSQLConnection(connection);
		con.setAutoCommit(false);
		PreparedStatement statement = con.prepareStatement(sql);

		// Loop through parameters and set them in the statement
		for (List<Object> paramSet : parameters) {
			for (int i = 0; i < paramSet.size(); i++) {
				statement.setObject(i + 1, paramSet.get(i));
			}
			statement.addBatch();
		}

		// Execute batch and handle errors
		Result result = null;
		try {
			int values[] = statement.executeBatch();
			con.commit();
			logger.info("Resulted values " + Arrays.toString(values));
			result = new Result();
			List<Map<String, Object>> statusList = new ArrayList<>();
			for (int element : values) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", element);
				statusList.add(map);
			}
			result.setStatus(statusList);
		} catch (SQLException e) {
			e.printStackTrace();
			// Extract error information
			int failedStatementIndex = e.getErrorCode();
			String errorMessage = e.getMessage();

			// Handle specific error based on index and message
			System.out.println("Error occurred at statement index " + failedStatementIndex);
			System.out.println("Error message: " + errorMessage);

			// Rollback if there is an error
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw e1;
			}
			throw e;
		} finally {
			statement.close();
			con.close();
		}
		return result;
	}

	public static Result executeUpdate(Connection connection, String sql, Map<String, Object> record)
			throws ClassNotFoundException, SQLException {
		logger.info(String.format("[SQL][Update]: %s", sql));
		java.sql.Connection con = getSQLConnection(connection);

		Statement st = con.createStatement();
		int count = st.executeUpdate(sql); // Execute query
		ResultSet rs = st.getResultSet();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		if (rs != null) {
			ResultSetMetaData rsMeta = rs.getMetaData();

			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
					String columnName = rsMeta.getColumnLabel(i + 1);
					Object value = null;
					try {
						value = rs.getObject(i + 1);
					} catch (Exception e) {
						logger.warning("Could not get value for " + columnName);
						// e.printStackTrace();
					}
					row.put(columnName, value);
				}
				rows.add(row);
			}
		}

		System.out.println("Rows: " + rows.size());
		st.close(); // close statement
		con.close(); // close connection
		System.out.println("Connection Closed....");

		Result result = new Result(rows);
		result.setTotalCount(count);
		return result;
	}

	public static Map<String, Object> formatRecord(Map<String, Object> record) {
        Map<String, Object> result = new HashMap<>();
        for (String key : record.keySet()) {
            Object element = record.get(key);
            try {
                if (element instanceof String && ((String) element).contains("|")) {
                    String[] splited = ((String) element).split("\\|");
                    String value = splited[0];
                    String dataType = splited[1];
                    Object formattedValue = formatValueForType(dataType, value);
                    result.put(key, formattedValue);
                } else if (element instanceof List<?>) {
                    List<Object> array = new ArrayList<>();
                    for (Object obj : (List<?>) element) {
                        Object recordObj = null;
                        if (obj instanceof List<?>) {
                            recordObj = new ArrayList<>(formatRecord((Map<String, Object>) obj).values());
                        } else if (obj instanceof Map<?, ?>) {
                            recordObj = formatRecord((Map<String, Object>) obj);
                        } else {
                            recordObj = obj;
                        }
                        array.add(recordObj);
                    }
                    result.put(key, array);
                } else if (element instanceof Map<?, ?>) {
                    result.put(key, formatRecord((Map<String, Object>) element));
                } else {
                    result.put(key, element);
                }
            } catch (Exception e) {
                result.put(key, element);
            }
        }
        return result;
    }

    public static Object formatValueForType(String dataType, String value) {
        Object result = value;
        switch (dataType) {
            case "date":
                result = (value.equals("null")) ? null : value.substring(0, 10);
                break;
            case "datetime":
                // result = (value.equals("null")) ? null : value;
				LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        		result = localDateTime.format(formatter);
                break;
            case "number":
                result = Double.parseDouble(value);
                break;
            default:
                result = value;
                break;
        }
        return result;
    }
}
