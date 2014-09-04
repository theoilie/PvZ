package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class JoinCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	@Override
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.join"))
			return;
		
		if (api.getGameManager().isPlayerInGame(player)) {
			Messages.sendMessage(player, Messages.getMessage("already in game"));
		} else {
			player.openInventory(api.getInvManager().getGameInv());
			api.getInvManager().removeFromGameInv(player.getUniqueId());;
		}
	}
}