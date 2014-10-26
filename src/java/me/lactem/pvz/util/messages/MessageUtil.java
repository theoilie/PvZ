package me.lactem.pvz.util.messages;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
	private String prefix = "";
	private API api;

	/**
	 * Converts color codes and sends a message to a player with the PvZ prefix from the configuration file
	 * @param messageReceiver the player, console sender, or whoever else the message will be sent to
	 * @param message the message that will be sent
	 */
	public void sendMessage(CommandSender messageReceiver, String message) {
		if (messageReceiver instanceof Player)
			messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
		else
			messageReceiver.sendMessage(stripColors(message));
	}

	/**
	 * This method does the same thing as {@link #sendMessage(CommandSender, String)}, but is for players only (no console).
	 * @param player the player to send the message to
	 * @param message the message that will be sent
	 */
	public void sendMessage(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}

	/**
	 * Converts color codes and sends a message to a player without adding the PvZ prefix
	 * @param player the player to send the message to
	 * @param message the message that will be sent
	 */
	public void sendMessageNoPrefix(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	/**
	 * Removes all color from a String
	 * @param msg the String to decolorize
	 * @return a decolorized version of msg
	 */
	public String stripColors(String msg) {
		String out = msg.replaceAll("[&][0-9a-f]", "");
		out = out.replaceAll(String.valueOf((char) 194), "");
		return out.replaceAll("[\u00a7][0-9a-f]", "");
	}

	/**
	 * Gets a message from the messages.yml file
	 * @param message the name of the message to get
	 * @return the message as it appears in messages.yml
	 */
	public String getMessage(String message) {
		return api.getFileUtils().getMessages().getString(message);
	}

	/**
	 * Sets the PvZ prefix
	 * @param prefix the value that the prefix will be set to
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setAPI() {
		api = Main.getAPI();
	}
}