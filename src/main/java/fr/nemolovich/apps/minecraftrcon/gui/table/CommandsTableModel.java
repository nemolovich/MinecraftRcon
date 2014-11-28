/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Nemolovich
 */
public class CommandsTableModel extends AbstractTableModel {

    private Object[][] data;
    private static final String[] TITLES = {"Command name"};

    public CommandsTableModel() {
        this.clear();
    }

    @Override
    public int getRowCount() {
        return this.data.length;
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.data[rowIndex][0];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public final void clear() {
        this.data = new Object[0][0];
    }

    public final void addRow(String command) {
        int indice = 0;
        int nbRow = this.getRowCount();
        int nbCol = this.getColumnCount();

        Object temp[][] = this.data;
        this.data = new Object[nbRow + 1][nbCol];

        for (Object[] value : temp) {
            this.data[indice++] = value;
        }

        Object[] newLine = new Object[1];
        newLine[0] = command;
        this.data[indice] = newLine;

        this.fireTableDataChanged();
    }

    public final void removeRow(int rowIndex) {
        int indice = 0;
        int newDataIndex = 0;
        int nbRow = this.getRowCount();
        int nbCol = this.getColumnCount();
        Object temp[][] = new Object[nbRow - 1][nbCol];

        for (Object[] value : this.data) {
            if (indice != rowIndex) {
                temp[newDataIndex++] = value;
            }
            indice++;
        }
        this.data = temp;
        
        this.fireTableDataChanged();
    }

}