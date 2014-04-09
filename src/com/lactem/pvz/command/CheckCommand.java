package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.Row;
import com.lactem.pvz.util.messages.Messages;

public class CheckCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.check"))
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

	private void missing(Player player, int rowNum, String name, String what) {
		if (what.equals("endpoint")) {
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + rowNum
					+ "&4 is missing an &6endpoint&4. Use &2/pvz endpoint "
					+ name + " " + rowNum + " &4to set it.");
		} else if (what.equals("plant spawn")) {
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + rowNum
					+ "&4 is missing a &6plant spawn&4. Use &2/pvz setspawn "
					+ name + " " + rowNum + " plant &4to set it.");
		} else if (what.equals("zombie spawn")) {
			Messages.sendMessageNoPrefix(player, "- &4Row &6" + rowNum
					+ "&4 is missing a &6zombie spawn&4. Use &2/pvz setspawn "
					+ name + " " + rowNum + " zombie &4to set it.");
		} else if (what.equals("nothing")) {
			Messages.sendMessageNoPrefix(player,
					"- &2No problems! &rIf you would like to add more rows, just type &2/pvz add "
							+ name + "&r.");
		} else if (what.equals("no rows")) {
			Messages.sendMessageNoPrefix(player,
					"- &4You have not created any rows. To do this, type &2/pvz add "
							+ name + "&r.");
		}
	}
}