/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table;

import java.util.Arrays;
import java.util.regex.PatternSyntaxException;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Nemolovich
 */
public abstract class CustomTableModel extends AbstractTableModel implements ITableModel {

    /**
	 * UID.
	 */
	private static final long serialVersionUID = -2754803157477100141L;
	private final String[] titles;
    protected Object[][] data;
    protected TableRowSorter<CustomTableModel> sorter;

    public CustomTableModel(String[] titles) {
        this.titles = titles;
    }

    @Override
    public int getRowCount() {
        return this.data.length;
    }

    @Override
    public int getColumnCount() {
        return this.titles.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.titles[column];
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

    @Override
    public final void clear() {
        this.data = new Object[0][0];
    }

    @Override
    public final void addRow(String[] row) {
        int indice = 0;
        int nbRow = this.getRowCount();
        int nbCol = this.getColumnCount();
        Object[][] temp = this.data;
        this.data = new Object[nbRow + 1][nbCol];
        for (Object[] value : temp) {
            this.data[indice++] = value;
        }
        Object[] newLine = new Object[row.length];
        System.arraycopy(row, 0, newLine, 0, row.length);
        newLine = Arrays.copyOf(row, row.length);
        this.data[indice] = newLine;
        this.fireTableDataChanged();
    }

    @Override
    public final void removeRow(int rowIndex) {
        int indice = 0;
        int newDataIndex = 0;
        int nbRow = this.getRowCount();
        int nbCol = this.getColumnCount();
        Object[][] temp = new Object[nbRow - 1][nbCol];
        for (Object[] value : this.data) {
            if (indice != rowIndex) {
                temp[newDataIndex++] = value;
            }
            indice++;
        }
        this.data = temp;
        this.fireTableDataChanged();
    }

    @Override
    public final void setTableSorter(JTable table) {
        TableRowSorter<CustomTableModel> rowSorter = 
            new TableRowSorter((CustomTableModel) table.getModel());
        table.setRowSorter(rowSorter);
        this.sorter = rowSorter;
    }

    @Override
    public void filter(String filter) throws PatternSyntaxException {
        RowFilter<CustomTableModel, Object> rf = RowFilter.regexFilter(filter, 0);
        this.sorter.setRowFilter(rf);
    }

}
