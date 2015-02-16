package com.bladeannihilation.main;

import java.awt.Graphics2D;

public interface Updatable {
	public void draw(double time, Graphics2D g);
	public void update();
}
