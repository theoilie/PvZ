package com.lactem.pvz.selection;

import java.io.Serializable;

public class SerializableSelection implements Serializable {
	private static final long serialVersionUID = 8416485634666912904L;
	private String block1;
	private String block2;

	public SerializableSelection(String block1, String block2) {
		this.block1 = block1;
		this.block2 = block2;
	}

	public String getBlock1() {
		return block1;
	}

	public void setBlock1(String block1) {
		this.block1 = block1;
	}

	public String getBlock2() {
		return block2;
	}

	public void setBlock2(String block2) {
		this.block2 = block2;
	}
}