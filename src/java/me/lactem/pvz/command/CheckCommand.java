package me.lactem.pvz.command;

import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;

import org.bukkit.entity.Player;

public class CheckCommand extends PvZAdminCommand {

	public CheckCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (args.length < 2) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no farm specified"));
			return;
		}
		if (!getAPI().getValidate().farmExists(args[1])) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no farm exists"));
			return;
		}
		
		Farm farm = getAPI().getFarmManager().readFarm(args[1] + ".txt");
		String name = farm.getName();
		getAPI().getMessageUtil().sendMessage(player, "&8-----------------------&2P&6v&4Z&8----------------------");
		getAPI().getMessageUtil().sendMessage(player, "Farm: &4" + name);
		getAPI().getMessageUtil().sendMessage(player, "Rows created: &4" + farm.getRows().size());
		getAPI().getMessageUtil().sendMessage(player, "Exisiting problems:");
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
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing an &6endpoint&4. Use &2/pvz endpoint " + name + " " + row + " &4to set it.");
			break;
		case "plant spawn":
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing a &6plant spawn&4. Use &2/pvz setspawn " + name + " " + row + " plant &4to set it.");
			break;
		case "zombie spawn":
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &4Row &6" + row + "&4 is missing a &6zombie spawn&4. Use &2/pvz setspawn " + name + " " + row + " zombie &4to set it.");
			break;
		case "no rows":
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &4This farm is missing rows. Use &2/pvz add to add one!");
			break;
		case "nothing":
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &2No problems! &rIf you would like to add more rows, just type &2/pvz add " + name + "&r.");
			break;
		default:
			getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &2No problems! &rIf you would like to add more rows, just type &2/pvz add " + name + "&r.");
		}
	}
}