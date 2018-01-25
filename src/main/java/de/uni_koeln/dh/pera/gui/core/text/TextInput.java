package de.uni_koeln.dh.pera.gui.core.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;

// TODO check
public class TextInput extends StyledText {

		private Logger logger = LoggerFactory.getLogger(getClass());
		
		private TextComposite parent = null;
		
		private final String defaultText = "...";		// TODO default text
		private int margin = 0;
		
		private FocusListener focus = new FocusListener() {
				private boolean initial = true;
				
			public void focusLost(FocusEvent arg0) {
				if (getText().trim().equals("")) 
					setText(defaultText);
			}
			
			public void focusGained(FocusEvent arg0) {
				if (!initial) {
					if (getText().trim().equals(defaultText)) setText("");
				} else
					initial = false;
			}
		};
		
		// TODO delete defaultText if firstEvent == keyEvent 
		private KeyListener key = new KeyListener() {
			public void keyReleased(KeyEvent e) {
				// RETURN or ENTER
				if ((e.keyCode == 0xd) || (e.keyCode == 0x1000050)) {		
					String str = getText().trim();
					
					if (!str.equals("") && !str.equals(defaultText))
						logger.info("INPUT (" + e.keyCode + "): " + str);
				}
			}
			
			public void keyPressed(KeyEvent arg0) {}
		};
		
	protected TextInput(TextComposite parent) {
		super(parent, SWT.SINGLE /*| SWT.BORDER*/);
		this.parent = parent;
	}
	
	protected void init() {
		logger.info("Initialize text input...");
		
		setMargin();
		setLayoutData(getWidth());

		setColors();
		configText();

		setFocus();
	}
	
	private void setColors() {
		setBackground(new Color(Display.getCurrent(), 51,51,51));		// TODO system color?
		setForeground(parent.getForeground());
	}
	
	// TODO name
	private GridData getWidth() {
		int inputWidth = parent.getWidth() - (2*margin);		// actually less pixel(s) required (whyever..); os different
		return LayoutHelper.getGridData(inputWidth);
	}
	
	private void configText() {
		setFont(parent.getFont(SWT.BOLD));
		setTextLimit(80);			// TODO adequate limit?
		setText(defaultText/*"Lorem ipsum dolor sit amet."*/);
		
		addFocusListener(focus);
		addKeyListener(key);
	}
	
	private void setMargin() {
		margin = parent.getTextMargin();
		LayoutHelper.setMargin(this, margin, 5);
	}
	
}
