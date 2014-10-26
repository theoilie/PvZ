package me.lactem.pvz.command;

import me.lactem.pvz.endpoint.Endpoint;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;

import org.bukkit.entity.Player;

public class EndpointCommand extends PvZAdminCommand {

	public EndpointCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (args.length < 2) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no farm given"));
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
		
		row.setEndpoint(new Endpoint(Selection.locationToString(player.getLocation())));
		getAPI().getFarmManager().writeFarm(farm);
		getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("endpoint set"));
	}
}