package com.bladeannihilation.state;

import java.awt.Color;
import java.awt.Graphics2D;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.Config;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Languages;
import com.bladeannihilation.main.Updatable;

public class PreferencesState implements Updatable {
	private Graphics2D g;
	private GameStateManager gsm;
	private boolean pressWaiting = false;
	
	public PreferencesState(Graphics2D g, GameStateManager gsm) {
		this.g = g;
		this.gsm = gsm;
	}
	
	@Override
	public void draw(double time) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		g.setColor(Color.WHITE);
		g.drawString("Press p to change language", 5, 27);
		g.drawString("Current language: " + languageToSet, 5, 39);
		g.drawString("Press esc to exit", 5, 51);
	}
	
	private String languageToSet = Languages.currentLanguage;
	private boolean languageSet = false;
	
	@Override
	public void update() {
		if(KeyBindings.keysPressed[KeyBindings.PAUSE]) {
			pressWaiting = true;
		} else if(pressWaiting == true) {
			pressWaiting = false;
			int index = java.util.Arrays.asList(Languages.strings).indexOf(languageToSet);
			if(index < Languages.strings.length - 1) {
				index++;
			} else {
				index = 0;
			}
			languageToSet = Languages.strings[index];
			languageSet = true;
		} else if(KeyBindings.keysPressed[KeyBindings.EXIT]) {
			if(languageSet) {
				Config.setLanguageAndUpdate(languageToSet);
			}
			gsm.setState(GameStateManager.State.MENU);
		}
	}

	public void setGraphics(Graphics2D g) {
		this.g = g;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

}
