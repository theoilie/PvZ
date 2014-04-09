package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.Row;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class SetSpawnCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.setspawn"))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player, Messages.getMessage("no farm given"));
			return;
		}
		if (!Main.validate.farmExists(args[1], player)) {
			Messages.sendMessage(player, Messages.getMessage("no farm exists"));
			return;
		}
		if (args.length < 3) {
			Messages.sendMessage(player, Messages.getMessage("no row given"));
			return;
		}
		int rowNum = 0;
		try {
			rowNum = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			Messages.sendMessage(
					player,
					Messages.getMessage("row not a number").replaceAll(
							"<number>", args[2]));
			return;
		}
		Farm farm = Main.farmManager.readFarm(args[1] + ".txt");
		Row row = null;
		try {
			row = farm.getRows().get(rowNum - 1);
		} catch (IndexOutOfBoundsException e) {
			Messages.sendMessage(player, Messages.getMessage("no row found")
					.replaceAll("<number>", rowNum + ""));
		}
		if (args.length < 4) {
			Messages.sendMessage(player, Messages.getMessage("which team"));
			return;
		}
		if (args[3].equalsIgnoreCase("plant")) {
			row.setPlantSpawn(Selection.locationToString(player.getLocation()));
			Messages.sendMessage(
					player,
					Messages.getMessage("row set plant")
							.replaceAll("<row>", rowNum + "")
							.replaceAll("<farm>", farm.getName()));
		} else if (args[3].equalsIgnoreCase("zombie")) {
			row.setZombieSpawn(Selection.locationToString(player.getLocation()));
			Messages.sendMessage(
					player,
					Messages.getMessage("row set zombie")
							.replaceAll("<row>", rowNum + "")
							.replaceAll("<farm>", farm.getName()));
		} else {
			Messages.sendMessage(player, Messages.getMessage("no team found")
					.replaceAll("<team>", args[3]));
			return;
		}
		Main.farmManager.writeFarm(farm);
	}
}