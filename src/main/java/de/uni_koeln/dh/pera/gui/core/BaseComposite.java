package de.uni_koeln.dh.pera.gui.core;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;

public class BaseComposite extends Composite {

		private Logger logger = LoggerFactory.getLogger(getClass());
		
		protected Display display = null;
		protected List<Node> nodes = null;
		protected int currentID = 0;
		protected Player player = null; //TODO does imgComp really needs player?
		
		// parent == shell
		protected int parentWidth = 0;
		private int	height = 0;
		
		private boolean initialized = false;
	
	protected BaseComposite(Composite parent) {
		super(parent, SWT.NONE);
		parentWidth = parent.getBounds().width;
		
		this.display = getDisplay();
	}

	public BaseComposite(Composite parent, List<Node> nodes, Player player) {
		super(parent, SWT.NONE);
		parentWidth = parent.getBounds().width;
		
		this.display = getDisplay();
		this.nodes = nodes;
		this.currentID = player.getCurrentChapterNode();
		this.player = player;
	}

	protected void init(int height) {
		this.height = height;
		
		setLayout(LayoutHelper.getNormalizedLayout());		
		setLayoutData(LayoutHelper.getGridData(parentWidth, height));
	}
	
	public int getHeight() {
		return height;
	}
	
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean isInitialized() {
		if (!initialized)
			logger.error("Core composite not initialized.");
		
		return initialized;
	}
	
	protected Color getDefaultFgColor() {
		return display.getSystemColor(SWT.COLOR_WHITE);
	}
	
	public Color getDefaultBgColor() {
		return display.getSystemColor(SWT.COLOR_BLACK);
	}
	
	protected Font getFont(String name, int height, int style) {
		FontData data = new FontData(name, height, style);
		return new Font(display, data);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public int getCurrentID() {
		return currentID;
	}

	public void setCurrentID(int currentID) {
		this.currentID = currentID;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
	
}
