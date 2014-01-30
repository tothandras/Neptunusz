package com.neptunusz.model.service;

import com.neptunusz.controller.Neptun;
import com.neptunusz.model.Subject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

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

    /**
     * Register all subjects with the given username and password
     *
     * @param username
     * @param password
     */
    public void register(String username, String password) {

        System.out.println("Opening firefox");
        WebDriver driver = new FirefoxDriver();
        Neptun neptun = new Neptun(driver);

        System.out.println("Logging in");
        neptun.login(username, password);

        System.out.println("Registering subjects...");
        for (Subject subject : subjects) {
            if (subject.isRegister()) {
                System.out.println(" - Registering " + subject);
                neptun.register(subject);
                System.out.println(" - " + subject + " successfully registered!");
            }
        }

        neptun.registeredSubjects();
        System.out.println("Done!");
    }
}