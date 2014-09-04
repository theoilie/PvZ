package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.wand"))
			return;
		
		player.getInventory().addItem(new ItemStack(Selection.getUniversalWandId(), 1));
		Messages.sendMessage(player, Messages.getMessage("wand given"));
	}
}