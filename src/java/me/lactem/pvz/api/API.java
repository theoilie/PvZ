package me.lactem.pvz.api;

import me.lactem.pvz.ability.PlantAbility;
import me.lactem.pvz.ability.ZombieAbility;
import me.lactem.pvz.command.CommandManager;
import me.lactem.pvz.command.PvZCommand;
import me.lactem.pvz.farm.FarmManager;
import me.lactem.pvz.game.GameManager;
import me.lactem.pvz.inventory.InventoryManager;
import me.lactem.pvz.team.TeamManager;
import me.lactem.pvz.util.ability.AbilityUtil;
import me.lactem.pvz.util.files.FileUtils;
import me.lactem.pvz.util.messages.MessageUtil;
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
	private CommandManager cmdManager;
	private MessageUtil msgUtil;
	private AbilityUtil abilityUtil;
	private PlantAbility plantAbility;
	private ZombieAbility zombieAbility;
	private SQLUtils sqlUtil;
	private Validate validate;
	private DisguiseCraftAPI dc;
	private boolean debug;
	
	public API(JavaPlugin plugin) {
		if (this.plugin != null)
			throw new AssertionError("The PvZ API has already been initialized, but something else tried to re-initialize it.");
		
		this.plugin = plugin;
		fileUtils = new FileUtils(plugin);
		invManager = new InventoryManager();
		cmdManager = new CommandManager();
		gameManager = new GameManager();
		teamManager = new TeamManager();
		farmManager = new FarmManager();
		msgUtil = new MessageUtil();
		plantAbility = new PlantAbility();
		zombieAbility = new ZombieAbility();
		abilityUtil = new AbilityUtil();
		sqlUtil = new SQLUtils();
		validate = new Validate();
		debug = fileUtils.getConfig().getBoolean("enable debugging");
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
	public SQLUtils getSqlUtil() {
		return sqlUtil;
	}
	
	/**
	 * Gets the class for checking various data
	 * @return PvZ's validation class
	 */
	public Validate getValidate() {
		return validate;
	}
	
	/**
	 * Gets the class that manages commands
	 * @return the CommandManager instance
	 */
	public CommandManager getCmdManager() {
		return cmdManager;
	}
	
	/**
	 * Gets the class that handles sending messages from the messages.yml files
	 * @return the MessageUtil instance
	 */
	public MessageUtil getMessageUtil() {
		return msgUtil;
	}
	
	/**
	 * Gets the class that handles special abilities
	 * @return the AbilityUtil instance
	 */
	public AbilityUtil getAbilityUtil() {
		return abilityUtil;
	}
	
	/**
	 * Gets the class that handles plant abilities
	 * @return the PlantAbility instance
	 */
	public PlantAbility getPlantAbility() {
		return plantAbility;
	}
	
	/**
	 * Gets the class that handles zombie abilities
	 * @return the ZombieAbility class
	 */
	public ZombieAbility getZombieAbility() {
		return zombieAbility;
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
	 * Checks if debugging is enabled.
	 * @return true if debugging mode is enabled
	 */
	public boolean debug() {
		return debug;
	}
	
	/**
	 * Updates any classes that have an API field so that they are not null.
	 */
	public void updateReferences() {
		invManager.setAPI();
		teamManager.setAPI();
		gameManager.setAPI();
		sqlUtil.setAPI();
		msgUtil.setAPI();
		abilityUtil.setAPI();
		plantAbility.setAPI();
		zombieAbility.setAPI();
		PvZCommand.setAPI();
	}
	
	/**
	 * Sends commands to a player based on his/her permissions
	 * @param player the player to send help information to
	 */
	public void sendHelp(Player player) {
		// hasAny is for checking if the player has any permissions for any commands at all.
		boolean hasAny = false;
		for (PvZCommand cmd : cmdManager.getRegisteredCommands()) {
			if (player.hasPermission(cmd.getPermission())) {
				msgUtil.sendMessage(player, "/" + cmd.getName());
				hasAny = true;
			}
		}
		if (!hasAny)
			msgUtil.sendMessage(player, msgUtil.getMessage("no permission"));
	}
}