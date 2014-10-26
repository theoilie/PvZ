package me.lactem.pvz.ability;

import me.lactem.pvz.Main;
import me.lactem.pvz.api.API;

public class ZombieAbility { // There are currently no abilities that zombies can use.
	private API api;
	
	public void setAPI() {
		api = Main.getAPI();
	}
}