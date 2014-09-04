package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.endpoint.Endpoint;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class EndpointCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	@Override
	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.set"))
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
		
		row.setEndpoint(new Endpoint(Selection.locationToString(player.getLocation())));
		api.getFarmManager().writeFarm(farm);
		Messages.sendMessage(player, Messages.getMessage("endpoint set"));
	}
}