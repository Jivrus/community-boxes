package com.jivrus.jcp.boxjava.common.query;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {

	private Object left;
	private Operator operator;
	private Object right;

	public Filter(Object left, Operator operator, Object right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Filter and(Filter filter) {
		return new Filter(this, Logical.AND, filter);
	}

	public Filter or(Filter filter) {
		return new Filter(this, Logical.OR, filter);
	}

	// Key function that constructs the filter
	public Object traverse(Object current, Filterable filterMaker, Formatable formatter) {
		if (current instanceof Filter) {
			// Process left operand
			Filter currentFilter = (Filter) current;
			Object left = currentFilter.left;
			String leftName;
			String dataType = "string";
			if (left instanceof Filter) {
				leftName = (String) this.traverse(left, filterMaker, formatter);
			} else if (left instanceof Attribute) {
				Attribute leftAttribute = (Attribute) left;
				leftName = leftAttribute.getName();
				if (formatter != null)
					leftName = formatter.formatAttribute(leftName);
				dataType = leftAttribute.getDataType();

			} else {
				leftName = "" + left;
			}

			// process right operand
			Object right = currentFilter.right;
			String rightName;


			//As right is coming as string, split with | and get the datatype
			if (right instanceof String) {
				Pattern pattern = Pattern.compile("([^|]+)\\s*\\|\\s*(\\w+)");
				Matcher matcher = pattern.matcher((String) right);
				
				if (matcher.find()) {
					//first string as right(value)
					right = matcher.group(1);
					//as datatype
					dataType = matcher.group(2);
				} 
			}

			if (right instanceof Filter) {
				rightName = (String) this.traverse(right, filterMaker, formatter);
			} else if (right instanceof Attribute) {
				rightName = ((Attribute) right).getName();
				if (formatter != null)
					rightName = formatter.formatAttribute(rightName);
			} else if (right instanceof Date || dataType.equals("date") || right instanceof String) {
				rightName = (formatter != null) ? (String) formatter.formatValue(right, dataType, null)
						: (String) right;
			} else if (right instanceof Number || right instanceof Boolean) {
				rightName = "" + right;
			} else {
				rightName = "" + right;
			}

			// Combine it with
			String buildResult = filterMaker == null ? leftName + operator + rightName
					: filterMaker.buildFilter(leftName, operator, rightName);

			return buildResult;

		} else if (current instanceof Attribute) {
			return ((Attribute) current).getName();
		} else {
			return (String) current;
		}
	}

	public String toString() {
		return (String) this.traverse(this, null, null);
	}

}
