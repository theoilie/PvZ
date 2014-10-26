package me.lactem.pvz.command;

import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;

import org.bukkit.entity.Player;

public class TypeCommand extends PvZPlayerCommand {

	public TypeCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (!getAPI().getGameManager().isPlayerInGame(player)) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("not in a game"));
			return;
		}
		
		Game game = getAPI().getGameManager().getGame(player);
		
		if (game.getState() != GameState.WAITING && game.getState() != GameState.STARTING) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("game already started"));
		} else {
			player.openInventory(getAPI().getInvManager().getTypeInv());
			getAPI().getInvManager().addToTypeInv(player.getUniqueId());
		}
	}
}