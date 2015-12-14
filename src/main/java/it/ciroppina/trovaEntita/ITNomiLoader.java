package it.ciroppina.trovaEntita;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ITNomiLoader {
	
	public static String NOMI;
	public static PrintStream console;
	
	static {
		console = System.out;
		FileInputStream in = null;
		File f = new File(System.getProperty("user.dir") + "/src/main/resources/nomi_italiani.txt");
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			f = new File(System.getProperty("user.dir") + "/applconf/nomi_italiani.txt");
			try {
				in = new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		byte[] b;
		try {
			b = new byte[in.available()];
			in.read(b);
			NOMI = new String(b, StandardCharsets.UTF_8);
			//debug:console.print(NOMI);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {}

	public static boolean splitAndSearch(String entity) {
		String[] splitted = entity.split(" ");
		for (String part : splitted) {
			if (part.trim().length() < 3) {}
			else //if (ITNomiLoader.NOMI.toUpperCase().contains(part.trim().toUpperCase()+"\n") == true) 
			{
				Matcher matcher = Pattern.compile("\\b"+part.trim().toUpperCase()+"\\b")
					.matcher(ITNomiLoader.NOMI.toUpperCase());
				if (matcher.find()) return true;
			}
		}
		return false;
	}
}
