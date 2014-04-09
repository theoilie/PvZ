package com.lactem.pvz.tasks;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.main.Main;

public class RemoveFromCooldown implements Runnable {
	public UUID uuid;
	public int gameID;
	public boolean isSunflower = false;
	public boolean isWinterMelon = false;

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Game game = Main.gameManager.getGame(gameID);
		if (game == null)
			return;
		ArrayList<UUID> inCooldown = game.getInCooldown();
		if (inCooldown.contains(uuid)) {
			inCooldown.remove(uuid);
			game.setInCooldown(inCooldown);
		}
		if (isSunflower) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					ItemStack item;
					ItemStack[] contents = player.getInventory().getContents();
					for (int i = 0; i < contents.length; i++) {
						ItemStack currentItem = contents[i];
						if (currentItem != null) {
							if (currentItem.getType() == Material
									.getMaterial(175)) {
								item = currentItem;
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(ChatColor
										.translateAlternateColorCodes('&',
												"&2Click to boost."));
								item.setItemMeta(meta);
								player.getInventory().setItem(i, item);
								break;
							}
						}
					}
				}
			}
		} else if (isWinterMelon) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					ItemStack snowball2 = new ItemStack(Material.SNOW_BALL, 1);
					ItemMeta snowMeta = snowball2.getItemMeta();
					snowMeta.setDisplayName("Catapult");
					snowball2.setItemMeta(snowMeta);
					player.setItemInHand(snowball2);
				}
			}
		}
	}
}