package com.jivrus.jcp.boxjava.common.query;

public class Page {

	public static final int DEFAULT = 100;

	private int number = 1;
	private Integer size = DEFAULT;
	private Integer total = -1;
	private Object nextPageToken;

	public Page() {
		// Take all default values
	}

	public Page(int number, Integer size, Integer total, Object nextPageToken) {
		this.number = number;
		this.size = size;
		this.total = total;
		this.nextPageToken = nextPageToken;
	}

	public Page(int number, int size) {
		this.number = number;
		this.size = size;
	}

	public int getNumber() {
		return number;
	}

	public Integer getSize() {
		return size;
	}

	public Integer getTotal() {
		return total;
	}

	public Object getNextPageToken() {
		return nextPageToken;
	}

	@Override
	public String toString() {
		return "{ number: " + number + ", size: " + size + ", total: " + total + ", nextPageToken:" + nextPageToken
				+ '}';
	}

	public void setNumber(int pageNumber) {
		this.number = pageNumber;

	}

	public void setSize(int size) {
		this.size = size;

	}
}
