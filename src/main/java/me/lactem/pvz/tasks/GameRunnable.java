package me.lactem.pvz.tasks;

import java.util.Iterator;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class GameRunnable extends BukkitRunnable {
	private Game game;

	public GameRunnable(Game game) {
		this.game = game;
	}
	
	@Override
	public void run() {
		try {
			game.setTimeLeft(game.getTimeLeft() - 1);
			Objective objective = game.getBoard().getObjective(DisplaySlot.SIDEBAR);
			game.getBoard().resetScores((game.getTimeLeft() + 1) + " seconds");
			
			int num = 0;
			for (int i = 0; i < game.getRows().size(); i++) {
				if (!game.getRows().get(i).isEndpointTaken())
					num++;
			}
			
			game.getBoard().resetScores("" + (num + 1));
			
			Score time = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lTime Remaining"));
			time.setScore(4);
			Score intTime = objective.getScore(game.getTimeLeft() + " seconds");
			intTime.setScore(3);
			Score endpoints = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lRows Remaining"));
			endpoints.setScore(2);
			Score numEnds = objective.getScore("" + num);
			numEnds.setScore(1);
		} catch (NullPointerException e) {}
		
		if (game.getTimeLeft() <= 0) {
			Main.getAPI().getGameManager().endGame(game, true);
			this.cancel();
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
			player.setScoreboard(game.getBoard());
		}
	}
}