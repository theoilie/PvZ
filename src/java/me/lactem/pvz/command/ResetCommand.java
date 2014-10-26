package me.lactem.pvz.command;

import org.bukkit.entity.Player;

public class ResetCommand extends PvZAdminCommand {

	public ResetCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!canExecute(player))
			return;
		if (args.length < 2) {
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no player specified"));
			return;
		}
		
		String name = args[1].toLowerCase();
		boolean sql = getAPI().getSqlUtil().isUsingMySQL();
		if (getAPI().getSqlUtil().resetStats(name, sql))
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("reset stats").replaceAll("<player>", name));
		else
			getAPI().getMessageUtil().sendMessage(player, getAPI().getMessageUtil().getMessage("no player exists"));
	}
}