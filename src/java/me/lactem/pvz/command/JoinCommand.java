package me.lactem.pvz.command;

import org.bukkit.entity.Player;

public class JoinCommand extends PvZPlayerCommand {

	public JoinCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		
		if (getAPI().getGameManager().isPlayerInGame(player)) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("already in game"));
		} else {
			player.openInventory(getAPI().getInvManager().getGameInv());
			getAPI().getInvManager().addToGameInv(player.getUniqueId());
		}
	}
}