package me.lactem.pvz.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.row.Row;
import me.lactem.pvz.row.TempRow;
import me.lactem.pvz.selection.Selection;
import me.lactem.pvz.tasks.Countdown;
import me.lactem.pvz.tasks.Fireworks;
import me.lactem.pvz.tasks.GameRunnable;
import me.lactem.pvz.team.plant.PlantTeam;
import me.lactem.pvz.team.plant.PlantType;
import me.lactem.pvz.team.zombie.ZombieTeam;
import me.lactem.pvz.team.zombie.ZombieType;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;

public class GameManager {
	private API api = Main.getAPI();
	
	private ArrayList<Game> games = new ArrayList<Game>();
	private ArrayList<Integer> countdownStarted = Lists.newArrayList();
	private ArrayList<UUID> deathCountdowns = Lists.newArrayList();

	public Game getGame(int slot) {
		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);
			if (game.getSlot() == slot)
				return game;
		}
		return null;
	}

	/**
	 * Gets the game that a player is in
	 * @param player the player to check for
	 * @return the game that the player is in, or null if there is none
	 */
	public Game getGame(Player player) {
		for (Game game : games) {
			if (game.getPlants().getMembers().containsKey(player.getUniqueId()) || game.getZombies().getMembers().containsKey(player.getUniqueId()))
				return game;
		}
		return null;
	}
	
	/**
	 * Checks if a player is in a game
	 * @param player the player to check
	 * @return true if the player is in a game
	 */
	public boolean isPlayerInGame(Player player) {
		return getGame(player) != null;
	}

	/**
	 * Updates the item that represents this game instance in the game menu
	 * @param game the game to be updated
	 */
	public void updateGame(Game game) {
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getSlot() == game.getSlot()) {
				api.getInvManager().updateDesc(game);
				games.set(i, game);
				break;
			}
		}
	}

	/**
	 * Starts the countdown for a game to start
	 * @param game the game to start a countdown for
	 */
	public void startCountdown(Game game) {
		game.setState(GameState.STARTING);
		api.getInvManager().updateDesc(game);
		Countdown task = new Countdown(game);
		task.runTaskTimer(api.getPlugin(), 0l, 20l);
		countdownStarted.add(game.getSlot());
	}
	
	/**
	 * Checks if the countdown has started for a game
	 * @param game the game to check
	 * @return true if the countdown
	 */
	public boolean hasCountDownStarted(Game game) {
		return countdownStarted.contains(game.getSlot());
	}

	/**
	 * Starts a game
	 * @param game the game to start
	 */
	public void startGame(Game game) {
		updateRows(game);
		game.setState(GameState.PLAYING);
		api.getInvManager().updateDesc(game);
		Scoreboard board = game.getBoard();
		Objective objective = board.registerNewObjective("name", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', api.getFileUtils().getConfig().getString("prefix")));
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					PlantType type = game.getPlants().getMembers().get(uuid);
					api.getInvManager().givePlantInventory(player, type, game);
					player.setFoodLevel(20);
					player.setHealth(20.0);
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					String name = type.toString();
					Team team = board.getTeam(name);
					if (team == null) {
						team = board.registerNewTeam(name);
						team.setCanSeeFriendlyInvisibles(false);
						team.setPrefix(ChatColor.translateAlternateColorCodes('&', "&2"));
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
					api.getInvManager().giveZombieInventory(player, type, game);
					player.setFoodLevel(20);
					player.setHealth(20.0);
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					String name = type.toString();
					Team team = board.getTeam(name);
					if (team == null) {
						team = board.registerNewTeam(name);
						team.setCanSeeFriendlyInvisibles(false);
						team.setPrefix(ChatColor.translateAlternateColorCodes('&', "&4"));
					}
					team.addPlayer(player);
				}
			}
		}
		
		GameRunnable task = new GameRunnable(game);
		task.runTaskTimer(api.getPlugin(), 0l, 20l);
	}

	/**
	 * Ends a game
	 * @param game the game to end
	 * @param plantsWon whether or not the plant team won
	 */
	public void endGame(final Game game, boolean plantsWon) {
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					if (deathCountdowns.contains(uuid))
						deathCountdowns.remove(uuid);
					Messages.sendMessage(player, Messages.getMessage(plantsWon ? "plants won" : "zombies won"));
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
					Messages.sendMessage(player, Messages.getMessage(plantsWon ? "plants won" : "zombies won"));
				}
			}
		}
		game.setState(GameState.ENDING);
		api.getInvManager().updateDesc(game);
		Fireworks fireworks = new Fireworks(game);
		fireworks.runTaskTimer(api.getPlugin(), 0l, 20l);
		Bukkit.getScheduler().scheduleSyncDelayedTask(api.getPlugin(), new Runnable() {
			@Override
			public void run() {
				restartGame(game);
			}
		}, 200l);
	}

	/**
	 * Restarts a game after it ended so that new players can join
	 * @param game the game to restart
	 */
	public void restartGame(Game game) {
		game.getBoard().clearSlot(DisplaySlot.SIDEBAR);
		game.setBoard(Bukkit.getScoreboardManager().getNewScoreboard());
		boolean sql = api.getSqlUtils().isUsingMySQL();
		Iterator<UUID> i = game.getPlants().getMembers().keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(player.getWorld().getSpawnLocation());
					api.getInvManager().removeInventory(player);
					player.setScoreboard(game.getBoard());
					api.getSqlUtils().setGamesPlayed(player.getUniqueId(), api.getSqlUtils().getGamesPlayed(player.getUniqueId(), sql) + 1, sql);
					api.getSqlUtils().setSun(player.getUniqueId(), api.getSqlUtils().getSun(player.getUniqueId(), sql) + api.getFileUtils().getConfig().getInt("sun per game"), sql);
				}
			}
		}
		Iterator<UUID> i2 = game.getZombies().getMembers().keySet().iterator();
		while (i2.hasNext()) {
			UUID uuid = i2.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(player.getWorld().getSpawnLocation());
					api.getInvManager().removeInventory(player);
					player.setScoreboard(game.getBoard());
					api.getSqlUtils().setGamesPlayed(player.getUniqueId(), api.getSqlUtils().getGamesPlayed(player.getUniqueId(), sql) + 1, sql);
					api.getSqlUtils().setSun(player.getUniqueId(), api.getSqlUtils().getSun(player.getUniqueId(), sql) + api.getFileUtils().getConfig().getInt("sun per game"), sql);
				}
			}
		}
		game.setPlants(new PlantTeam());
		game.setZombies(new ZombieTeam());
		game.setTimeLeft(api.getFileUtils().getConfig().getInt("round length") + 1);
		game.setTimeUntilStart(api.getFileUtils().getConfig().getInt("time until start") + 1);
		game.setState(GameState.WAITING);
		updateRows(game);
		if (countdownStarted.contains(game.getSlot())) {
			countdownStarted.remove(Integer.valueOf(game.getSlot()));
		}
		api.getInvManager().updateDesc(game);
	}

	/**
	 * Gets an available row
	 * @param game the game to get the row for
	 * @param plant whether or not this row is for a plant
	 * @return a row for a player to spawn in
	 */
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

	/**
	 * Updates all the rows in a game
	 * @param game the game to update rows in
	 */
	public void updateRows(Game game) {
		ArrayList<TempRow> temps = new ArrayList<TempRow>();
		try {
			for (int i = 0; i < game.getFarm().getRows().size(); i++) {
				Row row = game.getFarm().getRows().get(i);
				TempRow tempRow = new TempRow(row.getZombieSpawn(),
						row.getPlantSpawn(), row.getEndpoint());
				temps.add(tempRow);
			}
		} catch (NullPointerException e) {}
		game.setRows(temps);
	}

	/**
	 * Checks if all the endpoints have been claimed by the zombie team
	 * @param game the game to check
	 * @return true if all endpoints have been taken
	 */
	public boolean areAllEndpointsClaimed(Game game) {
		for (int i = 0; i < game.getRows().size(); i++) {
			TempRow row = game.getRows().get(i);
			if (!row.isEndpointTaken())
				return false;
		}
		return true;
	}

	/**
	 * Teleports all the players of one row to the next one (for when a row is captured)
	 * @param game the game with the row
	 * @param row the row that has been captured and needs to be "updated"
	 */
	public void updateAll(Game game, TempRow row) {
		TempRow newRowPlant = calculateRow(game, true);
		TempRow newRowZombie = calculateRow(game, false);
		for (int i = 0; i < row.getPlants().size(); i++) {
			UUID uuid = row.getPlants().get(i);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(Selection.locationFromString(newRowPlant.getPlantSpawn()));
					newRowPlant.getPlants().add(player.getUniqueId());
				}
			}
		}
		for (int i = 0; i < row.getZombies().size(); i++) {
			UUID uuid = row.getZombies().get(i);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.teleport(Selection.locationFromString(newRowZombie.getZombieSpawn()));
					newRowZombie.getZombies().add(player.getUniqueId());
				}
			}
		}
	}

	/**
	 * Adds a game to the list of games
	 * @param game the game to be added
	 */
	public void addGame(Game game) {
		games.add(game);
	}
	
	/**
	 * Removes a game from the list of games if it exists
	 * @param game the game to be removed
	 */
	public void removeGame(Game game) {
		if (games.contains(game))
			games.remove(game);
	}
	/**
	 * Adds a player to the death countdowns list
	 * @param uuid the unique identifier of the player to add
	 */
	public void addToDeathCountdown(UUID uuid) {
		deathCountdowns.add(uuid);
	}
	/**
	 * Checks if a player is waiting to respawn because he/she died
	 * @param uuid the unique identifier of the player
	 * @return true if the player is in the countdown list
	 */
	public boolean isInDeathCountdown(UUID uuid) {
		return deathCountdowns.contains(uuid);
	}
	
	/**
	 * Removes a player from the death countdown list if he/she is in it
	 * @param uuid the unique identifier of the player in the list
	 */
	public void removeFromDeathCountdown(UUID uuid) {
		if (deathCountdowns.contains(uuid))
			deathCountdowns.remove(uuid);
	}
	
	private ArrayList<TempRow> trim(ArrayList<TempRow> rows, boolean plant) {
		ArrayList<TempRow> newRows = new ArrayList<TempRow>();
		for (int i = 0; i < rows.size(); i++) {
			if (i - 1 >= 0) {
				TempRow currentRow = rows.get(i);
				TempRow previousRow = rows.get(i - 1);
				if (plant) {
					if (currentRow.getPlants().size() < previousRow.getPlants().size())
						newRows.add(currentRow);
					else
						newRows.add(previousRow);
				} else {
					if (currentRow.getZombies().size() < previousRow.getZombies().size())
						newRows.add(currentRow);
					else
						newRows.add(previousRow);
				}
			}
		}
		return newRows;
	}
}