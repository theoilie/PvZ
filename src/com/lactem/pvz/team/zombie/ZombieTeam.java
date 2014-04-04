package com.lactem.pvz.team.zombie;

import java.util.HashMap;
import java.util.UUID;

public class ZombieTeam {
	private HashMap<UUID, ZombieType> members = new HashMap<UUID, ZombieType>();

	public HashMap<UUID, ZombieType> getMembers() {
		return members;
	}

	public void setMembers(HashMap<UUID, ZombieType> members) {
		this.members = members;
	}
}