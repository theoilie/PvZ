package me.lactem.pvz.stats;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;

import org.bukkit.configuration.ConfigurationSection;

public class StatsManager {
	private API api = Main.getAPI();

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#getKills(java.util.UUID, boolean)}, but 
	 * reads from a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @return the amount of kills a player has in PvZ
	 */
	public int getKills(String player) {
		return get(player, "kills");
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#getRowsCaptured(java.util.UUID, boolean)}, but 
	 * reads from a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @return the amount of rows a player has captured in PvZ
	 */
	public int getRowsCaptured(String player) {
		return get(player, "rows_captured");
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#getDeaths(java.util.UUID, boolean)}, but 
	 * reads from a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @return the amount of times a player has died in PvZ
	 */
	public int getDeaths(String player) {
		return get(player, "deaths");
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#getGamesPlayed(java.util.UUID, boolean)}, but 
	 * reads from a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @return the amount of times a player has played PvZ
	 */
	public int getGamesPlayed(String player) {
		return get(player, "games_played");
	}
	
	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#getSun(java.util.UUID, boolean)}, but 
	 * reads from a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @return the amount of sun a player has in PvZ
	 */
	public int getSun(String player) {
		return get(player, "sun");
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#setKills(java.util.UUID, int, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @param kills the amount of kills a player has in PvZ
	 */
	public void setKills(String player, int kills) {
		set(player, "kills", kills);
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#setRowsCaptured(java.util.UUID, int, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @param rowsCaptured the amount of rows a player has captured in PvZ
	 */
	public void setRowsCaptured(String player, int rowsCaptured) {
		set(player, "rows_captured", rowsCaptured);
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#setDeaths(java.util.UUID, int, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @param deaths the amount of times a player has died in PvZ
	 */
	public void setDeaths(String player, int deaths) {
		set(player, "deaths", deaths);
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#setGamesPlayed(java.util.UUID, int, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @param gamesPlayed the amount of times a player has played PvZ
	 */
	public void setGamesPlayed(String player, int gamesPlayed) {
		set(player, "games_played", gamesPlayed);
	}
	
	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#setSun(java.util.UUID, int, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 * @param sun the amount of sun a player has in PvZ
	 */
	public void setSun(String player, int sun) {
		set(player, "sun", sun);
	}

	/**
	 * Does the same thing as
	 * {@link me.lactem.pvz.util.sql.SQLUtils#resetStats(String, boolean)}, but 
	 * saves to a local file instead of a MySQL database.
	 * @param player the {@link java.util.UUID} of the player
	 */
	public boolean reset(String player) {
		boolean b = false;
		ConfigurationSection cs = api.getFileUtils().getStats().getConfigurationSection("stats");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection cs2 = cs.getConfigurationSection(s); // stats.<stat>
			if (cs2.getInt(player) != 0) {
				b = true;
				cs2.set(player, 0);
				api.getFileUtils().saveConfig();
			}
		}
		return b;
	}

	private int get(String player, String stat) {
		return api.getFileUtils().getStats().getInt(
				"stats." + stat + "." + player.toLowerCase());
	}

	private void set(String player, String stat, int amount) {
		api.getFileUtils().getStats().set(
				"stats." + stat + "." + player.toLowerCase(), amount);
		api.getFileUtils().saveStats();
	}
}