package me.lactem.pvz.command;

import org.bukkit.entity.Player;


public interface BasePvZCommand {
	
	/**
	 * Executes a command
	 * @param player the player who is executing the commands
	 * @param args any arguments the player entered after the command
	 */
	public void execute(Player player, String[] args); 
}