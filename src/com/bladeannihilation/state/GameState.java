package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;

import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Updateable;

public class GameState implements Updateable{
	
	private Graphics2D g;
	
	public GameState(Graphics2D g) {
		this.g = g;
	}
	
	int r;
	int gz;
	int b;
	
	@Override
	public void draw(double time) {
		g.setColor(new Color(r, gz, b));
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.setColor(Color.WHITE);
	}

	@Override
	public void update() {
		if(++r > 50) {
			r = 0;
		}
		if(++gz > 150) {
			gz = 0;
		}
		if(++b > 254) {
			b = 0;
		}
	}

}
