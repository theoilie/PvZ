package me.lactem.pvz.util.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.logging.Level;

import me.lactem.pvz.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FileUtils {

	private Plugin plugin;
	
	private FileConfiguration config, messages, stats, joinInv, typeInv;
	
	private File configFile, messagesFile, statsFile, joinInvFile, typeInvFile;

	public FileUtils(JavaPlugin plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdir();
		
		setupFile(configFile, config, "config", "config.yml");
		setupFile(messagesFile, messages, "messages", "messages.yml");
		setupFile(joinInvFile, joinInv, "joinInv", "join inventory.yml");
		setupFile(typeInvFile, typeInv, "typeInv", "type inventory.yml");
		setupFile(statsFile, stats, "stats", "stats.yml");
	}

	// Config
	public FileConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		save(configFile, config, "config.yml");
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	// Messages
	public FileConfiguration getMessages() {
		return messages;
	}

	public void saveMessages() {
		save(messagesFile, messages, "messages.yml");
	}

	public void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}

	// Stats
	public FileConfiguration getStats() {
		return stats;
	}

	public void saveStats() {
		save(statsFile, stats, "stats.yml");
	}

	public void reloadStats() {
		stats = YamlConfiguration.loadConfiguration(statsFile);
	}
	
	// Join inventory
	public FileConfiguration getJoinInv() {
		return joinInv;
	}

	public void saveJoinInv() {
		save(joinInvFile, joinInv, "join inventory.yml");
	}

	public void reloadJoinInv() {
		joinInv = YamlConfiguration.loadConfiguration(joinInvFile);
	}
	
	// Type inventory
	public FileConfiguration getTypeInv() {
		return typeInv;
	}

	public void saveTypeInv() {
		save(typeInvFile, typeInv, "type inventory.yml");
	}

	public void reloadTypeInv() {
		typeInv = YamlConfiguration.loadConfiguration(typeInvFile);
	}
	
	// Copies a file from the JAR (including comments) and puts it into the plugins folder.
	private void copyFileFromJar(String fileName) {
		File file = new File(plugin.getDataFolder() + File.separator + fileName);
		InputStream fis = plugin.getResource(fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	
	// Sets up a file, including saving and copying from local file.
	private void setupFile(File file, FileConfiguration fileConfig, String variableName, String fileName) {
		file = new File(plugin.getDataFolder(), fileName);
		if (!file.exists())
			copyFileFromJar(fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		try {
			Field field1 = getClass().getDeclaredField(variableName);
			Field field2 = getClass().getDeclaredField(variableName.concat("File"));
			field1.setAccessible(true);
			field2.setAccessible(true);
			field1.set(this, fileConfig);
			field2.set(this, file);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			printLater(e);
		}
	}	    
		
	// Saves a file.
	private void save(File file, FileConfiguration fileConfig, String fileName) {
		try {
			fileConfig.save(file);
		} catch (final IOException e) {
			plugin.getLogger().log(Level.WARNING, "The file " + fileName + " couldn't be saved.");
			printLater(e);
		}
	}
	
	// Logs a stack trace after a one-second delay to ensure the API is initialized first.
	private void printLater(final Exception e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Main.getAPI().debug())
					e.printStackTrace();
			}
		}.runTaskLater(plugin, 20l);
	}
}