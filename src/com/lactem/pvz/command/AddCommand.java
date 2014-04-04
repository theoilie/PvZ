package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.Row;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class AddCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.add"))
			return;
		if (!Main.validate.hasSelection(player))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player,
					Messages.getMessage("no farm specified"));
			return;
		}
		if (!Main.validate.farmExists(args[1], player)) {
			Messages.sendMessage(player, Messages.getMessage("no farm exists"));
			return;
		}
		Farm farm = Main.farmManager.readFarm(args[1] + ".txt");
		farm.getRows().add(
				new Row(Selection.toSerializableSelection(Selection
						.getPlayerSelection(player))));
		Main.farmManager.writeFarm(farm);
		Messages.sendMessage(
				player,
				Messages.getMessage("row created")
						.replaceAll("<row>", farm.getRows().size() + "")
						.replaceAll("<farm>", farm.getName()));
	}
}