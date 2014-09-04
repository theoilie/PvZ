package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class CheckCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.check"))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player,
					Messages.getMessage("no farm specified"));
			return;
		}
		if (!api.getValidate().farmExists(args[1])) {
			Messages.sendMessage(player, Messages.getMessage("no farm exists"));
			return;
		}
		
		Farm farm = api.getFarmManager().readFarm(args[1] + ".txt");
		String name = farm.getName();
		Messages.sendMessage(player,
				"&8-----------------------&2P&6v&4Z&8-----------------------");
		Messages.sendMessage(player, "Farm: &4" + name);
		Messages.sendMessage(player, "Rows created: &4" + farm.getRows().size());
		Messages.sendMessage(player, "Exisiting problems:");
		if (farm.getRows().size() == 0) {
			missing(player, 0, name, "no rows");
			return;
		}
		
		boolean problem = false;
		
		for (Row row : farm.getRows()) {
			int rowNum = farm.getRows().indexOf(row) + 1;
			if (row.getEndpoint() == null) {
				missing(player, rowNum, name, "endpoint");
				problem = true;
			}
			if (row.getPlantSpawn() == null) {
				missing(player, rowNum, name, "plant spawn");
				problem = true;
			}
			if (row.getZombieSpawn() == null) {
				missing(player, rowNum, name, "zombie spawn");
				problem = true;
			}
		}
		if (!problem)
			missing(player, 0, name, "nothing");
	}

	private void missing(Player player, int row, String name, String what) {
		switch (what) {
		case "endpoint":
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing an &6endpoint&4. Use &2/pvz endpoint " + name + " " + row + " &4to set it.");
			break;
		case "plant spawn":
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing a &6plant spawn&4. Use &2/pvz setspawn " + name + " " + row + " plant &4to set it.");
			break;
		case "zombie spawn":
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing a &6zombie spawn&4. Use &2/pvz setspawn " + name + " " + row + " zombie &4to set it.");
			break;
		case "nothing":
			Messages.sendMessageNoPrefix(player, "- &2No problems! &rIf you would like to add more rows, just type &2/pvz add " + name + "&r.");
			break;
		default:
			Messages.sendMessageNoPrefix(player, "- &2No problems! &rIf you would like to add more rows, just type &2/pvz add " + name + "&r.");
		}
	}
}