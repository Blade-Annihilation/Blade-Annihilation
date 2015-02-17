package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Resources;
import com.bladeannihilation.main.Updatable;
import com.bladeannihilation.state.GameStateManager.State;
import com.bladeannihilation.gameobject.Level;
import com.bladeannihilation.gameobject.Location;
import com.bladeannihilation.gameobject.Player;

public class GameState implements Updatable, TileUpdate {

	public Level currentLevel;
	public ScrollingState scrollingState = ScrollingState.FOLLOW_PLAYER;
	private float scrollingVelocity = 4;
	private double x = 0;
	private double y = 0;
	private byte scrollTimer = 0;
	public static int gameScale = 16;
	private Player p;
	private Level[] levelStack = new Level[2]; //can accept more sublevels later
	private byte levelPointer = 0;
	private String[] infoText = null;
	private byte infoProgression = 0;
	private int[] infoPlacement = new int[2];
	private BufferedImage currentRender;
	private static final int renderDistance = 20;
	private int blockNumWidth = 0;
	private int blockNumHeight = 0;
	public GamePanel gp;
	private int startx;
	private int starty;
	private int xwidth;
	private int ywidth;
	private boolean updateRequired;

	public enum ScrollingState {
		FOLLOW_PLAYER,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		NONE,
		RETURN
	}
	
	public void dispose() {
		currentRender.flush();
		currentRender = null;
		currentLevel = null;
		for(int i = 0; i < levelStack.length; i++) {
			if(levelStack[i] != null) {
				levelStack[i] = null;
			}
		}
	}

	public void keyPressed(int key) {
		switch(key) {
		case KeyBindings.SCROLL_UP:
			scrollingState = ScrollingState.UP;
			break;
		case KeyBindings.SCROLL_DOWN:
			scrollingState = ScrollingState.DOWN;
			break;
		case KeyBindings.SCROLL_LEFT:
			scrollingState = ScrollingState.LEFT;
			break;
		case KeyBindings.SCROLL_RIGHT:
			scrollingState = ScrollingState.RIGHT;
			break;
		case KeyBindings.PAUSE:
			gp.gsm.setState(State.PAUSE);
			GamePanel.gameRunning = false;
			break;
		case KeyBindings.UP:
			if(infoText != null) {
				if(infoProgression < infoText.length - 1) {
					infoProgression++;
					FontMetrics fm = gp.getCurrentGraphics().getFontMetrics();
					Rectangle2D r = fm.getStringBounds(infoText[infoProgression], gp.getCurrentGraphics());
					int x = (GamePanel.WIDTH - 60 - (int) r.getWidth()) / 2;
					int y = (120 - (int) r.getHeight()) / 2 + fm.getAscent();
					infoPlacement[0] = x;
					infoPlacement[1] = y+GamePanel.HEIGHT-120;
				} else {
					infoProgression = 0;
					infoPlacement = new int[2];
					infoText = null;
				}
			}
			break;
		case KeyBindings.EXIT:
			gp.gsm.setState(State.MENU);
			GamePanel.gameRunning = false;
			gp.endGameState();
			//gp.initGame();
			break;
		case KeyBindings.ZOOM_IN:
			gameScale++;
			break;
		case KeyBindings.ZOOM_OUT:
			if(--gameScale <= 0) {
				gameScale = 1;
			}
			break;
		}
	}

	public void keyReleased(int key) {
		switch(key) {
		case KeyBindings.SCROLL_UP:
		case KeyBindings.SCROLL_DOWN:
		case KeyBindings.SCROLL_LEFT:
		case KeyBindings.SCROLL_RIGHT:
			scrollingState = ScrollingState.NONE;
			scrollingVelocity = 2;
			break;
		}
	}
	
	public synchronized void pushLevel(Level l) {
		if(levelPointer + 1 >= levelStack.length) {
			System.out.println("Level pushed, no array space. Allocating...");
			Level[] tmpLevelStack = new Level[levelPointer+1];
			for(int i = 0; i < levelStack.length; i++) {
				tmpLevelStack[i] = levelStack[i];
			}
			System.out.println("Allocated!");
			levelStack = tmpLevelStack;
		}
		levelStack[++levelPointer] = l;
		currentLevel = levelStack[levelPointer];
		initRender();
	}
	
	public synchronized void popLevel() {
		levelStack[levelPointer] = null;
		currentLevel = null;
		System.gc();
		currentLevel = levelStack[--levelPointer];
		initRender();
	}

	public GameState(GamePanel gp) {
		infoText = new String[2];
		infoText[0] = "Welcome to Blade Annihilation! Press " + ((char)KeyBindings.UP) + " to continue.";
		infoText[1] = "Use " + ((char)KeyBindings.UP) + ((char)KeyBindings.DOWN) + ((char)KeyBindings.LEFT) + ((char)KeyBindings.RIGHT) + " to move, and " + ((char)KeyBindings.SCROLL_UP) + ((char)KeyBindings.SCROLL_DOWN) + ((char)KeyBindings.SCROLL_LEFT) + ((char)KeyBindings.SCROLL_RIGHT) + " to move the camera. Find blue info blocks for text.";
		this.gp = gp;
		try {
			System.out.println("Grabbing level...");
			currentLevel = new Level("tutorial");
			levelPointer = 0;
			levelStack[0] = currentLevel;
			System.out.println("Level grabbed.");
			Location spawn = currentLevel.getSpawn();
			System.out.println("Spawn set.");
			p = new Player(spawn, this);
			x = spawn.x-GamePanel.WIDTH/(2*gameScale);
			y = spawn.y-GamePanel.HEIGHT/(2*gameScale);
			System.out.println("Player loaded.");
			initRender();
			System.out.println("Render initialized.");
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.out.println("Tutorial not found, shutting down.");
			System.exit(1);
		}
		System.out.println("--GAME INITIALIZED--");
	}
	
	public void initRender() {
		if(currentRender != null) {
			currentRender.flush();
			currentRender = null;
			System.gc();
		}
		blockNumWidth = GamePanel.WIDTH/gameScale+1;
		blockNumHeight = GamePanel.HEIGHT/gameScale+1;
		/*if(x < 0) {
			System.out.println("x1");
			startx = 0;
			xwidth = blockNumWidth+(int)x+renderDistance;
		} else if(x >= currentLevel.getWidth()) {
			System.out.println("x2");
			startx = currentLevel.getWidth()-1;
			xwidth = 1;
			//return;
		} else {
			System.out.println("x3");
			startx = (int)x - renderDistance/2;
			if(startx < 0) {
				System.out.println("x5");
				startx = 0;
				xwidth = blockNumWidth + renderDistance;
			} else {
				System.out.println("x6");
				xwidth = blockNumWidth + renderDistance;
			}
		}
		if(xwidth + startx >= currentLevel.getWidth()) {
			System.out.println("x4");
			xwidth = currentLevel.getWidth()-startx;
		}
		if(y < 0) {
			System.out.println("y1");
			starty = 0;
			ywidth = blockNumHeight+(int)y+renderDistance;
		} else if(y >= currentLevel.getHeight()) {
			System.out.println("y2");
			starty = currentLevel.getHeight()-1;
			ywidth = 1;
			//return;
		} else {
			System.out.println("y3");
			if(y-renderDistance/2 < 0) {
				System.out.println("y4");
				starty = 0;
			} else {
				System.out.println("y5");
				System.out.println(y);
				starty = (int)y-renderDistance/2;
			}
			ywidth = blockNumHeight + renderDistance;
		}
		if(ywidth + starty >= currentLevel.getHeight()) {
			System.out.println("y6");
			ywidth = currentLevel.getHeight()-starty;
		}*/
		startx = (int)x - renderDistance/2;
		xwidth = (int)x + blockNumWidth + renderDistance/2;
		/*if(startx < 0) {
			if(startx + xwidth < 0) {
				startx = 0;
				xwidth = 1;
			} else {
				xwidth += startx + renderDistance/2;
				startx = 0;
				if(xwidth < 1) {
					xwidth = 1;
				}
			}
			if(xwidth + startx >= currentLevel.getWidth()) {
				xwidth = (xwidth+startx) - currentLevel.getWidth();
			}
			if(xwidth < 1) {
				xwidth = 1;
			}
		} else if(startx >= currentLevel.getWidth()) {
			startx = currentLevel.getWidth()-1;
			xwidth = 1;
		}
		if(startx + xwidth >= currentLevel.getWidth()) {
			startx = currentLevel.getWidth()-1-renderDistance-blockNumWidth;
			xwidth = blockNumWidth + renderDistance;
		} else {
			startx = (int)x - renderDistance/2;
			xwidth = (int)x + blockNumWidth + renderDistance;
			if(startx + xwidth >= currentLevel.getWidth()) {
				xwidth = currentLevel.getWidth() - (startx + xwidth) - 100;
			}
		}
		if(startx < 0) {
			startx = 0;
		}*/
		starty = (int)y - renderDistance/2;
		ywidth = (int)y + blockNumHeight + renderDistance/2;
		/*if(starty < 0) {
			if(starty + ywidth < 0) {
				starty = 0;
				ywidth = 1;
			} else {
				ywidth += starty + renderDistance/2;
				starty = 0;
				if(ywidth < 1) {
					ywidth = 1;
				}
			}
			if(ywidth + starty >= currentLevel.getHeight()) {
				ywidth = (ywidth+starty) - currentLevel.getHeight();
			}
			if(ywidth < 1) {
				ywidth = 1;
			}
		} else if(starty >= currentLevel.getHeight()) {
			starty = currentLevel.getHeight()-1;
			ywidth = 1;
		}
		if(starty + ywidth >= currentLevel.getHeight()) {
			starty = currentLevel.getHeight()-1-renderDistance-blockNumHeight;
			ywidth = blockNumHeight + renderDistance;
		} else {
			starty = (int)y - renderDistance/2;
			ywidth = (int)y + blockNumHeight + renderDistance;
			if(starty + ywidth >= currentLevel.getHeight()) {
				ywidth = currentLevel.getHeight() - (starty + ywidth) - 100;
			}
		}
		if(starty < 0) {
			starty = 0;
		}*/
		if(startx < 0) {
			xwidth -= startx;
			startx = 0;
			if(xwidth >= currentLevel.getWidth()) {
				xwidth = currentLevel.getWidth() - 1;
			}
		} else if(startx + xwidth >= currentLevel.getWidth()) {
			if(startx >= currentLevel.getWidth()) {
				startx = currentLevel.getWidth()-1;
				xwidth = 1;
			} else {
				xwidth -= (startx + xwidth) - currentLevel.getWidth();
			}
		}
		if(starty < 0) {
			ywidth -= starty;
			starty = 0;
			if(ywidth >= currentLevel.getHeight()) {
				ywidth = currentLevel.getHeight() - 1;
			}
		} else if(starty + ywidth > currentLevel.getHeight()) {
			if(starty >= currentLevel.getHeight()) {
				starty = currentLevel.getHeight();
				ywidth = 1;
			} else {
				ywidth -= (starty + ywidth) - currentLevel.getHeight();
			}
		}
		System.out.println("Startx: " + startx);
		System.out.println(blockNumWidth + " " + blockNumHeight);
		currentRender = new BufferedImage(xwidth * gameScale, ywidth * gameScale, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)currentRender.getGraphics();
		for(int i = startx; i < startx+xwidth; i++) {
			for(int o = starty; o < starty+ywidth; o++) {
				//System.out.println(i + " " + o);
				g.drawImage(Resources.getTileImage(currentLevel.data[i][o]), i*16, o*16, 16, 16, null);
			}
		}
		g.dispose();
	}
	
	public void reRender() {
		
	}

	@Override
	public void draw(double time, Graphics2D g) {
		if(updateRequired) {
			initRender();
			updateRequired = false;
		}
		g.setColor(currentLevel.backgroundColor);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.drawImage(currentRender, -(int)((x-startx+(p.xSpeed*time))*gameScale), -(int)((y-starty+(p.ySpeed*time))*gameScale), xwidth*gameScale, ywidth*gameScale, null);
		//if(p.x > x && p.y > y && p.x < x + currentLevel.getWidth() && p.y < y + currentLevel.getHeight()) {
		g.drawImage(p.getImage(), (int)((p.x - x) * gameScale), (int)((p.y - y) * gameScale), gameScale, gameScale*2, null);
			//System.out.println("EXISTS");
		//}
		g.setColor(Color.BLACK);
		if(infoText != null) {
			if(infoPlacement[0] == 0) {
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D r = fm.getStringBounds(infoText[infoProgression], g);
				int x = (GamePanel.WIDTH - 60 - (int) r.getWidth()) / 2;
				int y = (120 - (int) r.getHeight()) / 2 + fm.getAscent();
				infoPlacement[0] = x;
				infoPlacement[1] = y+GamePanel.HEIGHT-120;
			}
			g.fillRect(0, GamePanel.HEIGHT - 120, GamePanel.WIDTH, 120);
			g.setColor(Color.WHITE);
			g.drawString(infoText[infoProgression], infoPlacement[0], infoPlacement[1]);
		} else {
			/*if(x > 0 && y > 0)
			g.drawString("x: " + (currentLevel.tileAt((int)x, (int)y)) + " y: ", 5, 39);
			g.drawString("camx: " + x + " camy: " + y, 5, 51);*/
			//g.fillRect(x, y, width, height);
			g.setColor(Color.WHITE);
		}
		g.drawString("x: " + p.x + " y: " + p.y, 5, 27);
		g.drawString("camx: " + x + " camy: " + y, 5, 39);
	}

	public void followPlayer() { //bring camera to show player
		double desiredx = p.x - GamePanel.WIDTH/(2*gameScale);
		double desiredy = p.y - GamePanel.HEIGHT/(2*gameScale);
		if(desiredx == x && desiredy == y) { //already at player
			scrollingState = ScrollingState.FOLLOW_PLAYER;
			return;
		}
		scrollingState = ScrollingState.RETURN;
		float totalDistance = (float) Math.sqrt(Math.pow(x - desiredx, 2) + Math.pow(y - desiredy, 2)); //pythagorean theorem!
		totalDistance/=500; //now pretend its name has changed because it represents speed
		totalDistance*=gameScale;
		System.out.println("Pre-scrolling velocity: " + totalDistance);
		if(totalDistance < 0.5f) { //make sure it can't go too slow
			totalDistance = 0.5f;
		}
		scrollingVelocity = totalDistance;
		System.out.println("Scrolling velocity: " + scrollingVelocity);
	}
	
	public boolean isFollowingPlayer() {
		return scrollingState == ScrollingState.RETURN || scrollingState == ScrollingState.FOLLOW_PLAYER;
	}

	@Override
	public void update() {
		if(infoText != null) {
			return;
		}
		switch(scrollingState) {
		case FOLLOW_PLAYER:
			scrollingVelocity = 2;
			x = p.x - GamePanel.WIDTH/(2*gameScale);
			y = p.y - GamePanel.HEIGHT/(2*gameScale);
			if((x <= startx && x >= 0) || (x + blockNumWidth >= startx + xwidth && x + blockNumWidth < currentLevel.getWidth()) || (y <= starty && y >= 0) || (y + blockNumHeight >= starty + ywidth && y + blockNumHeight < currentLevel.getHeight())) {
				System.out.println("RE-RENDERING");
				updateRequired = true;
			}
			break;
		case UP:
			y-=scrollingVelocity;
			if(++scrollTimer >= 50) {
				scrollTimer = 0;
				scrollingVelocity++;
			}
			break;
		case DOWN:
			y+=scrollingVelocity;
			if(++scrollTimer >= 50) {
				scrollTimer = 0;
				scrollingVelocity++;
			}
			break;
		case LEFT:
			System.out.println("X: " + x + " Y: " + y);
			x-=scrollingVelocity;
			if(++scrollTimer >= 50) {
				scrollTimer = 0;
				scrollingVelocity++;
			}
			break;
		case RIGHT:
			x+=scrollingVelocity;
			if(++scrollTimer >= 50) {
				scrollTimer = 0;
				scrollingVelocity++;
			}
			break;
		case RETURN:
			double desiredx = p.x - GamePanel.WIDTH/(2*gameScale);
			double desiredy = p.y - GamePanel.HEIGHT/(2*gameScale);
			if(x == desiredx && y == desiredy) {
				scrollingState = ScrollingState.FOLLOW_PLAYER;
				scrollingVelocity = 4;
			} else {
				if(x < desiredx) {
					x += scrollingVelocity;
					if(x >= desiredx) {
						x = desiredx;
						if(y == desiredy) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 4;
						}
					}
				} else if(x > desiredx) {
					x -= scrollingVelocity;
					if(x <= desiredx) {
						x = desiredx;
						if(y == desiredy) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 4;
						}
					}
				}
				if(y > desiredy) {
					y -= scrollingVelocity;
					if(y <= desiredy) {
						y = desiredy;
						if(x == desiredx) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 4;
						}
					}
				} else if(y < desiredy) {
					y += scrollingVelocity;
					if(y >= desiredy) {
						y = desiredy;
						if(x == desiredx) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 4;
						}
					}
				}
			}
		default:
			break;
		}

		p.update();
	}

	public void infoBlock(int x, int y) {
		infoText = currentLevel.infoAt(x, y).split("\\!BREAK");
	}

	@Override
	public void updateTile(int x, int y) {
		gp.getCurrentGraphics().drawImage(Resources.getTileImage(currentLevel.tileAt(x, y)), x*16, y*16, 16, 16, null);
	}

}
