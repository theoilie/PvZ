package me.lactem.pvz;

import me.lactem.pvz.api.API;
import me.lactem.pvz.command.PvZCommand;
import me.lactem.pvz.command.WandCommand;
import me.lactem.pvz.event.Events;
import me.lactem.pvz.selection.Selection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plants Versus Zombies for minecraft - main class.
 * @author Lactem
 */
public class Main extends JavaPlugin {
	private static API api;
	
	public void onEnable() {	
		api = new API(this);
		api.updateReferences();
		api.getCmdManager().registerDefaultCommands();
		
		api.getMessageUtil().setPrefix(api.getFileUtils().getConfig().getString("prefix"));
		Selection.setWandId(api.getFileUtils().getConfig().getInt("wand id"));
		Selection.setPermission(api.getCmdManager().getPermission(WandCommand.class));
		
		getServer().getPluginManager().registerEvents(new Events(api), this);
		
		api.getInvManager().updateInventories();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvz")) {
			if (!api.getValidate().isPlayer(sender))
				return true;
			Player player = (Player) sender;
			if (!api.getValidate().areArgs(args, player))
				return true;
			
			for (PvZCommand pvzCmd : api.getCmdManager().getRegisteredCommands().toArray(new PvZCommand[0])) {
				if (pvzCmd.isAcceptedLabel(args[0])) {
					pvzCmd.execute(player, args);
					return true;
				}
			}
			api.getMessageUtil().sendMessage(player, api.getMessageUtil().getMessage("no command found"));
			api.sendHelp(player);
			return true;
		}
		return true;
	}
	
	/**
	 * Gets the PvZ API
	 * @return Plants Versus Zombies API
	 */
	public static API getAPI() {
		return api;
	}
}