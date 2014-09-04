package me.lactem.pvz.command;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class CreateCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.create"))
			return;
		if (!api.getValidate().hasSelection(player))
			return;
		if (args.length < 2) {
			Messages.sendMessage(player, Messages.getMessage("no farm given"));
			return;
		}
		if (api.getValidate().farmExists(args[1])) {
			Messages.sendMessage(player, Messages.getMessage("name taken"));
			return;
		}
		
		Farm farm = new Farm(args[1], Selection.toSerializableSelection(Selection.getPlayerSelection(player)));
		api.getFarmManager().writeFarm(farm);
		Messages.sendMessage(player, Messages.getMessage("farm created"));
	}
}