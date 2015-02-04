package com.bladeannihilation.state;

import java.awt.Graphics2D;

import com.bladeannihilation.main.Updateable;

public class GameStateManager implements Updateable {
	private Graphics2D g;
	private State state;
	private MenuState ms;

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
				break;
			case PREFERENCES:
				break;
		}
		return false;
	}

	public GameStateManager() {
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
}
