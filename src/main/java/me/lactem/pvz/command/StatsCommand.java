package me.lactem.pvz.command;

import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand implements BasePvZCommand {
	private API api = Main.getAPI();
	
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.stats"))
			return;
		String name;
		if (args.length < 2) {
			name = player.getName().toLowerCase();
		} else {
			name = args[1].toLowerCase();
		}
		
		String title = "";
		String statsFor = "Stats for " + name;
		
		// A full row of chat is 52 characters long. I counted, except this probably changes from screen-to-screen. :(
		int length = Math.round((54 - statsFor.length())) / 2;
		
		for (int i = 1; i < length; i++) {
			title = title.concat("-");
		}
		title = title.concat("&4" + statsFor + "&r");
		for (int i = 1; i < length; i++) {
			title = title.concat("-");
		}
		
		try {
			@SuppressWarnings("deprecation")
			UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
			boolean sql = api.getSqlUtils().isUsingMySQL();
			Messages.sendMessageNoPrefix(player, title);
			Messages.sendMessage(player, "Kills: &4" + api.getSqlUtils().getKills(uuid, sql));
			Messages.sendMessage(player, "Deaths: &4" + api.getSqlUtils().getDeaths(uuid, sql));
			Messages.sendMessage(player, "Games played: &4" + api.getSqlUtils().getGamesPlayed(uuid, sql));
			Messages.sendMessage(player, "Rows captured: &4" + api.getSqlUtils().getRowsCaptured(uuid, sql));
			Messages.sendMessage(player, "Sun: &4" + api.getSqlUtils().getSun(uuid, sql));
		} catch (NullPointerException e) {
			Messages.sendMessage(player, Messages.getMessage("no player exists"));
			return;
		}
	}
}