package fr.nemolovich.apps.minecraftrcon.gui.table.model;

import fr.nemolovich.apps.minecraftrcon.config.GlobalConfig;
import fr.nemolovich.apps.minecraftrcon.gui.table.CustomTable;
import fr.nemolovich.apps.minecraftrcon.gui.table.frame.TableFrameModel;

public class TableModelManager {

    private static final TableFrameModel COMMANDS_FRAME;
    private static final TableFrameModel PLAYERS_FRAME;

    static {
        COMMANDS_FRAME = new TableFrameModel();
        COMMANDS_FRAME.setFrameTitle("List of available commands");
        COMMANDS_FRAME.setFrameBoxLabel("Command Help:");
        COMMANDS_FRAME.setFrameFilterTooltip(
            "Filter the commands list (Regular expressions can be used)");
        COMMANDS_FRAME.setFrameHeaderLabel(
            "List of server available commands:");
        CommandsTableModel commandsTableModel = new CommandsTableModel();
        COMMANDS_FRAME.setModel(commandsTableModel);
        COMMANDS_FRAME.setTable(new CustomTable(commandsTableModel));

        PLAYERS_FRAME = new TableFrameModel();
        PLAYERS_FRAME.setFrameTitle("List of connected players");
        PLAYERS_FRAME.setFrameBoxLabel("Command result:");
        PLAYERS_FRAME.setFrameFilterTooltip(
            "Filter the players list (Regular expressions can be used)");
        PLAYERS_FRAME.setFrameHeaderLabel(
            "List of connected users on server:");

        CustomTableModel playersTableModel;
        if ((Boolean) GlobalConfig.getInstance().get(
            GlobalConfig.PLAYERS_IP_AVAILABLE)) {
            playersTableModel = new PlayersIPTableModel();
        } else {
            playersTableModel = new PlayersTableModel();
        }
        PLAYERS_FRAME.setModel(playersTableModel);
        PLAYERS_FRAME.setTable(new CustomTable(playersTableModel));
    }

    public static TableFrameModel getCommandsFrame() {
        return COMMANDS_FRAME;
    }

    public static TableFrameModel getPlayersFrame() {
        return PLAYERS_FRAME;
    }
}
