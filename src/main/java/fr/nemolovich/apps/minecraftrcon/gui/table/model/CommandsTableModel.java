/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.table.model;

/**
 *
 * @author Nemolovich
 */
public class CommandsTableModel extends CustomTableModel {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -1306337541040072455L;
	private static final String[] TITLES = { "Command name" };

	public CommandsTableModel() {
		super(TITLES);
		this.clear();
	}

	public void addCommand(String command) {
		String[] row = new String[TITLES.length];
		row[0] = command;
		super.addRow(row);
	}

}
