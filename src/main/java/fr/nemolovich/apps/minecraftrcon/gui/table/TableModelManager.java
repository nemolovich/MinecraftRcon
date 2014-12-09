package fr.nemolovich.apps.minecraftrcon.gui.table;

import org.jdesktop.swingx.JXTable;

public class TableModelManager {
	private static final CommandsTableModel COMMMANDS_MODEL = new CommandsTableModel();
	private static final PlayersTableModel PLAYERS_MODEL = new PlayersTableModel();

	private static final JXTable COMMANDS_TABLE = new CustomTable(
			COMMMANDS_MODEL);
	private static final JXTable PLAYERS_TABLE = new CustomTable(PLAYERS_MODEL);

	public static JXTable getCommandsTable() {
		return COMMANDS_TABLE;
	}

	public static JXTable getPlayersTable() {
		return PLAYERS_TABLE;
	}
}
