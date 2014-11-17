package fr.nemolovich.apps.minecraftrcon.gui;

import fr.nemolovich.apps.minecraftrcon.gui.command.Command;

abstract class CommandAdapter extends Command {

	public CommandAdapter(String commandName, String description) {
		super(commandName, description);
	}

	@Override
	public abstract String doCommand(String... args);

}
