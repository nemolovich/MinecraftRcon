/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table.listener;

import fr.nemolovich.apps.minecraftrcon.gui.ParallelTask;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Nemolovich
 */
public class CustomListSelectionListener implements ListSelectionListener {

    private final JXTable table;
    private final ParallelTask worker;

    public CustomListSelectionListener(JXTable table, ParallelTask worker) {
        this.table = table;
        this.worker = worker;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting()) {
            return;
        }
        ListSelectionModel seletionModel
            = (ListSelectionModel) listSelectionEvent.getSource();
        if (!seletionModel.isSelectionEmpty()) {
            int selectedRow = this.table.convertRowIndexToModel(
                seletionModel.getLeadSelectionIndex());
            if (selectedRow > -1) {
                String command = (String) this.table.getModel().getValueAt(
                    selectedRow, 0);
                this.worker.setValue(command);
                this.worker.execute();

                this.table.scrollRowToVisible(this.table.getSelectedRow());
            }
        }
    }
}
