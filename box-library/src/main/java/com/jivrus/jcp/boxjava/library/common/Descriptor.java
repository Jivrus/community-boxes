package com.jivrus.jcp.boxjava.library.common;

import java.util.HashMap;
import java.util.Map;

public class Descriptor implements Descriptive {

    public Map<String, Object> descriptor = new HashMap<>();

    public Descriptor(Descriptive desc) {
        this.descriptor = desc.getAll();
    }
    
    public Descriptor(Map<String, Object> descriptor) {
        this.descriptor = descriptor;
    }
    
    public Descriptor(String id) {
       descriptor.put("__id", id);
       descriptor.put("name", id);
    }
    
    public Descriptor(String id, String name) {
    	descriptor.put("__id", id);
    	descriptor.put("name", name);
    }

	// Setters
    public Descriptor setId(String id) {
        descriptor.put("__id", id);
        return this;
    }

    public Descriptor setCode(String code) {
        descriptor.put("code", code);
        return this;
    }

    public Descriptor setName(String name) {
        descriptor.put("name", name);
        return this;
    }

    public Descriptor setDescription(String description) {
        descriptor.put("description", description);
        return this;
    }

    public Descriptor setIcon(String icon) {
        descriptor.put("icon", icon);
        return this;
    }

    public Descriptor setLogo(String logo) {
        descriptor.put("logo", logo);
        return this;
    }

    public Descriptor setWebsite(String website) {
        descriptor.put("website", website);
        return this;
    }

    public Descriptor setHelp(String help) {
        descriptor.put("help", help);
        return this;
    }

    public Descriptor setNotes(String notes) {
        descriptor.put("notes", notes);
        return this;
    }

    public Descriptor setRequired(boolean required) {
        descriptor.put("required", required);
        return this;
    }

    public Descriptor setDefaultValue(Object defaultValue) {
        descriptor.put("defaultValue", defaultValue);
        return this;
    }

    // int, string, date, time, datetime etc
    public Descriptor setDataType(String dataType) {
        descriptor.put("dataType", dataType);
        return this;
    }

    //lineItem
    public Descriptor setSemanticType(String semanticType) {
        descriptor.put("semanticType", semanticType);
        return this;
    }

    public Descriptor setSemanticOptions(Map<String, Object> semanticOptions) {
        descriptor.put("semanticOptions", semanticOptions);
        return this;
    }

    // VARCHAR, TIMESTAMP
    public Descriptor setNativeType(String nativeType) {
        descriptor.put("nativeType", nativeType);
        return this;
    }

    public Descriptor setFilterable(boolean flag) {
        descriptor.put("filterable", flag);
        return this;
    }

    public Descriptor setSortable(boolean flag) {
        descriptor.put("sortable", flag);
        return this;
    }

    public Descriptor setWritable(boolean writable) {
        descriptor.put("writable", writable);
        return this;
    }

    public Descriptor setReadable(boolean readable) {
        descriptor.put("readable", readable);
        return this;
    }


    // Getters
    public String getId() {
        return (String) descriptor.getOrDefault("__id", null);
    }

    public String getCode() {
        return (String) descriptor.getOrDefault("code", null);
    }

    public String getName() {
        return (String) descriptor.getOrDefault("name", null);
    }

    public String getDescription() {
        return (String) descriptor.getOrDefault("description", null);
    }

    public String getIcon() {
        return (String) descriptor.getOrDefault("icon", null);
    }

    public String getLogo() {
        return (String) descriptor.getOrDefault("logo", null);
    }

    public String getWebsite() {
        return (String) descriptor.getOrDefault("website", null);
    }

    public String getHelp() {
        return (String) descriptor.getOrDefault("help", null);
    }

    public String getNotes() {
        return (String) descriptor.getOrDefault("notes", null);
    }

    public boolean isRequired() {
        return (boolean) descriptor.getOrDefault("required", false);
    }

    public Object getDefaultValue() {
        return descriptor.get("defaultValue");
    }

    public String getDataType() {
        return (String) descriptor.getOrDefault("dataType",
                (String) descriptor.getOrDefault("datatype", null));
    }

    public String getNativeType() {
        return (String) descriptor.get("nativeType");
    }

	@Override
	public boolean isFilterable() {
		return (boolean) descriptor.getOrDefault("filterable", false);
	}

	@Override
	public boolean isSortable() {
		return (boolean) descriptor.getOrDefault("sortable", false);
	}

	@Override
	public Descriptive setDefaultValue(String value) {
		descriptor.put("defaultValue", value);
		return this;
	}

	@Override
	public Object getValue(String name) {
		return descriptor.get(name);
	}

    @Override
	public Object getOrDefaultValue(String name, String defaultValue) {
		return descriptor.getOrDefault(name, defaultValue);
	}

    @Override
	public Object getOrDefaultValue(String name, Boolean defaultValue) {
		return descriptor.getOrDefault(name, defaultValue);
	}

	@Override
	public void setValue(String name, Object value) {
		descriptor.put(name, value);
	}

	@Override
	public Map<String, Object> getAll() {
		return this.descriptor;
	}
	
	public String toString() {
		return this.descriptor.toString();
	}
}
 
