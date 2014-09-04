package me.lactem.pvz.util.messages;

import me.lactem.pvz.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {
	public static String prefix = "";

	public static void sendMessage(CommandSender sender, String message) {
		if (sender instanceof Player) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
		} else {
			sender.sendMessage(stripColors(message));
		}
	}

	public static void sendMessage(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}

	public static void sendMessageNoPrefix(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static String stripColors(String msg) {
		String out = msg.replaceAll("[&][0-9a-f]", "");
		out = out.replaceAll(String.valueOf((char) 194), "");
		return out.replaceAll("[\u00a7][0-9a-f]", "");
	}

	public static String getMessage(String message) {
		return Main.getAPI().getFileUtils().getMessages().getString(message);
	}

	public static void setPrefix(String newPrefix) {
		prefix = newPrefix;
	}
}