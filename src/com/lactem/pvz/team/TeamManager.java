package com.lactem.pvz.team;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.game.GameState;
import com.lactem.pvz.main.Main;
import com.lactem.pvz.team.plant.PlantTeam;
import com.lactem.pvz.team.plant.PlantType;
import com.lactem.pvz.team.zombie.ZombieTeam;
import com.lactem.pvz.team.zombie.ZombieType;
import com.lactem.pvz.util.messages.Messages;

public class TeamManager {
	public void addPlayer(Player player, Game game, PlantType plantType,
			ZombieType zombieType) {
		if (game.getState() == GameState.PLAYING) {
			Messages.sendMessage(player,
					Messages.getMessage("game already started"));
			return;
		}
		String ratio = Main.fileUtils.getConfig().getString(
				"plant to zombie ratio");
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
		int numNeeded = Main.fileUtils.getConfig()
				.getInt("players until start");
		if (game.getPlants().getMembers().size()
				+ game.getZombies().getMembers().size() >= numNeeded
				&& !Main.gameManager.countdownStarted.contains(String
						.valueOf(game.getSlot())))
			Main.gameManager.startCountdown(game);
	}

	public void addToPlants(Player player, Game game, PlantType type,
			boolean message) {
		HashMap<UUID, PlantType> plants = game.getPlants().getMembers();
		plants.put(player.getUniqueId(), type);
		game.getPlants().setMembers(plants);
		if (message)
			Messages.sendMessage(player,
					Messages.getMessage("joined plant team"));
		Main.gameManager.updateGame(game);
	}

	public void addToZombies(Player player, Game game, ZombieType type,
			boolean message) {
		HashMap<UUID, ZombieType> zombies = game.getZombies().getMembers();
		zombies.put(player.getUniqueId(), type);
		game.getZombies().setMembers(zombies);
		if (message)
			Messages.sendMessage(player,
					Messages.getMessage("joined zombie team"));
		Main.gameManager.updateGame(game);
	}

	public void removePlant(Player player) {
		Game game = Main.gameManager.getGame(player);
		if (game == null)
			return;
		if (game.getPlants().getMembers().containsKey(player.getUniqueId())) {
			game.getPlants().getMembers().remove(player.getUniqueId());
			Main.invManager.removeInventory(player);
		}
	}

	public void removeZombie(Player player) {
		Game game = Main.gameManager.getGame(player);
		if (game == null)
			return;
		if (game.getZombies().getMembers().containsKey(player.getUniqueId())) {
			game.getZombies().getMembers().remove(player.getUniqueId());
			Main.invManager.removeInventory(player);
		}
	}

	public boolean isPlayerInGame(Player player) {
		for (int i = 0; i < Main.gameManager.games.size(); i++) {
			Game game = Main.gameManager.games.get(i);
			if (game.getZombies().getMembers()
					.containsKey(player.getUniqueId())
					|| game.getPlants().getMembers()
							.containsKey(player.getUniqueId()))
				return true;
		}
		return false;
	}
}