package com.lactem.pvz.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.game.GameState;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.TempRow;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.tasks.RemoveFromCooldown;
import com.lactem.pvz.tasks.Unfreeze;
import com.lactem.pvz.team.plant.PlantType;
import com.lactem.pvz.team.zombie.ZombieType;
import com.lactem.pvz.util.messages.Messages;

public class Events implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	private void onPlayerInteractEvent(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (Selection.isUniversalWandSet()) {
			if ((player.getItemInHand().getTypeId() == Selection
					.getUniversalWandId())) {
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					Messages.sendMessage(player, Main.fileUtils.getMessages()
							.getString("block 1 set"));
					Selection.getPlayerSelection(player).setBlock1(
							event.getClickedBlock());
					event.setCancelled(true);
				}
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Messages.sendMessage(player, Main.fileUtils.getMessages()
							.getString("block 2 set"));
					Selection.getPlayerSelection(player).setBlock2(
							event.getClickedBlock());
					event.setCancelled(true);
				}
			}
		}
		if (Main.teamManager.isPlayerInGame(player)) {
			Game game = Main.gameManager.getGame(player);
			if (game.getState() != GameState.PLAYING)
				return;
			if (player.getItemInHand() == null)
				return;
			if (player.getItemInHand().getType() == Material.SLIME_BALL
					|| player.getItemInHand().getType() == Material.SNOW_BALL
					|| player.getItemInHand().getType() == Material
							.getMaterial(175)) {
				PlantType type = game.getPlants().getMembers()
						.get(player.getUniqueId());
				if (type == null)
					return;
				if (type == PlantType.PEASHOOTER) {
					snowball(player);
				} else if (type == PlantType.REPEATER) {
					snowball(player);
					// Launch a second snowball a fourth a second later.
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							Main.gameManager.plugin, new Runnable() {
								@Override
								public void run() {
									snowball(player);
								}
							}, 10l);
				} else if (type == PlantType.WINTER_MELON) {
					if (game.getInCooldown().contains(player.getUniqueId()))
						return;
					player.getInventory().setItemInHand(
							new ItemStack(Material.AIR));
					Snowball snowball = player.launchProjectile(Snowball.class);
					snowball.setVelocity(snowball.getVelocity().multiply(2));
					snowball.setShooter(player);
					ArrayList<UUID> inCooldown = game.getInCooldown();
					inCooldown.add(player.getUniqueId());
					game.setInCooldown(inCooldown);
					// Make a cooldown for a second.
					RemoveFromCooldown rfc = new RemoveFromCooldown();
					rfc.uuid = player.getUniqueId();
					rfc.gameID = game.getSlot();
					rfc.isWinterMelon = true;
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							Main.gameManager.plugin, rfc, 20l);
				} else if (type == PlantType.SUNFLOWER) {
					if (game.getInCooldown().contains(player.getUniqueId()))
						return;
					boost(player, game);
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(final InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			Game game = Main.gameManager.getGame(player);
			if (game != null) {
				if (game.getState() == GameState.PLAYING
						|| game.getState() == GameState.ENDING) {
					event.setCancelled(true);
					return;
				}
			}
			if (Main.invManager.inInv.contains(player.getUniqueId())) {
				event.setCancelled(true);
				Game g = Main.gameManager.getGame(event.getSlot());
				if (g == null)
					return;
				if (Main.teamManager.isPlayerInGame(player)) {
					Messages.sendMessage(player,
							Messages.getMessage("already in game"));
					return;
				}
				Main.teamManager.addPlayer(player, g, PlantType.PEASHOOTER,
						ZombieType.BASIC);
				player.closeInventory();
			} else if (Main.invManager.inTypeInv.contains(player.getUniqueId())) {
				event.setCancelled(true);
				if (game == null) {
					Messages.sendMessage(player,
							Messages.getMessage("not in a game"));
					return;
				}
				if (game.getState() == GameState.WAITING
						|| game.getState() == GameState.STARTING) {
					PlantType plantType = Main.invManager.getPlantType(event
							.getSlot());
					ZombieType zombieType = Main.invManager.getZombieType(event
							.getSlot());
					if (plantType == null && zombieType == null)
						return;
					if (game.getPlants().getMembers()
							.containsKey(player.getUniqueId())) {
						if (plantType != null) {
							if (!player.hasPermission(Main.invManager
									.getPermission(event.getSlot()))) {
								Messages.sendMessage(player,
										Messages.getMessage("upgrade"));
								player.closeInventory();
								return;
							}
							Main.teamManager.removePlant(player);
							HashMap<UUID, PlantType> plants = game.getPlants()
									.getMembers();
							plants.put(player.getUniqueId(), plantType);
							game.getPlants().setMembers(plants);
							player.closeInventory();
							Messages.sendMessage(player,
									Messages.getMessage("type changed"));
						}
					} else if (game.getZombies().getMembers()
							.containsKey(player.getUniqueId())) {
						if (zombieType != null) {
							if (!player.hasPermission(Main.invManager
									.getPermission(event.getSlot()))) {
								Messages.sendMessage(player,
										Messages.getMessage("upgrade"));
								player.closeInventory();
								return;
							}
							Main.teamManager.removeZombie(player);
							HashMap<UUID, ZombieType> zombies = game
									.getZombies().getMembers();
							zombies.put(player.getUniqueId(), zombieType);
							game.getZombies().setMembers(zombies);
							player.closeInventory();
							Messages.sendMessage(player,
									Messages.getMessage("type changed"));
						}
					}
				} else {
					Messages.sendMessage(player,
							Messages.getMessage("game already started"));
					player.closeInventory();
				}
			}
		}
	}

	@EventHandler
	private void onInventoryCloseEvent(final InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			if (Main.invManager.inInv.contains(player.getUniqueId()))
				Main.invManager.inInv.remove(player.getUniqueId());
			if (Main.invManager.inTypeInv.contains(player.getUniqueId()))
				Main.invManager.inTypeInv.remove(player.getUniqueId());
		}
	}

	@EventHandler
	private void onPlayerQuitEvent(final PlayerQuitEvent event) {
		if (!Main.teamManager.isPlayerInGame(event.getPlayer()))
			return;
		Main.teamManager.removePlant(event.getPlayer());
		Main.teamManager.removeZombie(event.getPlayer());
	}

	@EventHandler
	private void onPlayerKickEvent(final PlayerKickEvent event) {
		if (!Main.teamManager.isPlayerInGame(event.getPlayer()))
			return;
		Main.teamManager.removePlant(event.getPlayer());
		Main.teamManager.removeZombie(event.getPlayer());
	}

	@EventHandler
	private void onBlockBreakEvent(final BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (Main.teamManager.isPlayerInGame(player))
			event.setCancelled(true);
	}

	@EventHandler
	private void onBlockPlaceEvent(final BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (Main.teamManager.isPlayerInGame(player))
			event.setCancelled(true);
	}

	@EventHandler
	private void onPlayerDeathEvent(final PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (Main.teamManager.isPlayerInGame(player)) {
			Game game = Main.gameManager.getGame(player);
			if (game.getState() != GameState.PLAYING)
				return;
			Main.invManager.giveSpectatingInventory(
					player,
					game.getPlants().getMembers()
							.containsKey(player.getUniqueId()) ? game
							.getPlants().getMembers().get(player.getUniqueId())
							: null,
					game.getZombies().getMembers()
							.containsKey(player.getUniqueId()) ? game
							.getZombies().getMembers()
							.get(player.getUniqueId()) : null, game);
			event.getDrops().clear();
			event.setDeathMessage("");
			for (int i = 0; i < game.getRows().size(); i++) {
				TempRow row = game.getRows().get(i);
				if (row.getPlants().contains(player.getUniqueId()))
					row.getPlants().remove(player.getUniqueId());
				if (row.getZombies().contains(player.getUniqueId()))
					row.getZombies().remove(player.getUniqueId());
			}
			boolean sql = Main.sqlUtils.isUsingMySQL();
			Main.sqlUtils.setDeaths(player.getName(),
					Main.sqlUtils.getDeaths(player.getName(), sql) + 1, sql);
			if (player.getKiller() instanceof Player) {
				Player killer = player.getKiller();
				Main.sqlUtils.setKills(killer.getName(),
						Main.sqlUtils.getKills(killer.getName(), sql) + 1, sql);
			}
		}
	}

	@EventHandler
	private void onEntityDamageByEntityEvent(
			final EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (!Main.teamManager.isPlayerInGame(player))
			return;
		Game game = Main.gameManager.getGame(player);
		if (event.getDamager() instanceof Snowball) {
			if (game.getZombies().getMembers()
					.containsKey(player.getUniqueId())) {
				player.damage(1.0);
				if (game.getZombies().getMembers().get(player.getUniqueId()) != ZombieType.GARGANTUAR)
					player.setVelocity(((Snowball) event.getDamager())
							.getLocation().getDirection().multiply(0.5));
				return;
			}
			checkCancel(event, player, null, (Projectile) event.getDamager());
		} else if (event.getDamager() instanceof Player) {
			if (!(event.getDamager() instanceof Player))
				return;
			checkCancel(event, player, (Player) event.getDamager(), null);
		} else if (event.getDamager() instanceof Projectile) {
			checkCancel(event, player, null, (Projectile) event.getDamager());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	private void onProjectileHitEvent(final ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile instanceof Snowball) {
			if (projectile.getShooter() instanceof Player) {
				Player player = (Player) projectile.getShooter();
				Game game = Main.gameManager.getGame(player);
				if (game == null)
					return;
				if (game.getState() != GameState.PLAYING)
					return;
				PlantType type = game.getPlants().getMembers()
						.get(player.getUniqueId());
				if (type == null)
					return;
				if (type == PlantType.WINTER_MELON) {
					freeze(getZombies(projectile, game), game);
				}
			}
		}
	}

	@EventHandler
	private void onPlayerMoveEvent(final PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!Main.teamManager.isPlayerInGame(player))
			return;
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		Game game = Main.gameManager.getGame(player);
		if (game.getState() != GameState.PLAYING)
			return;
		UUID uuid = player.getUniqueId();
		if (!game.getZombies().getMembers().containsKey(uuid))
			return;
		if (player.isSprinting())
			player.setSprinting(false);
		if (game.getFrozen().contains(uuid))
			event.setCancelled(true);
		for (int i = 0; i < game.getRows().size(); i++) {
			TempRow row = game.getRows().get(i);
			if (row.isEndpointTaken())
				return;
			Location end = Selection.locationFromString(row.getEndpoint()
					.getLocation());
			if (end.getBlockX() == player.getLocation().getBlockX()
					&& end.getBlockZ() == player.getLocation().getBlockZ()) {
				row.setEndpointTaken(true);
				if (Main.gameManager.areAllEndpointsClaimed(game)) {
					Main.gameManager.endGame(game, false);
				} else {
					Main.gameManager.updateAll(game, row);
				}
				Firework firework = (Firework) end.getWorld().spawnEntity(end,
						EntityType.FIREWORK);
				FireworkMeta meta = firework.getFireworkMeta();
				meta.addEffect(FireworkEffect.builder().with(Type.BURST)
						.withColor(Color.RED).build());
				meta.setPower(2);
				firework.setFireworkMeta(meta);
				boolean sql = Main.sqlUtils.isUsingMySQL();
				Main.sqlUtils
						.setRowsCaptured(player.getName(), Main.sqlUtils
								.getRowsCaptured(player.getName(), sql) + 1,
								sql);
				break;
			}
		}
	}

	@EventHandler
	private void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Game game = Main.gameManager.getGame(player);
		if (game == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	private void onPlayerJoinEvent(final PlayerJoinEvent event) {
		if (Main.sqlUtils.isUsingMySQL())
			Main.sqlUtils.makeNewPlayer(event.getPlayer().getName());
	}

	@SuppressWarnings("deprecation")
	private void checkCancel(final EntityDamageByEntityEvent event,
			final Player player, Player damager, Projectile projectile) {
		if (damager == null)
			damager = (Player) projectile.getShooter();
		if (damager.getGameMode() == GameMode.CREATIVE) {
			event.setCancelled(true);
			return;
		}
		Game game = Main.gameManager.getGame(player);
		if (game.getState() != GameState.PLAYING) {
			event.setCancelled(true);
			return;
		}
		if ((game.getPlants().getMembers().containsKey(player.getUniqueId()) && game
				.getPlants().getMembers().containsKey(damager.getUniqueId()))
				|| (game.getZombies().getMembers()
						.containsKey(player.getUniqueId()) && game.getZombies()
						.getMembers().containsKey(damager.getUniqueId())))
			event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	private void snowball(final Player player) {
		Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setVelocity(snowball.getVelocity().multiply(2));
		snowball.setShooter(player);
	}

	private ArrayList<UUID> getZombies(final Projectile projectile,
			final Game game) {
		ArrayList<UUID> zombies = new ArrayList<UUID>();
		List<Entity> entities = projectile.getNearbyEntities(2.0, 0, 2.0);
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (game.getZombies().getMembers()
						.containsKey(player.getUniqueId()))
					zombies.add(player.getUniqueId());
			}
		}
		return zombies;
	}

	private void freeze(final ArrayList<UUID> zombies, final Game game) {
		ArrayList<UUID> frozen = game.getFrozen();
		for (int i = 0; i < zombies.size(); i++) {
			final UUID uuid = zombies.get(i);
			if (!frozen.contains(uuid)) {
				frozen.add(uuid);
				// Unfreeze the player after half a second.
				Unfreeze unfreeze = new Unfreeze();
				unfreeze.uuid = uuid;
				unfreeze.gameID = game.getSlot();
				Bukkit.getScheduler().scheduleSyncDelayedTask(
						Main.gameManager.plugin, unfreeze, 10l);
			}
		}
		game.setFrozen(frozen);
	}

	private void boost(final Player player, final Game game) {
		Random random = new Random();
		int i = random.nextInt(5);
		HashMap<UUID, PlantType> plants = game.getPlants().getMembers();
		if (i == 0) {
			// Strength I
			addEffect(plants, new PotionEffect(
					PotionEffectType.INCREASE_DAMAGE, 200, 1));
		} else if (i == 1) {
			// Strength II
			addEffect(plants, new PotionEffect(
					PotionEffectType.INCREASE_DAMAGE, 100, 2));
		} else if (i == 2) {
			// Regeneration I
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION,
					200, 1));
		} else if (i == 3) {
			// Regeneration II
			addEffect(plants, new PotionEffect(PotionEffectType.REGENERATION,
					100, 2));
		} else if (i == 4) {
			// Speed I
			addEffect(plants, new PotionEffect(PotionEffectType.SPEED, 300, 1));
		}
		ItemStack item = player.getItemInHand();
		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					"&4Cooldown in progress..."));
			item.setItemMeta(meta);
		}
		ArrayList<UUID> inCooldown = game.getInCooldown();
		inCooldown.add(player.getUniqueId());
		game.setInCooldown(inCooldown);
		RemoveFromCooldown rfc = new RemoveFromCooldown();
		rfc.uuid = player.getUniqueId();
		rfc.gameID = game.getSlot();
		rfc.isSunflower = true;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.gameManager.plugin,
				rfc, 200l);
	}

	private void addEffect(HashMap<UUID, PlantType> plants, PotionEffect effect) {
		Iterator<UUID> i = plants.keySet().iterator();
		while (i.hasNext()) {
			UUID uuid = i.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid) {
					player.addPotionEffect(effect);
				}
			}
		}
	}
}