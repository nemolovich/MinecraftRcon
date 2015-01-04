/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table.listener;

import fr.nemolovich.apps.minecraftrcon.gui.table.model.CustomTableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Nemolovich
 */
public abstract class PlayerActionListener implements ActionListener {

    private final Component parent;
    private final JXTable table;

    public PlayerActionListener(Component parent, JXTable table) {
        this.parent = parent;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = this.table.getSelectedRow();
        if (index != -1) {
            String player
                = (String) ((CustomTableModel) this.table.getModel())
                .getValueAt(this.table.convertRowIndexToModel(
                        index), 0);
            action(player);
        } else {
            JOptionPane.showMessageDialog(this.parent,
                "Please select a player", "Selection is empty",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public abstract void action(String playerName);
}
