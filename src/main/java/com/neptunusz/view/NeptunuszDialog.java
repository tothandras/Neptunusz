package com.neptunusz.view;

import com.neptunusz.model.Subject;
import com.neptunusz.model.SubjectType;
import com.neptunusz.model.service.SubjectService;
import com.neptunusz.model.service.SubjectServiceFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class NeptunuszDialog extends JDialog {
    private SubjectsData dataModel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonAddSubject;
    private JTable subjectsTable;
    private JButton buttonRemove;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField subjectCodeField;
    private JComboBox subjectTypeComboBox;
    private JTextField subjectNameField;
    private JTextField courseField;
    private JButton buttonAddCourse;
    private JButton buttonClearCourses;

    private SubjectService subjectService = SubjectServiceFactory.getInstance();

    public NeptunuszDialog() {

        //Exit on close
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Load database
        try {
            subjectService.loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Create dataModel
        dataModel = new SubjectsData();

        setContentPane(contentPane);
        setPreferredSize(new Dimension(750, 400));
        setResizable(false);
        setModal(true);
        setTitle("Neptunusz");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        subjectsTable.setModel(dataModel);

        buttonAddSubject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddSubject();
            }
        });

        buttonAddCourse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddCourse();
            }
        });

        buttonClearCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClearCourses();
            }
        });

        buttonRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRemove();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (usernameField.getText().isEmpty() || passwordField.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(null, "Add meg a neptun kódod és jelszavad", "Figyelmeztetés", JOptionPane.PLAIN_MESSAGE);
                } else {
                    onOK();
                }
            }
        });

        subjectsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    onRemove();
                }
            }
        });

    }

    /**
     *
     */
    private void onAddSubject() {
        //Add new subject
        SubjectType type = SubjectType.values()[subjectTypeComboBox.getSelectedIndex()];
        Subject subject = new Subject(subjectNameField.getText(), subjectCodeField.getText(), type);
        subjectService.getSubjects().add(subject);

        // reset fields
        subjectNameField.setText("");
        subjectCodeField.setText("");
        if (!courseField.getText().isEmpty()) {
            //add course if not empty
            subject.addCourse(courseField.getText());
            courseField.setText("");
        }

        //Refresh table
        dataModel.fireTableDataChanged();
    }

    /**
     *
     */
    private void onRemove() {
        int selectedRow = subjectsTable.getSelectedRow();
        try {
            Subject subject = subjectService.get(selectedRow);
            subjectService.getSubjects().remove(subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataModel.fireTableRowsDeleted(selectedRow, selectedRow);
    }

    /**
     *
     */
    private void onAddCourse() {
        try {
            int selectedRow = subjectsTable.getSelectedRow();
            String text = courseField.getText();

            //Add the course
            subjectService.get(selectedRow).addCourse(text);

            //Change table
            dataModel.fireTableCellUpdated(selectedRow, Columns.COURSES.ordinal());
            courseField.setText("");
        } catch (Exception e) {
            //No such subject
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void onClearCourses() {
        try {
            int selectedRow = subjectsTable.getSelectedRow();
            subjectService.get(selectedRow).clearCourses();

            //Change table
            dataModel.fireTableCellUpdated(selectedRow, Columns.COURSES.ordinal());
        } catch (Exception e) {
            //No such subject
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void onOK() {
        this.setVisible(false);
        subjectService.register(usernameField.getText().trim(), new String(passwordField.getPassword()));
        this.setVisible(true);
    }

    /**
     *
     */
    @Override
    public void dispose() {
        subjectService.saveToFile();
        System.exit(0);
        super.dispose();
    }
}
