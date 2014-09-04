package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class LeaveCommand implements BasePvZCommand {
	private API api = Main.getAPI();
	
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.leave"))
			return;
		if (!api.getGameManager().isPlayerInGame(player)) {
			Messages.sendMessage(player, Messages.getMessage("not in a game"));
			return;
		}
		
		api.getTeamManager().removePlant(player);
		api.getTeamManager().removeZombie(player);
		api.getGameManager().removeFromDeathCountdown(player.getUniqueId());
		Messages.sendMessage(player, Messages.getMessage("left"));
	}
}