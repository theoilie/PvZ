package com.lactem.pvz.util.validate;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class Validate {
	public Plugin plugin;

	public boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;
		else {
			sender.sendMessage("This can only be done in-game.");
			return false;
		}
	}

	public boolean hasPerm(Player player, String perm) {
		if (player.hasPermission(perm))
			return true;
		else {
			Messages.sendMessage(player, Messages.getMessage("no permission"));
			return false;
		}
	}

	public boolean areArgs(String[] args, Player player) {
		if (args.length == 0) {
			Main.sendHelp(player);
			return false;
		}
		return true;
	}

	public boolean hasSelection(Player player) {
		if (Selection.getPlayerSelection(player).areBothPointsSet()
				&& !Selection.getPlayerSelection(player)
						.areBlocksInDifferentWorlds())
			return true;
		else {
			Messages.sendMessage(player,
					Messages.getMessage("selection not set properly"));
			return false;
		}
	}

	public boolean farmExists(String name, Player player) {
		File dir = new File(plugin.getDataFolder() + "/farms");
		if (dir.exists()) {
			for (File child : dir.listFiles()) {
				if (child.getName().contains(".txt")) {
					Farm farm = Main.farmManager.readFarm(child.getName());
					if (farm.getName().equals(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}