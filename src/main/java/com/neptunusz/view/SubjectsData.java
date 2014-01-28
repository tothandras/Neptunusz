package com.neptunusz.view;

import com.neptunusz.model.Subject;
import com.neptunusz.model.SubjectType;
import com.neptunusz.model.service.SubjectService;
import com.neptunusz.model.service.SubjectServiceFactory;

import javax.swing.table.AbstractTableModel;

public class SubjectsData extends AbstractTableModel {

    //Get the singleton instance
    SubjectService subjectService = SubjectServiceFactory.getInstance();

    @Override
    public int getRowCount() {
        return subjectService.getSubjects().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Subject subject = subjectService.getSubjects().get(rowIndex);
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
        return Columns.values()[column].getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Columns.values()[columnIndex].getClazz();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            subjectService.get(rowIndex).setRegister((Boolean) aValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
