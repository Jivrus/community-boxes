package com.jivrus.jcp.boxjava;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.jivrus.jcp.boxjava.box.BoxGateway;

import static org.testng.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MySQLTest {
    private String boxId = "mysql";
    private String token = "";
    
    public static final HashMap<String, Object> configMap = new HashMap<>();
    Map<String, Object> config = new HashMap<>();

    @BeforeClass
    public void setUp() {
        System.out.println("MySQL test");
        // Map<String, Object> config = new HashMap<String, Object>();
        configMap.put("host", "database-3.c2djb5rbn5rs.us-east-2.rds.amazonaws.com");
        configMap.put("user", "admin");
        configMap.put("password", "12345678");
        configMap.put("database", "testdb");
        configMap.put("port", "3306");
        
        config.put("config", configMap);

        try {
            System.out.println("MySQL test" + config);
            token = BoxGateway.setConfig(boxId, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        // Clean up operations if needed
    }

    @Test
    public void testGetBoxConfigToken() {
        try {
            System.out.println("calling MySQL config token");
            String token = BoxGateway.setConfig(boxId, config);
            assertNotNull(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBoxConfig() {
        Map configParams = BoxGateway.getConfig(boxId);
        assertNotNull(configParams);
    }

    @Test
    public void testGetBox() {
        com.jivrus.jcp.boxjava.box.Box box = BoxGateway.getBox(boxId);
        String boxId = box.getId();
        System.out.println("boxId --: " + boxId);
        assertNotNull(boxId);
    }

    @Test
    public void testGetBoxObjects() {
        Map<String, Object> objects;
        try {
            objects = BoxGateway.getObjects(boxId, token);
            System.out.println("objects"+objects);
            assertTrue(objects.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @Test
    public void testGetBoxObjectFunctions() throws Exception {
        Map<String, Object> functions = BoxGateway.getObjectFunctions(boxId, "tickets", token);
        assertTrue(functions.size() > 0);
    }

    

    @Test
    public void testGetBoxFunctions() throws Exception {
        Map<String, Object> functions = BoxGateway.getFunctions(boxId, token);
        assertTrue(functions.size() > 0);
    }

    @Test
    public void testGetAttributes() {
        Map<String, Object> result;
        try {
            result = BoxGateway.executeFunction(boxId, "getattributes", token, Collections.singletonMap("object", "tickets"));
            System.out.println("result: " + result);
            assertNotNull(result);
        } catch (Exception e) {
            System.out.println("e: " + e);
            e.printStackTrace();
        }
        
    }

    @Test
    public void testGetData() {
        HashMap<String, Object> query = new HashMap<>();
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> option = new HashMap<>();
        query.put("filter", "");
        query.put("page", "1|100|1");
        
        config.put("query", query);
        config.put("option", option);

        // Add options if needed
        Map<String, Object> result3;
        System.out.println("config" + config);
        try {
            
            result3 = BoxGateway.executeObjectFunction(boxId, "tickets", "get", token, config);
            System.out.println("result3" + result3);
            assertNotNull(result3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
