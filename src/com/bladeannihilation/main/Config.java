package com.bladeannihilation.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Config {
	public String language;
	public Config() {
		File f = Resources.getFile("config");
		if(!f.exists()) {
			try {
				f.createNewFile();
				PrintWriter writer = new PrintWriter(f);
				writer.println("en_us");
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Scanner scan = new Scanner(f);
			language = scan.nextLine();
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		f = Resources.getFile("config_tmp");
		if(f.exists()) {
			f.delete();
		}
	}
	public static void setLanguageAndUpdate(String language) {
		setLanguage(language);
		Languages.initialize(language);
		Game.setTitle(Languages.TITLE);
	}
	public static void setLanguage(String language) {
		if(language.length() != 5) {
			return;
		}
		File f = Resources.getFile("config");
		if(!f.exists()) {
			try {
				f.createNewFile();
				PrintWriter writer = new PrintWriter(f);
				writer.println(language);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			File tmp = Resources.getFile("config_tmp");
			File config = Resources.getFile("config");
			BufferedReader br = new BufferedReader(new FileReader(config));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
			bw.write(language, 0, 5);
			String currentLine;
			br.readLine();
			while((currentLine = br.readLine()) != null) {
				bw.write(currentLine + "\n"); //don't care about cross platform separators
			}
			bw.flush();
			bw.close();
			br.close();
			tmp.renameTo(config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
