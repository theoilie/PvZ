package me.lactem.pvz;

import me.lactem.pvz.api.API;
import me.lactem.pvz.command.AddCommand;
import me.lactem.pvz.command.CheckCommand;
import me.lactem.pvz.command.CreateCommand;
import me.lactem.pvz.command.EndpointCommand;
import me.lactem.pvz.command.JoinCommand;
import me.lactem.pvz.command.LeaveCommand;
import me.lactem.pvz.command.ListCommand;
import me.lactem.pvz.command.ResetCommand;
import me.lactem.pvz.command.SetSpawnCommand;
import me.lactem.pvz.command.StatsCommand;
import me.lactem.pvz.command.TypeCommand;
import me.lactem.pvz.command.WandCommand;
import me.lactem.pvz.event.Events;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plants Versus Zombies for minecraft - main class!
 * @author Lactem
 */
public class Main extends JavaPlugin {
	private static API api;
	
	// Commands
	private final WandCommand wand = new WandCommand();
	private final CreateCommand create = new CreateCommand();
	private final AddCommand add = new AddCommand();
	private final SetSpawnCommand setspawn = new SetSpawnCommand();
	private final EndpointCommand endpoint = new EndpointCommand();
	private final JoinCommand join = new JoinCommand();
	private final LeaveCommand leave = new LeaveCommand();
	private final TypeCommand type = new TypeCommand();
	private final CheckCommand check = new CheckCommand();
	private final StatsCommand stats = new StatsCommand();
	private final ListCommand list = new ListCommand();
	private final ResetCommand reset = new ResetCommand();

	public void onEnable() {
		api = new API(this);
		
		Messages.setPrefix(api.getFileUtils().getConfig().getString("prefix"));
		Selection.setUniversalWandId(api.getFileUtils().getConfig().getInt("wand id"));
		Selection.setPermission("pvz.wand");
		
		getServer().getPluginManager().registerEvents(new Events(), this);
		
		api.getInvManager().updateInventories();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvz")) {
			if (!api.getValidate().isPlayer(sender))
				return true;
			Player player = (Player) sender;
			if (!api.getValidate().areArgs(args, player))
				return true;
			
			switch (args[0].toLowerCase()) {
			case "wand":
				wand.execute(player, args);
				break;
			case "create":
				create.execute(player, args);
				break;
			case "add":
				add.execute(player, args);
				break;
			case "setspawn":
				setspawn.execute(player, args);
				break;
			case "endpoint":
				endpoint.execute(player, args);
				break;
			case "join":
				join.execute(player, args);
				break;
			case "leave":
				leave.execute(player, args);
				break;
			case "type":
				type.execute(player, args);
				break;
			case "check":
				check.execute(player, args);
				break;
			case "list":
				list.execute(player, args);
				break;
			case "stats":
				stats.execute(player, args);
				break;
			case "reset":
			case "resetstas":
				reset.execute(player, args);
				break;
			default:
				Messages.sendMessage(player, Messages.getMessage("no command found"));
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
	
	/**
	 * Gets the PvZ API
	 * @return Plants Versus Zombies API
	 */
	public static API getAPI() {
		return api;
	}
}