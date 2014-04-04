package com.lactem.pvz.row;

import java.io.Serializable;

import com.lactem.pvz.endpoint.Endpoint;
import com.lactem.pvz.selection.SerializableSelection;

public class Row implements Serializable {
	private static final long serialVersionUID = -2447301529566621928L;
	private String zombieSpawn;
	private String plantSpawn;
	private SerializableSelection sel;
	private Endpoint endpoint;

	public Row(SerializableSelection sel) {
		this.sel = sel;
	}

	public String getZombieSpawn() {
		return zombieSpawn;
	}

	public void setZombieSpawn(String zombieSpawn) {
		this.zombieSpawn = zombieSpawn;
	}

	public String getPlantSpawn() {
		return plantSpawn;
	}

	public void setPlantSpawn(String plantSpawn) {
		this.plantSpawn = plantSpawn;
	}

	public SerializableSelection getSel() {
		return sel;
	}

	public void setSel(SerializableSelection sel) {
		this.sel = sel;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

}