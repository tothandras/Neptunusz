package com.neptunusz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subject implements Serializable {
    private String name;
    private String code;
    private SubjectType type;
    private List<String> courses;
    private boolean register;

    public Subject(String name, String code, SubjectType type) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.register = true;
        this.courses = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public SubjectType getType() {
        return type;
    }

    public List<String> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public void addCourse(String course) {
        courses.add(course);
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Subject && this.code.equals(((Subject)obj).code);
    }

    @Override
    public String toString() {
        return "Subject{name: " + name + ", code: " + code + ", type: " + type + "}";
    }
}
