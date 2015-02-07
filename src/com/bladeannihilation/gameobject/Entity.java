package com.bladeannihilation.gameobject;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity extends Rectangle {
	/**
	 * UID generated
	 */
	private static final long serialVersionUID = -3594415350717399713L;
	
	public abstract void update();
	public abstract void render(Graphics g, double time);
}
