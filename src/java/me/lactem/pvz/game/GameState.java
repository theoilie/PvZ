package me.lactem.pvz.game;

public enum GameState {
	WAITING("Waiting"), STARTING("Starting"), PLAYING("Playing"), ENDING("Ending");
	
	private String name;

	private GameState(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}