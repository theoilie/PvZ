package com.lactem.pvz.command;

import org.bukkit.entity.Player;

import com.lactem.pvz.endpoint.Endpoint;
import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.Row;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.util.messages.Messages;

public class EndpointCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.set"))
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
		row.setEndpoint(new Endpoint(Selection.locationToString(player
				.getLocation())));
		Main.farmManager.writeFarm(farm);
		Messages.sendMessage(player, Messages.getMessage("endpoint set"));
	}
}