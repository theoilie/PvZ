package com.lactem.pvz.stats;

import org.bukkit.configuration.ConfigurationSection;

import com.lactem.pvz.main.Main;

public class StatsManager {

	public int getKills(String player) {
		return get(player, "kills");
	}

	public int getRowsCaptured(String player) {
		return get(player, "rows_captured");
	}

	public int getDeaths(String player) {
		return get(player, "deaths");
	}

	public int getGamesPlayed(String player) {
		return get(player, "games_played");
	}

	public void setKills(String player, int kills) {
		set(player, "kills", kills);
	}

	public void setRowsCaptured(String player, int rowsCaptured) {
		set(player, "rows_captured", rowsCaptured);
	}

	public void setDeaths(String player, int deaths) {
		set(player, "deaths", deaths);
	}

	public void setGamesPlayed(String player, int gamesPlayed) {
		set(player, "games_played", gamesPlayed);
	}

	public boolean reset(String player) {
		boolean b = false;
		ConfigurationSection cs = Main.fileUtils.getStats()
				.getConfigurationSection("stats");
		for (String s : cs.getKeys(false)) {
			ConfigurationSection cs2 = cs.getConfigurationSection(s); // stats.<stat>
			if (cs2.getInt(player) != 0) {
				b = true;
				cs2.set(player, 0);
				Main.fileUtils.saveConfig();
			}
		}
		return b;
	}

	private int get(String player, String stat) {
		return Main.fileUtils.getStats().getInt(
				"stats." + stat + "." + player.toLowerCase());
	}

	private void set(String player, String stat, int amount) {
		Main.fileUtils.getStats().set(
				"stats." + stat + "." + player.toLowerCase(), amount);
		Main.fileUtils.saveStats();
	}
}