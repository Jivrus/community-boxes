package com.jivrus.jcp.boxjava.common.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jivrus.jcp.boxjava.util.CollectionUtil;

public class QueryMaker implements Maker {
	public QueryMaker() {
		
	}
	@Override
	public Object make(String name, Map<String, Object> data, Map<String, Object> options) {
		// Attributes
		String attributesStr = (String) CollectionUtil.getValue(data, name + ".attributes");
		List<Attribute> attributes = attributesStr == null ? null : buildAttributes(attributesStr); 
				
		// Filter
		String filterStr = (String) CollectionUtil.getValue(data, name + ".filter");
		Filter filter = filterStr == null ? null : buildFilter(filterStr);
		
		// Sort
		String sortStr = (String) CollectionUtil.getValue(data, name + ".sort");
		Sort sort = sortStr == null ? null : buildSort(sortStr);

		// Page
		String pageStr = (String) CollectionUtil.getValue(data, name + ".page");
		Page page = pageStr == null ? null : buildPage(pageStr);

		// Group
		String groupStr = (String) CollectionUtil.getValue(data, name + ".group");
		List<Attribute> group = groupStr == null ? null : buildGroup(groupStr);

		return new Query(attributes, filter, sort, page, group);
	}

	private static List<Attribute> buildAttributes(String attributesStr) {
		return Arrays.stream(attributesStr.split(",")).map(Attribute::new).collect(Collectors.toList());
	}

	// private static final String[] OPERATORS = { "<=", ">=", "<", "!%", "%", "!=", ">", "=", "#" };

	


	private Filter buildFilter(String filterStr) {

		// Handle "AND" conditions and "OR" conditions
		if (filterStr.contains(Logical.andNotation)) { // TODO - make the andNotiation stronger
			String[] andParts = filterStr.split(Logical.andNotation);
			Filter andFilter = null;
			for (String andPart : andParts) {
				Filter partFilter = buildFilter(andPart);
				andFilter = (andFilter == null) ? partFilter : andFilter.and(partFilter);
			}
			return andFilter;
		} else if (filterStr.contains(Logical.orNotation)) { // TODO - make the orNotiation stronger
			String[] andParts = filterStr.split(Logical.orNotation);
			Filter orFilter = null;
			for (String andPart : andParts) {
				Filter partFilter = buildFilter(andPart);
				orFilter = (orFilter == null) ? partFilter : orFilter.or(partFilter);
			}
			return orFilter;
		} else {
			return buildSimpleFilter(filterStr);
		}
	}

	private Filter buildSimpleFilter(String str) {
		
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
			
			return new Filter(left, new Relational(operator), right);
		} else
			throw new IllegalArgumentException("Invalid filter part: " + str);
	}

	public Sort buildSort(String sortStr) {
		
		List<String> sortFields = Arrays.asList(sortStr.split(","));
		Sort sort = null;

		for (String field : sortFields) {
			String[] pair = field.split("=");

			// Check if pair has expected length 2
			if (pair.length == 2) {
				String attribute = pair[0];
				String order = pair[1];
	
				Sort newSort = new Sort(new Attribute(attribute), order);
				sort = (sort == null) ? newSort : sort.add(new Attribute(attribute), order);
			}
		}

		return sort;
	}

	public Page buildPage(String pageStr) {
		String[] pageParams = pageStr.split("\\|");
		int number = pageParams.length > 0 ? Integer.parseInt(pageParams[0]) : 1;
		Integer size = pageParams.length > 1 ? Integer.parseInt(pageParams[1]) : null;
		Integer total = pageParams.length > 2 ? Integer.parseInt(pageParams[2]) : null;
		String nextPageToken = pageParams.length > 3 ? pageParams[3] : null;

		return new Page(number, size, total, nextPageToken);
	}

	private List<Attribute> buildGroup(String groupStr) {
		List<Attribute> groups = new ArrayList<>();
		String[] groupParts = groupStr.split(",");

		for (String groupPart : groupParts) {
			groups.add(new Attribute(groupPart));
		}

		return groups;
	}

}
