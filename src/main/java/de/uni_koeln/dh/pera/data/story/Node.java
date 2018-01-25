package de.uni_koeln.dh.pera.data.story;


import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private int id;
	private String chapter;
	private String latitude;
	private String longitude;
	private String text;
	private Boolean openanswer;
	private Boolean clearinventory;
	private List<Choice> choices;
	private List<Element> elements = new ArrayList<Element>();
	
	

	public Node(String id, String text, Boolean openanswer, List<Choice> choices,
			String latitude, String longitude, String picture) {
		this.text = text;
		this.id = Integer.parseInt(id);
		this.choices = choices;
		this.openanswer = openanswer;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Node() {
		clearinventory = false;
	}
	
	
	
	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setOpenanswer(Boolean openanswer) {
		this.openanswer = openanswer;
	}
	
	public Boolean getOpenanswer() {
		return openanswer;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public int getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public List<Choice> getChoices() {
		return choices;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + id + "--" + "chapter: " + chapter +  "\n");
		sb.append(text + "\n");
		for(Choice choice : choices) {
			sb.append(choice);
		}
		sb.append("lat: " + latitude + " - long: " + longitude + "\n");
		if(elements != null) {
			for(Element element : elements) {
				sb.append(element.getElement() + "\n");
			}
		}
		sb.append("*************");
		
		return sb.toString();
	
	}

	public Boolean getClearinventory() {
		return clearinventory;
	}

	public void setClearinventory(Boolean clearinventory) {
		this.clearinventory = clearinventory;
	}

	
}
