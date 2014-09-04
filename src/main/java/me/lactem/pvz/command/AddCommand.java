package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class AddCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.add"))
			return;
		if (!api.getValidate().hasSelection(player))
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
		farm.getRows().add(new Row(Selection.toSerializableSelection(Selection.getPlayerSelection(player))));
		api.getFarmManager().writeFarm(farm);
		Messages.sendMessage(player, Messages.getMessage("row created").replaceAll("<row>", farm.getRows().size() + "").replaceAll("<farm>", farm.getName()));
	}
}