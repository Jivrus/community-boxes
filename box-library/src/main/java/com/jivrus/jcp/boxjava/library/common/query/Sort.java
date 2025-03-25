package com.jivrus.jcp.boxjava.library.common.query;

import java.util.ArrayList;
import java.util.List;

public class Sort {

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    

    private List<SortCriteria> sortBy = new ArrayList<>();

    public Sort(Attribute attribute, String order) {
        this.sortBy.add(new SortCriteria(attribute, order));
    }

    public Sort add(Attribute attribute, String order) {
        this.sortBy.add(new SortCriteria(attribute, order));
        return this;
    }

    public List<SortCriteria> getSortBy() {
        return sortBy;
    }

    public static class SortCriteria {

        private Attribute attribute;
        private String order;

        public SortCriteria(Attribute attribute, String order) {
            this.attribute = attribute;
            this.order = order;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public String getOrder() {
            return order;
        }
        public String toString() {
        	return "{ attribute: " + attribute + ", order: " + order + "}";
        }
    }

    @Override
    public String toString() {
        return "{ sortBy: " + sortBy + '}';
    }
}
