package com.neptunusz.model.service;

import com.neptunusz.controller.Neptun;
import com.neptunusz.model.Subject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew
 * This class manages everything about the subjects
 */
public class SubjectService {
    private static final File FILE = new File("subjects.dat");
    private List<Subject> subjects = new ArrayList<>();

    /**
     *
     */
    public void saveToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
            oos.writeObject(subjects);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadFromFile() throws IOException, ClassNotFoundException {
        if (FILE.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
            this.subjects.addAll((List<Subject>) ois.readObject());
            ois.close();
        }
    }

    /**
     * @return
     */
    public List<Subject> getSubjects() {
        Collections.sort(subjects);
        return subjects;
    }

    /**
     * @param index
     * @return subject
     * @throws Exception
     */
    public Subject get(int index) throws Exception {
        if (subjects.size() <= index) throw new Exception();
        return subjects.get(index);
    }

    /**
     * Register all subjects with the given username and password
     *
     * @param username Neptun identifier
     * @param password Password
     */
    public void register(String username, String password) {

        Neptun neptun = new Neptun();

        boolean loggedIn = false;
        while(!loggedIn) {
            loggedIn = neptun.login(username, password);
        }

        for (Subject subject : subjects) {
            neptun.register(subject);
        }

        neptun.registeredSubjects();
    }
}