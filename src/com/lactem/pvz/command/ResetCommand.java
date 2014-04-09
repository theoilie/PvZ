package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class ResetCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.reset"))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player,
					Messages.getMessage("no player specified"));
			return;
		}
		String name = args[1].toLowerCase();
		boolean sql = Main.sqlUtils.isUsingMySQL();
		if (Main.sqlUtils.resetStats(name, sql))
			Messages.sendMessage(player, Messages.getMessage("reset stats")
					.replaceAll("<player>", name));
		else
			Messages.sendMessage(player, Messages.getMessage("no player exists"));
	}
}