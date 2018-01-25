package de.uni_koeln.dh.pera.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<String> inventory = null;
	private int currentChapterNode;
	
	public Player() {
		inventory = new ArrayList<String>();
		currentChapterNode = 0;
	}

	public List<String> getInventory() {
		return inventory;
	}

	public void setInventory(List<String> inventory) {
		this.inventory = inventory;
	}
	
	public void addIcon(String icon) {
		inventory.add(icon);
	}

	public int getCurrentChapterNode() {
		return currentChapterNode;
	}

	public void setCurrentChapterNode(int currentChapterNode) {
		this.currentChapterNode = currentChapterNode;
	}

}
