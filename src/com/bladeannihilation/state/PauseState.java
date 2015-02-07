package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Languages;
import com.bladeannihilation.main.Updatable;

public class PauseState implements Updatable {
	private Graphics2D g;
	//private GamePanel gp;
	private Color transparent = new Color(0, 0, 0, 10);
	private byte progression = 0;
	
	public PauseState(Graphics2D g, GameStateManager gameStateManager) {
		this.g = g;
		//this.gp = gp;
	}
	
	public void init() {
		progression = 0;
	}
	
	@Override
	public void draw(double time) {
		if(progression < 100) {
			g.setColor(transparent);
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
			progression++;
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		g.setColor(Color.WHITE);
		g.drawString(Languages.PAUSE, 5, 39);
	}

	@Override
	public void update() {
		
	}

	public void keyPressed(int keyCode) {
		if(keyCode == KeyBindings.PAUSE) {
			GamePanel.gameRunning = true;
		}
	}

	public void setGraphics(Graphics2D g) {
		this.g = g;
	}
}
