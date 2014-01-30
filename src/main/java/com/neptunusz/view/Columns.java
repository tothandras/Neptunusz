package com.neptunusz.view;

import com.neptunusz.model.SubjectType;

/**
 * This enum contains all information about the columns
 */
public enum Columns {
    NAME("Név", String.class), CODE("Kód", String.class), TYPE("Típus", SubjectType.class), COURSES("Kurzusok", String.class), PRIORITY("Prioritás", Integer.class), ENROLL("Felvesz", Boolean.class),;

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
