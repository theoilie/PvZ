package me.lactem.pvz.command;

import me.lactem.pvz.Main;

import org.bukkit.entity.Player;

public abstract class PvZPlayerCommand extends PvZCommand {

	public PvZPlayerCommand(String name, String permission) {
		super(name, permission);
	}

	public PvZPlayerCommand(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}
	
	@Override
	public boolean canExecute(Player player) {
		return player.hasPermission("pvz.player") || Main.getAPI().getValidate().hasPerm(player, getPermission());
	}
}