package com.bladeannihilation.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.bladeannihilation.gameobject.Tile;

public class Resources {
	public static String sep = File.separator;
	public static BufferedImage breakable = getTile("breakable.png");
	public static BufferedImage cobblestone = getTile("cobblestone.png");
	public static BufferedImage dirt = getTile("dirt.png");
	public static BufferedImage door = getTile("door.png");
	public static BufferedImage grass = getTile("grass.png");
	public static BufferedImage roof = getTile("roof.png");
	public static BufferedImage tree = getTile("tree.png");
	public static BufferedImage spawn = getTile("spawn.png");
	public static BufferedImage unknown = getTile("unknown.png");
	public static BufferedImage wall = getTile("wall.png");
	public static BufferedImage wall_house = getTile("wall_house.png");
	public static BufferedImage getImage(String filename) {
		File f = new File("resources" + sep + "images" + sep + filename);
		try {
			return ImageIO.read(f);
		} catch (IOException e) {
			System.out.print("RESOURCE UNAVAILABLE");
			e.printStackTrace();
		}
		return null;
	}
	public static File getFile(String filename) {
		return new File(filename);
	}
	public static File getLevel(String filename) {
		return new File("level" + sep + filename);
	}
	public static void initialize() {

	}
	private static BufferedImage getTile(String filename) {
		File f = new File("resources" + sep + "tile" + sep + filename);
		try {
			return ImageIO.read(f);
		} catch (IOException e) {
			System.out.print("RESOURCE UNAVAILABLE");
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public static BufferedImage getTileImage(Tile t) {
		switch(t) {
		case BREAKABLE:
			return breakable;
		case COBBLESTONE:
			return cobblestone;
		case DIRT:
			return dirt;
		case DOOR:
			return door;
		case GRASS:
			return grass;
		case ROOF:
			return roof;
		case TREE:
			return tree;
		case WALL:
			return wall;
		case WALL_HOUSE:
			return wall_house;
		case SPAWN:
			return spawn;
		}
		return unknown;
	}
	public static BufferedImage getPlayerState(String state) {
		File f = new File("resources" + sep + "player" + sep + state + ".png");
		try {
			return ImageIO.read(f);
		} catch (IOException e) {
			System.out.print("RESOURCE UNAVAILABLE");
			e.printStackTrace();
		}
		return null;
	}
	//public static int getLevels() {
	//	
	//}
}
