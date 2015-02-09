package com.bladeannihilation.keyboard;

import java.awt.event.KeyEvent;

public class KeyBindings {
	public static final int UP = 87;
	public static final int DOWN = 83;
	public static final int RIGHT = 68;
	public static final int LEFT = 65;
	public static final int ATTACK = 81;
	public static final int ABILITY_1 = 69;
	public static final int ABILITY_2 = 82;
	public static final int ABILITY_3 = 70;
	public static final int ABILITY_4 = 84;
	public static final int PAUSE = 80;
	public static final int EXIT = 27;
	public static final int SCROLL_UP = 73;
	public static final int SCROLL_LEFT = 74;
	public static final int SCROLL_RIGHT = 76;
	public static final int SCROLL_DOWN = 75;
	public static final int ZOOM_IN = KeyEvent.VK_PERIOD;
	public static final int ZOOM_OUT = KeyEvent.VK_COMMA;
	//public static final int length = State.values().length;
	public static final boolean[] keysPressed = new boolean[150];
	/*public enum State {
		UP(),
		DOWN(),
		RIGHT(),
		LEFT(),
		ATTACK(),
		ABILITY_1(),
		ABILITY_2(),
		ABILITY_3(),
		ABILITY_4(),
		PAUSE(),
		EXIT(),
		SCROLL_UP(),
		SCROLL_LEFT(),
		SCROLL_RIGHT(),
		SCROLL_DOWN;
		
		public boolean pressed;
		private State() {
			pressed = false;
		}
		public void setPressed(boolean pressed) {
			this.pressed = pressed;
		}
	}*/
}
