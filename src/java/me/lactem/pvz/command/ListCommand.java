package me.lactem.pvz.command;

import java.io.File;

import me.lactem.pvz.farm.Farm;

import org.bukkit.entity.Player;

public class ListCommand extends PvZAdminCommand {

	public ListCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		
		boolean map = false;
		getAPI().getMessageUtil().sendMessage(player, "Maps:");
		File dir = new File(getAPI().getPlugin().getDataFolder() + "/farms");
		if (dir.exists()) {
			for (File child : dir.listFiles()) {
				if (child.getName().contains(".txt")) {
					// For in case the file is .DS_Store
					try {
						Farm farm = getAPI().getFarmManager().readFarm(child.getName());
						getAPI().getMessageUtil().sendMessageNoPrefix(player, "- &4" + farm.getName() + ": &6" + farm.getRows().size() + " &4rows");
						map = true;
					} catch (NullPointerException e) {}
				}
			}
		}
		
		if (!map)
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no maps"));
	}
}