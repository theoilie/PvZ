package com.lactem.pvz.tasks;

import java.util.ArrayList;
import java.util.UUID;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.main.Main;

public class Unfreeze implements Runnable {
	public UUID uuid;
	public int gameID;

	@Override
	public void run() {
		Game game = Main.gameManager.getGame(gameID);
		if (game == null)
			return;
		ArrayList<UUID> frozen = game.getFrozen();
		if (frozen.contains(uuid)) {
			frozen.remove(uuid);
			game.setFrozen(frozen);
		}
	}
}