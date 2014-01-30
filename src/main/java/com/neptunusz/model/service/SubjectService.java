package com.neptunusz.model.service;

import com.neptunusz.model.Subject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Andrew on 1/28/14.
 * This class manages everything about the subjects
 */
public class SubjectService {
    private static final File FILE = new File("subjects.dat");
    private List<Subject> subjects = new ArrayList<Subject>();

    public void saveToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
            oos.writeObject(subjects);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() throws IOException, ClassNotFoundException {
        if (FILE.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
            this.subjects.addAll((List<Subject>) ois.readObject());
            ois.close();
        }
    }

    public List<Subject> getSubjects() {
        Collections.sort(subjects, new Comparator<Subject>() {
            @Override
            public int compare(Subject o1, Subject o2) {
                return ((Integer)o2.getPriority()).compareTo(o1.getPriority());
            }
        });
        return subjects;
    }

    public Subject get(int index) throws Exception {
        if (subjects.size() <= index) throw new Exception();
        return subjects.get(index);
    }
}