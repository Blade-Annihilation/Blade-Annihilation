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
	public static String NEW_GAME;
	public static String SAVE_RESTORE;
	public static String BACK;
	private static final String defaultLanguage = "en_us";
	public static String currentLanguage = defaultLanguage;
	public static final String[] strings = {"en_us", "std_b"};

	public static void initialize(String language) {
		File f = Resources.getLanguage(language);
		try {
			Scanner s = new Scanner(f);
			parse(s);
			currentLanguage = language;
		} catch (FileNotFoundException e) {
			try {
				Scanner s = new Scanner(Resources.getLanguage(defaultLanguage));
				parse(s);
				Config.setLanguage(defaultLanguage);
				currentLanguage = defaultLanguage;
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
			} else if(header.equals("NEW_GAME")) {
				NEW_GAME = line;
			} else if(header.equals("SAVE_RESTORE")) {
				SAVE_RESTORE = line;
			} else if(header.equals("BACK")) {
				BACK = line;
			}
		}
		s.close();
	}

}
