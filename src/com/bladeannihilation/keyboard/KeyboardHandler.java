package com.bladeannihilation.keyboard;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyboardHandler implements KeyListener {
	private KeyBindings[] kb;
	public void keyPressed(KeyEvent e) {
		System.out.println("Press: " + e.getKeyCode());
	}
	public void keyReleased(KeyEvent e) {
		System.out.println("Release: " + e.getKeyCode());
	}
	public void keyTyped(KeyEvent e) {
		System.out.println("Type: " + e.getKeyCode());
	}

}
