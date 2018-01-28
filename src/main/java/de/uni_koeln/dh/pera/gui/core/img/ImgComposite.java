package de.uni_koeln.dh.pera.gui.core.img;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
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
	
	private Coordinate position = null;

	// TODO zoom out
	private SelectionListener zoomOutSelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			System.out.println("zoomOut");
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	// TODO zoom in
	private SelectionListener zoomInSelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			System.out.println("zoomIn");
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	// TODO city layer
	private SelectionListener citySelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			System.out.println("city: " + ((Button) e.widget).getSelection());
			map.changeVisibility(2); //layer[2] = standorte
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	// TODO route layer
	private SelectionListener routeSelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			System.out.println("route: " + ((Button) e.widget).getSelection());
			map.changeVisibility(0); //layer[0] = routen
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	public ImgComposite(Composite parent) {
		super(parent);
	}

	public ImgComposite(Composite parent, List<Node> nodes, Player player) {
		super(parent, nodes, player);
	}

	public void init(int height) {
		logger.info("Initialize image composite...");
		super.init(height);

		// sample data:
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

	private void setHeader(int headHeight) {
		logger.info("Initialize header...");

		header = new Label(this, SWT.CENTER);
		header.setLayoutData(/*LayoutHelper.getCenteredData()*/LayoutHelper.getAlignment(LayoutHelper.getGridData(parentWidth), true, true));
//		header.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA));
		header.setForeground(getDefaultFgColor());
		header.setFont(getHeaderFont(headHeight));
		header.setText(getChapter());
		
	}
	
	private void setControls() {
		logger.info("Initialize controls...");
		
		Composite ctrlComp = new Composite(this, SWT.NONE);
		ctrlComp.setLayout(getCtrlLayout());
		ctrlComp.setLayoutData(LayoutHelper.getCenteredData());
//		ctrlComp.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
		
		// TODO button images
		
		Button zoomOutButton = new Button(ctrlComp, SWT.PUSH);
		zoomOutButton.setText("-");
		zoomOutButton.addSelectionListener(zoomOutSelection);
		
		Button zoomInButton = new Button(ctrlComp, SWT.PUSH);
		zoomInButton.setText("+");
		zoomInButton.addSelectionListener(zoomInSelection);
		
		Button cityButton = new Button(ctrlComp, SWT.TOGGLE);
		cityButton.setText("S");	
		cityButton.addSelectionListener(citySelection);
		
		Button routeButton = new Button(ctrlComp, SWT.TOGGLE);
		routeButton.setText("R");
		routeButton.addSelectionListener(routeSelection);

	}
	
	private Font getHeaderFont(int headHeight) {
		Font font = getFont("Palatino Linotype", (headHeight / 3), SWT.BOLD);
		return font;
	}
	
	private Layout getCtrlLayout() {
		RowLayout layout = LayoutHelper.getRowLayout(parentWidth / 10);
		return layout;
	}
	
	protected int getWidth() {
		return parentWidth;
	}
	
	public int getInnerWidth() {
		return map.getWidth();
	}

	public void updateAll() {
		header.setText(getChapter());
		
		Coordinate newCoordinate = new Coordinate(Double.parseDouble(nodes.get(currentID).getLatitude()),
				Double.parseDouble(nodes.get(currentID).getLongitude()));
		
		if(!newCoordinate.equals(position)) { //if position changed
			position = newCoordinate;
			map.updatePosition(position);
		}

	}

	private String getChapter() {
		Node node = super.nodes.get(super.currentID);
		return node.getChapter();
	}



}
