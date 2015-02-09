package com.bladeannihilation.main;	

import javax.sound.sampled.*;
import java.io.*;

public class Audio{
	public void setSong(String songName){
      String song = spongName;
   } 
   public void audioRun(){
      AudioInputStream stream;
		AudioFormat format;
		DataLine.Info info;
		Clip clip;
        while(true){
			stream = AudioSystem.getAudioInputStream(song);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();			
		}//end while loop  	
   }
}
