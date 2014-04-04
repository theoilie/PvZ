package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class LeaveCommand implements BasePvZCommand {
	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.leave"))
			return;
		if (!Main.teamManager.isPlayerInGame(player)) {
			Messages.sendMessage(player, Messages.getMessage("not in a game"));
			return;
		}
		Main.teamManager.removePlant(player);
		Main.teamManager.removeZombie(player);
		Messages.sendMessage(player, Messages.getMessage("left"));
	}
}