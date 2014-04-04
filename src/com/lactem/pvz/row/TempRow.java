package com.lactem.pvz.row;

import java.util.ArrayList;
import java.util.UUID;

import com.lactem.pvz.endpoint.Endpoint;

public class TempRow {
	private String zombieSpawn, plantSpawn;
	private boolean isEndpointTaken = false;
	private Endpoint endpoint;
	private ArrayList<UUID> plants = new ArrayList<UUID>();
	private ArrayList<UUID> zombies = new ArrayList<UUID>();

	public TempRow(String zombieSpawn, String plantSpawn, Endpoint endpoint) {
		this.zombieSpawn = zombieSpawn;
		this.plantSpawn = plantSpawn;
		this.endpoint = endpoint;
	}

	public String getPlantSpawn() {
		return plantSpawn;
	}

	public void setPlantSpawn(String plantSpawn) {
		this.plantSpawn = plantSpawn;
	}

	public String getZombieSpawn() {
		return zombieSpawn;
	}

	public void setZombieSpawn(String zombieSpawn) {
		this.zombieSpawn = zombieSpawn;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public ArrayList<UUID> getZombies() {
		return zombies;
	}

	public void setZombies(ArrayList<UUID> zombies) {
		this.zombies = zombies;
	}

	public ArrayList<UUID> getPlants() {
		return plants;
	}

	public void setPlants(ArrayList<UUID> plants) {
		this.plants = plants;
	}

	public boolean isEndpointTaken() {
		return isEndpointTaken;
	}

	public void setEndpointTaken(boolean isEndpointTaken) {
		this.isEndpointTaken = isEndpointTaken;
	}
}