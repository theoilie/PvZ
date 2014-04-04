package com.lactem.pvz.event;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.game.GameState;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.row.TempRow;
import com.lactem.pvz.selection.Selection;
import com.lactem.pvz.team.plant.PlantType;
import com.lactem.pvz.team.zombie.ZombieType;
import com.lactem.pvz.util.messages.Messages;

public class Events implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
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
			if (Main.gameManager.getGame(player).getState() == GameState.PLAYING) {
				if (player.getItemInHand() != null) {
					if (player.getItemInHand().getType() == Material.SLIME_BALL) {
						Snowball snowball = player
								.launchProjectile(Snowball.class);
						snowball.setVelocity(snowball.getVelocity().multiply(2));
						snowball.setShooter(player);
						try {
							if (player.getItemInHand().getItemMeta()
									.getDisplayName().contains("Repeating")) {
								Snowball snowball2 = player
										.launchProjectile(Snowball.class);
								snowball2.setVelocity(snowball.getVelocity()
										.multiply(2));
								snowball.setShooter(player);
							}
						} catch (NullPointerException e) {

						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			Game game = Main.gameManager.getGame(player);
			if (game != null) {
				if (game.getState() != GameState.WAITING
						&& game.getState() != GameState.STARTING) {
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
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			if (Main.invManager.inInv.contains(player.getUniqueId()))
				Main.invManager.inInv.remove(player.getUniqueId());
			if (Main.invManager.inTypeInv.contains(player.getUniqueId()))
				Main.invManager.inTypeInv.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if (!Main.teamManager.isPlayerInGame(event.getPlayer()))
			return;
		Main.teamManager.removePlant(event.getPlayer());
		Main.teamManager.removeZombie(event.getPlayer());
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		if (!Main.teamManager.isPlayerInGame(event.getPlayer()))
			return;
		Main.teamManager.removePlant(event.getPlayer());
		Main.teamManager.removeZombie(event.getPlayer());
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (Main.teamManager.isPlayerInGame(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (Main.teamManager.isPlayerInGame(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (Main.teamManager.isPlayerInGame(player)) {
			Game game = Main.gameManager.getGame(player);
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
		}
	}

	@EventHandler
	private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (!Main.teamManager.isPlayerInGame(player))
			return;
		if (event.getDamager() instanceof Snowball) {
			Game game = Main.gameManager.getGame(player);
			if (game.getZombies().getMembers()
					.containsKey(player.getUniqueId())) {
				player.damage(1.0);
				if (game.getZombies().getMembers().get(player.getUniqueId()) != ZombieType.GARGANTUAR)
					player.setVelocity(((Snowball) event.getDamager())
							.getLocation().getDirection().multiply(0.5));
			}
		} else if (event.getDamager() instanceof Player) {
			if (!(event.getDamager() instanceof Player))
				return;
			checkCancel(event, player, (Player) event.getDamager(), null);
		} else if (event.getDamager() instanceof Projectile) {
			checkCancel(event, player, null, (Projectile) event.getDamager());
		}
	}

	@EventHandler
	private void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!Main.teamManager.isPlayerInGame(player))
			return;
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		Game game = Main.gameManager.getGame(player);
		if (game.getState() != GameState.PLAYING)
			return;
		if (game.getZombies().getMembers().containsKey(player.getUniqueId())) {
			if (player.isSprinting())
				player.setSprinting(false);
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
					Firework firework = (Firework) end.getWorld().spawnEntity(
							end, EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(FireworkEffect.builder().with(Type.BURST)
							.withColor(Color.RED).build());
					meta.setPower(2);
					firework.setFireworkMeta(meta);
					break;
				}
			}
		}
	}

	@EventHandler
	private void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Game game = Main.gameManager.getGame(player);
		if (game == null)
			return;
		event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	private void checkCancel(EntityDamageByEntityEvent event, Player player,
			Player damager, Projectile projectile) {
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
}