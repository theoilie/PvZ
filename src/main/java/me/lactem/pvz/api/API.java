package me.lactem.pvz.api;

import me.lactem.pvz.farm.FarmManager;
import me.lactem.pvz.game.GameManager;
import me.lactem.pvz.inventory.InventoryManager;
import me.lactem.pvz.team.TeamManager;
import me.lactem.pvz.util.files.FileUtils;
import me.lactem.pvz.util.messages.Messages;
import me.lactem.pvz.util.sql.SQLUtils;
import me.lactem.pvz.util.validate.Validate;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

public class API {
	private final JavaPlugin plugin;
	
	private FileUtils fileUtils;
	private InventoryManager invManager;
	private GameManager gameManager;
	private TeamManager teamManager;
	private FarmManager farmManager;
	private SQLUtils sqlUtils;
	private Validate validate;
	private DisguiseCraftAPI dc;
	
	public API(JavaPlugin plugin) {
		if (plugin != null)
			throw new AssertionError("The PvZ API has already been initialized, but something else tried to re-initialize it.");
		
		this.plugin = plugin;
		fileUtils = new FileUtils(plugin);
		invManager = new InventoryManager();
		gameManager = new GameManager();
		teamManager = new TeamManager();
		farmManager = new FarmManager();
		sqlUtils = new SQLUtils();
		validate = new Validate();
		if (useDC())
			dc = DisguiseCraft.getAPI();
	}
	
	/**
	 * Gets the utility class for files, including the config
	 * @return PvZ's file utility class
	 */
	public FileUtils getFileUtils() {
		return fileUtils;
	}
	
	/**
	 * Gets the class that manages inventories
	 * @return inventory manager class
	 */
	public InventoryManager getInvManager() {
		return invManager;
	}
	
	/**
	 * Gets the class that manages games
	 * @return game manager class
	 */
	public GameManager getGameManager() {
		return gameManager;
	}
	
	/**
	 * Gets the class that manages teams
	 * @return team manager class
	 */
	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	/**
	 * Gets the class that manages farms (arenas)
	 * @return farm manager class
	 */
	public FarmManager getFarmManager() {
		return farmManager;
	}
	
	/**
	 * Gets the class that handles SQL queries
	 * @return PvZ's SQL utility class
	 */
	public SQLUtils getSqlUtils() {
		return sqlUtils;
	}
	
	/**
	 * Gets the class for checking various data
	 * @return PvZ's validation class
	 */
	public Validate getValidate() {
		return validate;
	}
	
	/**
	 * Gets the main class
	 * @return PvZ main class
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets whether or not to use DisguiseCraft
	 * @return true if "use disguisecraft" is set to true in the config
	 */
	public boolean useDC() {
		return fileUtils.getConfig().getBoolean("use disguisecraft");
	}
	
	/**
	 * Gets the DisguiseCraftAPI instance used by PvZ. <br>
	 * See {@link #useDC()}.
	 * @return api of DisguiseCraft that is being used by PvZ
	 */
	public DisguiseCraftAPI getDC() {
		return dc;
	}
	
	/**
	 * Sends commands to a player based on his/her permissions
	 * @param player the player to send help information to
	 */
	public void sendHelp(Player player) {
		// hasAny is for checking if the player has any permissions for any commands at all.
		boolean hasAny = false;
		
		if (player.hasPermission("pvz.wand")) {
			Messages.sendMessage(player, "/pvz wand");
			hasAny = true;
		}
		if (player.hasPermission("pvz.create")) {
			Messages.sendMessage(player, "/pvz create <name>");
			hasAny = true;
		}
		if (player.hasPermission("pvz.add")) {
			Messages.sendMessage(player, "/pvz add <farm>");
			hasAny = true;
		}
		if (player.hasPermission("pvz.setspawn")) {
			Messages.sendMessage(player,
					"/pvz setspawn <farm> <row> <zombie|plant>");
			hasAny = true;
		}
		if (player.hasPermission("pvz.endpoint")) {
			Messages.sendMessage(player, "/pvz endpoint <farm> <row>");
			hasAny = true;
		}
		if (player.hasPermission("pvz.join")) {
			Messages.sendMessage(player, "/pvz join");
			hasAny = true;
		}
		if (player.hasPermission("pvz.leave")) {
			Messages.sendMessage(player, "/pvz leave");
			hasAny = true;
		}
		if (player.hasPermission("pvz.type")) {
			Messages.sendMessage(player, "/pvz type");
			hasAny = true;
		}
		if (player.hasPermission("pvz.check")) {
			Messages.sendMessage(player, "/pvz check <farm>");
			hasAny = true;
		}
		if (player.hasPermission("pvz.list")) {
			Messages.sendMessage(player, "/pvz list");
			hasAny = true;
		}
		if (player.hasPermission("pvz.stats")) {
			Messages.sendMessage(player, "/pvz stats [player]");
			hasAny = true;
		}
		if (player.hasPermission("pvz.reset")) {
			Messages.sendMessage(player, "/pvz reset <player>");
			hasAny = true;
		}
		if (!hasAny)
			Messages.sendMessage(player, Messages.getMessage("no permission"));
	}
}