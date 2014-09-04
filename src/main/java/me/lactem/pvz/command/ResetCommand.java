package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class ResetCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.reset"))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player, Messages.getMessage("no player specified"));
			return;
		}
		
		String name = args[1].toLowerCase();
		boolean sql = api.getSqlUtils().isUsingMySQL();
		if (api.getSqlUtils().resetStats(name, sql))
			Messages.sendMessage(player, Messages.getMessage("reset stats").replaceAll("<player>", name));
		else
			Messages.sendMessage(player, Messages.getMessage("no player exists"));
	}
}