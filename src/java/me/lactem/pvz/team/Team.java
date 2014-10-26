package me.lactem.pvz.team;

import java.util.HashMap;
import java.util.UUID;

public abstract class Team<T> {
	private HashMap<UUID, T> members = new HashMap<UUID, T>();

	public abstract String getName();
	public HashMap<UUID, T> getMembers() {
		return members;
	}

	public void setMembers(HashMap<UUID, T> members) {
		this.members = members;
	}
}