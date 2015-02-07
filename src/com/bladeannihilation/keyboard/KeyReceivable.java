package com.bladeannihilation.keyboard;

public interface KeyReceivable {
	public void receiveKeydown(KeyBindings kb);
	public void receiveKeyup(KeyBindings kb);
	public void receiveRelease(KeyBindings kb);
}
