package com.jivrus.jcp.boxjava.gcf;

import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

public class CloudFunction implements HttpFunction {
	static final List<Map<String, Object>> users = new ArrayList<>();
	static Logger logger = Logger.getLogger(CloudFunction.class.getName());
	
	
	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		// Set CORS headers to allow requests from any origin
		response.appendHeader("Access-Control-Allow-Origin", "*");
		response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, boxConfigToken, eventtoken, traceid, Cache-Control");

		// Handle preflight OPTIONS requests
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatusCode(HttpURLConnection.HTTP_OK);
			return;
		}
	

		// Read Request
		String path = request.getPath();
		String contentType = request.getContentType().orElse("");
		String method = request.getMethod();
		Map<String, List<String>> params = request.getQueryParameters();
		logger.info(String.format("Method is %s Path is %s Content type is ", method, path, contentType));
		
		// Validate
		if (!"application/json".equals(contentType)) {
			BufferedWriter writer = response.getWriter();
			writer.write("Invalid or missing Content-Type header");
			response.setStatusCode(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
			return;
		}
		
		
		// Route to handler
		List <String> routes = new ArrayList<String>();
		routes.add("/api");
		Map <String, RouteHandler> routesMap = new HashMap<String, RouteHandler>();
		routesMap.put("/api", new BoxRouteHandler());
		
		for (int i = 0; i < routes.size(); i++) {
			String route = routes.get(i);
			if (path.startsWith(route)) {
				RouteHandler handler = routesMap.get(route);
				if (handler != null) {
					Map <String, String> context = new HashMap();
					context.put("method", method);
					context.put("route", route);
					context.put("path", path);
					
					switch (method) {
						case "GET":
							handler.get(request, response, context);
							break;
						case "POST":
							handler.post(request, response, context);
							break;
					}
					break;
				}
			}
		}
		
		
		
/*
		

		Gson gson = new Gson();

		switch (method) {
		case "GET":
			// Get all users
			String json = gson.toJson(users);
			System.out.println("JSON: " + json);
			writer.write(json);
			response.setStatusCode(HttpURLConnection.HTTP_OK);
			break;
		case "POST":
			// Create user
			
			Map<String, Object> newUser = gson.fromJson(request.getReader(), Map.class);
			users.add(newUser);
			response.setStatusCode(HttpURLConnection.HTTP_CREATED);
			break;
		case "PUT":
			// Update user
			if (!"application/json".equals(contentType)) {
				writer.write("Invalid or missing Content-Type header");
				response.setStatusCode(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
				break;
			}
			String userToUpdate = path.replace("/", "");
			writer.write("Unable to update non-existend user - " + userToUpdate);
			response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
			break;
		case "DELETE":
			String userToRemove = path.replace("/", "");

			writer.write("Unable to delete non-existend user " + userToRemove);
			response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
			break;
		default:
			response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
			writer.write("Http method not supported");
			break;
		}
		*/
	}
}