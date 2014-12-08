/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table;

/**
 *
 * @author Nemolovich
 */
public class CommandsTableModel extends CustomTableModel {

    private static final String[] TITLES = {"Command name"};

    public CommandsTableModel() {
        super(TITLES);
        this.clear();
    }

    public void addCommand(String command) {
        String[] row=new String[1];
        row[0]=command;
        super.addRow(row);
    }


}
