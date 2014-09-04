package me.lactem.pvz.util.validate;

import java.io.File;

import me.lactem.pvz.Main;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Validate {

	/**
	 * Checks if a CommandSender is a Player and sends a message if he/she is not.
	 * @param sender the sender of a command
	 * @return true if the CommandSender is a Player
	 */
	public boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;
		else {
			sender.sendMessage("This can only be done in-game.");
			return false;
		}
	}

	/**
	 * Checks if a player has a permission and sends a message if he/she does not.
	 * @param player the player to check
	 * @param perm the permission to check
	 * @return true if the player has the permission
	 */
	public boolean hasPerm(Player player, String perm) {
		if (player.hasPermission(perm))
			return true;
		Messages.sendMessage(player, Messages.getMessage("no permission"));
		return false;
	}

	/**
	 * Checks if args is empty and sends a message if it is.
	 * @param args a String array of arguments for a command
	 * @param player the player who sent the command with args
	 * @return true if arguments is not empty
	 */
	public boolean areArgs(String[] args, Player player) {
		if (args.length == 0) {
			Main.sendHelp(player);
			return false;
		}
		return true;
	}

	/**
	 * Checks if a player has a selection and sends a message if he/she does not.
	 * @param player the player to check
	 * @return true if the player has a selection
	 */
	public boolean hasSelection(Player player) {
		if (Selection.getPlayerSelection(player).areBothPointsSet() && Selection.getPlayerSelection(player).areBlocksInSameWorld())
			return true;
		else {
			Messages.sendMessage(player, Messages.getMessage("selection not set properly"));
			return false;
		}
	}

	/**
	 * Checks if a farm exists
	 * @param name the name of the farm
	 * @return true if the farm exists
	 */
	public boolean farmExists(String name) {
		File dir = new File(Main.getAPI().getPlugin().getDataFolder() + "/farms");
		if (!dir.exists())
			return false;
		for (File child : dir.listFiles()) {
			if (child.getName().contains(".txt")) {
				Farm farm = Main.getAPI().getFarmManager().readFarm(child.getName());
				if (farm.getName().equals(name))
					return true;
			}
		}
		return false;
	}
}