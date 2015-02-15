package com.bladeannihilation.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

class BackgroundAudio implements Runnable {
	protected AudioInputStream ais;
	protected SourceDataLine sdl;
	boolean contRun = true;
	private String name;
	private File audioFile;
	protected BackgroundAudio(String name) {
		this.name = name;
	}
	@Override
	public void run() {
		while(contRun) {
			audioFile = Resources.getAudio(name);
			try {
				ais = AudioSystem.getAudioInputStream(audioFile);
			} catch (UnsupportedAudioFileException e) {
				System.out.println("BGUnable to play audio (Err 1): " + name);
				e.printStackTrace();
				return;
			} catch (IOException e) {
				System.out.println("BGUnable to play audio (Err 3): " + name);
				e.printStackTrace();
				return;
			}
			AudioFormat format = ais.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine sdl;
			int bufferSize = 128000;
			try {
				sdl = (SourceDataLine)AudioSystem.getLine(info);
				bufferSize = sdl.getBufferSize();
				sdl.open(format);
			} catch (LineUnavailableException e) {
				System.out.println("BGUnable to play audio (Err 2): " + name);
				e.printStackTrace();
				return;
			}
			sdl.start();
			int bytesRead = 0;
			byte[] buffer = new byte[bufferSize];
			while(bytesRead != -1 && contRun) {
				if(ais == null) {
					return;
				}
				try {
					bytesRead = ais.read(buffer, 0, bufferSize);
				} catch(IOException ioe) {
					System.out.println("BGAudio error during playback (Err 4): " + name);
				}
				if(bytesRead >= 0) {
					sdl.write(buffer, 0, bytesRead);
				}
			}
			if(sdl != null) {
				sdl.drain();
				sdl.close();
			}
			try {
				if(ais != null) {
					ais.close();
				}
			} catch (IOException e) {
				System.out.println("AIS close error");
				e.printStackTrace();
			}
		}
	}
	public void endRun() {
		System.out.println("BG audio: Received end run");
		contRun = false;
		if(sdl != null) {
			sdl.drain();
			sdl.close();
		}
		try {
			if(ais != null) {
				ais.close();
			}
		} catch (IOException e) {
			System.out.println("AIS close error");
			e.printStackTrace();
		}
		ais = null;
		sdl = null;
	}

}
