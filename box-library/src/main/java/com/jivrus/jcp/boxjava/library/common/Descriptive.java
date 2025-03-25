package com.jivrus.jcp.boxjava.library.common;

import java.util.Map;

public interface Descriptive {

    // Getter
    String getId();
    String getCode();
    String getName();
    String getDescription();
    String getIcon();
    String getLogo();
    String getWebsite();
    String getHelp();
    String getNotes() ;
    boolean isRequired();
    Object getDefaultValue();
    String getDataType();
    boolean isFilterable();
    boolean isSortable();

    // Setter
    Descriptive setId(String id);
    Descriptive setCode(String id);
    Descriptive setName(String id);
    Descriptive setDescription(String id);
    Descriptive setIcon(String id);
    Descriptive setLogo(String id);
    Descriptive setWebsite(String id);
    Descriptive setHelp(String id);
    Descriptive setNotes(String id);
    Descriptive setRequired(boolean required);
    Descriptive setDefaultValue(String value);
    Descriptive setDataType(String dataType);
    Descriptive setNativeType(String nativeType);
    Descriptive setFilterable(boolean filterable);
    Descriptive setSortable(boolean sortable);
    Descriptive setWritable(boolean writable);
    Descriptive setReadable(boolean readable);

    // Generic Getter and Setter
    Object getValue(String name);
    void setValue(String name, Object value);

    Object getOrDefaultValue(String name, String defaultValue);
    Object getOrDefaultValue(String name, Boolean defaultValue);

    Map<String, Object> getAll();
}
