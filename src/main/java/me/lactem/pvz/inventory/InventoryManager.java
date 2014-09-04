package me.lactem.pvz.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;
import me.lactem.pvz.row.TempRow;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.team.plant.PlantType;
import me.lactem.pvz.team.zombie.ZombieType;
import me.lactem.pvz.util.messages.Messages;

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

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class InventoryManager {
	private API api = Main.getAPI();
	
	// Order: boots, leggings, chestplate, helmet
	private final ItemStack[] PLANT_ARMOR;
	
	private Inventory gameInv = null;
	private Inventory typeInv = null;
	
	private ArrayList<UUID> inInv = new ArrayList<UUID>();
	private ArrayList<UUID> inTypeInv = new ArrayList<UUID>();
	
	private HashMap<Integer, String> itemsWithPermissions = new HashMap<Integer, String>();
	private HashMap<Integer, PlantType> itemsWithPlantTypes = new HashMap<Integer, PlantType>();
	private HashMap<Integer, ZombieType> itemsWithZombieTypes = new HashMap<Integer, ZombieType>();
	
	public InventoryManager() {
		PLANT_ARMOR = getPlantArmor();
	}
	
	/**
	 * Updates the inventory GUIs for joining and changing type
	 */
	public void updateInventories() {
		updateJoinInventory();
		updateTypeInventory();
	}
	
	/**
	 * Gets the game inventory GUI
	 * @return game inventory GUI from the config
	 */
	public Inventory getGameInv() {
		return gameInv;
	}
	
	/**
	 * Gets the type inventory GUI
	 * @return type inventory GUI from the config
	 */
	public Inventory getTypeInv() {
		return typeInv;
	}
	
	/**
	 * Adds a player to the list of players in the game inventory
	 * @param uuid the unique identifier of the player to add
	 */
	public void addToGameInv(UUID uuid) {
		inInv.add(uuid);
	}
	
	/**
	 * Adds a player to the list of players in the type inventory
	 * @param uuid the unique identifier of the player to add
	 */
	public void addToTypeInv(UUID uuid) {
		inTypeInv.add(uuid);
	}
	
	/**
	 * Checks if a player is in the game inventory
	 * @param uuid the unique identifier of the player to check
	 * @return true if the player is in the game inventory
	 */
	public boolean inGameInvContains(UUID uuid) {
		return inInv.contains(uuid);
	}
	
	/**
	 * Checks if a player is in the type inventory
	 * @param uuid the unique identifier of the player to check
	 * @return true if the player is in the type inventory
	 */
	public boolean inTypeInvContains(UUID uuid) {
		return inTypeInv.contains(uuid);
	}
	/**
	 * Removes a player from the list of players in the game inventory
	 * @param uuid the unique identifier of the player to remove
	 */
	public void removeFromGameInv(UUID uuid) {
		if (inInv.contains(uuid))
		inInv.remove(uuid);
	}
	
	/**
	 * Removes a player from the list of players in the type inventory
	 * @param uuid the unique identifier of the player to remove
	 */
	public void removeFromTypeInv(UUID uuid) {
		if (inTypeInv.contains(uuid))
		inTypeInv.remove(uuid);
	}
	/**
	 * Gets a list of items to be used as game GUI buttons
	 * @return list of items in game inventory
	 */
	public List<ItemStack> getGameItems() {
		List<ItemStack> gameItems = new ArrayList<ItemStack>();
		ConfigurationSection cs = api.getFileUtils().getJoinInv().getConfigurationSection("items");
		
		for (String s : cs.getKeys(false)) {
			gameItems.add(createItem(cs.getConfigurationSection(s)));
		}
		return gameItems;
	}
	
	/**
	 * Updates the description of a game in the game inventory
	 * @param game the game to update
	 */
	public void updateDesc(Game game) {
		List<String> list = game.getList();
		ItemStack item = game.getItem();
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> newList = new ArrayList<String>();
		
		for (int i = 0; i < list.size(); i++) {
			newList = replaceHolders(i, list, newList, game);
		}
		
		meta.setLore(newList);
		item.setItemMeta(meta);
		game.setItemStack(item);
		gameInv.setItem(game.getSlot(), item);
	}

	/**
	 * Replaces placeholders surrounded by "<>" such as map and status in one list of lore.
	 * See {@link #updateDesc(Game)}.
	 * @param index the index to be replaced in the list
	 * @param list the old list to replace from
	 * @param newList the new list that will be added to with replaced placeholders
	 * @param game the game that the item belongs to
	 * @return the new lore of an item in the game inventory
	 */
	public ArrayList<String> replaceHolders(int index, List<String> list, ArrayList<String> newList, Game game) {
		String line = list.get(index);
		newList.add(line);
		String newLine = newList.get(index);
		if (game.getFarm() == null)
			newLine = newLine.replaceAll("<map>", "ERROR: NO MAP FOUND");
		else
			newLine = newLine.replaceAll("<map>", game.getFarm().getName());
		newLine = newLine.replaceAll("<currentplayers>", (game.getZombies().getMembers().size() + game.getPlants().getMembers().size()) + "");
		newLine = newLine.replaceAll("<maxplayers>", game.getMaxPlayers() + "");
		newLine = newLine.replaceAll("<status>", game.getState().toString());
		newList.set(index, ChatColor.translateAlternateColorCodes('&', newLine));
		return newList;
	}
	
	/**
	 * Gives a player his/her items based on type
	 * @param player the player to set the inventory of
	 * @param type the plant type the player has chosen
	 * @param game the game the player is in
	 */
	public void givePlantInventory(Player player, PlantType type, Game game) {
		removeInventory(player);
		PlayerInventory inv = player.getInventory();
		inv.setHelmet(PLANT_ARMOR[3]);
		
		switch (type) {
		case PEASHOOTER:
			inv.setChestplate(PLANT_ARMOR[2]);
			inv.setLeggings(PLANT_ARMOR[1]);
			inv.setBoots(PLANT_ARMOR[0]);
			inv.addItem(new ItemStack(Material.SLIME_BALL, 1));
			break;
		case REPEATER:
			inv.setChestplate(PLANT_ARMOR[2]);
			inv.setLeggings(PLANT_ARMOR[1]);
			inv.setBoots(PLANT_ARMOR[0]);
			ItemStack slimeball = new ItemStack(Material.SLIME_BALL, 1);
			ItemMeta meta = slimeball.getItemMeta();
			meta.setDisplayName("Repeating Slime Ball");
			slimeball.setItemMeta(meta);
			inv.addItem(slimeball);
			break;
		case BONK_CHOY:
			inv.setChestplate(PLANT_ARMOR[2]);
			inv.setLeggings(PLANT_ARMOR[1]);
			inv.setBoots(PLANT_ARMOR[0]);
			inv.addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
			break;
		case WINTER_MELON:
			inv.setChestplate(PLANT_ARMOR[2]);
			inv.setLeggings(PLANT_ARMOR[1]);
			inv.setBoots(PLANT_ARMOR[0]);
			ItemStack snowball = new ItemStack(Material.SNOW_BALL, 1);
			ItemMeta snowMeta = snowball.getItemMeta();
			snowMeta.setDisplayName("Catapult");
			snowball.setItemMeta(snowMeta);
			inv.addItem(snowball);
			break;
		case SUNFLOWER:
			@SuppressWarnings("deprecation")
			ItemStack sunflower = new ItemStack(Material.getMaterial(175));
			ItemMeta sunMeta = sunflower.getItemMeta();
			sunMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2Click to boost."));
			sunflower.setItemMeta(sunMeta);
			inv.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE, 3));
			inv.setLeggings(new ItemStack(Material.GOLD_LEGGINGS, 3));
			inv.setBoots(new ItemStack(Material.GOLD_BOOTS, 3));
			inv.addItem(sunflower);
			break;
		}
		findRow(player, game, true);
	}

	/**
	 * Gives a player his/her items based on type
	 * @param player the player to set the inventory of
	 * @param type the zombie type the player has chosen
	 * @param game the game the player is in
	 */
	public void giveZombieInventory(Player player, ZombieType type, Game game) {
		PlayerInventory inv = player.getInventory();
		removeInventory(player);
		Disguise disguise = null;
		if (api.useDC())
			disguise = new Disguise(api.getDC().newEntityID(), DisguiseType.Zombie);
		
		switch (type) {
		case BASIC:
			inv.addItem(new ItemStack(Material.WOOD_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
			break;
		case CONEHEAD:
			inv.addItem(new ItemStack(Material.STONE_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
			break;
		case GARGANTUAR:
			inv.addItem(new ItemStack(Material.IRON_SWORD, 1));
			inv.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
			inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
			inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
			inv.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 4));
			if (api.useDC())
				disguise = new Disguise(api.getDC().newEntityID(), DisguiseType.Giant);
			break;
		}
		
		findRow(player, game, false);
		
		if (api.useDC()) {
			if (api.getDC().isDisguised(player))
				api.getDC().disguisePlayer(player, disguise);
			else
				api.getDC().changePlayerDisguise(player, disguise);
		}
	}
	
	/**
	 * Resets a player by clearing inventory, removing potion effects, setting the player to survival, resetting food/health, unhiding, and undisguising
	 * @param player the player to be reset
	 */
	public void removeInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		if (player.getGameMode() != GameMode.SURVIVAL)
			player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
		if (api.useDC() && api.getDC().isDisguised(player))
			api.getDC().undisguisePlayer(player);
	}

	/**
	 * Prepares a player for spectating
	 * @param player
	 * @param plantType
	 * @param zombieType
	 * @param game
	 */
	public void giveSpectatingInventory(final Player player, final PlantType plantType, final ZombieType zombieType, final Game game) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0);
		if (player.getGameMode() != GameMode.CREATIVE)
			player.setGameMode(GameMode.CREATIVE);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		Messages.sendMessage(player, Messages.getMessage("respawn"));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(api.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (api.getGameManager().isInDeathCountdown(player.getUniqueId())) {
					if (plantType == null)
						giveZombieInventory(player, zombieType, game);
					else
						givePlantInventory(player, plantType, game);
				}
			}
		}, 400l);
		api.getGameManager().addToDeathCountdown(player.getUniqueId());
	}

	/**
	 * Gets the permission for a specific slot in the inventory
	 * @param slot the slot number to check the permission for
	 * @return the permission for the item in the slot, or "" if there is no permission for the specified slot
	 */
	public String getPermission(int slot) {
		Iterator<Integer> i = itemsWithPermissions.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithPermissions.get(next);
		}
		return "";
	}

	/**
	 * Gets the plant type for a specific slot in the type inventory
	 * @param slot the slot number to check the plant type for
	 * @return the plant type for the item in the slot, or null if there is no plant type associated
	 */
	public PlantType getPlantType(int slot) {
		Iterator<Integer> i = itemsWithPlantTypes.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithPlantTypes.get(next);
		}
		return null;
	}

	/**
	 * Gets the zombie type for a specific slot in the type inventory
	 * @param slot the slot number to check the zombie type for
	 * @return the zombie type for the item in the slot, or null if there is no zombie type associated
	 */
	public ZombieType getZombieType(int slot) {
		Iterator<Integer> i = itemsWithZombieTypes.keySet().iterator();
		while (i.hasNext()) {
			int next = i.next();
			if (slot == next)
				return itemsWithZombieTypes.get(next);
		}
		return null;
	}

	/**
	 * Gets the PlantType for a String
	 * @param name the name of the plant type
	 * @return PlantType for name
	 */
	public PlantType getPlantType(String name) {
		String newName = name.replaceAll(" ", "_");
		for (PlantType type : PlantType.values()) {
			if (type.toString().equalsIgnoreCase(newName))
				return type;
		}
		return null;
	}

	/**
	 * Gets the ZombieType for a String
	 * @param name the name of the zombie type
	 * @return ZombieType for name
	 */
	public ZombieType getZombieType(String name) {
		String newName = name.replaceAll(" ", "_");
		for (ZombieType type : ZombieType.values()) {
			if (type.toString().equalsIgnoreCase(newName))
				return type;
		}
		return null;
	}

	private void updateJoinInventory() {
		createJoinInventory();
		setJoinItems();
	}

	private void updateTypeInventory() {
		createTypeInventory();
		setTypeItems();
	}

	private void createJoinInventory() {
		int size = api.getFileUtils().getJoinInv().getInt("size");
		String name = ChatColor.translateAlternateColorCodes('&', api.getFileUtils().getJoinInv().getString("name"));
		gameInv = Bukkit.getServer().createInventory(null, size % 9 == 0 ? size : 27, name);
	}

	private void createTypeInventory() {
		int size = api.getFileUtils().getTypeInv().getInt("size");
		String name = ChatColor.translateAlternateColorCodes('&', api.getFileUtils().getTypeInv().getString("name"));
		typeInv = Bukkit.getServer().createInventory(null, size % 9 == 0 ? size : 27, name);
	}

	private void setJoinItems() {
		ConfigurationSection cs = api.getFileUtils().getJoinInv().getConfigurationSection("items");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection itemSection = cs.getConfigurationSection(s);
			addItem(itemSection);
		}
	}

	private void setTypeItems() {
		ConfigurationSection cs = api.getFileUtils().getTypeInv().getConfigurationSection("items");
		for (String s : cs.getKeys(false)) {
			addTypeItem(cs.getConfigurationSection(s));
		}
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
		Game game = new Game(slot - 1, GameState.WAITING, item, itemSection.getStringList("desc"), itemSection.getString("map"), itemSection.getInt("max players"));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemSection.getString("name")));
		item.setItemMeta(meta);
		updateDesc(game);
		api.getGameManager().updateRows(game);
		api.getGameManager().addGame(game);
	}

	private void addTypeItem(ConfigurationSection itemSection) {
		ItemStack item = createItem(itemSection);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemSection.getString("name")));
		List<String> descList = itemSection.getStringList("desc");
		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < descList.size(); i++) {
			newList.add(ChatColor.translateAlternateColorCodes('&', descList.get(i)));
		}
		meta.setLore(newList);
		item.setItemMeta(meta);
		int slot = itemSection.getInt("slot");
		itemsWithPermissions.put(slot - 1, itemSection.getString("permission"));
		typeInv.setItem(slot - 1, item);
		itemsWithPlantTypes.put(slot - 1, getPlantType(itemSection.getString("plant type")));
		itemsWithZombieTypes.put(slot - 1, getZombieType(itemSection.getString("zombie type")));
	}

	private void findRow(Player player, Game game, boolean plant) {
		TempRow row = api.getGameManager().calculateRow(game, plant);
		if (row == null) {
			Messages.sendMessage(player, Messages.getMessage("no rows created"));
			return;
		}
		try {
			if (plant) {
				player.teleport(Selection.locationFromString(row.getPlantSpawn()));
				row.getPlants().add(player.getUniqueId());
			} else {
				player.teleport(Selection.locationFromString(row.getZombieSpawn()));
				row.getZombies().add(player.getUniqueId());
			}

		} catch (NullPointerException e) {
			Messages.sendMessage(player, Messages.getMessage("no spawn set"));
		}
		api.getGameManager().removeFromDeathCountdown(player.getUniqueId());
	}

	private ItemStack[] getPlantArmor() {
		if (PLANT_ARMOR != null)
			return PLANT_ARMOR;
		
		// Boots
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bootsLAM = (LeatherArmorMeta) boots.getItemMeta();
		bootsLAM.setColor(Color.LIME);
		boots.setItemMeta(bootsLAM);
		
		// Leggings
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta leggingsLAM = (LeatherArmorMeta) leggings.getItemMeta();
		leggingsLAM.setColor(Color.LIME);
		leggings.setItemMeta(leggingsLAM);
		
		// Chestplate
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestplateLAM = (LeatherArmorMeta) chestplate.getItemMeta();
		chestplateLAM.setColor(Color.LIME);
		chestplate.setItemMeta(chestplateLAM);
		
		// Helmet
		ItemStack helmet = new ItemStack(Material.LEAVES, 1, (short) 3);
		
		return new ItemStack[] {boots, leggings, chestplate, helmet};
	}
}