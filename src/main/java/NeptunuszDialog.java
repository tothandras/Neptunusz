import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;

public class NeptunuszDialog extends JDialog {
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

    private SubjectsData subjectsData;

    public NeptunuszDialog() {
        try {
            subjectsData = new SubjectsData();
            File subjectsDataFile = new File("subjects.dat");
            if (subjectsDataFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(subjectsDataFile));
                subjectsData.setSubjects((List<Subject>) ois.readObject());
                ois.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setContentPane(contentPane);
        setPreferredSize(new Dimension(750, 400));
        setResizable(false);
        setModal(true);
        setTitle("Neptunusz");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        subjectsTable.setModel(subjectsData);

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

    private void onAddSubject() {
        SubjectType type;
        switch (subjectTypeComboBox.getSelectedIndex()) {
            case 0:
                type = SubjectType.CURRICULUM;
                break;
            case 1:
                type = SubjectType.OPTIONAL;
                break;
            default:
                type = SubjectType.ALL;
                break;
        }
        subjectsData.addSubject(subjectNameField.getText(), subjectCodeField.getText(), type);
        // reset fields
        subjectNameField.setText("");
        subjectCodeField.setText("");
        if (!courseField.getText().isEmpty()) {
            subjectsData.addCourse(subjectsData.getRowCount()-1, courseField.getText());
            courseField.setText("");
        }
    }

    private void onRemove() {
        subjectsData.deleteSubject(subjectsTable.getSelectedRow());
    }

    private void onAddCourse() {
        subjectsData.addCourse(subjectsTable.getSelectedRow(), courseField.getText());
        // reset field
        courseField.setText("");
    }

    private void onOK() {
        dispose();

        WebDriver driver = new FirefoxDriver();

        Neptun neptun = new Neptun(driver);

        neptun.login(usernameField.getText().trim(), new String(passwordField.getPassword()));
        for (Subject subject : subjectsData.getSubjects()) {
            if (subject.isRegister()) {
                neptun.registrate(subject);
            }
        }
        neptun.registeredSubjects();

    }

    @Override
    public void dispose() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("subjects.dat"));
            oos.writeObject(subjectsData.getSubjects());
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        NeptunuszDialog dialog = new NeptunuszDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
