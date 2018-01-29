package de.uni_koeln.dh.pera.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uni_koeln.dh.pera.data.Player;

public class Serializer {
	
	private static String storageFolder = "src/main/resources/gamestatus";
	private static String demoConfig = "src/main/resources/demo.config";

	/**
	 * serializes players inventory at the given node
	 * @param currentID
	 * @param player
	 * @throws IOException
	 */
	public static void serialize(int currentID, Player player) throws IOException {
		
		//TODO serialize player object?
		List<String>inventory = player.getInventory();
		
		StringBuilder sb = new StringBuilder();
		sb.append("elements: ");
		for (String string : inventory) {
			sb.append(string + ",");
		}
		sb.append("\n" + "node: " + currentID);
		File configFile = new File(storageFolder + "/gamestatus.config");
		if(!configFile.exists()) {
			File folder = new File(storageFolder);
			if(!folder.exists()) {
				folder.createNewFile();
				
			}
			folder.mkdirs();
			//TODO causes exception if folder doesn't exist
			configFile.createNewFile();
		}
			

			
		FileWriter fw = new FileWriter(configFile);
		fw.write(sb.toString());
		fw.close();

	}
	
	/**
	 * proves if any game status exists. if not, a new player-object will be
	 * initialized
	 * @param player
	 * @return
	 * @throws IOException
	 */
	public static Player deserialize(Player player){
		
		player = new Player();
		FileInputStream in;
		try {
			in = new FileInputStream(storageFolder + "/gamestatus.config");
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader bfr = new BufferedReader(isr);
			
			String currentLine;
			while((currentLine = bfr.readLine()) != null) {
				if(currentLine.startsWith("elements: ")){
					player.setInventory(new ArrayList<String>(Arrays.asList(
							currentLine.replace("elements: ", "").split(","))));
				}
				if(currentLine.startsWith("node: "))
					player.setCurrentChapterNode(Integer.parseInt(currentLine.replace("node: ", "")));
			}
			bfr.close();
		} catch (FileNotFoundException e) {
			
		} catch (NumberFormatException e) {
			
		} catch (IOException e) {
			
		}

		return player;
	}

	public static void deleteFiles() {

		File folder = new File(storageFolder);
		if(folder.isDirectory()) {
			String[] files = folder.list();
			
			for (int i = 0; i < files.length; i++) {
				File file = new File(storageFolder + "/" + files[i]);
				String path = file.getAbsolutePath();
				if (path.endsWith(".config"))
					file.delete();
				
			}
		}
		
	}

	public static Player deserializeDemo(Player player) {
		player = new Player();
		FileInputStream in;
		try {
			in = new FileInputStream(demoConfig);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader bfr = new BufferedReader(isr);
			
			String currentLine;
			while((currentLine = bfr.readLine()) != null) {
				if(currentLine.startsWith("elements: ")){
					player.setInventory(new ArrayList<String>(Arrays.asList(
							currentLine.replace("elements: ", "").split(","))));
				}
				if(currentLine.startsWith("node: "))
					player.setCurrentChapterNode(Integer.parseInt(currentLine.replace("node: ", "")));
			}
			bfr.close();
		} catch (FileNotFoundException e) {
			
		} catch (NumberFormatException e) {
			
		} catch (IOException e) {
			
		}

		return player;
	}

}
