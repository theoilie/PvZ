package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class SetSpawnCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.setspawn"))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player, Messages.getMessage("no farm given"));
			return;
		}
		if (!api.getValidate().farmExists(args[1])) {
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
			Messages.sendMessage(player, Messages.getMessage("row not a number").replaceAll("<number>", args[2]));
			return;
		}
		
		Farm farm = api.getFarmManager().readFarm(args[1] + ".txt");
		Row row = null;
		try {
			row = farm.getRows().get(rowNum - 1);
		} catch (IndexOutOfBoundsException e) {
			Messages.sendMessage(player, Messages.getMessage("no row found").replaceAll("<number>", rowNum + ""));
		}
		if (args.length < 4) {
			Messages.sendMessage(player, Messages.getMessage("which team"));
			return;
		}
		
		if (args[3].equalsIgnoreCase("plant")) {
			row.setPlantSpawn(Selection.locationToString(player.getLocation()));
			Messages.sendMessage(player, Messages.getMessage("row set plant").replaceAll("<row>", rowNum + "").replaceAll("<farm>", farm.getName()));
		} else if (args[3].equalsIgnoreCase("zombie")) {
			row.setZombieSpawn(Selection.locationToString(player.getLocation()));
			Messages.sendMessage(player, Messages.getMessage("row set zombie").replaceAll("<row>", rowNum + "").replaceAll("<farm>", farm.getName()));
		} else {
			Messages.sendMessage(player, Messages.getMessage("no team found").replaceAll("<team>", args[3]));
			return;
		}
		api.getFarmManager().writeFarm(farm);
	}
}