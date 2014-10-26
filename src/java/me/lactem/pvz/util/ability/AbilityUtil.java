package me.lactem.pvz.util.ability;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.lactem.pvz.Main;
import me.lactem.pvz.ability.Ability;
import me.lactem.pvz.ability.PlantAbility;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;
import me.lactem.pvz.tasks.RemoveFromCooldown;
import me.lactem.pvz.team.plant.PlantType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityUtil {
	private API api;
	
	/**
	 * Checks if any projectile should be launched when a PlayerInteractEvent is fired
	 * @param event the PlayerInteractEvent to check
	 * @return true if a projectile is launched
	 */
	@SuppressWarnings("deprecation")
	public boolean checkProjectileAbility(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!api.getGameManager().isPlayerInGame(player))
			return false;

		event.setCancelled(true); // Players do not need to interact if they're not using a special ability.

		Game game = api.getGameManager().getGame(player);
		if (game.getState() != GameState.PLAYING)
			return false;
		if (player.getItemInHand() == null)
			return false;

		Material hand = player.getItemInHand().getType();
		if (hand != Material.SLIME_BALL && hand != Material.SNOW_BALL && hand != Material.getMaterial(175))
			return false;

		PlantType type = game.getPlants().getMembers().get(player.getUniqueId());
		if (type == null)
			return false;

		switch (type) {
		case PEASHOOTER:
			useAbility(Ability.SNOWBALL, player);
			return true;
		case REPEATER:
			useAbility(Ability.SNOWBALL, player);
			new BukkitRunnable() {	// Launch a second snowball a fourth of a second later...
				@Override
				public void run() {
					useAbility(Ability.SNOWBALL, player);
				}
			}.runTaskLater(api.getPlugin(), 10l);
			return true;
		case WINTER_MELON:
			if (game.getInCooldown().contains(player.getUniqueId()))
				return false;
	
			player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			Snowball snowball = player.launchProjectile(Snowball.class);
			snowball.setVelocity(snowball.getVelocity().multiply(2));
			snowball.setShooter(player);
			ArrayList<UUID> inCooldown = game.getInCooldown();
			inCooldown.add(player.getUniqueId());
			game.setInCooldown(inCooldown);
	
			// Make a cooldown for a second.
			RemoveFromCooldown rfc = new RemoveFromCooldown(player.getUniqueId(), game.getSlot(), false, true);
			rfc.runTaskLater(api.getPlugin(), 20l);
			return true;
		case SUNFLOWER:
			if (game.getInCooldown().contains(player.getUniqueId()))
				return false;
	
			useAbility(Ability.SUNFLOWER_BOOST, player, game);
			return true;
		case BONK_CHOY:
			return false;
		default:
			return false;
		}
	}
	
	/**
	 * Activates an ability
	 * @param ability the {@link me.lactem.pvz.ability.Ability} to be activated
	 * @param args any arguments required for the specific method that activates the ability.
	 * @see the methods in {@link me.lactem.pvz.ability.PlantAbility} and {@link me.lactem.pvz.ability.ZombieAbility}.
	 */
	public void useAbility(Ability ability, Object... args) {
		Class<?>[] params = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			params[i] = args[i].getClass();
		}
		try {
			switch (ability) {
			case SNOWBALL:
				PlantAbility.class.getMethod("snowball", params).invoke(api.getPlantAbility(), args);
				break;
			case SUNFLOWER_BOOST:
				PlantAbility.class.getMethod("boost", params).invoke(api.getPlantAbility(), args);
				break;
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			api.getPlugin().getLogger().log(Level.WARNING, "There was an error with a player using the ability " + ability.name() + ".");
			if (api.debug())
				e.printStackTrace();
		}
	}
	
	public void setAPI() {
		api = Main.getAPI();
	}
}