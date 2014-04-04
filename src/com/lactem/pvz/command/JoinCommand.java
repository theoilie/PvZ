package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class JoinCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.join"))
			return;
		if (!Main.teamManager.isPlayerInGame(player)) {
			player.openInventory(Main.invManager.gameInv);
			Main.invManager.inInv.add(player.getUniqueId());
		} else
			Messages.sendMessage(player,
					Messages.getMessage("already in game"));
	}
}