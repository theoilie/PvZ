package me.lactem.pvz.command;

import java.util.UUID;

import me.lactem.pvz.api.API;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand extends PvZPlayerCommand {

	public StatsCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		API api = getAPI();
		if (!canExecute(player))
			return;
		String name;
		if (args.length < 2) {
			name = player.getName().toLowerCase();
		} else {
			name = args[1].toLowerCase();
		}
		
		String title = "";
		String statsFor = "Stats for " + name;
		
		int length = Math.round((54 - statsFor.length())) / 2;
		title += StringUtils.repeat("-", length);
		title += "&4" + statsFor + "&r";
		title += StringUtils.repeat("-", length);
		
		try {
			@SuppressWarnings("deprecation")
			UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
			boolean sql = api.getSqlUtil().isUsingMySQL();
			getAPI().getMessageUtil().sendMessageNoPrefix(player, title);
			getAPI().getMessageUtil().sendMessage(player, "Kills: &4" + api.getSqlUtil().getKills(uuid, sql));
			getAPI().getMessageUtil().sendMessage(player, "Deaths: &4" + api.getSqlUtil().getDeaths(uuid, sql));
			getAPI().getMessageUtil().sendMessage(player, "Games played: &4" + api.getSqlUtil().getGamesPlayed(uuid, sql));
			getAPI().getMessageUtil().sendMessage(player, "Rows captured: &4" + api.getSqlUtil().getRowsCaptured(uuid, sql));
			getAPI().getMessageUtil().sendMessage(player, "Sun: &4" + api.getSqlUtil().getSun(uuid, sql));
		} catch (NullPointerException e) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no player exists"));
		}
	}
}