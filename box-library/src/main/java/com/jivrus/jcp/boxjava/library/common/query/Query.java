package com.jivrus.jcp.boxjava.library.common.query;

import java.util.List;

public class Query {

    private List<Attribute> attributes;
    public  Filter filter;
    private Sort sort;
    private Page page;
    private List<Attribute> group;

    public Query(List<Attribute> attributes, Filter filter, Sort sort, Page page, List<Attribute> group) {
        this.attributes = attributes;
        this.filter = filter;
        this.sort = sort;
        this.page = page;
        this.group = group;
    }
    
    public Query(List<Attribute> attributes, Filter filter, Sort sort, Page page) {
        this.attributes = attributes;
        this.filter = filter;
        this.sort = sort;
        this.page = page;
    }
    
    public Query(List<Attribute> attributes, Filter filter, Sort sort) {
        this.attributes = attributes;
        this.filter = filter;
        this.sort = sort;
    }
    
    public Query(List<Attribute> attributes, Filter filter) {
        this.attributes = attributes;
        this.filter = filter;
    }
    
    public Query(Filter filter) {
        this.filter = filter;
    }


    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Attribute> getGroup() {
        return group;
    }

    public void setGroup(List<Attribute> group) {
        this.group = group;
    }
    
    public String toString() {
    	return "{ attributes: " + attributes + ", filter: " + filter + ", sort: " + sort + ", group = " + group + "}";
    }
}
