package me.lactem.pvz.command;

import me.lactem.pvz.selection.Selection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandCommand extends PvZAdminCommand {

	public WandCommand(String name, String permission) {
		super(name, permission);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		
		player.getInventory().addItem(new ItemStack(Selection.getWandId(), 1));
		getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("wand given"));
	}
}