package com.bladeannihilation.gameobject;

import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Color;

import com.bladeannihilation.main.Resources;

public class Level {
	private File f;
	public Tile[][] data;
	private String name;
	private int numRows = 0;
	private int numColumns = 0;
	public Color backgroundColor;
	private String filename;
	protected char sublevelname;
	protected boolean isSublevel = false;
	private Location spawn;
	private Hashtable<String, Character> doors = new Hashtable<String, Character>(); //also contains info block information
	private String[] infoArr;
	private Location storedLoc;
	public Level(String filename, char sublevelname) throws FileNotFoundException {
		this.filename = filename;
		this.sublevelname = sublevelname;
		isSublevel = true;
		f = Resources.getSubLevel(filename, sublevelname);
		Scanner s = new Scanner(f);
		init(s);
	}
	public Level(String filename) throws FileNotFoundException {
		this.filename = filename;
		f = Resources.getLevel(filename);
		Scanner s = new Scanner(f);
		init(s);
	}
	private void init(Scanner s) {
		String dataStr = s.nextLine();
		String[] dataArr = dataStr.split(" ");
		name = dataArr[0];
		numRows = Integer.parseInt(dataArr[1]);
		numColumns = Integer.parseInt(dataArr[2]);
		String backgroundString = dataArr[3];
		backgroundColor = new Color(Integer.parseInt(backgroundString.substring(0, 3)), Integer.parseInt(backgroundString.substring(3, 6)), Integer.parseInt(backgroundString.substring(6, 9)));
		data = new Tile[numColumns][numRows];
		String line;
		//BufferedImage bi = new BufferedImage(numColumns*16, numRows*16, BufferedImage.TYPE_INT_RGB);
		//Graphics2D g = (Graphics2D)bi.getGraphics();
		byte infoNum = 0;
		for(int l = 0; l < numRows; l++) {
			line = s.nextLine();
			char[] a = line.toCharArray();
			boolean isDoorChain = false;
			byte goPast = 0;
			byte doorChainLength = 0;
			for(int i = 0; i < numColumns + goPast; i++) {
				if(isDoorChain && a[i] != 'd') {
					for(int pos = i-doorChainLength; pos < i; pos++) {
						doors.put(pos-goPast + "/" + l, new Character(a[i-goPast]));
					}
					isDoorChain = false;
					doorChainLength = 0;
					goPast++; //allow one extra block in row
					continue;
				}
				switch(a[i]) {
				case '#':
					data[i-goPast][l] = Tile.WALL;
					break;
				case '.':
					data[i-goPast][l] = Tile.DIRT;
					break;
				case ':':
					data[i-goPast][l] = Tile.GRASS;
					break;
				case '\'':
					data[i-goPast][l] = Tile.COBBLESTONE;
					break;
				case 'a':
					data[i-goPast][l] = Tile.TREE;
					break;
				case 'd':
					data[i-goPast][l] = Tile.DOOR;
					isDoorChain = true;
					doorChainLength++;
					break;
				case 'w':
					data[i-goPast][l] = Tile.ROOF_TOP;
					break;
				case 'z':
					data[i-goPast][l] = Tile.ROOF_CENTER;
					break;
				case 'y':
					data[i-goPast][l] = Tile.ROOF_RIGHT;
					break;
				case 'x':
					data[i-goPast][l] = Tile.ROOF_LEFT;
					break;
				case 'q':
					data[i-goPast][l] = Tile.WALL_HOUSE;
					break;
				case 'b':
					data[i-goPast][l] = Tile.BREAKABLE;
					break;
				case 'f':
					data[i-goPast][l] = Tile.FLOORING;
					break;
				case 'p':
					data[i-goPast][l] = Tile.SPAWN;
					if(spawn == null) {
						spawn = new Location(i-goPast, l);
					}
					break;
				case 'i':
					data[i-goPast][l] = Tile.INFO;
					doors.put(i-goPast + "i" + l, new Character((char)infoNum));
					infoNum++;
					break;
				case 'r':
					data[i-goPast][l] = Tile.RETURN;
					if(spawn == null) {
						spawn = new Location(i-goPast, l);
					}
					break;
				default:
					data[i][l] = Tile.UNKNOWN;
				}
				//g.drawImage(Resources.getTileImage(data[i-goPast][l]), (i-goPast)*16, l*16, 16, 16, null);
			}
		}
		if(spawn == null) {
			spawn = new Location(0, 0);
		}
		if(infoNum > 0) {
			infoArr = new String[infoNum];
			for(int i = 0; i < infoNum; i++) {
				infoArr[i] = s.nextLine();
			}
		}
		s.close();
	}
	public boolean feetTouchExit(int x, int y) {
		if(++y >= numRows) {
			y = numRows-1;
		}
		return data[x][y] == Tile.RETURN;
	}
	public boolean headTouchesSpecial(int x, int y) {
		return data[x][y] == Tile.DOOR || data[x][y] == Tile.INFO;
	}
	public char doorID(int x, int y) {
		return doors.get(x + "/" + y);
	}
	public String getFilename() {
		return filename;
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
		case ROOF_TOP:
		case ROOF_RIGHT:
		case ROOF_CENTER:
		case ROOF_LEFT:
		case BREAKABLE:
		case TREE:
			//g.drawImage(Resources.spawn, x*16, y*16, 16, 16, null);
			//g.dispose();
			return false;
		}
		//g.drawImage(Resources.wall, x*16, y*16, 16, 16, null);
		//g.dispose();
		return true;
	}
	public void replaceTileAt(int x, int y, Tile t) {
		data[x][y] = t;
	}
	public Tile tileAt(int x, int y) {
		return data[x][y];
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
	public void storeLoc(Location storedLoc) {
		this.storedLoc = storedLoc;
	}
	public Location loadLoc() {
		Location a = storedLoc;
		storedLoc = null;
		return a;
	}
	public boolean headTouchesDoor(int x, int y) {
		return data[x][y] == Tile.DOOR;
	}
	public boolean headTouchesInfo(int x, int y) {
		if(data[x][y] == Tile.INFO) {
			replaceTileAt(x, y, Tile.GRASS);
			return true;
		}
		return false;
	}
	public String infoAt(int x, int y) {
		return infoArr[(int)doors.get(x + "i" + y)];
	}
}
