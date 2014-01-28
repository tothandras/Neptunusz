package com.neptunusz.view;

import com.neptunusz.model.SubjectType;

/**
 * Created by Andrew on 1/28/14.
 * This enum contains all information about the columns
 */
public enum Columns {
    NAME("", String.class), CODE("", String.class), TYPE("", SubjectType.class), CUORSES("", String.class), ENROLL("", Boolean.class);

    private String name;
    private Class clazz;

    Columns(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * The name of the column
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The class of the column
     *
     * @return the class
     */
    public Class getClazz() {
        return clazz;
    }
}
