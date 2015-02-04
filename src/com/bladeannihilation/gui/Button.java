package com.bladeannihilation.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Button {
	
	public boolean coveredByMouse = false;
	private BufferedImage bi;
	private Rectangle rect;
	private Graphics2D g;
	private String text;
	private State state = State.STANDARD;
	
	private enum State {
		HOVER,
		PRESS,
		STANDARD
	}

	public Button(String text, int x, int y, int width, int height) {
		this.text = text;
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D)bi.getGraphics();
		rect = new Rectangle(x, y, width, height);
		drawReg();
	}
	public void regState() {
		if(state != State.STANDARD) {
			drawReg();
		}
	}
	public void drawReg() {
		//System.out.println("REDRAW");
		state = State.STANDARD;
		g.setColor(Colors.gray);
		g.fillRect(0, 0, rect.width, rect.height);
		g.setColor(Colors.light_gray);
		g.fillRect(0, 0, rect.width, 5);
		g.setColor(Colors.dark_gray);
		g.fillRect(rect.width - 5, 0, 5, rect.height);
		g.fillRect(0, 0, 5, rect.height);
		g.setColor(Colors.dark_shadow_gray);
		g.fillRect(0, rect.height - 5, rect.width, 5);
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        int x = (rect.width - (int) r.getWidth()) / 2;
        int y = (rect.height - (int) r.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
	}
	public void drawHover() {
		//System.out.println("REDRAW");
		state = State.HOVER;
		g.setColor(Colors.light2_gray);
		g.fillRect(0, 0, rect.width, rect.height);
		g.setColor(Colors.light_light_gray);
		g.fillRect(0, 0, rect.width, 5);
		g.setColor(Colors.light_dark_gray);
		g.fillRect(rect.width - 5, 0, 5, rect.height);
		g.fillRect(0, 0, 5, rect.height);
		g.setColor(Colors.light_dark_shadow_gray);
		g.fillRect(0, rect.height - 5, rect.width, 5);
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        int x = (rect.width - (int) r.getWidth()) / 2;
        int y = (rect.height - (int) r.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
	}
	public void drawPressed() {
		//System.out.println("REDRAW");
		state = State.PRESS;
		g.setColor(Colors.dark_gray);
		g.fillRect(0, 0, rect.width, rect.height);
		g.setColor(Colors.dark_dark_shadow_gray);
		g.fillRect(0, 0, rect.width, 5);
		g.setColor(Colors.dark_shadow_gray);
		g.fillRect(rect.width - 5, 0, 5, rect.height);
		g.fillRect(0, 0, 5, rect.height);
		g.setColor(Colors.dark_light_dark_gray);
		g.fillRect(0, rect.height - 5, rect.width, 5);
		g.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(text, g);
        int x = (rect.width - (int) r.getWidth()) / 2;
        int y = (rect.height - (int) r.getHeight()) / 2 + fm.getAscent() + 1;
        g.drawString(text, x, y);
	}
	public void setLocation(int x, int y) {
		rect.setLocation(x, y);
	}
	public void draw(Graphics2D g) {
		g.drawImage(bi, rect.x, rect.y, rect.width, rect.height, null);
	}
	public boolean testCollision(Point p, boolean mouseDown) {
		if(this.rect.contains(p)) {
			if(!mouseDown) {
				if(state != State.HOVER)
					drawHover();
			} else {
				if(state != State.PRESS) {
					drawPressed();
				}
			}
			return true;
		} else {
			if(state != State.STANDARD)
				drawReg();
		}
		return false;
	}
	public BufferedImage getImage() {
		return bi;
	}
	public int getX() {
		return rect.x;
	}
	public int getY() {
		return rect.y;
	}
}
