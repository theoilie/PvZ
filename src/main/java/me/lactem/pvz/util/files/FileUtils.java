package me.lactem.pvz.util.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FileUtils {

	private Plugin plugin;
	
	private FileConfiguration config, messages, stats, joinInv, typeInv;
	
	private File configFile, messagesFile, statsFile, joinInvFile, typeInvFile;

	public FileUtils(JavaPlugin plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		// Config setup
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			copyFileFromJar("config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		saveConfig();
		
		// Messages setup
		messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			copyFileFromJar("messages.yml");
		}
		messages = YamlConfiguration.loadConfiguration(messagesFile);
		saveMessages();
		
		// Stats setup
		statsFile = new File(plugin.getDataFolder(), "stats.yml");
		if (!statsFile.exists()) {
			copyFileFromJar("stats.yml");
		}
		stats = YamlConfiguration.loadConfiguration(statsFile);
		saveStats();
		
		// Join inventory setup
		messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			copyFileFromJar("messages.yml");
		}
		messages = YamlConfiguration.loadConfiguration(messagesFile);
		saveMessages();
		
		// Type inventory setup
		statsFile = new File(plugin.getDataFolder(), "stats.yml");
		if (!statsFile.exists()) {
			copyFileFromJar("stats.yml");
		}
		stats = YamlConfiguration.loadConfiguration(statsFile);
		saveStats();
	}

	// Config
	public FileConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "The file config.yml couldn't be saved!");
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	// End config

	// Messages
	public FileConfiguration getMessages() {
		return messages;
	}

	public void saveMessages() {
		try {
			messages.save(messagesFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "The file messages.yml couldn't be saved!");
		}
	}

	public void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}

	// End messages

	// Stats
	public FileConfiguration getStats() {
		return stats;
	}

	public void saveStats() {
		try {
			stats.save(statsFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "The file stats.yml couldn't be saved!");
		}
	}

	public void reloadStats() {
		stats = YamlConfiguration.loadConfiguration(statsFile);
	}
	
	// Join inventory
	public FileConfiguration getJoinInv() {
		return typeInv;
	}

	public void saveJoinInv() {
		try {
			joinInv.save(joinInvFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "The file join inventory.yml couldn't be saved!");
		}
	}

	public void reloadJoinInv() {
		joinInv = YamlConfiguration.loadConfiguration(joinInvFile);
	}
	
	// End join inventory
	
	// Type inventory
	public FileConfiguration getTypeInv() {
		return typeInv;
	}

	public void saveTypeInv() {
		try {
			typeInv.save(typeInvFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "The file type inventory.yml couldn't be saved!");
		}
	}

	public void reloadTypeInv() {
		typeInv = YamlConfiguration.loadConfiguration(typeInvFile);
	}

	// End type inventory
	
	public void copyFileFromJar(String fileName) {
		File file = new File(plugin.getDataFolder() + File.separator + fileName);
		InputStream fis = plugin.getResource(fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}