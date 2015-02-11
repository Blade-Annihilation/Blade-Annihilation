package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.swing.text.JTextComponent.KeyBinding;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Resources;
import com.bladeannihilation.main.Updatable;
import com.bladeannihilation.state.GameStateManager.State;
import com.bladeannihilation.gameobject.Level;
import com.bladeannihilation.gameobject.Location;
import com.bladeannihilation.gameobject.Player;

public class GameState implements Updatable {

	private Graphics2D g;
	public Level currentLevel;
	public ScrollingState scrollingState = ScrollingState.FOLLOW_PLAYER;
	private float scrollingVelocity = 1;
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
	private static final int renderDistance = 16*20;
	private int blockNumWidth = 0;
	private int blockNumHeight = 0;
	public GamePanel gp;
	private int startx;
	private int starty;
	private int xwidth;
	private int ywidth;

	public enum ScrollingState {
		FOLLOW_PLAYER,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		NONE,
		RETURN
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
					FontMetrics fm = g.getFontMetrics();
					Rectangle2D r = fm.getStringBounds(infoText[infoProgression], g);
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
			break;
		case KeyBindings.ZOOM_IN:
			gameScale++;
			break;
		case KeyBindings.ZOOM_OUT:
			gameScale--;
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
			scrollingVelocity = 1;
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

	public GameState(Graphics2D g, GamePanel gp) {
		infoText = new String[2];
		infoText[0] = "Welcome to Blade Annihilation! Press " + ((char)KeyBindings.UP) + " to continue.";
		infoText[1] = "Use " + ((char)KeyBindings.UP) + ((char)KeyBindings.DOWN) + ((char)KeyBindings.LEFT) + ((char)KeyBindings.RIGHT) + " to move, and " + ((char)KeyBindings.SCROLL_UP) + ((char)KeyBindings.SCROLL_DOWN) + ((char)KeyBindings.SCROLL_LEFT) + ((char)KeyBindings.SCROLL_RIGHT) + " to move the camera. Find blue info blocks for text.";
		this.g = g;
		this.gp = gp;
		try {
			currentLevel = new Level("tutorial");
			levelPointer = 0;
			levelStack[0] = currentLevel;
			Location spawn = currentLevel.getSpawn();
			p = new Player(spawn, this);
			x = spawn.x-GamePanel.WIDTH/32;
			y = spawn.y-GamePanel.HEIGHT/32;
			initRender();
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.out.println("Tutorial not found, shutting down.");
			System.exit(1);
		}
	}
	
	public void initRender() {
		if(currentRender != null) {
			currentRender.flush();
			currentRender = null;
			System.gc();
		}
		blockNumWidth = GamePanel.WIDTH/gameScale+1;
		blockNumHeight = GamePanel.HEIGHT/gameScale+1;
		if(x < 0) {
			startx = 0;
			xwidth = blockNumWidth+(int)x;
		} else if(x >= currentLevel.getWidth()) {
			startx = currentLevel.getWidth()-1;
			xwidth = 0;
			return;
		} else {
			startx = (int)x;
			xwidth = blockNumWidth;
		}
		if(y < 0) {
			starty = 0;
			ywidth = blockNumWidth+(int)y;
		} else if(y >= currentLevel.getHeight()) {
			starty = currentLevel.getHeight()-1;
			ywidth = 0;
			return;
		} else {
			starty = (int)y;
			ywidth = blockNumHeight;
		}
		System.out.println(blockNumWidth + " " + blockNumHeight);
		currentRender = new BufferedImage(xwidth * gameScale, ywidth * gameScale, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)currentRender.getGraphics();
		for(int i = 0; i < xwidth; i++) {
			for(int o = 0; o < ywidth; o++) {
				g.drawImage(Resources.getTileImage(currentLevel.data[i][o]), i*16, o*16, 16, 16, null);
			}
		}
		g.dispose();
	}
	
	public void reRender() {
		
	}

	public void setGraphics(Graphics2D g) {
		this.g = g;
	}

	@Override
	public void draw(double time) {
		g.setColor(currentLevel.backgroundColor);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.drawImage(currentRender, -(int)(x*gameScale), -(int)(y*gameScale), xwidth*gameScale, ywidth*gameScale, null);
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
		totalDistance/=1000; //now pretend it's name has changed because it represents speed
		totalDistance*=gameScale;
		System.out.println("Pre-scrolling velocity: " + totalDistance);
		if(totalDistance < 0.35f) { //make sure it can't go too slow
			totalDistance = 0.35f;
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
			scrollingVelocity = 1;
			x = p.x - GamePanel.WIDTH/(2*gameScale);
			y = p.y - GamePanel.HEIGHT/(2*gameScale);
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
				scrollingVelocity = 1;
			} else {
				if(x < desiredx) {
					x += scrollingVelocity;
					if(x >= desiredx) {
						x = desiredx;
						if(y == desiredy) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 1;
						}
					}
				} else if(x > desiredx) {
					x -= scrollingVelocity;
					if(x <= desiredx) {
						x = desiredx;
						if(y == desiredy) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 1;
						}
					}
				}
				if(y > desiredy) {
					y -= scrollingVelocity;
					if(y <= desiredy) {
						y = desiredy;
						if(x == desiredx) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 1;
						}
					}
				} else if(y < desiredy) {
					y += scrollingVelocity;
					if(y >= desiredy) {
						y = desiredy;
						if(x == desiredx) {
							scrollingState = ScrollingState.FOLLOW_PLAYER;
							scrollingVelocity = 1;
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

}
