package me.lactem.pvz.tasks;

import java.util.Iterator;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {
	private Game game;

	public GameRunnable(Game game) {
		this.game = game;
	}
	
	@Override
	public void run() {
		game.setTimeLeft(game.getTimeLeft() - 1);
		game.getBoard().update();
		
		if (game.getState() == GameState.ENDING)
			cancel();
		if (game.getTimeLeft() <= 0) {
			Main.getAPI().getGameManager().endGame(game, true);
			cancel();
		}
		
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				update(player, uuid);
			}
		}
		Iterator<UUID> i2 = game.getZombies().getMembers().keySet().iterator();
		while (i2.hasNext()) {
			UUID uuid = i2.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				update(player, uuid);
			}
		}
	}

	private void update(Player player, UUID uuid) {
		if (player.getUniqueId() == uuid) {
			player.setFoodLevel(20);
			player.setScoreboard(game.getBoard().getBoard());
		}
	}
}