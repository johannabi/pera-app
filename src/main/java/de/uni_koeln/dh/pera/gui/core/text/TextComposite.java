package de.uni_koeln.dh.pera.gui.core.text;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.Updater;
import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.core.BaseComposite;
import de.uni_koeln.dh.pera.gui.core.img.ImgComposite;

public class TextComposite extends BaseComposite {
	
		private Logger logger = LoggerFactory.getLogger(getClass());
		
		private ImgComposite neighbour = null;
	
	public TextComposite(Composite parent, ImgComposite neighbour) {
		super(parent);
		this.neighbour = neighbour;
	}
	
	public TextComposite(Composite parent, ImgComposite neighbour, Map<Integer, Node> nodes, Player player) {
		super(parent, nodes, player);
		this.neighbour = neighbour;
	}

	public void init(int height) {
		logger.info("Initialize text composite...");
		super.init(height);		
				
		setColors();
		setInitialized(true);
	}
	
	public void addTexts() {
		logger.info("Add text boxes...");
		
		TextOutput output = new TextOutput(this);
		Updater updater = new Updater(this, neighbour, output);
		TextInput input = new TextInput(this, updater);
		
		
		output.init(updater.getInitialText(player));
		input.init();
		
//		Listener listener = new Listener(this, neighbour, input);
	}

	private void setColors() {
		setBackground(getDefaultBgColor());
		setForeground(getDefaultFgColor());
	}
	
	protected int getLeftRightMargin() {
		return (parentWidth - neighbour.getInnerWidth()) / 4;
	}
	
	protected int getWidth() {
		return parentWidth;
	}
	
	protected Font getFont(int style) {	
		int fHeight = getHeight() / 25;
		return getFont("Courier", fHeight, style);
	}
	
}
