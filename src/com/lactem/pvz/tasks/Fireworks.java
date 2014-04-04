package com.lactem.pvz.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.lactem.pvz.game.Game;
import com.lactem.pvz.selection.Selection;

public class Fireworks implements Runnable {
	private int timer = 11;
	public int task;
	public Game game;

	@Override
	public void run() {
		timer--;
		if (timer <= 0)
			Bukkit.getScheduler().cancelTask(task);
		Random r = new Random();
		Location block1 = Selection.locationFromString(game.getFarm().getSel()
				.getBlock1());
		block1.setX(block1.getX() + (r.nextInt(10) + 1));
		block1.setZ(block1.getZ() + (r.nextInt(10) + 1));
		Location block2 = Selection.locationFromString(game.getFarm().getSel()
				.getBlock2());
		block2.setX(block2.getX() + (r.nextInt(10) + timer));
		block2.setZ(block2.getZ() + (r.nextInt(10) + timer));
		Firework fw = (Firework) block1.getWorld().spawnEntity(
				timer % 2 == 0 ? block1 : block2, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		int typeInt = r.nextInt(5) + 1;
		Type type = Type.BURST;
		if (typeInt == 1)
			type = Type.BALL;
		if (typeInt == 2)
			type = Type.BALL_LARGE;
		if (typeInt == 3)
			type = Type.BURST;
		if (typeInt == 4)
			type = Type.CREEPER;
		if (typeInt == 5)
			type = Type.STAR;

		int colorR = r.nextInt(100) + 1;
		int colorG = r.nextInt(100) + 1;
		int colorB = r.nextInt(100) + 1;
		Color color = Color.fromRGB(colorR, colorG, colorB);

		FireworkEffect effect = FireworkEffect.builder()
				.flicker(r.nextBoolean()).withColor(color).with(type)
				.trail(r.nextBoolean()).build();

		fwm.addEffect(effect);

		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);

		fw.setFireworkMeta(fwm);
	}
}