package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;

import org.bukkit.entity.Player;

public abstract class PvZCommand {
	private static API api;
	private final String name;
	private final String permission;
	private final String[] aliases;
	
	public PvZCommand(String name, String permission) {
		this.name = name;
		this.permission = permission;
		aliases = new String[0];
	}
	
	public PvZCommand(String name, String permission, String[] aliases) {
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
	}
	
	/**
	 * Executes a command.
	 * @param player the player who is executing the commands
	 * @param args any arguments the player entered after the command
	 */
	public abstract void execute(Player player, String[] args);
	
	/**
	 * Checks if a player has permission to use this command.
	 * @param player the player to check permissions for.
	 * @return true if the player has permission to execute this command
	 */
	public abstract boolean canExecute(Player player);
	
	/**
	 * Gets the name of this command.
	 * @return the name of this command
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the permission node required to execute this command.
	 * @return the permission node required to use this command
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Gets the aliases (other accepted input in addition to name) for this command.
	 * @return the command's aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Checks if the label is accepted by this command. For example /set can also be called with /setspawn.
	 * @param label the label to check
	 * @return true if the label is accepted (if it's the name of the command or one if its aliases)
	 */
	public boolean isAcceptedLabel(String label) {
		if (name.equalsIgnoreCase(label))
			return true;
		
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(label))
				return true;
		}
		return false;
	}
	
	/**
	 * Gets PvZ's API for convenience in subclasses.
	 * @return the API used for PvZ
	 */
	protected API getAPI() {
		return api;
	}
	
	/**
	 * Sets the API to Main.getAPI() for convenience in subclasses.
	 */
	public static final void setAPI() {
		api = Main.getAPI();
	}
}