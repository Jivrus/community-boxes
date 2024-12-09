package com.jivrus.jcp.boxjava.common.query;

import java.util.Map;

import com.jivrus.jcp.boxjava.box.BoxFunction;

public class ParameterMaker {
	 
	public static Map<String, Object> makeParams(BoxFunction func, Map<String, Object> data) {
		//TODO - dummy function but may require expansion in the future
		Map<String, Object> result = (Map<String, Object>) data.get("parameters");
		if (result == null) result = data;  // fallback on full data if parameters is not sent
		return result;
	}
 
/*    public static List<Object> make(Map<String, Object> input, Map<String, Object> data, Map<String, Object> options) {
        List<Object> result = new ArrayList<>();

        // Logging statement equivalent to TypeScript's console.log
        System.out.printf("[BOX]: Parameter Maker: Input: %s Data: %s Options: %s%n", input, data, options);

        if (input != null && input.containsKey("list")) {
            List<String> paramList = (List<String>) input.get("list");
            for (String param : paramList) {
                Object paramResult = null;
                Map<String, Object> paramDetails = (Map<String, Object>) input.get(param);
                if (paramDetails != null) {
                    paramResult = getParamResult(paramDetails, data, options);
                }
                result.add(paramResult);
            }
        }

        // Add the function options as the last parameter
        if (options != null) {
            result.add(options);
        }

        // Logging statement equivalent to TypeScript's console.log
        System.out.println("All Parameters: " + result);
        return result;
    }

    public static Object getParamResult(Map<String, Object> paramDetails, Map<String, Object> data, Map<String, Object> options) {
        // Logging statements equivalent to TypeScript's console.log
        System.out.println("[PARAMETER MAKER] getParamResults() paramDetails: " + paramDetails);
        System.out.println("[PARAMETER MAKER] getParamResults() data: " + data);
        System.out.println("[PARAMETER MAKER] getParamResults() options: " + options);

        Object paramResult = null;
        String paramName = (String) paramDetails.getOrDefault("__id", paramDetails.getOrDefault("name", null));
        String paramType = (String) paramDetails.get("dataType");
        Map<String, Object> paramOptions = (Map<String, Object>) paramDetails.getOrDefault("options", new HashMap<>());
        Map<String, Object> finalParamOptions = new HashMap<>(options); // Create a copy to avoid modifying original
        finalParamOptions.putAll(paramOptions); // Merge in paramOptions (order might need adjustment based on requirements)

        if (paramType != null) {
            // Implement getMaker() function to retrieve appropriate maker based on paramType
            // Assuming a method getMaker(String paramType) is available
            Function maker = getMaker(paramType);
            if (maker != null) {
                paramResult = maker.apply(paramName, data, finalParamOptions, paramDetails);
                // Logging statement equivalent to TypeScript's console.log
                System.out.printf("Param Name: %s Param Result: %s%n", paramName, paramResult);
            }
        }
        return paramResult;
    } */
}
