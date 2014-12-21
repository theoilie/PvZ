package me.lactem.pvz.scoreboard;

import me.lactem.pvz.Main;
import me.lactem.pvz.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PvZBoard {
	private Scoreboard board;
	private Objective objective;
	private Game game;
	
	public PvZBoard(Game game) {
		reload(game);
	}
	
	public void update() {
		board.resetScores((game.getTimeLeft() + 1) + " seconds");
		int rowsRemaining = 0;
		for (int i = 0; i < game.getRows().size(); i++) {
			if (!game.getRows().get(i).isEndpointTaken())
				rowsRemaining++;
		}
		
		board.resetScores("" + (rowsRemaining + 1));
		
		Score time = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lTime Remaining"));
		time.setScore(4);
		Score intTime = objective.getScore(game.getTimeLeft() + " seconds");
		intTime.setScore(3);
		Score endpoints = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&lRows Remaining"));
		endpoints.setScore(2);
		Score numEnds = objective.getScore("" + rowsRemaining);
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