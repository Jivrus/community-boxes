package com.jivrus.jcp.boxjava.util;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtil {

	public static Map setValue(Map map, String path, Object value) {
		String[] tokens = path.split(".");
		Map current = map;
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			Object valueObj = current.get(token);
			if (valueObj == null) {
				valueObj = new HashMap<String, Object>();

			}
		}
		return map;
	}

	public static Object getValue(Map<String, Object> map, String path) {

		// Check for direct match
		Object result = map.get(path);
		if (result != null)
			return result;

		return getValueByPath(map, path);
	}

	private static Object getValueByPath(Map<String, Object> map, String path) {
		String[] pathParts = path.split("\\.");
		Map<String, Object> currentMap = map;
		
		for (int i = 0; i < pathParts.length; i++) {
			String key = pathParts[i];
			Object value = currentMap.get(key);

			if (value instanceof Map) {
				currentMap = (Map<String, Object>) value;
			} else if (i == pathParts.length - 1) {
				// Reached the end of the path, return the value
				return value;
			} else {
				// Path not found
				return null;
			}
		}

		// Path not found
		return null;
	}
	public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("level1", new HashMap<String, Object>() {{
            put("level2a", "value2a");
            put("level2b", new HashMap<String, Object>() {{
                put("level3", "value3");
            }});
        }});

        String path1 = "level1.level2a";
        String path2 = "level1.level2b.level3";
        String path3 = "level1.level2c"; // Path not found

        System.out.println(getValueByPath(data, path1)); // Output: value2a
        System.out.println(getValueByPath(data, path2)); // Output: value3
        System.out.println(getValueByPath(data, path3)); // Output: null
    }

}
