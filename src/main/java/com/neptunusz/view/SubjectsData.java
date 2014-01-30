package com.neptunusz.view;

import com.neptunusz.model.Subject;
import com.neptunusz.model.SubjectType;
import com.neptunusz.model.service.SubjectService;
import com.neptunusz.model.service.SubjectServiceFactory;

import javax.swing.table.AbstractTableModel;

public class SubjectsData extends AbstractTableModel {

    // Get the singleton instance
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
            case 4:
                return subject.getPriority();
            default:
                return subject.isRegister();
        }
    }

    @Override
    public int getColumnCount() {
        return 6;
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
        return columnIndex == 4 || columnIndex == 5;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            switch (columnIndex) {
                case 4:
                    subjectService.get(rowIndex).setPriority((Integer) aValue);
                    break;
                case 5:
                    subjectService.get(rowIndex).setRegister((Boolean) aValue);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}