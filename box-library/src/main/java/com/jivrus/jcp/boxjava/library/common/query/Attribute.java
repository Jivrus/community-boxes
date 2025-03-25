package com.jivrus.jcp.boxjava.library.common.query;

import com.jivrus.jcp.boxjava.library.common.Descriptor;

public class Attribute extends Descriptor {

    public Attribute(String id, String name) {
        super(id, name);
    }

    public Attribute(String id) {
		super(id, id);
	}

	public String getId() {
        return super.getId();
    }

    public Descriptor setId(String id) {
        super.setId(id);
        return this;
    }

    public String getName() {
        return super.getName();
    }

    public Descriptor setName(String name) {
        super.setName(name);
        return this;
    }

}