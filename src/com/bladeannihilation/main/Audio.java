package com.bladeannihilation.main;	

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
	private static BackgroundAudio ba;
	private static Thread bgThread;
	private static boolean isBackgroundRunning = false;
	public static void playSound(final String name) {
		new Thread() {
			@Override
			public void run() {
				AudioInputStream audioIn;
				try {
					audioIn = AudioSystem.getAudioInputStream(Resources.getAudio(name));
				} catch (UnsupportedAudioFileException e) {
					System.out.println("Unable to play audio (Err 1): " + name);
					e.printStackTrace();
					return;
				} catch (IOException e) {
					System.out.println("Unable to play audio (Err 3): " + name);
					e.printStackTrace();
					return;
				}
				AudioFormat format = audioIn.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				SourceDataLine sdl;
				int bufferSize = 128000;
				try {
					sdl = (SourceDataLine)AudioSystem.getLine(info);
					bufferSize = sdl.getBufferSize();
					sdl.open(format);
				} catch (LineUnavailableException e) {
					System.out.println("Unable to play audio (Err 2): " + name);
					e.printStackTrace();
					return;
				}
				sdl.start();
				int bytesRead = 0;
				byte[] buffer = new byte[bufferSize];
				while(bytesRead != -1) {
					try {
						bytesRead = audioIn.read(buffer, 0, bufferSize);
					} catch(IOException ioe) {
						System.out.println("Audio error during playback (Err 4): " + name);
					}
					if(bytesRead >= 0) {
						sdl.write(buffer, 0, bytesRead);
					}
				}
				sdl.drain();
				sdl.close();
				try {
					audioIn.close();
				} catch (IOException e) {
					System.out.println("Couldn't close properly (Err 5): " + name);
					e.printStackTrace();
				}
			}
		}.start();
	}
	public static synchronized void setBackgroundMusic(String name) {
		if(ba != null) {
			ba.endRun();
		}
		bgThread = null;
		ba = null;
		System.gc();
		isBackgroundRunning = true;
		ba = new BackgroundAudio(name);
		bgThread = new Thread(ba);
		bgThread.start();
	}
	public static void endBackgroundMusic() {
		if(ba != null) {
			ba.endRun();
			ba = null;
		}
		bgThread = null;
		System.gc();
		isBackgroundRunning = false;
	}
	public static void main(String[] args) throws Exception
	{
		setBackgroundMusic("turnDownForWhat");
		
		for(int i = 0; i < 100; i++) {
			setBackgroundMusic("turnDownForWhat");
			Thread.sleep(100);
		}
	}
}
