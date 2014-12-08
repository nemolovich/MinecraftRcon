/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table;

import java.util.regex.PatternSyntaxException;
import javax.swing.JTable;

/**
 *
 * @author Nemolovich
 */
public interface ITableModel {

    void addRow(String[] row);

    void clear();

    void filter(String filter) throws PatternSyntaxException;

    Class<?> getColumnClass(int columnIndex);

    int getColumnCount();

    String getColumnName(int column);

    int getRowCount();

    Object getValueAt(int rowIndex, int columnIndex);

    boolean isCellEditable(int rowIndex, int columnIndex);

    void removeRow(int rowIndex);

    void setTableSorter(JTable table);
    
}
