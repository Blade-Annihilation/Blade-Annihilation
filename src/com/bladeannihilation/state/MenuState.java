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
	Graphics2D g;
	Button begin;
	Button preferences;
	Button quit;
	Button[] gameStart;
	int placement = 0;
	int grassWidth = 50;
	Color lightGreen = new Color(20, 220, 60);
	Color darkGreen = new Color(0, 200, 0);
	BufferedImage logo = Resources.getImage("logo.png");
	State state = State.MAIN;
	
	private enum State {
		MAIN,
		BEGIN
	}
	
	public MenuState(Graphics2D g, GameStateManager gsm) {
		this.gsm = gsm;
		this.g = g;
		int initialY = GamePanel.HEIGHT/2 - 55;
		begin = new Button(Languages.START, GamePanel.WIDTH/2-125, initialY, 250, 50);
		preferences = new Button(Languages.PREFERENCES, GamePanel.WIDTH/2-125, initialY+60, 250, 50);
		quit = new Button(Languages.QUIT, GamePanel.WIDTH/2-125, initialY+120, 250, 50);
	}
	
	public void setParent(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public void setGraphics(Graphics2D g) {
		this.g = g;
	}

	public void draw(double time) {
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
			g.fillRect(i*grassWidth + placement, 0, grassWidth, GamePanel.HEIGHT);
		}
		
		if(state == State.MAIN) {
			drawMainButtons();
		} else if(state == State.BEGIN){ 
			drawBeginState();
		}
		
		g.setColor(Color.WHITE);
		//g.drawString("BLADE ANNIHILATION", GamePanel.WIDTH/2, GamePanel.HEIGHT/2-95);
	}
	
	public void drawBeginState() {
		if(gameStart[0].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			gameStart[1].regState();
			gameStart[2].regState();
			if(GamePanel.releaseCheck()) {
				GamePanel.gameRunning = true;
				gsm.initGame();
				state = State.MAIN;
			}
		} else if(gameStart[1].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			gameStart[2].regState();
			if(GamePanel.releaseCheck()) {
				System.out.println("WHOOPS CAN'T DO THAT YET");
			}
		} else if(gameStart[2].testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			if(GamePanel.releaseCheck()) {
				state = State.MAIN;
				gameStart = null;
				System.gc();
				return;
			}
		}
		gameStart[0].draw(g);
		gameStart[1].draw(g);
		gameStart[2].draw(g);
	}
	
	public void drawMainButtons() {
		//g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		if(begin.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			preferences.regState();
			quit.regState();
			if(GamePanel.releaseCheck()) {
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
				gsm.setState(GameStateManager.State.GAME);
			}
		} else if(preferences.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			quit.regState();
			if(GamePanel.releaseCheck()) {
				gsm.setState(GameStateManager.State.PREFERENCES);
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
		if(++placement >= grassWidth*2) placement = 0;
	}
	
}
