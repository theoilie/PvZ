package com.lactem.pvz.util.files;

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

public class FileUtils {

	private Plugin plugin;

	private FileUtils() {
	}

	static FileUtils instance = new FileUtils();

	public static FileUtils getInstance() {
		return instance;
	}

	FileConfiguration config;
	FileConfiguration messages;
	File messagesFile;
	File configFile;

	public void setup(Plugin plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			copyFileFromJar("config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		saveConfig();
		messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			copyFileFromJar("messages.yml");
		}
		messages = YamlConfiguration.loadConfiguration(messagesFile);
		saveMessages();
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			Bukkit.getServer()
					.getLogger()
					.severe(ChatColor.RED
							+ "The file config.yml couldn't be saved!");
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public FileConfiguration getMessages() {
		return messages;
	}

	public void saveMessages() {
		try {
			messages.save(messagesFile);
		} catch (IOException e) {
			Bukkit.getServer()
					.getLogger()
					.severe(ChatColor.RED
							+ "The file messages.yml couldn't be saved!");
		}
	}

	public void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}

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