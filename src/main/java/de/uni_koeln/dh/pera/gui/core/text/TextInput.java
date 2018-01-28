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

import de.uni_koeln.dh.pera.Updater;
import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;

public class TextInput extends StyledText {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final int MARGIN_TOPBOTTOM = 5;

	private TextComposite parent = null;
	private Updater updater = null;

	private final String defaultText = "...";
	private int margin_leftRight = 0;

	private FocusListener focus = new FocusListener() {
		private boolean initial = true;

		public void focusLost(FocusEvent arg0) {
			if (getText().trim().equals(""))
				setText(defaultText);
		}

		public void focusGained(FocusEvent arg0) {
			if (!initial) {
				if (getText().trim().equals(defaultText))
					setText("");
			} else
				initial = false;
		}
	};

	private KeyListener key = new KeyListener() {
		private String input;

		public void keyReleased(KeyEvent e) {
			// RETURN or ENTER
			if ((e.keyCode == 0xd) || (e.keyCode == 0x1000050)) {
				input = getText().trim();

				if (!input.equals("") && !input.equals(defaultText))
					updater.updateFields();
			}
		}

		public void keyPressed(KeyEvent e) {
			input = getText().trim();

			if (!input.equals("") && (input.charAt(0) == e.character) && input.substring(1).equals(defaultText)) {
				setText(String.valueOf(e.character));
				setSelection(1);
			}
		}
	};

	protected TextInput(TextComposite parent) {
		super(parent, SWT.SINGLE /* | SWT.BORDER */);
		this.parent = parent;
	}

	protected TextInput(TextComposite parent, Updater updater) {
		super(parent, SWT.SINGLE /* | SWT.BORDER */);
		this.parent = parent;
		this.updater = updater;
		updater.setInput(this);
	}

	protected void init() {
		logger.info("Initialize text input...");

		setMargin(MARGIN_TOPBOTTOM);
		setLayoutData(getWidthByMargin());

		setColors();
		configText();

		setFocus();
	}

	private void setColors() {
		setBackground(new Color(Display.getCurrent(), 50, 50, 50));
		setForeground(parent.getForeground());
	}


	private GridData getWidthByMargin() {
		int inputWidth = parent.getWidth() - (2 * margin_leftRight); // actually less pixel(s) required (whyever..); os
																		// different
		return LayoutHelper.getGridData(inputWidth);
	}

	private void configText() {
		setFont(parent.getFont(SWT.BOLD));
		setTextLimit(60);
		setText(defaultText);

		addFocusListener(focus);
		addKeyListener(key);
	}

	private void setMargin(int topBottom) {
		margin_leftRight = parent.getLeftRightMargin();
		LayoutHelper.setMargin(this, margin_leftRight, topBottom);
	}

}
