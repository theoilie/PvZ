package me.lactem.pvz.team;

import java.util.HashMap;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;
import me.lactem.pvz.game.GameState;
import me.lactem.pvz.team.plant.PlantTeam;
import me.lactem.pvz.team.plant.PlantType;
import me.lactem.pvz.team.zombie.ZombieTeam;
import me.lactem.pvz.team.zombie.ZombieType;
import me.lactem.pvz.util.messages.Messages;

import org.bukkit.entity.Player;

public class TeamManager {
	private API api = Main.getAPI();
	
	/**
	 * Adds a player to a game with the specified plant and/or zombie type.<br>
	 * If plantType or zombieType is null, the player will be added to the opposite team.
	 * @param player the player to be added
	 * @param game the game to add the player to
	 * @param plantType the type of plant the player chose, if any
	 * @param zombieType the type of zombie the player chose, if any
	 */
	public void addPlayer(Player player, Game game, PlantType plantType, ZombieType zombieType) {
		if (game.getState() == GameState.PLAYING) {
			Messages.sendMessage(player, Messages.getMessage("game already started"));
			return;
		}
		
		String ratio = api.getFileUtils().getConfig().getString("plant to zombie ratio");
		int plants = Integer.valueOf(ratio.substring(0, ratio.indexOf(":")));
		int zombies = Integer.valueOf(ratio.substring(ratio.indexOf(":") + 1));
		
		if (plants < zombies) {
			PlantTeam team = game.getPlants();
			if (team.getMembers().size() == 0)
				addToPlants(player, game, plantType, true);
			else {
				int zombiesReq = (team.getMembers().size() * plants) * zombies;
				if (game.getZombies().getMembers().size() < zombiesReq)
					addToZombies(player, game, zombieType, true);
				else
					addToPlants(player, game, plantType, true);
			}
		} else {
			ZombieTeam team = game.getZombies();
			if (team.getMembers().size() == 0)
				addToZombies(player, game, zombieType, true);
			else {
				int plantsReq = (team.getMembers().size() * zombies) * plants;
				if (game.getPlants().getMembers().size() < plantsReq)
					addToPlants(player, game, plantType, true);
				else
					addToZombies(player, game, zombieType, true);
			}
		}
		
		int numNeeded = api.getFileUtils().getConfig().getInt("players until start");
		if (game.getPlants().getMembers().size() + game.getZombies().getMembers().size() >= numNeeded && !api.getGameManager().hasCountDownStarted(game))
			api.getGameManager().startCountdown(game);
	}

	/**
	 * Adds a player to the plant team
	 * @param player the player to add
	 * @param game the game the player is in
	 * @param type the type/kit the player chose
	 * @param message whether or not to send the player a join message
	 */
	public void addToPlants(Player player, Game game, PlantType type, boolean message) {
		HashMap<UUID, PlantType> plants = game.getPlants().getMembers();
		plants.put(player.getUniqueId(), type);
		game.getPlants().setMembers(plants);
		
		if (message)
			Messages.sendMessage(player, Messages.getMessage("joined plant team"));
		
		api.getGameManager().updateGame(game);
	}

	/**
	 * Adds a player to the zombie team
	 * @param player the player to add
	 * @param game the game the player is in
	 * @param type the type/kit the player chose
	 * @param message whether or not to send the player a join message
	 */
	public void addToZombies(Player player, Game game, ZombieType type, boolean message) {
		HashMap<UUID, ZombieType> zombies = game.getZombies().getMembers();
		zombies.put(player.getUniqueId(), type);
		game.getZombies().setMembers(zombies);
		
		if (message)
			Messages.sendMessage(player, Messages.getMessage("joined zombie team"));
		
		api.getGameManager().updateGame(game);
	}

	/**
	 * Removes a plant player from a game
	 * @param player the player to remove
	 */
	public void removePlant(Player player) {
		Game game = api.getGameManager().getGame(player);
		
		if (game == null)
			return;
		
		if (game.getPlants().getMembers().containsKey(player.getUniqueId())) {
			game.getPlants().getMembers().remove(player.getUniqueId());
			api.getInvManager().removeInventory(player);
		}
	}

	/**
	 * Removes a zombie player from the game
	 * @param player the player to remove
	 */
	public void removeZombie(Player player) {
		Game game = api.getGameManager().getGame(player);
		
		if (game == null)
			return;
		
		if (game.getZombies().getMembers().containsKey(player.getUniqueId())) {
			game.getZombies().getMembers().remove(player.getUniqueId());
			api.getInvManager().removeInventory(player);
		}
		
		if (!api.useDC())
			return;
		
		if (api.getDC().isDisguised(player))
			api.getDC().undisguisePlayer(player);
	}
}