package me.lactem.pvz.farm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import me.lactem.pvz.Main;

public class FarmManager {
	
	/**
	 * Saves a fFarm object to a file in the farms folder
	 * @param farm the Farm object to be saved
	 */
	public void writeFarm(Farm farm) {
		FileOutputStream fow = null;
		ObjectOutputStream oos = null;
		try {
			File f = new File(Main.getAPI().getPlugin().getDataFolder() + "/farms");
			if (!f.exists()) {
				f.mkdirs();
			}
			fow = new FileOutputStream(Main.getAPI().getPlugin().getDataFolder() + "/farms/" + farm.getName() + ".txt");
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

	/**
	 * Gets a Farm object from a file in the farms folder
	 * @param farmName the name of the file in the farms folder (including the .txt extension).
	 * @return the Farm from the farms folder
	 */
	public Farm readFarm(String farmName) {
		Farm farm = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(Main.getAPI().getPlugin().getDataFolder() + "/farms/" + farmName);
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