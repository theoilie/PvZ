package com.lactem.pvz.command;

import org.bukkit.entity.Player;


public interface BasePvZCommand {
	public abstract void execute(Player player, String[] args); 
}