package com.lactem.pvz.selection;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Selection implements Serializable {

	private static final long serialVersionUID = -1655621435227518958L;
	private Block block1;
	private Block block2;
	private Player player;
	public static String permission = "";
	public static int id = 0;
	public static HashMap<String, Selection> selections = new HashMap<String, Selection>();

	public Selection(Player player) {
		this.player = player;
	}

	public Block getBlock1() {
		return block1;
	}

	public Block getBlock2() {
		return block2;
	}

	public Player getPlayer() {
		return player;
	}

	public void setBlock1(Block block1) {
		this.block1 = block1;
	}

	public void setBlock2(Block block2) {
		this.block2 = block2;
	}

	public boolean areBothPointsSet() {
		return getBlock1() == null || getBlock2() == null ? false : true;
	}

	public boolean areBlocksInDifferentWorlds() {
		return getBlock1().getWorld().getName()
				.equals(getBlock2().getWorld().getName()) ? false : true;
	}

	public static String getPermission() {
		return Selection.permission;
	}

	public static void setPermission(String permission) {
		Selection.permission = permission;
	}

	public static int getUniversalWandId() {
		return id;
	}

	public static void setUniversalWandId(int id) {
		Selection.id = id;
	}

	public static boolean isUniversalWandSet() {
		return Selection.id == 0 ? false : true;
	}

	public static Selection getPlayerSelection(Player player) {
		if (!selections.containsKey(player.getName())) {
			selections.put(player.getName(), new Selection(player));
		}
		return selections.get(player.getName());
	}

	public static SerializableSelection toSerializableSelection(Selection sel) {
		String block1 = sel.getBlock1().getLocation().getWorld().getName()
				+ "," + sel.getBlock1().getLocation().getBlockX() + ","
				+ sel.getBlock1().getLocation().getBlockY() + ","
				+ sel.getBlock1().getLocation().getBlockZ();
		String block2 = sel.getBlock2().getLocation().getWorld().getName()
				+ "," + sel.getBlock2().getLocation().getBlockX() + ","
				+ sel.getBlock2().getLocation().getBlockY() + ","
				+ sel.getBlock2().getLocation().getBlockZ();
		return new SerializableSelection(block1, block2);
	}

	public static Block blockFromString(String str) {
		Block block = null;
		String[] stringBlock = str.split(",");
		World world = Bukkit.getWorld(stringBlock[0]);
		block = world.getBlockAt(Integer.parseInt(stringBlock[1]),
				Integer.parseInt(stringBlock[2]),
				Integer.parseInt(stringBlock[3]));
		return block;
	}

	public static Location locationFromString(String str) {
		String[] location = str.split(",");
		return new Location(Bukkit.getWorld(location[0]),
				Double.parseDouble(location[1]),
				Double.parseDouble(location[2]),
				Double.parseDouble(location[3]));
	}

	public static String locationToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY()
				+ "," + loc.getZ();
	}
}