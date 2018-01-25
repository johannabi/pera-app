package de.uni_koeln.dh.pera;

import java.io.IOException;

import de.uni_koeln.dh.pera.gui.View;

// TODO Javadoc
public class Application {
	
	public static void main(String[] args) {		
		View view = new View("Peros abenteuerliche Reise nach Konstantinopel");
		view.init();
		
		if (view.isInitialized()) {
			try {
				view.loadComponents();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			view.show();
		}
		
		view.dispose();
	}

}
