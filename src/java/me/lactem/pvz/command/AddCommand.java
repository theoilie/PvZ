package me.lactem.pvz.command;

import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.Selection;

import org.bukkit.entity.Player;

public class AddCommand extends PvZAdminCommand {

	public AddCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (!getAPI().getValidate().hasSelection(player))
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
		farm.getRows().add(new Row(Selection.toSerializableSelection(Selection.getPlayerSelection(player))));
		getAPI().getFarmManager().writeFarm(farm);
		getAPI().getMessageUtil().sendMessage(player,
				getAPI().getMessageUtil().getMessage("row created").replaceAll("<row>", farm.getRows().size() + "").replaceAll("<farm>", farm.getName()));
	}
}