/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Nemolovich
 */
public class CommandsUtils {


    /*
     * Statics variables.
     */
    private static final List<Command> INTERNAL_COMMANDS;
    private static final List<String> SERVER_COMMANDS;

    private static final ConcurrentMap<String, String> COMMANDS_HELP;

    static {
        INTERNAL_COMMANDS = Collections
            .synchronizedList(new ArrayList<Command>());
        SERVER_COMMANDS = Collections.synchronizedList(new ArrayList<String>());

        COMMANDS_HELP = new ConcurrentHashMap<>();
    }

    public static String getCommandHelp(String command) {
        return COMMANDS_HELP.get(command);
    }

    public static void addCommandHelp(String command, String helpMsg) {
        COMMANDS_HELP.put(command, helpMsg);
    }

    public static void addCommand(Command command) {
        INTERNAL_COMMANDS.add(command);
        addCommandHelp(command.getCommandName(),
            buildInternalCommandHelp(command.getCommandName()));
    }

    public static void addServerCommand(String... commands) {
        addServerCommand(Arrays.asList(commands));
    }

    public static void addServerCommand(List<String> commands) {
        SERVER_COMMANDS.addAll(commands);
        for (String command : commands) {
            addCommandHelp(command, "");
        }
    }

    public static final String buildInternalCommandHelp(String commandName) {
        String result = null;

        if (getInternaleCommandNames().contains(commandName)) {
            String help = getInternalCommand(commandName).getHelp();
            String head = String.format("%-33s", commandName)
                .replaceAll(" ", "-").replaceFirst("-", " \u00A7e");
            result = String
                .format("\u00A7e--------- \u00A7fHelp: %s\n"
                    + "\u00A76Description: \u00A7f%s\n\u00A76Usage: \u00A7f%s\n",
                    head, help.replaceAll("\t", ""), commandName);
        }

        return result;
    }

    public static final List<String> getAvailableCommands() {
        List<String> result = new ArrayList();
        for (Command command : INTERNAL_COMMANDS) {
            result.add(command.getCommandName());
        }
        result.addAll(SERVER_COMMANDS);
        Collections.sort(result);

        return result;
    }

    public static String getInternalCommandHelp(String commandName) {
        String result = null;
        for (Command command : INTERNAL_COMMANDS) {
            if (command.getCommandName().equalsIgnoreCase(commandName)) {
                result = command.getHelp();
                break;
            }
        }
        return result;
    }

    private static List<String> getInternaleCommandNames() {
        List<String> result = new ArrayList<>();
        for (Command command : INTERNAL_COMMANDS) {
            result.add(command.getCommandName());
        }
        return result;
    }

    public static List<String> getInternalCommands() {
        return getInternaleCommandNames();
    }

    public static Command getInternalCommand(String commandName) {
        Command result = null;
        for (Command command : INTERNAL_COMMANDS) {
            if (command.getCommandName().equals(commandName)) {
                result = command;
                break;
            }
        }
        return result;
    }

    public static void clearAll() {
        COMMANDS_HELP.clear();
        for (Command command : INTERNAL_COMMANDS) {
            addCommandHelp(command.getCommandName(),
                buildInternalCommandHelp(command.getCommandName()));
        }
        SERVER_COMMANDS.clear();
    }

}
