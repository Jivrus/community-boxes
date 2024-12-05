package com.jivrus.jcp.boxjava.common.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Result {

	private List<Map<String, Object>> data = new ArrayList<>();
	private Object dataObject;
	private Page page = new Page();
	private int totalCount = 0;
	private int successCount = 0;
	private int failedCount = 0;
	private Object derivationData; //TODO - not sure the use of this. Can this be removed?
	private List<Map<String, Object>> statusList = new ArrayList<>();

	public Result(List<Map<String, Object>> data, Page page, int totalCount, Object derivationData) {
		this.data = data;
		this.page = page;
		this.totalCount = totalCount;
		this.derivationData = derivationData;
	}

	public Result(Object dataObject) {
		this.dataObject = dataObject;
	}

	public Result(List<Map<String, Object>> data) {
		this.data = data;
		page.setSize(data.size());
	}

	public Result() {
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public Object getDataObject(){
		return dataObject;
	}

	public Page getPage() {
		return page;
	}

	public int getTotalCount() {
		return totalCount;
	}

	// TODO - not sure where is this used
	public Object getDerivationData() {
		return derivationData;
	}

	@Override
	public String toString() {
		return "{" + "data: " + data + ", page: " + page + ", totalCount: " + totalCount + ", derivationData:"
				+ derivationData + ", statusList: " + statusList + ", successCount: " + successCount + ", failedCount: " + failedCount + '}';
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
		
	}

	public void setStatus(List<Map<String, Object>> statusList) {
		this.statusList  = statusList;
	}
	public void setTotalCount(int count) {
		totalCount = count;
	}

	public void setSuccessCount(int count) {
		successCount = count;
	}

	public void setFailedCount(int count) {
		failedCount = count;
	}
}
