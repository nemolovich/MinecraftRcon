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

/**
 *
 * @author Nemolovich
 */
public class CommandsUtils {

    private static final List<Command> INTERNAL_COMMANDS;
    private static final List<String> SERVER_COMMANDS;

    static {
        INTERNAL_COMMANDS = Collections
            .synchronizedList(new ArrayList<Command>());
        SERVER_COMMANDS = Collections.synchronizedList(new ArrayList<String>());
    }

    public static void addCommand(Command command) {
        INTERNAL_COMMANDS.add(command);
    }

    public static void addServerCommand(String... commands) {
        SERVER_COMMANDS.addAll(Arrays.asList(commands));
    }

    public static void addServerCommand(List<String> commands) {
        SERVER_COMMANDS.addAll(commands);
    }

    public static final List<String> getAvailableCommands() {
        List<String> result = new ArrayList();
        for (Command command : INTERNAL_COMMANDS) {
            result.add(command.getCommandName());
        }
        result.addAll(SERVER_COMMANDS);

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

}
