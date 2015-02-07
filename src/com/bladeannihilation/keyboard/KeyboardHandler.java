package com.bladeannihilation.keyboard;

public class KeyboardHandler {
	private static KeyReceivable[] events;
	private static void addToReceivable(KeyReceivable kr) {
		int len = events.length;
		KeyReceivable[] buffer = new KeyReceivable[len+1];
		for(int i = 0; i < events.length; i++) {
			buffer[i] = events[i];
		}
		buffer[len] = kr;
		events = buffer;
		System.gc();
	}
	public static void registerHandler(KeyReceivable kr) {
		addToReceivable(kr);
	}
}
