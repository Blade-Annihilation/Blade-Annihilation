package com.bladeannihilation.main;	

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Audio{
	//what is this actually being passed?
   public static void audioRun(String name) throws IOException {
	   InputStream in = new FileInputStream(Resources.getAudio(name));
	   AudioStream audioStream = new AudioStream(in);
	   AudioPlayer.player.start(audioStream);
   }
   public static void main(String[] args) throws Exception
   {
	   String turn="turnDownForWhat.wav";
	   audioRun(turn);
   }
}
