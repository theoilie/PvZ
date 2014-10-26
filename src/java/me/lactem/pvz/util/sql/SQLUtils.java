package me.lactem.pvz.util.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import me.lactem.pvz.Main;
import me.lactem.pvz.stats.StatsManager;

import org.bukkit.Bukkit;

public class SQLUtils {
	private static Connection connection;
	private StatsManager statsManager = new StatsManager();

	/**
	 * Closes the database connection
	 */
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets a player's kills while playing PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return the amount of kills a player has
	 */
	public int getKills(UUID uuid, boolean sql) {
		if (sql)
			return get(uuid.toString(), "kills");
		else
			return statsManager.getKills(uuid.toString());
	}

	/**
	 * Gets how many rows a player has captured in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return the amount of rows a player has captured
	 */
	public int getRowsCaptured(UUID uuid, boolean sql) {
		if (sql)
			return get(uuid.toString(), "rows_captured");
		else
			return statsManager.getRowsCaptured(uuid.toString());
	}

	/**
	 * Gets how many times a player died in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return how many times a player has died in PvZ
	 */
	public int getDeaths(UUID uuid, boolean sql) {
		if (sql)
			return get(uuid.toString(), "deaths");
		else
			return statsManager.getDeaths(uuid.toString());
	}

	/**
	 * Gets how many times a player played PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return how many times a player has played PvZ
	 */
	public int getGamesPlayed(UUID uuid, boolean sql) {
		if (sql)
			return get(uuid.toString(), "games_played");
		else
			return statsManager.getGamesPlayed(uuid.toString());
	}

	/**
	 * Gets how much sun a player has in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return how much sun a player has in PvZ
	 */
	public int getSun(UUID uuid, boolean sql) {
		if (sql)
			return get(uuid.toString(), "sun");
		else
			return statsManager.getSun(uuid.toString());
	}

	/**
	 * Sets the amount of kills a player has in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @param kills how many times a player has killed in PvZ
	 */
	public void setKills(UUID uuid, int kills, boolean sql) {
		if (sql)
			set(uuid.toString(), "kills", kills);
		else
			statsManager.setKills(uuid.toString(), kills);
	}

	/**
	 * Sets the amount of rows a player has captured in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @param rowsCaptured how many rows a player has captured in PvZ
	 */
	public void setRowsCaptured(UUID uuid, int rowsCaptured, boolean sql) {
		if (sql)
			set(uuid.toString(), "rows_captured", rowsCaptured);
		else
			statsManager.setRowsCaptured(uuid.toString(), rowsCaptured);
	}

	/**
	 * Sets the amount of times a player died in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @param deaths how many times a player has died in PvZ
	 */
	public void setDeaths(UUID uuid, int deaths, boolean sql) {
		if (sql)
			set(uuid.toString(), "deaths", deaths);
		else
			statsManager.setDeaths(uuid.toString(), deaths);
	}

	/**
	 * Sets the amount of games a player has played
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @param gamesPlayed how many times a player has played PvZ
	 */
	public void setGamesPlayed(UUID uuid, int gamesPlayed, boolean sql) {
		if (sql)
			set(uuid.toString(), "games_played", gamesPlayed);
		else
			statsManager.setGamesPlayed(uuid.toString(), gamesPlayed);
	}

	/**
	 * Sets the amount of sun a player has in PvZ
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @param sun how much sun a player has in PvZ
	 */
	public void setSun(UUID uuid, int sun, boolean sql) {
		if (sql)
			set(uuid.toString(), "sun", sun);
		else
			statsManager.setSun(uuid.toString(), sun);
	}

	/**
	 * Resets a player's statistics
	 * @param uuid the uuid of the player
	 * @param sql whether or not to use MySQL
	 * @return whether or not the statistics could be reset
	 */
	public boolean resetStats(String uuid, boolean sql) {
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
						if (name.equals(uuid)) {
							preparedStatement = connection.prepareStatement("DELETE FROM PvZPlayerStats WHERE PlayerUUID = ?");
							preparedStatement.setString(1, uuid);
							preparedStatement.execute();
							return true;
						}
					}
				}
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (preparedStatement != null)
						preparedStatement.close();
				} catch (SQLException e) {
					Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				}
			}
			return false;
		} else {
			return statsManager.reset(uuid);
		}
	}
	
	/**
	 * Checks if PvZ is using MySQL
	 * @return true if MySQL is being used
	 */
	public boolean isUsingMySQL() {
		return Main.getAPI().getFileUtils().getConfig().getBoolean("stats.use MySQL");
	}

	private int get(String uuid, String query) {
		ResultSet rs = null;
		Statement statement = null;
		boolean b;
		int stat = 0;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			makeNewPlayer(uuid);
			b = statement.execute("SELECT * FROM PvZPlayerStats");
			if (b) {
				rs = statement.getResultSet();
				while (rs.next()) {
					String name = rs.getString(1);
					int kills = rs.getInt(2);
					int deaths = rs.getInt(3);
					int gamesPlayed = rs.getInt(4);
					int rowsCaptured = rs.getInt(5);
					int sun = rs.getInt(6);
					if (name.equals(uuid.toLowerCase())) {
						if (query.equals("kills"))
							stat = kills;
						else if (query.equals("deaths"))
							stat = deaths;
						else if (query.equals("games_played"))
							stat = gamesPlayed;
						else if (query.equals("rows_captured"))
							stat = rowsCaptured;
						else if (query.equals("sun"))
							stat = sun;
					}
				}
			}
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			}
		}
		return stat;
	}

	private void set(String uuid, String stat, int amount) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS PvZPlayerStats (PlayerUUID varchar(200), kills int, deaths int, games_played int, rows_captured int, sun int)");
			makeNewPlayer(uuid);
			preparedStatement = connection.prepareStatement("UPDATE PvZPlayerStats SET " + stat + " = ? WHERE PlayerUUID = ?");
			preparedStatement.setInt(1, amount);
			preparedStatement.setString(2, uuid);
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
				Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			}
		}
	}

	public void makeNewPlayer(String uuid) {
		Statement statement = null;
		ResultSet rs = null;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS PvZPlayerStats (PlayerUUID varchar(200), kills int, deaths int, games_played int, rows_captured int, sun int)");
			rs = statement.executeQuery("SELECT * FROM PvZPlayerStats WHERE PlayerUUID = '" + uuid + "' LIMIT 1");
			if (!rs.next()) {
				statement.execute("INSERT INTO PvZPlayerStats (PlayerUUID, kills, deaths, games_played, rows_captured, sun) VALUES ('" + uuid + "', 0, 0, 0, 0, 0)");
			}
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			}
		}
	}
	
	private void openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String host = Main.getAPI().getFileUtils().getConfig().getString("stats.MySQL.host");
			String port = Main.getAPI().getFileUtils().getConfig().getString("stats.MySQL.port");
			String database = Main.getAPI().getFileUtils().getConfig().getString("stats.MySQL.database");
			String user = Main.getAPI().getFileUtils().getConfig().getString("stats.MySQL.user");
			String pass = Main.getAPI().getFileUtils().getConfig().getString("stats.MySQL.pass");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setAPI() {
		statsManager.setAPI();
	}
}