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
public class PlayersTableModel extends CustomTableModel {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -1306337541040072455L;
	private static final String[] TITLES = { "Player name", "Player IP" };

	public PlayersTableModel() {
		super(TITLES);
		this.clear();
	}

	public void addPlayer(String playerName, String playerIP) {
		String[] row = new String[TITLES.length];
		row[0] = playerName;
		row[1] = playerIP == null ? "" : playerIP;
		super.addRow(row);
	}

}
