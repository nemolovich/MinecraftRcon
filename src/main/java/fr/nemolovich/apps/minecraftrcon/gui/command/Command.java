/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon.gui.command;

/**
 *
 * @author Nemolovich
 */
public abstract class Command implements Comparable<Command> {

    private final String commandName;
    private final String description;

    public Command(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    public final String getCommandName() {
        return this.commandName;
    }

    public final String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return String.format("Usage: %s",
            this.commandName);
    }

    public String getHelp() {
        return String.format("\t%s", this.description);
    }

    public abstract String doCommand(String... args);

    @Override
    public final int compareTo(Command command) {
        return this.commandName.compareTo(command.commandName);
    }

}
