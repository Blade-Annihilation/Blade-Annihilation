package com.bladeannihilation.gameobject;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import com.bladeannihilation.main.Resources;
import com.bladeannihilation.main.GamePanel;

public class Level {
	private File f;
	private Tile[][] data;
	private String name;
	private int numRows = 0;
	private int numColumns = 0;
   private int x = 0;
   private int y = 0;
   public BufferedImage bi;
	
	public Level(String filename) throws FileNotFoundException {
		f = Resources.getLevel(filename);
		Scanner s = new Scanner(f);
		String dataStr = s.nextLine();
		String[] dataArr = dataStr.split(" ");
		name = dataArr[0];
		numRows = Integer.parseInt(dataArr[1]);
		numColumns = Integer.parseInt(dataArr[2]);
		data = new Tile[numRows][numColumns];
		String line;
      bi = new BufferedImage(numColumns*16, numRows*16, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = (Graphics2D)bi.getGraphics();
		for(int l = 0; l < numRows; l++) {
			line = s.nextLine();
			char[] a = line.toCharArray();
			for(int i = 0; i < a.length; i++) {
				switch(a[i]) {
				case '#':
					data[l][i] = Tile.WALL;
					break;
				case '.':
					data[l][i] = Tile.DIRT;
					break;
				case ':':
					data[l][i] = Tile.GRASS;
					break;
				case '\'':
					data[l][i] = Tile.COBBLESTONE;
					break;
				case 'a':
					data[l][i] = Tile.TREE;
					break;
				case 'd':
					data[l][i] = Tile.DOOR;
					break;
				case 'w':
					data[l][i] = Tile.ROOF;
					break;
				case 'q':
					data[l][i] = Tile.WALL_HOUSE;
					break;
				case 'b':
					data[l][i] = Tile.BREAKABLE;
					break;
				case 'p':
					data[l][i] = Tile.SPAWN;
					break;
				default:
					data[l][i] = Tile.UNKNOWN;
				}
            g.drawImage(Resources.getTileImage(data[l][i]), i*16, l*16, 16, 16, null);
			}
		}
		s.close();
      g.dispose();
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
}
