package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;

import java.io.FileNotFoundException;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.GamePanel;
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
	public GamePanel gp;

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
		case KeyBindings.EXIT:
			gp.gsm.setState(State.MENU);
			GamePanel.gameRunning = false;
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
			System.out.println("Better check your levels!");
			return;
		}
		levelStack[++levelPointer] = l;
		currentLevel = levelStack[levelPointer];
	}
	
	public synchronized void popLevel() {
		levelStack[levelPointer] = null;
		currentLevel = null;
		System.gc();
		currentLevel = levelStack[--levelPointer];
	}

	public GameState(Graphics2D g, GamePanel gp) {
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
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.out.println("Tutorial not found, shutting down.");
			System.exit(1);
		}
	}

	public void setGraphics(Graphics2D g) {
		this.g = g;
	}

	@Override
	public void draw(double time) {
		g.setColor(currentLevel.backgroundColor);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.drawImage(currentLevel.bi, -(int)(x*gameScale), -(int)(y*gameScale), currentLevel.getWidth()*gameScale, currentLevel.getHeight()*gameScale, null);
		//if(p.x > x && p.y > y && p.x < x + currentLevel.getWidth() && p.y < y + currentLevel.getHeight()) {
		g.drawImage(p.getImage(), (int)((p.x - x) * gameScale), (int)((p.y - y) * gameScale), gameScale, gameScale*2, null);
			//System.out.println("EXISTS");
		//}
		g.setColor(Color.WHITE);
		g.drawString("x: " + p.x + " y: " + p.y, 5, 27);
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

}
