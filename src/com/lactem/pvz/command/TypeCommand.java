package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.game.GameState;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class TypeCommand implements BasePvZCommand {
	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.type"))
			return;
		if (!Main.teamManager.isPlayerInGame(player)) {
			Messages.sendMessage(player, Messages.getMessage("not in a game"));
			return;
		}
		Game game = Main.gameManager.getGame(player);
		if (game.getState() != GameState.WAITING
				&& game.getState() != GameState.STARTING)
			Messages.sendMessage(player,
					Messages.getMessage("game already started"));
		else {
			player.openInventory(Main.invManager.typeInv);
			Main.invManager.inTypeInv.add(player.getUniqueId());
		}
	}
}