package de.uni_koeln.dh.pera.gui.core.img;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;

import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.core.BaseComposite;
import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

public class ImgComposite extends BaseComposite {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Map map = null;
	private Label header = null;

	private Button cityButton, routeButton, politButton;

	private Coordinate position = null;

	////////////////LISTENER/////////////////////////////////
	private Listener placesSelection = new Listener() {
		public void handleEvent(Event e) {
			map.setLayerVisibility(
					getSelection(e), 
					map.getPlacesLayer());
		}
	};

	private Listener routeSelection = new Listener() {
		public void handleEvent(Event e) {
			map.setLayerVisibility(
					getSelection(e), 
					map.getRouteLayer());
		}
	};

	private Listener territorySelection = new Listener() {
		public void handleEvent(Event e) {
			map.setLayerVisibilities(
					getSelection(e), 
					map.getTerritoryLayers(), 
					map.getLegendShell(getTooltip(e)));
		}		
	};

	///////////////////////CONSTRUCTORS////////////////////////////
	public ImgComposite(Composite parent) {
		super(parent);
	}

	public ImgComposite(Composite parent, java.util.Map<Integer, Node> nodes, Player player) {
		super(parent, nodes, player);
	}

	////////////METHODS/////////////////////////////
	public void init(int height) {
		logger.info("Initialize image composite...");
		super.init(height);

		// http://al-teef.info/wp-content/uploads/2017/12/texture-seamless-seamless-city-textures-door-texture-65-best-wood-texture-images-on-pinterest-wood-texture-and-wood.jpg
		setBackgroundImage(new Image(display, "src/main/resources/img/wood.jpg"));
		setInitialized(true);
	}

	public void addMapComponents() {
		logger.info("Add map components...");

		int height = getHeight();
		int mapHeight = (int) Calc.getValByPct(height, Map.H_HIMGCOMP_PCT);
		int headHeight = (height - mapHeight) / 2;

		setHeader(headHeight);

		map = new Map(this);
		map.init(mapHeight);
		position = new Coordinate(Double.parseDouble(nodes.get(currentID).getLatitude()),
				Double.parseDouble(nodes.get(currentID).getLongitude()));

		map.updatePosition(position);

		setControls();
	}
	
	public int getInnerWidth() {
		return map.getWidth();
	}

	public void updateAll() {
		header.setText(getChapter());

		Coordinate newCoordinate = new Coordinate(Double.parseDouble(nodes.get(currentID).getLatitude()),
				Double.parseDouble(nodes.get(currentID).getLongitude()));

		if (!newCoordinate.equals(position)) { // if position changed
			position = newCoordinate;
			map.updatePosition(position);
		}

	}
	
	protected int getWidth() {
		return parentWidth;
	}

	private void setHeader(int headHeight) {
		logger.info("Initialize header...");

		header = new Label(this, SWT.CENTER);
		header.setLayoutData(/* LayoutHelper.getCenteredData() */LayoutHelper
				.getAlignment(LayoutHelper.getGridData(parentWidth), true, true));
		header.setForeground(getDefaultFgColor());
		header.setFont(getHeaderFont(headHeight));
		header.setText(getChapter());

	}

	private void setControls() {
		logger.info("Initialize controls...");

		Composite ctrlComp = new Composite(this, SWT.NONE);
		ctrlComp.setLayout(getCtrlLayout());
		ctrlComp.setLayoutData(LayoutHelper.getCenteredData());

		cityButton = new Button(ctrlComp, SWT.TOGGLE);
		cityButton.setText("\u25bc");
		cityButton.setToolTipText("Orte");
		cityButton.addListener(SWT.Selection, placesSelection);
		
		routeButton = new Button(ctrlComp, SWT.TOGGLE);
		routeButton.setText("⚓︎");
		routeButton.setSelection(true);
		routeButton.setToolTipText("Reiseroute");
		routeButton.addListener(SWT.Selection, routeSelection);
		
		politButton = new Button(ctrlComp, SWT.TOGGLE);
		politButton.setText("♕");
		politButton.setToolTipText("Hoheitsgebiete");
		politButton.addListener(SWT.Selection, territorySelection);

	}

	private Font getHeaderFont(int headHeight) {
		Font font = getFont("Palatino Linotype", (headHeight / 3), SWT.BOLD);
		return font;
	}

	private Layout getCtrlLayout() {
		RowLayout layout = LayoutHelper.getRowLayout(parentWidth / 8);
		return layout;
	}

	private String getChapter() {
		Node node = super.nodes.get(super.currentID);
		return node.getChapter();
	}
	
	private boolean getSelection(Event e) {
		return ((Button)e.widget).getSelection();
	}
	
	private String getTooltip(Event e) {
		return ((Button)e.widget).getToolTipText();
	}

}
