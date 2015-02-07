package com.bladeannihilation.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
	public static String sep = File.separator;
	public static BufferedImage getImage(String filename) {
		File f = new File("resources" + sep + "images" + sep + filename);
		try {
			return ImageIO.read(f);
		} catch (IOException e) {
			System.out.print("RESOURCE UNAVAILABLE");
			e.printStackTrace();
		}
		return null;
	}
	public static File getFile(String filename) {
		return new File(filename);
	}
	public static File getLevel(String filename) {
		return new File("level" + sep + filename);
	}
	//public static int getLevels() {
	//	
	//}
}
