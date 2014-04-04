package com.lactem.pvz.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class WandCommand implements BasePvZCommand {

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.wand"))return;
		player.getInventory().addItem(new ItemStack(Selection.getUniversalWandId(), 1));
		Messages.sendMessage(player, Messages.getMessage("wand given"));
	}
}