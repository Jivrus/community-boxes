package com.jivrus.jcp.boxjava.library.gcf;

import java.util.Map;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

public interface RouteHandler {
	public void get(HttpRequest request, HttpResponse response, Map <String, String> context) throws Exception;
	public void post(HttpRequest request, HttpResponse response, Map <String, String> context) throws Exception;
}
