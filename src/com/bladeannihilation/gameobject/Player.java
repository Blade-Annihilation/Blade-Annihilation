package com.bladeannihilation.gameobject;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import com.bladeannihilation.keyboard.KeyBindings;
import com.bladeannihilation.main.Resources;
import com.bladeannihilation.state.GameState;

public class Player extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7866169072443073011L;
	public static final BufferedImage norm = Resources.getPlayerState("norm");
	public static final BufferedImage up = Resources.getPlayerState("up");
	public static final BufferedImage down = Resources.getPlayerState("down");
	public static final BufferedImage left = Resources.getPlayerState("left");
	public static final BufferedImage right = Resources.getPlayerState("right");
	public static final double movementDelta = 0.2;
	private GameState l;
	public Player(Location loc, GameState l) {
		this.l = l;
		x = loc.x;
		y = loc.y;
	}
	public enum Movement {
		CONTINUE,
		RETURN,
		NORM
	}
	public Movement xState = Movement.NORM;
	public Movement yState = Movement.NORM;
	@Override
	public void update() {
		if(KeyBindings.keysPressed[KeyBindings.LEFT]) {
			if(l.currentLevel.movable((int)(x-movementDelta), (int)y) && l.currentLevel.movable((int)(x-movementDelta), (int)(y+1)) && ((int)y == y ? true : l.currentLevel.movable((int)(x-movementDelta), (int)(y+2)))) {
				x-=movementDelta;
			} else if(x > (int)(x-movementDelta+1)) {
				x = (int)(x-movementDelta+1);
			}
			xState = Movement.RETURN;
			if(!l.isFollowingPlayer()) {
				l.followPlayer();
			}
		} else if(KeyBindings.keysPressed[KeyBindings.RIGHT]) {
			if(l.currentLevel.movable((int)(x+1+movementDelta), (int)y) && l.currentLevel.movable((int)(x+1+movementDelta), (int)(y+1)) && ((int)y == y ? true : l.currentLevel.movable((int)(x+1+movementDelta), (int)(y+2)))) {
				x+=movementDelta;
				if(x+1 >= l.currentLevel.getWidth()) {
					x = l.currentLevel.getWidth()-1-movementDelta;
				}
			} else if(x < (int)(x+movementDelta)) {
				x = (int)(x+movementDelta);
			}
			xState = Movement.CONTINUE;
			if(!l.isFollowingPlayer()) {
				l.followPlayer();
			}
		} else {
			xState = Movement.NORM;
		}
		if(KeyBindings.keysPressed[KeyBindings.DOWN]) {
			if(l.currentLevel.movable((int)x, (int)(y+2+movementDelta)) && ((int)x == x ? true : l.currentLevel.movable((int)(x+1), (int)(y+2+movementDelta)))) {
				y+=movementDelta;
			} else if(y < (int)(y+2+movementDelta)) {
				y = (int)(y+movementDelta);
			}
			yState = Movement.CONTINUE;
			if(!l.isFollowingPlayer()) {
				l.followPlayer();
			}
			if(l.currentLevel.isSublevel && l.currentLevel.feetTouchExit((int)x, (int)y)) {
				l.popLevel();
				Location s = l.currentLevel.loadLoc();
				x = s.x;
				y = s.y+1;
			}
		} else if(KeyBindings.keysPressed[KeyBindings.UP]) {
			if(l.currentLevel.movable((int)x, (int)(y-movementDelta)) && ((int)x == x ? true : l.currentLevel.movable((int)(x+1), (int)(y-movementDelta)))) {
				y-=movementDelta;
			} else if(y > (int)y) {
				y = (int)y;
			}
			yState = Movement.RETURN;
			if(!l.isFollowingPlayer()) {
				l.followPlayer();
			}
			if(l.currentLevel.headTouchesDoor((int)x, (int)y)) {
				try {
					l.currentLevel.storeLoc(new Location((int)x, (int)y));
					l.pushLevel(new Level(l.currentLevel.getFilename(), l.currentLevel.doorID((int)x, (int)y)));
					Location s = l.currentLevel.getSpawn();
					x = s.x;
					y = s.y-2;
				} catch (FileNotFoundException e) {
					l.currentLevel.loadLoc();
				}
			}
		} else {
			yState = Movement.NORM;
		}
	}
	@SuppressWarnings("incomplete-switch")
	public BufferedImage getImage() {
		switch(xState) {
		case CONTINUE:
			return right;
		case RETURN:
			return left;
		}
		switch(yState) {
		case CONTINUE:
			return down;
		case RETURN:
			return up;
		}
		return norm;
	}
}