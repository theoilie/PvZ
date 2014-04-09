package com.lactem.pvz.util.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.lactem.pvz.main.Main;
import com.lactem.pvz.stats.StatsManager;

public class SQLUtils {
	private Connection connection;
	private StatsManager statsManager = new StatsManager();

	public void openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String host = Main.fileUtils.getConfig().getString(
					"stats.MySQL.host");
			String port = Main.fileUtils.getConfig().getString(
					"stats.MySQL.port");
			String database = Main.fileUtils.getConfig().getString(
					"stats.MySQL.database");
			String user = Main.fileUtils.getConfig().getString(
					"stats.MySQL.user");
			String pass = Main.fileUtils.getConfig().getString(
					"stats.MySQL.pass");
			connection = DriverManager.getConnection("jdbc:mysql://" + host
					+ ":" + port + "/" + database, user, pass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int getKills(String player, boolean sql) {
		if (sql)
			return get(player, "kills");
		else
			return statsManager.getKills(player);
	}

	public int getRowsCaptured(String player, boolean sql) {
		if (sql)
			return get(player, "rows_captured");
		else
			return statsManager.getRowsCaptured(player);
	}

	public int getDeaths(String player, boolean sql) {
		if (sql)
			return get(player, "deaths");
		else
			return statsManager.getDeaths(player);
	}

	public int getGamesPlayed(String player, boolean sql) {
		if (sql)
			return get(player, "games_played");
		else
			return statsManager.getGamesPlayed(player);
	}

	public void setKills(String player, int kills, boolean sql) {
		if (sql)
			set(player, "kills", kills);
		else
			statsManager.setKills(player, kills);
	}

	public void setRowsCaptured(String player, int rowsCaptured, boolean sql) {
		if (sql)
			set(player, "rows_captured", rowsCaptured);
		else
			statsManager.setRowsCaptured(player, rowsCaptured);
	}

	public void setDeaths(String player, int deaths, boolean sql) {
		if (sql)
			set(player, "deaths", deaths);
		else
			statsManager.setDeaths(player, deaths);
	}

	public void setGamesPlayed(String player, int gamesPlayed, boolean sql) {
		if (sql)
			set(player, "games_played", gamesPlayed);
		else
			statsManager.setGamesPlayed(player, gamesPlayed);
	}

	public boolean resetStats(String player, boolean sql) {
		if (sql) {
			ResultSet rs = null;
			PreparedStatement preparedStatement = null;
			Statement statement = null;
			boolean b;
			try {
				if (connection == null)
					openConnection();
				statement = connection.createStatement();
				b = statement.execute("SELECT * FROM PvZPlayerStats");
				if (b) {
					rs = statement.getResultSet();
					while (rs.next()) {
						String name = rs.getString(1);
						if (name.equals(player.toLowerCase())) {
							preparedStatement = connection
									.prepareStatement("DELETE FROM PvZPlayerStats WHERE PlayerName = ?");
							preparedStatement
									.setString(1, player.toLowerCase());
							preparedStatement.execute();
							return true;
						}
					}
				}
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE,
						"Error with MySQL: " + e.getMessage());
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (preparedStatement != null)
						preparedStatement.close();
				} catch (SQLException e) {
					Bukkit.getLogger().log(Level.SEVERE,
							"Error with MySQL: " + e.getMessage());
				}
			}
			return false;
		} else
			return statsManager.reset(player);
	}

	private int get(String player, String query) {
		ResultSet rs = null;
		Statement statement = null;
		boolean b;
		int stat = 0;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			makeNewPlayer(player);
			b = statement.execute("SELECT * FROM PvZPlayerStats");
			if (b) {
				rs = statement.getResultSet();
				while (rs.next()) {
					String name = rs.getString(1);
					int kills = rs.getInt(2);
					int deaths = rs.getInt(3);
					int gamesPlayed = rs.getInt(4);
					int rowsCaptured = rs.getInt(5);
					if (name.equals(player.toLowerCase())) {
						if (query.equals("kills"))
							stat = kills;
						else if (query.equals("deaths"))
							stat = deaths;
						else if (query.equals("games_played"))
							stat = gamesPlayed;
						else if (query.equals("rows_captured"))
							stat = rowsCaptured;
					}
				}
			}
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Error with MySQL: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE,
						"Error with MySQL: " + e.getMessage());
			}
		}
		return stat;
	}

	private void set(String player, String stat, int amount) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS PvZPlayerStats (PlayerName varchar(200), kills int, deaths int, games_played int, rows_captured int)");
			makeNewPlayer(player);
			preparedStatement = connection
					.prepareStatement("UPDATE PvZPlayerStats SET " + stat
							+ " = ? WHERE PlayerName = ?");
			preparedStatement.setInt(1, amount);
			preparedStatement.setString(2, player.toLowerCase());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE,
						"Error with MySQL: " + e.getMessage());
			}
		}
	}

	public boolean makeNewPlayer(String player) {
		Statement statement = null;
		ResultSet rs = null;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS PvZPlayerStats (PlayerName varchar(200), kills int, deaths int, games_played int, rows_captured int)");
			rs = statement
					.executeQuery("SELECT * FROM PvZPlayerStats WHERE PlayerName = '"
							+ player.toLowerCase() + "' LIMIT 1");
			if (!rs.next()) {
				statement
						.execute("INSERT INTO PvZPlayerStats (PlayerName, kills, deaths, games_played, rows_captured) VALUES ('"
								+ player.toLowerCase() + "', 0, 0, 0, 0)");
			}
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Error with MySQL: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE,
						"Error with MySQL: " + e.getMessage());
			}
		}
		return false;
	}

	public boolean isUsingMySQL() {
		return Main.fileUtils.getConfig().getBoolean("stats.use MySQL");
	}
}