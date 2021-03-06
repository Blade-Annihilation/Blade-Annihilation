package com.bladeannihilation.keyboard;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.state.GameState;
import com.bladeannihilation.state.GameStateManager;

public class KeyboardHandler implements KeyListener {
	//private KeyBindings[] kb;
	GameStateManager gsm;
	GameState gs;
	public KeyboardHandler(GameStateManager gsm, GameState gs) {
		this.gsm = gsm;
		this.gs = gs;
	}
	public void setGameState(GameState gs) {
		this.gs = null;
		this.gs = gs;
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() < 150) {
			KeyBindings.keysPressed[e.getKeyCode()] = true;
		}
		if(GamePanel.gameRunning) {
			if(gs != null) {
				gs.keyPressed(e.getKeyCode());
			}
		} else {
			if(gsm != null) {
				gsm.keyPressed(e.getKeyCode());
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() < 150) {
			KeyBindings.keysPressed[e.getKeyCode()] = false;
		}
		if(GamePanel.gameRunning) {
			if(gs != null) {
				gs.keyReleased(e.getKeyCode());
			}
		} else {
			if(gsm != null) {
				gsm.keyReleased(e.getKeyCode());
			}
		}
	}
	public void keyTyped(KeyEvent e) {}

}
