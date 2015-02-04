package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;

import java.io.FileNotFoundException;

import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Updateable;
import com.bladeannihilation.gameobject.Level;

public class GameState implements Updateable{
	
	private Graphics2D g;
   private Level currentLevel;
	
	public GameState(Graphics2D g) {
		this.g = g;
      try {
         currentLevel = new Level("tutorial.lvl");
      } catch(FileNotFoundException fnfe) {
         fnfe.printStackTrace();
         System.out.println("Tutorial not found, shutting down.");
         System.exit(1);
      }
	}
   
   public void setGraphics(Graphics2D g) {
      this.g = g;
   }
	
	int r;
	int gz;
	int b;
	
	@Override
	public void draw(double time) {
		//g.setColor(new Color(r, gz, b));
      g.drawImage(currentLevel.bi, 0, 0, currentLevel.getWidth()*16, currentLevel.getHeight()*16, null);
		//g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
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
