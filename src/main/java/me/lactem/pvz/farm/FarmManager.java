package me.lactem.pvz.farm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.plugin.Plugin;

public class FarmManager {
	public Plugin plugin;
	public void writeFarm(Farm farm) {
		FileOutputStream fow = null;
		ObjectOutputStream oos = null;
		try {
			File f = new File(plugin.getDataFolder() + "/farms");
			if (!f.exists()) {
				f.mkdirs();
			}
			fow = new FileOutputStream(plugin.getDataFolder() + "/farms/"
					+ farm.getName() + ".txt");
			oos = new ObjectOutputStream(fow);
			oos.writeObject(farm);
		} catch (IOException e) {
		} finally {
			try {
				if (oos != null && fow != null) {
					oos.close();
					fow.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public Farm readFarm(String farmName) {
		Farm farm = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(plugin.getDataFolder() + "/farms/"
					+ farmName);
			ois = new ObjectInputStream(fis);
			farm = (Farm) ois.readObject();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} finally {
			try {
				if (ois != null && fis != null) {
					ois.close();
					fis.close();
				}
			} catch (IOException e) {
			}
		}
		return farm;
	}
}