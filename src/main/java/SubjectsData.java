import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubjectsData extends AbstractTableModel {
    List<Subject> subjects;

    public SubjectsData() {
        subjects = new ArrayList<Subject>();
    }

    @Override
    public int getRowCount() {
        return subjects.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subject subject = subjects.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return subject.getName();
            case 1:
                return subject.getCode();
            case 2:
                SubjectType type = subject.getType();
                return type == SubjectType.CURRICULUM ? "Alapképzés" : type == SubjectType.OPTIONAL ? "Választható" : "Minden";
            case 3:
                String courses = "";
                for (String course : subject.getCourses()) {
                    courses += " " + course;
                }
                return courses;
            default:
                return subject.isRegister();
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Név";
            case 1:
                return "Kód";
            case 2:
                return "Típus";
            case 3:
                return "Kurzusok";
            default:
                return "Felvesz";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return SubjectType.class;
            case 3:
                return String.class;
            default:
                return Boolean.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        subjects.get(rowIndex).setRegister((Boolean) aValue);
    }

    public void addSubject(String name, String code, SubjectType type) {
        subjects.add(new Subject(name, code, type));
        fireTableRowsInserted(0, subjects.size());
    }

    public void deleteSubject(int rowIndex) {
        subjects.remove(rowIndex);
        fireTableRowsDeleted(0, subjects.size());
    }

    public void addCourse(int rowIndex, String course) {
        subjects.get(rowIndex).addCourse(course);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public List<Subject> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public void setSubjects(List<Subject> subjects) {
        // this.subjects = subjects causes error
        // input object is an unmodifiable list
        this.subjects.clear();
        this.subjects.addAll(subjects);
    }
}
