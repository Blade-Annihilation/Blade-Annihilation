package com.bladeannihilation.state;

import java.awt.Graphics2D;

import com.bladeannihilation.main.Audio;
import com.bladeannihilation.main.GamePanel;
import com.bladeannihilation.main.Updatable;

public class GameStateManager implements Updatable {
	private GamePanel gp;
	private State state;
	private MenuState ms;
	private PauseState ps;
	private PreferencesState prefs;

	public enum State {
		MENU,
		GAME,
		PAUSE,
		PREFERENCES,
		GAME_PREFERENCES
	}
	
	public boolean setState(State state) {
		switch(state) {
			case GAME:
				break;
			case GAME_PREFERENCES:
				break;
			case MENU:
				if(ms == null) {
					ms = new MenuState(this);
				}
				ms.init();
				prefs = null;
				ps = null;
				System.gc();
				this.state = State.MENU;
				Audio.setBackgroundMusic("turnDownForWhat");
				break;
			case PAUSE:
				if(ps == null) {
					ps = new PauseState(this);
				} else {
					ps.init();
				}
				this.state = State.PAUSE;
				Audio.endBackgroundMusic();
				break;
			case PREFERENCES:
				if(prefs == null) {
					prefs = new PreferencesState(this);
				} else {
					prefs.init();
				}
				ps = null;
				ms = null;
				System.gc();
				this.state = State.PREFERENCES;
				Audio.setBackgroundMusic("testfile");
				break;
		}
		return false;
	}

	public GameStateManager(GamePanel gp) {
		this.gp = gp;
		setState(State.MENU);
	}

	public void draw(double time, Graphics2D g) {
		switch(state) {
			case GAME:
				break;
			case GAME_PREFERENCES:
				break;
			case MENU:
				ms.draw(time, g);
				break;
			case PAUSE:
				if(ps.progression < ps.progressionMax) {
					gp.gs.draw(time, g);
				}
				ps.draw(time, g);
				break;
			case PREFERENCES:
				prefs.draw(time, g);
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
			ps.update();
			break;
		case PREFERENCES:
			prefs.update();
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
