package com.jivrus.jcp.boxjava.common.query;

public class Logical extends Operator {

	public Logical(String code) {
		super(code);
	}
	
    public static Logical AND = new Logical("AND");
    public static Logical OR = new Logical("OR");
    
    public static String andNotation = ",";
    public static String orNotation = ";";
}
