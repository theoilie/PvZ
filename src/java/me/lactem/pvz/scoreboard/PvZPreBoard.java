package me.lactem.pvz.scoreboard;

import me.lactem.pvz.Main;
import me.lactem.pvz.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PvZPreBoard {
	private Scoreboard board;
	private Objective objective;
	private Game game;
	private final int playersUntilStart;
	private int playersNeeded = 0, lastPlayersNeeded = 0;
	private int startingIn = 0, lastStartingIn = 0;
	
	public PvZPreBoard(Game game) {
		reload(game);
		playersUntilStart = Main.getAPI().getFileUtils().getConfig().getInt("players until start");
	}
	
	public void update() {
		board.resetScores(lastPlayersNeeded + "");
		board.resetScores(lastStartingIn + "");
		
		if (playersUntilStart - (game.getPlants().getMembers().size() + game.getZombies().getMembers().size()) < 0)
			playersNeeded = 0;
		else
			playersNeeded = playersUntilStart - (game.getPlants().getMembers().size() + game.getZombies().getMembers().size());
		startingIn = game.getTimeUntilStart();
		lastPlayersNeeded = playersNeeded;
		lastStartingIn = startingIn;
		
		
		Score time = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lPlayers Needed"));
		time.setScore(4);
		Score intTime = objective.getScore(playersNeeded + "");
		intTime.setScore(3);
		Score endpoints = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lStarting In"));
		endpoints.setScore(2);
		Score numEnds = objective.getScore(startingIn + "");
		numEnds.setScore(1);
	}
	
	public Scoreboard getBoard() {
		return board;
	}
	
	public void reload(Game game) {
		this.game = game;
		if (board != null) {
			board.clearSlot(DisplaySlot.SIDEBAR);
			objective.unregister();
		}
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = board.registerNewObjective("PvZ #" + game.getSlot(), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getAPI().getFileUtils().getConfig().getString("prefix")));
	}
}