package de.uni_koeln.dh.pera.data.story;

import java.util.ArrayList;
import java.util.List;

public class Choice {

	private String choice;
	private int next;
	private List<Access> accesses = new ArrayList<Access>();
	
	public Choice(String choice, String next) {
		this.choice = choice;
		this.next = Integer.parseInt(next);
	}
	
	public Choice() {

	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public String getChoice() {
		return choice;
	}

	public int getNext() {
		return next;
	}

	public List<Access> getAccesses() {
		return accesses;
	}

	public void setAccesses(List<Access> accesses) {
		this.accesses = accesses;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("access: ");
		for (Access access : accesses) {
			sb.append(access);
		}
		sb.append("--");
		sb.append("choice: " + choice + " - next: " + next + "\n");
		return sb.toString();
	}
	
	
}
