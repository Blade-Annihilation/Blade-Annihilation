package com.bladeannihilation.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Languages {
	public static String TITLE;
	public static String START;
	public static String PREFERENCES;
	public static String QUIT;
	public static String PAUSE;
	private static final String defaultLanguage = "en_us";

	public static void initialize(String language) {
		File f = Resources.getLanguage(language);
		try {
			Scanner s = new Scanner(f);
			parse(s);
		} catch (FileNotFoundException e) {
			try {
				Scanner s = new Scanner(Resources.getLanguage(defaultLanguage));
				parse(s);
				Config.setLanguage(defaultLanguage);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	private static void parse(Scanner s) {
		String line;
		String header;
		while(s.hasNextLine()) {
			line = s.nextLine();
			header = line.substring(0, line.indexOf(' '));
			line = line.substring(line.indexOf(' ')+1);
			if(header.equals("TITLE")) {
				TITLE = line;
			} else if(header.equals("START")) {
				START = line;
			} else if(header.equals("PREFERENCES")) {
				PREFERENCES = line;
			} else if(header.equals("QUIT")) {
				QUIT = line;
			} else if(header.equals("PAUSE")) {
				PAUSE = line;
			}
		}
		s.close();
	}

}
