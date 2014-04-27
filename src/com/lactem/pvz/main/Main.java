package com.lactem.pvz.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

import com.lactem.pvz.command.AddCommand;
import com.lactem.pvz.command.CheckCommand;
import com.lactem.pvz.command.CreateCommand;
import com.lactem.pvz.command.EndpointCommand;
import com.lactem.pvz.command.JoinCommand;
import com.lactem.pvz.command.LeaveCommand;
import com.lactem.pvz.command.ListCommand;
import com.lactem.pvz.command.ResetCommand;
import com.lactem.pvz.command.SetSpawnCommand;
import com.lactem.pvz.command.StatsCommand;
import com.lactem.pvz.command.TypeCommand;
import com.lactem.pvz.command.WandCommand;
import com.lactem.pvz.event.Events;
import com.lactem.pvz.farm.FarmManager;
import com.lactem.pvz.game.GameManager;
import com.lactem.pvz.inventory.InventoryManager;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.team.TeamManager;
import com.lactem.pvz.util.files.FileUtils;
import com.lactem.pvz.util.messages.Messages;
import com.lactem.pvz.util.sql.SQLUtils;
import com.lactem.pvz.util.validate.Validate;

public class Main extends JavaPlugin {
	public static final FileUtils fileUtils = FileUtils.getInstance();
	public static final InventoryManager invManager = new InventoryManager();
	public static final GameManager gameManager = new GameManager();
	public static final TeamManager teamManager = new TeamManager();
	public static final FarmManager farmManager = new FarmManager();
	public static final SQLUtils sqlUtils = new SQLUtils();
	public static final Validate validate = new Validate();
	public static DisguiseCraftAPI dc = null;
	private WandCommand wand = new WandCommand();
	CreateCommand create = new CreateCommand();
	AddCommand add = new AddCommand();
	SetSpawnCommand setSpawn = new SetSpawnCommand();
	EndpointCommand end = new EndpointCommand();
	JoinCommand join = new JoinCommand();
	LeaveCommand leave = new LeaveCommand();
	TypeCommand type = new TypeCommand();
	CheckCommand check = new CheckCommand();
	StatsCommand stats = new StatsCommand();
	ListCommand list = new ListCommand();
	ResetCommand reset = new ResetCommand();

	public void onEnable() {
		farmManager.plugin = this;
		validate.plugin = this;
		gameManager.plugin = this;
		fileUtils.setup(this);
		Messages.setPrefix(fileUtils.getConfig().getString("prefix"));
		Selection.setUniversalWandId(fileUtils.getConfig().getInt("wand id"));
		Selection.setPermission("pvz.wand");
		if (fileUtils.getConfig().getBoolean("use disguisecraft"))
			dc = DisguiseCraft.getAPI();
		getServer().getPluginManager().registerEvents(new Events(), this);
		invManager.updateInventories();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvz")) {
			if (!Main.validate.isPlayer(sender))
				return true;
			Player player = (Player) sender;
			if (!Main.validate.areArgs(args, player))
				return true;
			/*
			 * I'm not using a switch statement here because you can only switch
			 * on a string in Java 7 and higher and some servers are still on
			 * Java 6.
			 */
			if (args[0].equalsIgnoreCase("wand")) {
				wand.execute(player, args);
			} else if (args[0].equalsIgnoreCase("create")) {
				create.execute(player, args);
			} else if (args[0].equalsIgnoreCase("add")) {
				add.execute(player, args);
			} else if (args[0].equalsIgnoreCase("setspawn")) {
				setSpawn.execute(player, args);
			} else if (args[0].equalsIgnoreCase("endpoint")) {
				end.execute(player, args);
			} else if (args[0].equalsIgnoreCase("join")) {
				join.execute(player, args);
			} else if (args[0].equalsIgnoreCase("leave")) {
				leave.execute(player, args);
			} else if (args[0].equalsIgnoreCase("type")) {
				type.execute(player, args);
			} else if (args[0].equalsIgnoreCase("check")) {
				check.execute(player, args);
			} else if (args[0].equalsIgnoreCase("list")) {
				list.execute(player, args);
			} else if (args[0].equalsIgnoreCase("stats")) {
				stats.execute(player, args);
			} else if (args[0].equalsIgnoreCase("reset")) {
				reset.execute(player, args);
			} else {
				Messages.sendMessage(player,
						Messages.getMessage("no command found"));
				sendHelp(player);
			}
			return true;
		}
		return true;
	}

	public static void sendHelp(Player player) {
		// hasAny is for checking if the player has any permissions for any
		// commands at all.
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