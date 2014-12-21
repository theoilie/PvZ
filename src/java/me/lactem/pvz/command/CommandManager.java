package me.lactem.pvz.command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.google.common.collect.Lists;

import me.lactem.pvz.Main;

public class CommandManager {
	private List<PvZCommand> commands;
	
	public CommandManager() {
		commands = Lists.newArrayList();
		registerDefaultCommands();
	}
	
	/**
	 * Registers a command. The command must inherit from PvZCommand.
	 * @param cmd the class of the command to be registered, for example WandCommand.class
	 * @param args any arguments required in the constructor of the command (Please note that by default all commands require at least a name and a permission node.)
	 * @return the newly-registered command
	 */
	@SuppressWarnings("unchecked")
	public <X extends PvZCommand, Type extends X> Type registerCommand(Class<? extends X> cmd, Object... args) {
		try {
			Class<?>[] params = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				params[i] = args[i].getClass();
			}
			
			return (Type) cmd.getConstructor(params).newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			if (Main.getAPI() == null || Main.getAPI().debug())
				e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the list of registered commands extending PvZCommand.
	 * @return the list of registered PvZ commands
	 */
	public List<PvZCommand> getRegisteredCommands() {
		return commands;
	}
	
	/**
	 * Gets the registered command of a class cmdClass.
	 * @param cmdClass the class of the desired command object
	 * @return the registered command whose class is cmdClass
	 */
	@SuppressWarnings("unchecked")
	public <X extends PvZCommand, Type extends X> Type getCommand(Class<? extends X> cmdClass) {
		for (PvZCommand cmd : commands.toArray(new PvZCommand[0])) {
			if (cmd.getClass() == cmdClass)
				return (Type) cmd;
		}
		return null;
	}
	
	/**
	 * Gets the name of a command whose class is cmdClass.
	 * Alternatively use {@link #getCommand(Class)} and call getName() on that object.
	 * @param cmdClass the class to get the command name from
	 * @return the name of the command with the class cmdClass
	 */
	public String getName(Class<? extends PvZCommand> cmdClass) {
		return getCommand(cmdClass) == null ? null : getCommand(cmdClass).getName();
	}
	
	/**
	 * Gets the permission of a command whose class is cmdClass.
	 * Alternatively use {@link #getCommand(Class)} and call getPermission() on that object.
	 * @param cmdClass the class to get the permission from
	 * @return the permission of the command with the class cmdClass
	 */
	public String getPermission(Class<? extends PvZCommand> cmdClass) {
		return getCommand(cmdClass) == null ? null : getCommand(cmdClass).getPermission();
	}
	
	/**
	 * Registers all default commands in the me.lactem.pvz.command package.
	 * This method is called automatically in this class' the constructor.
	 * To manually register a command, use {@link #registerCommand(Class, String, Object...)}.
	 */
	public void registerDefaultCommands() {
		commands.add(registerCommand(WandCommand.class, "wand", "pvz.admin.wand"));
		commands.add(registerCommand(CreateCommand.class, "create", "pvz.admin.create"));
		commands.add(registerCommand(AddCommand.class, "add", "pvz.adin.add"));
		commands.add(registerCommand(SetSpawnCommand.class, "set", "pvz.admin.set", new String[] { "setspawn" }));
		commands.add(registerCommand(EndpointCommand.class, "endpoint", "pvz.admin.endpoint"));
		commands.add(registerCommand(JoinCommand.class, "join", "pvz.user.join"));
		commands.add(registerCommand(LeaveCommand.class, "leave", "pvz.user.leave"));
		commands.add(registerCommand(TypeCommand.class, "type", "pvz.user.type"));
		commands.add(registerCommand(CheckCommand.class, "check", "pvz.admin.check"));
		commands.add(registerCommand(StatsCommand.class, "stats", "pvz.user.stats"));
		commands.add(registerCommand(ListCommand.class, "list", "pvz.admin.list"));
		commands.add(registerCommand(ResetCommand.class, "reset", "pvz.admin.reset"));
	}
}