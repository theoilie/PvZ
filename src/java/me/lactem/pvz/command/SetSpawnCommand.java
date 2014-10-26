package me.lactem.pvz.command;

import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;

import org.bukkit.entity.Player;

public class SetSpawnCommand extends PvZAdminCommand {

	public SetSpawnCommand(String name, String permission) {
		super(name, permission);
	}
	
	public SetSpawnCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
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
		if (args.length < 3) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no row given"));
			return;
		}
		
		int rowNum = 0;
		try {
			rowNum = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("row not a number").replaceAll("<number>", args[2]));
			return;
		}
		
		Farm farm = getAPI().getFarmManager().readFarm(args[1] + ".txt");
		Row row = null;
		try {
			row = farm.getRows().get(rowNum - 1);
		} catch (IndexOutOfBoundsException e) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no row found").replaceAll("<number>", rowNum + ""));
		}
		if (args.length < 4) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("which team"));
			return;
		}
		
		if (args[3].equalsIgnoreCase("plant")) {
			row.setPlantSpawn(Selection.locationToString(player.getLocation()));
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("row set plant").replaceAll("<row>", rowNum + "").replaceAll("<farm>", farm.getName()));
		} else if (args[3].equalsIgnoreCase("zombie")) {
			row.setZombieSpawn(Selection.locationToString(player.getLocation()));
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("row set zombie").replaceAll("<row>", rowNum + "").replaceAll("<farm>", farm.getName()));
		} else {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no team found").replaceAll("<team>", args[3]));
			return;
		}
		getAPI().getFarmManager().writeFarm(farm);
	}
}