package me.lactem.pvz.command;

import org.bukkit.entity.Player;

public class LeaveCommand extends PvZPlayerCommand {

	public LeaveCommand(String name, String permission) {
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
		
		getAPI().getTeamManager().removePlant(player);
		getAPI().getTeamManager().removeZombie(player);
		getAPI().getGameManager().removeFromDeathCountdown(player.getUniqueId());
		getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("left"));
	}
}