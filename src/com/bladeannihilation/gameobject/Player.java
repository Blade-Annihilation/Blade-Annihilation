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
	private static BufferedImage norm = Resources.getPlayerState("norm");
	private static BufferedImage up = Resources.getPlayerState("up");
	private static BufferedImage down = Resources.getPlayerState("down");
	private static BufferedImage left = Resources.getPlayerState("left");
	private static BufferedImage right = Resources.getPlayerState("right");
	public static BufferedImage[] normAnim = new BufferedImage[4];
	public static BufferedImage[] upAnim = new BufferedImage[4];
	public static BufferedImage[] downAnim = new BufferedImage[4];
	public static BufferedImage[] leftAnim = new BufferedImage[4];
	public static BufferedImage[] rightAnim = new BufferedImage[4];
	public static final double movementDelta = 0.2;
	private GameState l;
	private byte animPlacement = 0;
	private byte animTimer = 0;
	public Player(Location loc, GameState l) {
		this.l = l;
		x = loc.x;
		y = loc.y;
		for(int i = 0; i < normAnim.length; i++) {
			normAnim[i] = norm.getSubimage(i*16, 0, 16, 32);
		}
		norm.flush();
		norm = null;
		for(int i = 0; i < upAnim.length; i++) {
			upAnim[i] = up.getSubimage(i*16, 0, 16, 32);
		}
		up.flush();
		up = null;
		for(int i = 0; i < downAnim.length; i++) {
			downAnim[i] = down.getSubimage(i*16, 0, 16, 32);
		}
		down.flush();
		down = null;
		for(int i = 0; i < leftAnim.length; i++) {
			leftAnim[i] = left.getSubimage(i*16, 0, 16, 32);
		}
		left.flush();
		left = null;
		for(int i = 0; i < rightAnim.length; i++) {
			rightAnim[i] = right.getSubimage(i*16, 0, 16, 32);
		}
		right.flush();
		right = null;
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
			if(KeyBindings.keysPressed[KeyBindings.ATTACK] && x >= 1 && l.currentLevel.tileAt((int)(x-1), (int)(y+0.5)) == Tile.BREAKABLE) {
				l.currentLevel.replaceTileAt((int)(x-1), (int)(y+0.5), Tile.COBBLESTONE);
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
			if(KeyBindings.keysPressed[KeyBindings.ATTACK] && x < l.currentLevel.getWidth()-1 && l.currentLevel.tileAt((int)(x+1), (int)(y+0.5)) == Tile.BREAKABLE) {
				l.currentLevel.replaceTileAt((int)(x+1), (int)(y+0.5), Tile.COBBLESTONE);
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
			if(KeyBindings.keysPressed[KeyBindings.ATTACK] && y < l.currentLevel.getHeight()-2 && l.currentLevel.tileAt((int)(x+0.5), (int)(y+2)) == Tile.BREAKABLE) {
				l.currentLevel.replaceTileAt((int)(x+0.5), (int)(y+2), Tile.COBBLESTONE);
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
			if(KeyBindings.keysPressed[KeyBindings.ATTACK] && y >= 0.5 && l.currentLevel.tileAt((int)(x+0.5), (int)(y-0.5)) == Tile.BREAKABLE) {
				l.currentLevel.replaceTileAt((int)(x+0.5), (int)(y-0.5), Tile.COBBLESTONE);
			}
		} else {
			yState = Movement.NORM;
		}
	}
	@SuppressWarnings("incomplete-switch")
	public BufferedImage getImage() {
		if(++animTimer > 5) {
			if(animPlacement < 3) {
				animPlacement++;
			} else {
				animPlacement = 0;
			}
			animTimer = 0;
		}
		switch(xState) {
		case CONTINUE:
			return rightAnim[animPlacement];
		case RETURN:
			return leftAnim[animPlacement];
		}
		switch(yState) {
		case CONTINUE:
			return downAnim[animPlacement];
		case RETURN:
			return upAnim[animPlacement];
		}
		return normAnim[animPlacement];
	}
}
