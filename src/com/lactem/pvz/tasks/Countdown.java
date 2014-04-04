package com.lactem.pvz.tasks;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.util.messages.Messages;

public class Countdown implements Runnable {
	public Game game;
	public int id;
	private UUID uuid;
	Iterator<UUID> iterator;

	@Override
	public void run() {
		game.setTimeUntilStart(game.getTimeUntilStart() - 1);
		if (game.getTimeUntilStart() == 0) {
			Main.gameManager.startGame(game);
			Bukkit.getScheduler().cancelTask(id);
			return;
		}
		if (game.getTimeUntilStart() % 5 == 0 || game.getTimeUntilStart() < 6) {
			iterator = game.getPlants().getMembers().keySet().iterator();
			while (iterator.hasNext()) {
				uuid = iterator.next();
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getUniqueId() == uuid) {
						Messages.sendMessage(
								player,
								Messages.getMessage("starts in")
										.replaceAll("<time>",
												game.getTimeUntilStart() + ""));
					}
				}
			}
			iterator = game.getZombies().getMembers().keySet().iterator();
			while (iterator.hasNext()) {
				uuid = iterator.next();
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getUniqueId() == uuid) {
						Messages.sendMessage(
								player,
								Messages.getMessage("starts in")
										.replaceAll("<time>",
												game.getTimeUntilStart() + ""));
					}
				}
			}
		}
	}
}