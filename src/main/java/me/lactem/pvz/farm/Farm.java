package me.lactem.pvz.farm;

import java.io.Serializable;
import java.util.ArrayList;

import me.lactem.pvz.row.Row;
import me.lactem.pvz.selection.SerializableSelection;

public class Farm implements Serializable {
	private static final long serialVersionUID = -3521198446835971702L;
	private String name;
	private SerializableSelection sel;
	private ArrayList<Row> rows = new ArrayList<Row>();

	public Farm(String name, SerializableSelection sel) {
		this.name = name;
		this.sel = sel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SerializableSelection getSel() {
		return sel;
	}

	public void setSel(SerializableSelection sel) {
		this.sel = sel;
	}

	public ArrayList<Row> getRows() {
		return rows;
	}

	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}
}