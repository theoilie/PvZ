package com.lactem.pvz.command;

import java.io.File;

import org.bukkit.entity.Player;

import com.lactem.pvz.farm.Farm;
import com.lactem.pvz.main.Main;

import com.lactem.pvz.util.messages.Messages;

public class ListCommand implements BasePvZCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (!Main.validate.hasPerm(player, "pvz.list"))
			return;
		boolean map = false;
		Messages.sendMessage(player, "Maps:");
		File dir = new File(Main.farmManager.plugin.getDataFolder() + "/farms");
		if (dir.exists()) {
			for (File child : dir.listFiles()) {
				if (child.getName().contains(".txt")) {
					// For in case the file is .DS_Store
					try {
						Farm farm = Main.farmManager.readFarm(child.getName());
						Messages.sendMessageNoPrefix(player,
								"- &4" + farm.getName() + ": &6"
										+ farm.getRows().size() + " &4rows");
						map = true;
					} catch (NullPointerException e) {
					}
				}
			}
		}
		if (!map)
			Messages.sendMessage(player, Messages.getMessage("no maps"));
	}
}