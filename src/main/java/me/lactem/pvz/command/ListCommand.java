package me.lactem.pvz.command;

import java.io.File;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.farm.Farm;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class ListCommand implements BasePvZCommand {
	private API api = Main.getAPI();

	public void execute(Player player, String[] args) {
		if (!api.getValidate().hasPerm(player, "pvz.list"))
			return;
		
		boolean map = false;
		Messages.sendMessage(player, "Maps:");
		File dir = new File(api.getPlugin().getDataFolder() + "/farms");
		if (dir.exists()) {
			for (File child : dir.listFiles()) {
				if (child.getName().contains(".txt")) {
					// For in case the file is .DS_Store
					try {
						Farm farm = api.getFarmManager().readFarm(child.getName());
						Messages.sendMessageNoPrefix(player, "- &4" + farm.getName() + ": &6" + farm.getRows().size() + " &4rows");
						map = true;
					} catch (NullPointerException e) {}
				}
			}
		}
		
		if (!map)
			Messages.sendMessage(player, Messages.getMessage("no maps"));
	}
}