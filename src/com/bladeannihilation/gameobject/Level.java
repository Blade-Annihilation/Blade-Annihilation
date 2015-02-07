package com.bladeannihilation.gameobject;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import com.bladeannihilation.main.Resources;

public class Level {
	private File f;
	private Tile[][] data;
	private String name;
	private int numRows = 0;
	private int numColumns = 0;
	private Location spawn;
	public BufferedImage bi;

	public Level(String filename) throws FileNotFoundException {
		f = Resources.getLevel(filename);
		Scanner s = new Scanner(f);
		String dataStr = s.nextLine();
		String[] dataArr = dataStr.split(" ");
		name = dataArr[0];
		numRows = Integer.parseInt(dataArr[1]);
		numColumns = Integer.parseInt(dataArr[2]);
		data = new Tile[numColumns][numRows];
		String line;
		bi = new BufferedImage(numColumns*16, numRows*16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)bi.getGraphics();
		for(int l = 0; l < numRows; l++) {
			line = s.nextLine();
			char[] a = line.toCharArray();
			for(int i = 0; i < a.length; i++) {
				switch(a[i]) {
				case '#':
					data[i][l] = Tile.WALL;
					break;
				case '.':
					data[i][l] = Tile.DIRT;
					break;
				case ':':
					data[i][l] = Tile.GRASS;
					break;
				case '\'':
					data[i][l] = Tile.COBBLESTONE;
					break;
				case 'a':
					data[i][l] = Tile.TREE;
					break;
				case 'd':
					data[i][l] = Tile.DOOR;
					break;
				case 'w':
					data[i][l] = Tile.ROOF;
					break;
				case 'q':
					data[i][l] = Tile.WALL_HOUSE;
					break;
				case 'b':
					data[i][l] = Tile.BREAKABLE;
					break;
				case 'p':
					data[i][l] = Tile.SPAWN;
					spawn = new Location(i, l);
					break;
				default:
					data[l][i] = Tile.UNKNOWN;
				}
				g.drawImage(Resources.getTileImage(data[i][l]), i*16, l*16, 16, 16, null);
			}
		}
		s.close();
		g.dispose();
	}
	@SuppressWarnings("incomplete-switch")
	public boolean movable(int x, int y) {
		if(x < 0 || y < 0 || x > numColumns-1 || y > numRows-1) {
			if(y > numRows) {
				return true;
			}
			return false;
		}
		//Graphics2D g = (Graphics2D)bi.getGraphics();
		switch(data[x][y]) {
		case WALL:
		case WALL_HOUSE:
		case ROOF:
		case BREAKABLE:
			//g.drawImage(Resources.spawn, x*16, y*16, 16, 16, null);
			//g.dispose();
			return false;
		}
		//g.drawImage(Resources.wall, x*16, y*16, 16, 16, null);
		//g.dispose();
		return true;
	}
	public Tile tileAt(int x, int y) {
		return data[y][x];
	}
	public String getName() {
		return name;
	}
	public int getWidth() {
		return numColumns;
	}
	public int getHeight() {
		return numRows;
	}
	public Location getSpawn() {
		return spawn;
	}
}
