package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class TypeCommand implements BasePvZCommand {
	private API api = Main.getAPI();
	
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.type"))
			return;
		if (!api.getGameManager().isPlayerInGame(player)) {
			Messages.sendMessage(player, Messages.getMessage("not in a game"));
			return;
		}
		
		Game game = api.getGameManager().getGame(player);
		
		if (game.getState() != GameState.WAITING && game.getState() != GameState.STARTING) {
			Messages.sendMessage(player,Messages.getMessage("game already started"));
		} else {
			player.openInventory(api.getInvManager().getTypeInv());
			api.getInvManager().addToTypeInv(player.getUniqueId());
		}
	}
}