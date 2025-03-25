/**
 * This is route handler for all box functions
 */
package com.jivrus.jcp.boxjava.library.gcf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.jivrus.jcp.boxjava.library.box.BoxGateway;

public class BoxRouteHandler implements RouteHandler {

	static Logger logger = Logger.getLogger(BoxRouteHandler.class.getName());

	@Override
	public void get(HttpRequest request, HttpResponse response, Map<String, String> context) throws IOException {

		String path = context.get("path");

		String json = "";
		boolean matches = false;
		Map<String, Object> errMap = null;
		String configRegex = "/api/box/([\\w.-]+)/config";
		String objectsRegex = "/api/box/([\\w.-]+)/objects";
		String functionsRegex = "/api/box/([\\w.-]+)/functions";
		String allFunctionsRegex = "/api/box/([\\w.-]+)/all/functions";
		String objectFunctionsRegex = "/api/box/([\\w.-]+)/([\\w.-]+)/functions";
		String objectGetFunctionRegex = "/api/box/([\\w.-]+)/([\\w.-]+)/function/([\\w.-]+)";
		logger.info("path matches allFunctionsRegex" + path.matches(allFunctionsRegex));
		try {
			if (path.matches(configRegex)) {
				matches = true;
				logger.info("path matches config");
				String boxId = getMatch(path, configRegex);
				JSONObject boxConfig = new JSONObject(BoxGateway.getConfig(boxId));

				// As boxConfig packing the object into all key, we are looping and getting that
				// out
				// Get the "attributes" array from the JSONObject
				JSONArray attributesArray = boxConfig.getJSONArray("attributes");
				JSONArray simplifiedArray = new JSONArray();
				// Loop through the attributes array
				for (int i = 0; i < attributesArray.length(); i++) {
					JSONObject attributeObject = attributesArray.getJSONObject(i);
					// packing inside descriptor as box nodejs is giving this structure
					JSONObject discriptorObject = new JSONObject().put("descriptor",
							attributeObject.getJSONObject("all"));
					simplifiedArray.put(discriptorObject);
				}
				JSONObject convertedConfig = new JSONObject().put("attributes", simplifiedArray);
				json = convertedConfig.toString();
			} else if (path.matches(objectsRegex)) {
				matches = true;
				logger.info("path matches objects");
				String boxId = getMatch(path, objectsRegex);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				logger.info(String.format("Box %s Token %s", boxId, boxConfigToken));
				json = new JSONObject(BoxGateway.getObjects(boxId, boxConfigToken)).toString();
			} else if (path.matches(functionsRegex)) {
				matches = true;
				logger.info("path matches functions");
				String boxId = getMatch(path, functionsRegex);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				logger.info(String.format("Box %s Token %s", boxId, boxConfigToken));
				json = new JSONObject(BoxGateway.getFunctions(boxId, boxConfigToken)).toString();
			} else if (path.matches(allFunctionsRegex)) {
				matches = true;
				logger.info("path matches all functions");
				String boxId = getMatch(path, allFunctionsRegex);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				logger.info(String.format("Box %s Token %s", boxId, boxConfigToken));
				json = new JSONObject(BoxGateway.getAllFunctions(boxId, boxConfigToken)).toString();
			} else if (path.matches(objectFunctionsRegex)) {
				matches = true;
				logger.info("path matches object functions");
				List<String> matchStrs = getMatches(path, objectFunctionsRegex);
				String boxId = matchStrs.get(0);
				String objectId = matchStrs.get(1);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				json = new JSONObject(BoxGateway.getObjectFunctions(boxId, objectId, boxConfigToken)).toString();
			} else if (path.matches(objectGetFunctionRegex)) {
				matches = true;
				logger.info("path matches object get function");
				List<String> matchStrs = getMatches(path, objectGetFunctionRegex);
				System.out.print("fId" + matchStrs);
				String boxId = matchStrs.get(0);
				String objectId = matchStrs.get(1);
				String fId = matchStrs.get(2);
				System.out.print("fId" + fId);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();

				JSONObject funConfig = new JSONObject(
						BoxGateway.getObjectFunction(boxId, objectId, fId, boxConfigToken));
				// As funConfig packing the object into all key, we are getting that out
				JSONObject discriptorObject = funConfig.getJSONObject("all");
				JSONObject result = new JSONObject(Map.of("result", discriptorObject));
				json = result.toString();
			}
		} catch (Exception e) {
			logger.severe("EXCEPTION: " + e);
			errMap = Map.of("message", e.getLocalizedMessage(), "status", HttpURLConnection.HTTP_BAD_REQUEST, "error", e.getMessage());
			e.printStackTrace();
		}

		BufferedWriter writer = response.getWriter();
		response.setContentType("application/json");

		if (errMap != null) {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
			writer.write(new JSONObject(errMap).toString());
		} else {
			if (matches) {
				logger.info("JSON: " + json);
				writer.write(json);
				response.setStatusCode(HttpURLConnection.HTTP_OK);
			} else {
				response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
				writer.write("{\"error\" : \"Http method not supported\"}");
			}
		}
	}

	private final static String getMatch(String input, String regex) {
		String result = null;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find() && matcher.groupCount() >= 1) {
			result = matcher.group(1);
		}
		return result;
	}

	private final static List<String> getMatches(final String input, final String regex) {
		List<String> matches = new ArrayList<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			int count = matcher.groupCount();
			for (int i = 1; i <= count; i++) {
				matches.add(matcher.group(i));
			}
		}
		return matches;
	}

	@Override
	public void post(HttpRequest request, HttpResponse response, Map<String, String> context) throws Exception {

		String path = context.get("path");
		String json = "";
		boolean matches = false;
		Map<String, Object> errMap = null;

		String bodyStr = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		if (bodyStr == null || bodyStr.isBlank())
			bodyStr = "{}";
		Map<String, Object> body = new JSONObject(bodyStr).toMap();

		String configRegex = "/api/box/([\\w.-]+)/config"; // support to [., -] in url path
		String functionRegex = "/api/box/([\\w.-]+)/([\\w.-]+)";
		String objectFunctionRegex = "/api/box/([\\w.-]+)/([\\w.-]+)/([\\w.-]+)";
		try {
			if (path.matches(configRegex)) {
				matches = true;
				logger.info("path matches config - post");
				String boxId = getMatch(path, configRegex);
				String token = BoxGateway.setConfig(boxId, body);
				json = new JSONObject(Map.of("boxConfigToken", token)).toString();
			} else if (path.matches(functionRegex)) {
				matches = true;
				logger.info("path matches function execution - post");
				List<String> matchStrs = getMatches(path, functionRegex);
				String boxId = matchStrs.get(0);
				String functionId = matchStrs.get(1);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				Map<String, Object> result = BoxGateway.executeFunction(boxId, functionId, boxConfigToken, body);

				// As result packing the object into all key, we are getting that out if all is there
				JSONObject jsonRes;
				jsonRes = new JSONObject(result);
				if (jsonRes.has("result") && jsonRes.get("result") instanceof JSONObject && ((JSONObject) jsonRes.get("result")).has("all")) {
					// Extract the "all" object
					JSONObject descriptorObject = jsonRes.getJSONObject("result").getJSONObject("all");
					// Wrap the extracted object in the "result" key
					json = new JSONObject(Map.of("result", descriptorObject)).toString();
				} else {
					// Directly assign
					json = jsonRes.toString();
				}
				// json = new JSONObject(result).toString();
			} else if (path.matches(objectFunctionRegex)) {
				matches = true;
				logger.info("path matches object function execution - post");
				List<String> matchStrs = getMatches(path, objectFunctionRegex);
				String boxId = matchStrs.get(0);
				String objectId = matchStrs.get(1);
				String functionId = matchStrs.get(2);
				Optional<String> boxConfigTokenOptional = request.getFirstHeader("boxConfigToken");
				if (!boxConfigTokenOptional.isPresent())
					throw new Exception("Access token missing");
				String boxConfigToken = boxConfigTokenOptional.get();
				json = new JSONObject(
						BoxGateway.executeObjectFunction(boxId, objectId, functionId, boxConfigToken, body))
						.toString();
			}
		} catch (Exception e) {
			logger.severe("EXCEPTION: " + e);
			errMap = Map.of("message", e.getLocalizedMessage(), "status", HttpURLConnection.HTTP_BAD_REQUEST, "error", e.getMessage());
			e.printStackTrace();
		}
		BufferedWriter writer = response.getWriter();
		response.setContentType("application/json");
		System.out.println("errMap: "+errMap);
		if (errMap != null) {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
			writer.write(new JSONObject(errMap).toString());
		} else {
			if (matches) {
				logger.info("JSON: " + json);
				writer.write(json);
				response.setStatusCode(HttpURLConnection.HTTP_OK);
			} else {
				response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
				writer.write("{\"error\" : \"Http method not supported\"}");
			}
		}

	}

	public static void main(String[] args) {
		String input = "/api/box/mysql/objects";
		String regex = "/api/box/([\\w.-]+)/([\\w.-]+)";
		System.out.println("Output : " + getMatches(input, regex));
		System.out.println("Output : " + getMatch(input, regex));
	}

}
