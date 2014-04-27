package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class StatsCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.stats"))
			return;
		String name;
		if (args.length < 2) {
			name = player.getName().toLowerCase();
		} else {
			name = args[1].toLowerCase();
		}
		String title = "";
		String statsFor = "Stats for " + name;
		// A full row of chat is 52 characters long. I counted. ;)
		int length = Math.round((54 - statsFor.length())) / 2;
		for (int i = 1; i < length; i++) {
			title = title.concat("-");
		}
		title = title.concat("&4" + statsFor + "&r");
		for (int i = 1; i < length; i++) {
			title = title.concat("-");
		}
		boolean sql = Main.sqlUtils.isUsingMySQL();
		Messages.sendMessageNoPrefix(player, title);
		Messages.sendMessage(player,
				"Kills: &4" + Main.sqlUtils.getKills(name, sql));
		Messages.sendMessage(player,
				"Deaths: &4" + Main.sqlUtils.getDeaths(name, sql));
		Messages.sendMessage(player,
				"Games played: &4" + Main.sqlUtils.getGamesPlayed(name, sql));
		Messages.sendMessage(player,
				"Rows captured: &4" + Main.sqlUtils.getRowsCaptured(name, sql));
		Messages.sendMessage(player,
				"Sun: &4" + Main.sqlUtils.getSun(name, sql));
	}
}