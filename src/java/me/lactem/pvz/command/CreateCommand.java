package me.lactem.pvz.command;

import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.selection.Selection;

import org.bukkit.entity.Player;

public class CreateCommand extends PvZAdminCommand {

	public CreateCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (!getAPI().getValidate().hasSelection(player))
			return;
		if (args.length < 2) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no farm given"));
			return;
		}
		if (getAPI().getValidate().farmExists(args[1])) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("name taken"));
			return;
		}
		
		Farm farm = new Farm(args[1], Selection.toSerializableSelection(Selection.getPlayerSelection(player)));
		getAPI().getFarmManager().writeFarm(farm);
		getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("farm created"));
	}
}