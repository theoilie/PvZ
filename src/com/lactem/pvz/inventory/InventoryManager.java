package com.lactem.pvz.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.game.GameState;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.TempRow;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.team.plant.PlantType;
import com.lactem.pvz.team.zombie.ZombieType;
import com.lactem.pvz.util.messages.Messages;

public class InventoryManager {
	public Inventory gameInv = null;
	public Inventory typeInv = null;
	public ArrayList<UUID> inInv = new ArrayList<UUID>();
	public ArrayList<UUID> inTypeInv = new ArrayList<UUID>();
	public HashMap<Integer, String> itemsWithPermissions = new HashMap<Integer, String>();
	public HashMap<Integer, PlantType> itemsWithPlantTypes = new HashMap<Integer, PlantType>();
	public HashMap<Integer, ZombieType> itemsWithZombieTypes = new HashMap<Integer, ZombieType>();

	public void updateInventory() {
		int size = Main.fileUtils.getConfig().getInt("inventory.size");
		String name = ChatColor.translateAlternateColorCodes('&',
				Main.fileUtils.getConfig().getString("inventory.name"));
		gameInv = Bukkit.getServer().createInventory(null,
				size % 9 == 0 ? size : 27, name);
		ConfigurationSection cs = Main.fileUtils.getConfig()
				.getConfigurationSection("inventory.items");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection itemSection = cs.getConfigurationSection(s);
			addItem(itemSection);
		}
	}

	public void updateTypeInventory() {
		int size = Main.fileUtils.getConfig().getInt("type inventory.size");
		String name = ChatColor.translateAlternateColorCodes('&',
				Main.fileUtils.getConfig().getString("type inventory.name"));
		typeInv = Bukkit.getServer().createInventory(null,
				size % 9 == 0 ? size : 27, name);
		ConfigurationSection cs = Main.fileUtils.getConfig()
				.getConfigurationSection("type inventory.items");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection itemSection = cs.getConfigurationSection(s);
			addTypeItem(itemSection);
		}
	}

	public ArrayList<ItemStack> getGameItems() {
		ArrayList<ItemStack> gameItems = new ArrayList<ItemStack>();
		ConfigurationSection cs = Main.fileUtils.getConfig()
				.getConfigurationSection("inventory.items");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection itemSection = cs.getConfigurationSection(s);
			gameItems.add(createItem(itemSection));
		}
		return gameItems;
	}

	private ItemStack createItem(ConfigurationSection itemSection) {
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(itemSection.getInt("item id"),
				itemSection.getInt("item amount"));
		return item;
	}

	private void addItem(ConfigurationSection itemSection) {
		ItemStack item = createItem(itemSection);
		int slot = itemSection.getInt("slot");
		Game game = new Game(slot - 1, GameState.WAITING, item,
				itemSection.getStringList("desc"),
				itemSection.getString("map"), itemSection.getInt("max players"));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				itemSection.getString("name")));
		item.setItemMeta(meta);
		updateDesc(game);
		Main.gameManager.updateRows(game);
		Main.gameManager.games.add(game);
	}

	private void addTypeItem(ConfigurationSection itemSection) {
		ItemStack item = createItem(itemSection);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				itemSection.getString("name")));
		List<String> descList = itemSection.getStringList("desc");
		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < descList.size(); i++) {
			newList.add(ChatColor.translateAlternateColorCodes('&',
					descList.get(i)));
		}
		meta.setLore(newList);
		item.setItemMeta(meta);
		int slot = itemSection.getInt("slot");
		itemsWithPermissions.put(slot - 1, itemSection.getString("permission"));
		typeInv.setItem(slot - 1, item);
		itemsWithPlantTypes.put(slot - 1,
				getPlantType(itemSection.getString("plant type")));
		itemsWithZombieTypes.put(slot - 1,
				getZombieType(itemSection.getString("zombie type")));
	}

	public void updateDesc(Game game) {
		List<String> list = game.getList();
		ItemStack item = game.getItem();
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			newList.add(line);
			String newLine = newList.get(i);
			if (game.getFarm() == null)
				newLine = newLine.replaceAll("<map>", "ERROR: NO MAP FOUND");
			else
				newLine = newLine.replaceAll("<map>", game.getFarm().getName());
			newLine = newLine.replaceAll("<currentplayers>",
					(game.getZombies().getMembers().size() + game.getPlants()
							.getMembers().size())
							+ "");
			newLine = newLine.replaceAll("<maxplayers>", game.getMaxPlayers()
					+ "");
			newLine = newLine
					.replaceAll("<status>", game.getState().toString());
			newList.set(i, ChatColor.translateAlternateColorCodes('&', newLine));
		}
		meta.setLore(newList);
		item.setItemMeta(meta);
		game.setItemStack(item);
		gameInv.setItem(game.getSlot(), item);
	}

	public void givePlantInventory(Player player, PlantType type, Game game) {
		removeInventory(player);
		PlayerInventory inv = player.getInventory();
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta chestplateLAM = (LeatherArmorMeta) chestplate
				.getItemMeta();
		chestplateLAM.setColor(Color.LIME);
		chestplate.setItemMeta(chestplateLAM);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsLAM = (LeatherArmorMeta) chestplate
				.getItemMeta();
		leggingsLAM.setColor(Color.LIME);
		leggings.setItemMeta(leggingsLAM);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		LeatherArmorMeta bootsLAM = (LeatherArmorMeta) chestplate.getItemMeta();
		bootsLAM.setColor(Color.LIME);
		boots.setItemMeta(bootsLAM);
		inv.setHelmet(new ItemStack(Material.LEAVES, 1, (short) 3));
		switch (type) {
		case PEASHOOTER:
			inv.setChestplate(chestplate);
			inv.setLeggings(leggings);
			inv.setBoots(boots);
			inv.addItem(new ItemStack(Material.SLIME_BALL, 1));
			break;
		case REPEATER:
			inv.setChestplate(chestplate);
			inv.setLeggings(leggings);
			inv.setBoots(boots);
			ItemStack slimeball = new ItemStack(Material.SLIME_BALL, 1);
			ItemMeta meta = slimeball.getItemMeta();
			meta.setDisplayName("Repeating Slime Ball");
			slimeball.setItemMeta(meta);
			inv.addItem(slimeball);
			break;
		}
		TempRow row = Main.gameManager.calculateRow(game, true);
		player.teleport(Selection.locationFromString(row.getPlantSpawn()));
		row.getPlants().add(player.getUniqueId());
		if (Main.gameManager.deathCountdowns.contains(player.getUniqueId()))
			Main.gameManager.deathCountdowns.remove(player.getUniqueId());
	}

	public void giveZombieInventory(Player player, ZombieType type, Game game) {
		PlayerInventory inv = player.getInventory();
		removeInventory(player);
		switch (type) {
		case BASIC:
			inv.addItem(new ItemStack(Material.WOOD_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					Integer.MAX_VALUE, 1));
			break;
		case CONEHEAD:
			inv.addItem(new ItemStack(Material.STONE_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					Integer.MAX_VALUE, 1));
			break;
		case GARGANTUAR:
			inv.addItem(new ItemStack(Material.IRON_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
			inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
			inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
			inv.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
			player.addPotionEffect(new PotionEffect(
					PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 2));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					Integer.MAX_VALUE, 4));
			break;
		}
		TempRow row = Main.gameManager.calculateRow(game, false);
		player.teleport(Selection.locationFromString(row.getZombieSpawn()));
		row.getZombies().add(player.getUniqueId());
		if (Main.gameManager.deathCountdowns.contains(player.getUniqueId()))
			Main.gameManager.deathCountdowns.remove(player.getUniqueId());
	}

	public void removeInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		if (player.getGameMode() != GameMode.SURVIVAL)
			player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
	}

	public void giveSpectatingInventory(final Player player,
			final PlantType plantType, final ZombieType zombieType,
			final Game game) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0);
		if (player.getGameMode() != GameMode.CREATIVE)
			player.setGameMode(GameMode.CREATIVE);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		Messages.sendMessage(player, Messages.getMessage("respawn"));
		Bukkit.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(Main.gameManager.plugin,
						new Runnable() {
							@Override
							public void run() {
								if (Main.gameManager.deathCountdowns
										.contains(player.getUniqueId())) {
									if (plantType == null) {
										giveZombieInventory(player, zombieType,
												game);
									} else {
										givePlantInventory(player, plantType,
												game);
									}
								}
							}
						}, 400l);
		Main.gameManager.deathCountdowns.add(player.getUniqueId());
	}

	public String getPermission(int slot) {
		Iterator<Integer> i = itemsWithPermissions.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithPermissions.get(next);
		}
		return "";
	}

	public PlantType getPlantType(int slot) {
		Iterator<Integer> i = itemsWithPlantTypes.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithPlantTypes.get(next);
		}
		return null;
	}

	public ZombieType getZombieType(int slot) {
		Iterator<Integer> i = itemsWithZombieTypes.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithZombieTypes.get(next);
		}
		return null;
	}

	public PlantType getPlantType(String name) {
		String newName = name.replaceAll(" ", "_");
		for (PlantType type : PlantType.values()) {
			if (type.toString().equalsIgnoreCase(newName))
				return type;
		}
		return null;
	}

	public ZombieType getZombieType(String name) {
		String newName = name.replaceAll(" ", "_");
		for (ZombieType type : ZombieType.values()) {
			if (type.toString().equalsIgnoreCase(newName))
				return type;
		}
		return null;
	}
}