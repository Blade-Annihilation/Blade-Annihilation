package com.bladeannihilation.state;

import java.awt.Graphics2D;

import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Updatable;

public class GameStateManager implements Updatable {
	private GamePanel gp;
	private Graphics2D g;
	private State state;
	private MenuState ms;
	private PauseState ps;

	public enum State {
		MENU,
		GAME,
		PAUSE,
		PREFERENCES,
		GAME_PREFERENCES
	}
	
	public void setGraphics(Graphics2D g) {
		this.g = g;
		if(ms != null) {
			ms.setGraphics(g);
		}
	}

	public boolean setState(State state) {
		switch(state) {
			case GAME:
				break;
			case GAME_PREFERENCES:
				break;
			case MENU:
				if(ms == null) {
					ms = new MenuState(g, this);
				}
				this.state = State.MENU;
				break;
			case PAUSE:
				if(ps == null) {
					ps = new PauseState(g, this);
				} else {
					ps.init();
				}
				this.state = State.PAUSE;
				break;
			case PREFERENCES:
				break;
		}
		return false;
	}

	public GameStateManager(GamePanel gp) {
		this.gp = gp;
		setState(State.MENU);
	}

	public void draw(double time) {
		switch(state) {
			case GAME:
				break;
			case GAME_PREFERENCES:
				break;
			case MENU:
				ms.draw(time);
				break;
			case PAUSE:
				//gp.gs.draw(time);
				ps.draw(time);
				break;
			case PREFERENCES:
				break;
			default:
				break;
		}
	}

	public void update() {
		switch(state) {
		case GAME:
			break;
		case GAME_PREFERENCES:
			break;
		case MENU:
			ms.update();
			break;
		case PAUSE:
			break;
		case PREFERENCES:
			break;
		default:
			break;
		}
	}

	public void keyPressed(int keyCode) {
		switch(state) {
		case GAME:
			break;
		case GAME_PREFERENCES:
			break;
		case MENU:
			break;
		case PAUSE:
			ps.keyPressed(keyCode);
			break;
		case PREFERENCES:
			break;
		default:
			break;
		}
	}

	public void keyReleased(int keyCode) {
		
	}

	public void initGame() {
		gp.initGame();
	}
}
