package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.bladeannihilation.gui.Button;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Resources;
import com.bladeannihilation.main.Updateable;

public class MenuState implements Updateable {
	GameStateManager gsm;
	Graphics2D g;
	Button begin;
	Button preferences;
	Button quit;
	int placement = 0;
	int grassWidth = 50;
	Color lightGreen = new Color(20, 220, 60);
	Color darkGreen = new Color(0, 200, 0);
	BufferedImage logo = Resources.getImage("logo.png");
	
	public MenuState(Graphics2D g, GameStateManager gsm) {
		this.gsm = gsm;
		this.g = g;
		begin = new Button("START THAT GAME WOW YUM YUM", GamePanel.WIDTH/2-125, 10, 250, 50);
		preferences = new Button("HEY GIMME SOME PREFERENCES", GamePanel.WIDTH/2-125, 120, 250, 50);
		quit = new Button("I WANNA LEAVE GET OUT HEY", GamePanel.WIDTH/2-125, 180, 250, 50);
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
		
		//g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		if(begin.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			preferences.regState();
			quit.regState();
			if(GamePanel.releaseCheck()) {
				GamePanel.gameRunning = true;
				gsm.initGame();
			}
		} else if(preferences.testCollision(GamePanel.mouseLocation, GamePanel.mousePressed)) {
			quit.regState();
			if(GamePanel.releaseCheck()) {
				System.out.println("HEY PREFERENCES");
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
		g.setColor(Color.WHITE);
		g.drawImage(logo, GamePanel.WIDTH/2-125, GamePanel.HEIGHT/2 - 115, 250, 50, null);
		//g.drawString("BLADE ANNIHILATION", GamePanel.WIDTH/2, GamePanel.HEIGHT/2-95);
	}

	@Override
	public void update() {
		if(++placement >= grassWidth*2) placement = 0;
	}
	
}
