package com.jivrus.jcp.boxjava.common.query;

public abstract class Operator {
	private String code;

	public Operator(String code) {
		this.code = code;
	}
	
	public String toString() {
		return code;
	}
}
