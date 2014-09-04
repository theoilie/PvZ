package me.lactem.pvz.tasks;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.lactem.pvz.Main;
import me.lactem.pvz.game.Game;

public class Unfreeze extends BukkitRunnable {
	private UUID uuid;
	private int gameID;

	public Unfreeze(UUID uuid, int gameID) {
		this.uuid = uuid;
		this.gameID = gameID;
	}
	
	@Override
	public void run() {
		Game game = Main.getAPI().getGameManager().getGame(gameID);
		
		if (game == null)
			return;
		
		ArrayList<UUID> frozen = game.getFrozen();
		if (frozen.contains(uuid)) {
			frozen.remove(uuid);
			game.setFrozen(frozen);
		}
	}
}