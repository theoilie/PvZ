package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class CreateCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.create"))
			return;
		if (!Main.validate.hasSelection(player))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player, Messages.getMessage("no farm given"));
			return;
		}
		if (Main.validate.farmExists(args[1], player)) {
			Messages.sendMessage(player, Messages.getMessage("name taken"));
			return;
		}
		Farm farm = new Farm(args[1],
				Selection.toSerializableSelection(Selection
						.getPlayerSelection(player)));
		Main.farmManager.writeFarm(farm);
		Messages.sendMessage(player, Messages.getMessage("farm created"));
	}
}