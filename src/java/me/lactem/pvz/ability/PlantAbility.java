package me.lactem.pvz.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.tasks.RemoveFromCooldown;
import me.lactem.pvz.team.plant.PlantType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlantAbility {
	private API api;
	
	/**
	 * Launches a pea (snowball) like in the game
	 * @param player the player who is launching the snowball
	 */
	public void snowball(final CraftPlayer player) {
		Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity().multiply(2));
		snowball.setShooter(player);
	}
	
	/**
	 * Launches a pea (snowball) like in the game
	 * @param player the player who is launching the snowball
	 */
	public void snowball(final Player player) {
		Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity().multiply(2));
		snowball.setShooter(player);
	}
	
	/**
	 * Activates the sunflower boost ability
	 * @param player the player who is activating the ability
	 */
	public void boost(CraftPlayer player) {
		boost(player, api.getGameManager().getGame(player));
	}
	
	/**
	 * Activates the sunflower boost ability
	 * @param player the player who is activating the ability
	 */
	public void boost(Player player) {
		boost(player, api.getGameManager().getGame(player));
	}
	
	/**
	 * Activates the sunflower boost ability
	 * @param player the player who is activating the ability
	 * @param game the game instance the player is in
	 */
	public void boost(final CraftPlayer player, final Game game) {
		Random random = new Random();
		int i = random.nextInt(5);
		HashMap<UUID, PlantType> plants = game.getPlants().getMembers();
		
		switch (i) {
		case 0:
			// Strength I
			addEffect(plants, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
			break;
		case 1:
			// Strength I
			addEffect(plants, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
			break;
		case 2:
			// Regeneration I
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
			break;
		case 3:
			// Regeneration II
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
			break;
		case 4:
			// Speed I
			addEffect(plants, new PotionEffect(PotionEffectType.SPEED, 300, 1));
			break;
		}
		
		ItemStack item = player.getItemInHand();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Cooldown in progress..."));
			item.setItemMeta(meta);
		}
		
		ArrayList<UUID> inCooldown = game.getInCooldown();
		inCooldown.add(player.getUniqueId());
		game.setInCooldown(inCooldown);
		RemoveFromCooldown rfc = new RemoveFromCooldown(player.getUniqueId(), game.getSlot(), true, false);
		rfc.runTaskLater(api.getPlugin(), 200l);
	}
	
	/**
	 * Activates the sunflower boost ability
	 * @param player the player who is activating the ability
	 * @param game the game instance the player is in
	 */
	public void boost(final Player player, final Game game) {
		Random random = new Random();
		int i = random.nextInt(5);
		HashMap<UUID, PlantType> plants = game.getPlants().getMembers();
		
		switch (i) {
		case 0:
			// Strength I
			addEffect(plants, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
			break;
		case 1:
			// Strength I
			addEffect(plants, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
			break;
		case 2:
			// Regeneration I
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
			break;
		case 3:
			// Regeneration II
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
			break;
		case 4:
			// Speed I
			addEffect(plants, new PotionEffect(PotionEffectType.SPEED, 300, 1));
			break;
		}
		
		ItemStack item = player.getItemInHand();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Cooldown in progress..."));
			item.setItemMeta(meta);
		}
		
		ArrayList<UUID> inCooldown = game.getInCooldown();
		inCooldown.add(player.getUniqueId());
		game.setInCooldown(inCooldown);
		RemoveFromCooldown rfc = new RemoveFromCooldown(player.getUniqueId(), game.getSlot(), true, false);
		rfc.runTaskLater(api.getPlugin(), 200l);
	}
	
	private void addEffect(HashMap<UUID, PlantType> plants, PotionEffect effect) {
		Iterator<UUID> i = plants.keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid)
					player.addPotionEffect(effect);
			}
		}
	}
	
	public void setAPI() {
		api = Main.getAPI();
	}
}