package com.jivrus.jcp.boxjava.library.common.query;

public class Relational extends Operator {

	public Relational(String code) {
		super(code);
	}
	
	public static Relational EQUAL = new Relational("=");
	public static Relational NOT_EQUAL = new Relational("<>");
	public static Relational LESS_THAN = new Relational("<");
	public static Relational GREATER_THAN = new Relational(">");
	public static Relational LESS_THAN_OR_EQUAL = new Relational("<=");
	public static Relational GREATOR_THAN_OR_EQUAL = new Relational(">=");
	public static Relational CONTAIN = new Relational("%");
	public static Relational NOT_CONTAIN = new Relational("!%");
}

