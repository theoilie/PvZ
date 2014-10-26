package me.lactem.pvz.tasks;

import java.util.Iterator;
import java.util.UUID;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;
import me.lactem.pvz.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
	private API api = Main.getAPI();
	private Game game;
	private UUID uuid;
	
	public Countdown(Game game) {
		this.game = game;
	}

	@Override
	public void run() {
		game.setTimeUntilStart(game.getTimeUntilStart() - 1);
		if (game.getTimeUntilStart() == 0) {
			api.getGameManager().startGame(game);
			this.cancel();
		}
		
		if ((game.getTimeUntilStart() % 5 == 0 || game.getTimeUntilStart() < 6) && game.getTimeUntilStart() != 0) {
			alertTeam(game.getPlants().getMembers().keySet().iterator());
			alertTeam(game.getZombies().getMembers().keySet().iterator());
		}
	}
	
	private void alertTeam(Iterator<UUID> iterator) {
		while (iterator.hasNext()) {
			uuid = iterator.next();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == uuid)
					api.getMessageUtil().sendMessage(player, api.getMessageUtil().getMessage("starts in").replaceAll("<time>", game.getTimeUntilStart() + ""));
			}
		}
	}
}