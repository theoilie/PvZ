package com.lactem.pvz.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.Row;
import com.lactem.pvz.row.TempRow;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.tasks.Countdown;
import com.lactem.pvz.tasks.Fireworks;
import com.lactem.pvz.tasks.GameRunnable;
import com.lactem.pvz.team.plant.PlantTeam;
import com.lactem.pvz.team.plant.PlantType;
import com.lactem.pvz.team.zombie.ZombieTeam;
import com.lactem.pvz.team.zombie.ZombieType;
import com.lactem.pvz.util.messages.Messages;

public class GameManager {
	public ArrayList<Game> games = new ArrayList<Game>();
	public Plugin plugin;
	public ArrayList<String> countdownStarted = new ArrayList<String>();
	public ArrayList<UUID> deathCountdowns = new ArrayList<UUID>();

	public Game getGame(int slot) {
		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);
			if (game.getSlot() == slot)
				return game;
		}
		return null;
	}

	public Game getGame(Player player) {
		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);
			if (game.getPlants().getMembers().containsKey(player.getUniqueId())
					|| game.getZombies().getMembers()
							.containsKey(player.getUniqueId()))
				return game;
		}
		return null;
	}

	public void updateGame(Game game) {
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getSlot() == game.getSlot()) {
				Main.invManager.updateDesc(game);
				games.set(i, game);
				break;
			}
		}
	}

	public void startCountdown(Game game) {
		game.setState(GameState.STARTING);
		Main.invManager.updateDesc(game);
		Countdown task = new Countdown();
		task.game = game;
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task,
				0l, 20l);
		task.id = id;
		countdownStarted.add(String.valueOf(game.getSlot()));
	}

	public void startGame(Game game) {
		updateRows(game);
		game.setState(GameState.PLAYING);
		Main.invManager.updateDesc(game);
		Scoreboard board = game.getBoard();
		Objective objective = board.registerNewObjective("name", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				Main.fileUtils.getConfig().getString("prefix")));
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					PlantType type = game.getPlants().getMembers().get(uuid);
					Main.invManager.givePlantInventory(player, type, game);
					player.setFoodLevel(20);
					player.setHealth(20.0);
					player.setScoreboard(Bukkit.getScoreboardManager()
							.getNewScoreboard());
					String name = type.toString();
					Team team = board.getTeam(name);
					if (team == null) {
						team = board.registerNewTeam(name);
						team.setCanSeeFriendlyInvisibles(false);
						team.setPrefix(ChatColor.translateAlternateColorCodes(
								'&', "&2"));
					}
					team.addPlayer(player);
				}
			}
		}
		Iterator<UUID> i2 = game.getZombies().getMembers().keySet().iterator();
		while (i2.hasNext()) {
			UUID uuid = i2.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					ZombieType type = game.getZombies().getMembers().get(uuid);
					Main.invManager.giveZombieInventory(player, type, game);
					player.setFoodLevel(20);
					player.setHealth(20.0);
					player.setScoreboard(Bukkit.getScoreboardManager()
							.getNewScoreboard());
					String name = type.toString();
					Team team = board.getTeam(name);
					if (team == null) {
						team = board.registerNewTeam(name);
						team.setCanSeeFriendlyInvisibles(false);
						team.setPrefix(ChatColor.translateAlternateColorCodes(
								'&', "&4"));
					}
					team.addPlayer(player);
				}
			}
		}
		GameRunnable task = new GameRunnable();
		task.game = game;
		int id = Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(plugin, task, 0l, 20l);
		task.id = id;
	}

	public void endGame(final Game game, boolean plantsWon) {
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					if (deathCountdowns.contains(uuid))
						deathCountdowns.remove(uuid);
					Messages.sendMessage(player, Messages
							.getMessage(plantsWon ? "plants won"
									: "zombies won"));
				}
			}
		}
		Iterator<UUID> i2 = game.getZombies().getMembers().keySet().iterator();
		while (i2.hasNext()) {
			UUID uuid = i2.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					if (deathCountdowns.contains(uuid))
						deathCountdowns.remove(uuid);
					Messages.sendMessage(player, Messages
							.getMessage(plantsWon ? "plants won"
									: "zombies won"));
				}
			}
		}
		game.setState(GameState.ENDING);
		Main.invManager.updateDesc(game);
		Fireworks fireworks = new Fireworks();
		fireworks.game = game;
		int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
				fireworks, 0l, 20l);
		fireworks.task = task;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				restartGame(game);
			}
		}, 200l);
	}

	public void restartGame(Game game) {
		game.getBoard().clearSlot(DisplaySlot.SIDEBAR);
		game.setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
		boolean sql = Main.sqlUtils.isUsingMySQL();
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(player.getWorld().getSpawnLocation());
					Main.invManager.removeInventory(player);
					player.setScoreboard(game.getBoard());
					Main.sqlUtils
							.setGamesPlayed(
									player.getName(),
									Main.sqlUtils.getGamesPlayed(
											player.getName(), sql) + 1, sql);
					Main.sqlUtils.setSun(
							player.getName(),
							Main.sqlUtils.getSun(player.getName(), sql)
									+ Main.fileUtils.getConfig().getInt(
											"sun per game"), sql);
				}
			}
		}
		Iterator<UUID> i2 = game.getZombies().getMembers().keySet().iterator();
		while (i2.hasNext()) {
			UUID uuid = i2.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(player.getWorld().getSpawnLocation());
					Main.invManager.removeInventory(player);
					player.setScoreboard(game.getBoard());
					Main.sqlUtils
							.setGamesPlayed(
									player.getName(),
									Main.sqlUtils.getGamesPlayed(
											player.getName(), sql) + 1, sql);
					Main.sqlUtils.setSun(
							player.getName(),
							Main.sqlUtils.getSun(player.getName(), sql)
									+ Main.fileUtils.getConfig().getInt(
											"sun per game"), sql);
				}
			}
		}
		game.setPlants(new PlantTeam());
		game.setZombies(new ZombieTeam());
		game.setTimeLeft(Main.fileUtils.getConfig().getInt("round length") + 1);
		game.setTimeUntilStart(Main.fileUtils.getConfig().getInt(
				"time until start") + 1);
		game.setState(GameState.WAITING);
		updateRows(game);
		if (countdownStarted.contains(String.valueOf(game.getSlot()))) {
			countdownStarted.remove(String.valueOf(game.getSlot()));
		}
		Main.invManager.updateDesc(game);
	}

	public TempRow calculateRow(Game game, boolean plant) {
		ArrayList<TempRow> rows = game.getRows();
		ArrayList<TempRow> rows2 = new ArrayList<TempRow>();
		for (int i = 0; i < rows.size(); i++) {
			if (!rows.get(i).isEndpointTaken())
				rows2.add(rows.get(i));
		}
		while (rows2.size() > 1) {
			rows2 = trim(rows2, plant);
		}
		return rows2.get(0);
	}

	private ArrayList<TempRow> trim(ArrayList<TempRow> rows, boolean plant) {
		ArrayList<TempRow> newRows = new ArrayList<TempRow>();
		for (int i = 0; i < rows.size(); i++) {
			if (i - 1 >= 0) {
				TempRow currentRow = rows.get(i);
				TempRow previousRow = rows.get(i - 1);
				if (plant) {
					if (currentRow.getPlants().size() < previousRow.getPlants()
							.size())
						newRows.add(currentRow);
					else
						newRows.add(previousRow);
				} else {
					if (currentRow.getZombies().size() < previousRow
							.getZombies().size())
						newRows.add(currentRow);
					else
						newRows.add(previousRow);
				}
			}
		}
		return newRows;
	}

	public void updateRows(Game game) {
		ArrayList<TempRow> temps = new ArrayList<TempRow>();
		try {
			for (int i = 0; i < game.getFarm().getRows().size(); i++) {
				Row row = game.getFarm().getRows().get(i);
				TempRow tempRow = new TempRow(row.getZombieSpawn(),
						row.getPlantSpawn(), row.getEndpoint());
				temps.add(tempRow);
			}
		} catch (NullPointerException e) {

		}
		game.setRows(temps);
	}

	public boolean areAllEndpointsClaimed(Game game) {
		for (int i = 0; i < game.getRows().size(); i++) {
			TempRow row = game.getRows().get(i);
			if (!row.isEndpointTaken())
				return false;
		}
		return true;
	}

	public void updateAll(Game game, TempRow row) {
		TempRow newRowPlant = calculateRow(game, true);
		TempRow newRowZombie = calculateRow(game, false);
		for (int i = 0; i < row.getPlants().size(); i++) {
			UUID uuid = row.getPlants().get(i);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(Selection.locationFromString(newRowPlant
							.getPlantSpawn()));
					newRowPlant.getPlants().add(player.getUniqueId());
				}
			}
		}
		for (int i = 0; i < row.getZombies().size(); i++) {
			UUID uuid = row.getZombies().get(i);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(Selection.locationFromString(newRowZombie
							.getZombieSpawn()));
					newRowZombie.getZombies().add(player.getUniqueId());
				}
			}
		}
	}
}