package de.uni_koeln.dh.pera.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.gui.ClientWrapper;
import de.uni_koeln.dh.pera.gui.core.img.ImgComposite;
import de.uni_koeln.dh.pera.gui.core.text.TextComposite;

public class Listener implements KeyListener  {
	
	private ClientWrapper wrapper = null; //TODO delete attribute?
	private TextComposite txtComp = null;
	private ImgComposite imgComp = null;
	
	public Listener(ClientWrapper wrapper, ImgComposite imgComp, TextComposite txtComp) {
		this.wrapper = wrapper;
		this.imgComp = imgComp;
		this.txtComp = txtComp;
//		this.txtComp.getStyledText().addKeyListener(this);
		
		
	}

	public void keyPressed(KeyEvent ke) {
//		if (ke.keyCode == SWT.CR) {
////		int newID = txtComp.updateText();
////		imgComp.updateAll(newID);
//	}		
	}


	public void keyReleased(KeyEvent arg0) {
		
		
	}


}
