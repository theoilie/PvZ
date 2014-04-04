package com.lactem.pvz.team.plant;

import java.util.HashMap;
import java.util.UUID;

public class PlantTeam {
	private HashMap<UUID, PlantType> members = new HashMap<UUID, PlantType>();

	public HashMap<UUID, PlantType> getMembers() {
		return members;
	}

	public void setMembers(HashMap<UUID, PlantType> members) {
		this.members = members;
	}
}