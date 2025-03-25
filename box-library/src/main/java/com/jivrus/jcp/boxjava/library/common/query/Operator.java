package com.jivrus.jcp.boxjava.library.common.query;

public abstract class Operator {
	private String code;

	public Operator(String code) {
		this.code = code;
	}
	
	public String toString() {
		return code;
	}
}
