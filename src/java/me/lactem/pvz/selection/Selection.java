package me.lactem.pvz.selection;

import java.util.Map;

import me.lactem.pvz.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Maps;

public class Selection {
	private Block block1;
	private Block block2;
	private Player player;
	public static String permission = "";
	public static int id = 0;
	public static Map<String, Selection> selections = Maps.newHashMap();

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
		return getBlock1() != null && getBlock2() != null;
	}

	public boolean areBlocksInSameWorld() {
		return getBlock1().getWorld().getName().equals(getBlock2().getWorld().getName());
	}

	public static String getPermission() {
		return Selection.permission;
	}

	public static void setPermission(String permission) {
		Selection.permission = permission;
	}

	public static int getWandId() {
		return id;
	}

	public static void setWandId(int id) {
		Selection.id = id;
	}

	public static boolean isWandSet() {
		return Selection.id == 0 ? false : true;
	}

	public static Selection getPlayerSelection(Player player) {
		if (!selections.containsKey(player.getName())) {
			selections.put(player.getName(), new Selection(player));
		}
		return selections.get(player.getName());
	}
	
	@SuppressWarnings("deprecation")
	public static boolean checkSetPoint(PlayerInteractEvent event) {
		if (!isWandSet())
			return false;
		
		Player player = event.getPlayer();
		if ((player.getItemInHand().getTypeId() == getWandId())) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				Main.getAPI().getMessageUtil().sendMessage(player, Main.getAPI().getFileUtils().getMessages().getString("block 1 set"));
				Selection.getPlayerSelection(player).setBlock1(event.getClickedBlock());
				event.setCancelled(true);
				return true;
			}
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Main.getAPI().getMessageUtil().sendMessage(player, Main.getAPI().getFileUtils().getMessages().getString("block 2 set"));
				Selection.getPlayerSelection(player).setBlock2(event.getClickedBlock());
				event.setCancelled(true);
				return true;
			}
		}
		return false;
	}
	
	public static SerializableSelection toSerializableSelection(Selection sel) {
		StringBuilder block1 = new StringBuilder(), block2 = new StringBuilder();
		
		block1.append(sel.getBlock1().getLocation().getWorld().getName());
		block1.append(",");
		block1.append(sel.getBlock1().getLocation().getBlockX());
		block1.append(",");
		block1.append(sel.getBlock1().getLocation().getBlockY());
		block1.append(",");
		block1.append(sel.getBlock1().getLocation().getBlockZ());
		
		block2.append(sel.getBlock2().getLocation().getWorld().getName());
		block2.append(",");
		block2.append(sel.getBlock2().getLocation().getBlockX());
		block2.append(",");
		block2.append(sel.getBlock2().getLocation().getBlockY());
		block2.append(",");
		block2.append(sel.getBlock2().getLocation().getBlockZ());

		return new SerializableSelection(block1.toString(), block2.toString());
	}

	public static Block blockFromString(String str) {
		Block block = null;
		String[] stringBlock = str.split(",");
		World world = Bukkit.getWorld(stringBlock[0]);
		block = world.getBlockAt(Integer.parseInt(stringBlock[1]), Integer.parseInt(stringBlock[2]), Integer.parseInt(stringBlock[3]));
		return block;
	}

	public static Location locationFromString(String str) {
		String[] location = str.split(",");
		return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
	}

	public static String locationToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}
}