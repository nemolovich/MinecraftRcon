package fr.nemolovich.apps.minecraftrcon.gui.table.model;

import fr.nemolovich.apps.minecraftrcon.config.GlobalConfig;
import fr.nemolovich.apps.minecraftrcon.gui.table.CustomTable;
import fr.nemolovich.apps.minecraftrcon.gui.table.frame.TableFrameModel;

public class TableModelManager {

    private static final TableFrameModel COMMANDS_FRAME;
    private static final TableFrameModel PLAYERS_FRAME;
    private static final TableFrameModel BANNED_PLAYERS_FRAME;
    private static final TableFrameModel OP_PLAYERS_FRAME;

    static {
        COMMANDS_FRAME = new TableFrameModel();
        COMMANDS_FRAME.setFrameTitle("List of available commands");
        COMMANDS_FRAME.setFrameBoxLabel("Command Help:");
        COMMANDS_FRAME.setFrameFilterTooltip(
            "Filter the commands list (Regular expressions can be used)");
        COMMANDS_FRAME.setFrameHeaderLabel(
            "List of server available commands:");

        CustomTableModel commandsTableModel = new CommandsTableModel();
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
        if (Boolean.parseBoolean(GlobalConfig.getInstance().getProperty(
            GlobalConfig.PLAYERS_IP_AVAILABLE))) {
            playersTableModel = new PlayersIPTableModel();
        } else {
            playersTableModel = new PlayersTableModel();
        }
        PLAYERS_FRAME.setModel(playersTableModel);
        PLAYERS_FRAME.setTable(new CustomTable(playersTableModel));

        BANNED_PLAYERS_FRAME = new TableFrameModel();
        BANNED_PLAYERS_FRAME.setFrameTitle("List of banned players");
        BANNED_PLAYERS_FRAME.setFrameBoxLabel("Command result:");
        BANNED_PLAYERS_FRAME.setFrameFilterTooltip(
            "Filter the banned players list (Regular expressions can be used)");
        BANNED_PLAYERS_FRAME.setFrameHeaderLabel(
            "List of banned players on server:");

        CustomTableModel bannedPlayersTableModel = new PlayersTableModel();
        BANNED_PLAYERS_FRAME.setModel(bannedPlayersTableModel);
        BANNED_PLAYERS_FRAME.setTable(new CustomTable(bannedPlayersTableModel));

        OP_PLAYERS_FRAME = new TableFrameModel();
        OP_PLAYERS_FRAME.setFrameTitle("List of OP players");
        OP_PLAYERS_FRAME.setFrameBoxLabel("Command result:");
        OP_PLAYERS_FRAME.setFrameFilterTooltip(
            "Filter the admin players list (Regular expressions can be used)");
        OP_PLAYERS_FRAME.setFrameHeaderLabel(
            "List of banned players on server:");

        CustomTableModel opPlayersTableModel = new PlayersTableModel();
        OP_PLAYERS_FRAME.setModel(opPlayersTableModel);
        OP_PLAYERS_FRAME.setTable(new CustomTable(opPlayersTableModel));
    }

    public static TableFrameModel getCommandsFrame() {
        return COMMANDS_FRAME;
    }

    public static TableFrameModel getPlayersFrame() {
        return PLAYERS_FRAME;
    }

    public static TableFrameModel getBannedPlayersFrame() {
        return BANNED_PLAYERS_FRAME;
    }

    public static TableFrameModel getOPPlayersFrame() {
        return OP_PLAYERS_FRAME;
    }
}
