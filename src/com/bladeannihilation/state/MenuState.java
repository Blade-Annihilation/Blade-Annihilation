package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.bladeannihilation.gui.Button;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Languages;
import com.bladeannihilation.main.Resources;
import com.bladeannihilation.main.Updatable;

public class MenuState implements Updatable {
	GameStateManager gsm;
	Button begin;
	Button preferences;
	Button quit;
	Button[] gameStart;
	int placement = 0;
	static final int grassWidth = 50;
	static final Color lightGreen = new Color(20, 220, 60);
	static final Color darkGreen = new Color(0, 200, 0);
	static final byte incrementFactor = 2; //amount by which columns move
	static BufferedImage logo;
	State state = State.MAIN;
	
	private enum State {
		MAIN,
		BEGIN
	}
	
	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public void init() {
		if(logo == null) {
			logo = Resources.getImage("logo.png");
		}
		if(begin != null) {
			begin.dispose();
		}
		if(preferences != null) {
			preferences.dispose();
		}
		if(quit != null) {
			quit.dispose();
		}
		int initialY = GamePanel.HEIGHT/2 - 55;
		begin = new Button(Languages.START, GamePanel.WIDTH/2-125, initialY, 250, 50);
		preferences = new Button(Languages.PREFERENCES, GamePanel.WIDTH/2-125, initialY+60, 250, 50);
		quit = new Button(Languages.QUIT, GamePanel.WIDTH/2-125, initialY+120, 250, 50);
	}
	
	public void setParent(GameStateManager gsm) {
		this.gsm = gsm;
	}

	public void draw(double time, Graphics2D g) {
		if(GamePanel.handleSize()) { //size changed since last frame
			int initialY = GamePanel.HEIGHT/2 - 55;
			begin.setLocation(GamePanel.WIDTH/2-125, initialY);
			preferences.setLocation(GamePanel.WIDTH/2-125, initialY + 60);
			quit.setLocation(GamePanel.WIDTH/2-125, initialY + 120);
		}
		
		g.setColor(lightGreen);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.setColor(darkGreen);
		for(int i = -grassWidth; i < GamePanel.WIDTH/(double)grassWidth; i+=2) {
			g.fillRect(i*grassWidth + placement + (int)(incrementFactor * time), 0, grassWidth, GamePanel.HEIGHT);
		}
		
		if(state == State.MAIN) {
			drawMainButtons(g);
		} else if(state == State.BEGIN){ 
			drawBeginState(g);
		}
		
		g.setColor(Color.WHITE);
		//g.drawString("BLADE ANNIHILATION", GamePanel.WIDTH/2, GamePanel.HEIGHT/2-95);
	}
	
	public void drawBeginState(Graphics2D g) {
		if(gameStart[0].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			gameStart[1].regState();
			gameStart[2].regState();
			if(GamePanel.releaseCheck()) {
				disposeMainButtons();
				disposeBeginState();
				GamePanel.gameRunning = true;
				gsm.initGame();
				state = State.MAIN;
				return;
			}
		} else if(gameStart[1].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			gameStart[2].regState();
			if(GamePanel.releaseCheck()) {
				System.out.println("WHOOPS CAN'T DO THAT YET");
			}
		} else if(gameStart[2].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			if(GamePanel.releaseCheck()) {
				state = State.MAIN;
				disposeBeginState();
				System.gc();
				init();
				return;
			}
		} else {
			GamePanel.releaseCheck();
		}
		gameStart[0].draw(g);
		gameStart[1].draw(g);
		gameStart[2].draw(g);
	}
	
	public void disposeResources() {
		disposeMainButtons();
		disposeBeginState();
		logo.flush();
		logo = null;
	}
	
	public void disposeMainButtons() {
		if(begin != null) {
			begin.dispose();
			begin = null;
		}
		if(preferences != null) {
			preferences.dispose();
			preferences = null;
		}
		if(quit != null) {
			quit.dispose();
			quit = null;
		}
	}
	
	public void disposeBeginState() {
		for(int i = 0; i < gameStart.length; i++) {
			gameStart[i].dispose();
			gameStart[i] = null;
		}
		gameStart = null;
	}
	
	public void drawMainButtons(Graphics2D g) {
		//g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		if(begin.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			preferences.regState();
			quit.regState();
			if(GamePanel.releaseCheck()) {
				disposeMainButtons();
				if(gameStart != null) {
					disposeBeginState();
				}
				//if(gameStart == null) {
					int initialY = GamePanel.HEIGHT/2 - 85;
					gameStart = new Button[3];
					gameStart[0] = new Button(Languages.NEW_GAME, GamePanel.WIDTH/2-125, initialY, 250, 50);
					gameStart[1] = new Button(Languages.SAVE_RESTORE, GamePanel.WIDTH/2-125, initialY + 60, 250, 50);
					gameStart[2] = new Button(Languages.BACK, GamePanel.WIDTH/2-125, initialY + 120, 250, 50);
				//GamePanel.gameRunning = true;
				//gsm.initGame();
				//}
				state = State.BEGIN;
				//gsm.setState(GameStateManager.State.GAME);
				return;
			}
		} else if(preferences.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			quit.regState();
			if(GamePanel.releaseCheck()) {
				disposeMainButtons();
				gsm.setState(GameStateManager.State.PREFERENCES);
				return;
			}
		} else if(quit.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			if(GamePanel.releaseCheck()) {
				System.exit(0);
			}
		} else {
			GamePanel.releaseCheck();
		}
		begin.draw(g);
		preferences.draw(g);
		quit.draw(g);
		g.drawImage(logo, GamePanel.WIDTH/2-125, GamePanel.HEIGHT/2 - 115, 250, 50, null);
	}

	@Override
	public void update() {
		if((placement+=incrementFactor) >= grassWidth*2) placement = 0;
	}
	
}
